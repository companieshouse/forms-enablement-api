package com.ch.helpers;

import com.ch.application.FormServiceConstants;
import com.ch.configuration.FormsServiceConfiguration;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.exception.DatabaseException;
import com.ch.exception.DuplicatePackageException;
import com.ch.model.FormStatus;
import com.ch.model.FormsPackage;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.ExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.HttpProxyFactory;
import de.flapdoodle.embed.process.runtime.Network;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by elliott.jenkins on 30/06/2016.
 */
public final class MongoHelper {

  private static MongoHelper instance = new MongoHelper();
  private final ITransformConfig config = new TransformConfig();
  private FormsServiceConfiguration configuration;
  private MongoClient client;

  private static String EMBEDDED_MONGO_PROXY_HOST_KEY = "embeddedMongoProxyHost";
  private static String EMBEDDED_MONGO_PROXY_PORT_KEY = "embeddedMongoProxyPort";

  private MongoHelper() {
  }

  /**
   * Init with the supplied config.
   *
   */
  public static void init(FormsServiceConfiguration configuration) {
    
    if (configuration.isTestMode()) {
      try {
        
        MongodStarter starter;

        String proxyHost = System.getProperty(EMBEDDED_MONGO_PROXY_HOST_KEY);
        Integer proxyPort = Integer.parseInt(System.getProperty(EMBEDDED_MONGO_PROXY_PORT_KEY, "8080"));

        if (proxyHost != null && proxyHost.length() > 0) {

          Command command = Command.MongoD;
  
          @SuppressWarnings("deprecation")
          IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
              .defaults(command)
              .artifactStore(new ExtractedArtifactStoreBuilder()
                  .defaults(command)
                  .download(new DownloadConfigBuilder()
                      .defaultsForCommand(command)
                      .proxyFactory(new HttpProxyFactory(proxyHost, proxyPort))))
                  .build();
          
          starter = MongodStarter.getInstance(runtimeConfig);
        } else {
          starter = MongodStarter.getDefaultInstance();
        }
        
        int port = Network.getFreeServerPort();
       
        IMongodConfig mongodConfig = new MongodConfigBuilder()
          .version(Version.Main.PRODUCTION)
          .net(new Net(port, Network.localhostIsIPv6()))
          .build();

        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        
        MongoClient testMongoClient = new MongoClient("localhost", port);

        instance.setConfiguration(configuration, testMongoClient);
      } catch (IOException ie) {
        ie.printStackTrace();
      }
        
    } else {
      instance.setConfiguration(configuration);
    }
    
  }

  public static MongoHelper getInstance() {
    return instance;
  }

  private void setConfiguration(FormsServiceConfiguration configuration) {
    this.configuration = configuration;
    this.client = setupMongoClient();
  }

  private void setConfiguration(FormsServiceConfiguration configuration, MongoClient client) {
    this.configuration = configuration;
    this.client = client;
  }

  /**
   * Get the mongo client.
   *
   * @return MongoClient
   */
  public MongoClient getMongoClient() {
    return client;
  }

  /**
   * Get mongo database.
   *
   * @return MongoDatabase
   */
  public MongoDatabase getDatabase() {
    return client.getDatabase(configuration.getMongoDbName());
  }

  /**
   * Get the packages collection.
   *
   * @return MongoCollection
   */
  public MongoCollection<Document> getPackagesCollection() {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbPackagesCollectionName());
  }

  /**
   * Get the packages collection by submission number.
   *
   * @return MongoCollection
   */
  public Document getPackageByPackageId(String packageId) {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbPackagesCollectionName())
      .find(new Document(config.getPackageIdentifierElementNameOut(), packageId))
      .first();
  }

  /**
   * Get the packages collection by status, limit number to parameter, sort oldest first. A count of 0, returns all packages.
   *
   * @return MongoCollection
   */
  public FindIterable<Document> getPackagesCollectionByStatus(String formStatus, int count) {
    MongoDatabase database = getDatabase();

    if (count == 0) {
      return database.getCollection(configuration.getMongoDbPackagesCollectionName())
        .find(new Document(config.getFormStatusPropertyNameOut(), formStatus)).sort(new Document("date", 1));
    } else {
      return database.getCollection(configuration.getMongoDbPackagesCollectionName())
        .find(new Document(config.getFormStatusPropertyNameOut(), formStatus))
        .limit(count).sort(new Document("date", 1));
    }
  }

  /**
   * Remove packages collection by submission number.*
   */
  public void removePackageByPackageId(String packageId) {
    MongoDatabase database = getDatabase();
    database.getCollection(configuration.getMongoDbPackagesCollectionName())
      .deleteOne(new Document(config.getPackageIdentifierElementNameOut(), packageId));
  }

  /**
   * update a package status by package Id.
   *
   * @return boolean depending on success.
   */
  public boolean updatePackageStatusByPackageId(String packageId, String formStatus) {
    MongoDatabase database = getDatabase();

    long modifiedCount = database.getCollection(configuration.getMongoDbPackagesCollectionName()).updateOne(new Document(
      config.getPackageIdentifierElementNameOut(), packageId), new Document("$set",
      new Document(config.getFormStatusPropertyNameOut(), formStatus))).getModifiedCount();

    if (modifiedCount == 1) {
      return true;
    }
    return false;
  }
  
  /**
   * Find packages by identifier and status.
   *
   * @return MongoCollection
   */
  public FindIterable<Document> getPackagesCollectionByPackageIdAndStatus(String packageId, String status) {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbPackagesCollectionName())
      .find(new Document(config.getFormStatusPropertyNameOut(), status.toUpperCase(Locale.ENGLISH))
        .append(config.getPackageIdentifierElementNameOut(), packageId));
  }

  /**
   * Get the forms collection.
   *
   * @return MongoCollection
   */
  public MongoCollection<Document> getFormsCollection() {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbFormsCollectionName());
  }


  /**
   * Get the forms collection by packageId.
   *
   * @return MongoCollection
   */
  public FindIterable<Document> getFormsCollectionByPackageId(String packageId) {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbFormsCollectionName()).find(new Document(config
      .getPackageIdentifierElementNameOut(), packageId));
  }

  /**
   * Get the forms collection by status.
   *
   * @return MongoCollection
   */
  public FindIterable<Document> getFormsCollectionByStatus(FormStatus status) {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbFormsCollectionName())
      .find(new Document(config.getFormStatusPropertyNameOut(), status.toString().toLowerCase()));
  }

  /**
   * Get the forms collection by package id and status.
   *
   * @return MongoCollection
   */
  public FindIterable<Document> getFormsCollectionByPackageIdAndStatus(String packageId, String status) {
    MongoDatabase database = getDatabase();
    return database.getCollection(configuration.getMongoDbFormsCollectionName())
      .find(new Document(config.getFormStatusPropertyNameOut(), status.toUpperCase(Locale.ENGLISH))
        .append(config.getPackageIdentifierElementNameOut(), packageId));
  }

  /**
   * Remove the forms collection by packageId.
   **/
  public void removeFormsCollectionByPackageId(String packageId) {
    MongoDatabase database = getDatabase();
    database.getCollection(configuration.getMongoDbFormsCollectionName()).deleteMany(new Document(config
      .getPackageIdentifierElementNameOut(), packageId));
  }

  /**
   * update matching forms status by package Id.
   *
   * @return MongoCollection
   */
  public boolean updateFormsStatusByPackageId(String packageId, String formStatus) {
    MongoDatabase database = getDatabase();

    long modifiedCount = database.getCollection(configuration.getMongoDbFormsCollectionName()).updateMany(new Document(
        config.getPackageIdentifierElementNameOut(), packageId),
      new Document("$set", new Document(config.getFormStatusPropertyNameOut(), formStatus))).getModifiedCount();

    if (modifiedCount == 1) {
      return true;
    }
    return false;
  }

  /**
   * update matching form status by object Id.
   *
   * @return MongoCollection
   */
  public boolean updateFormStatusByObjectId(ObjectId formId, String formStatus) {
    MongoDatabase database = getDatabase();

    long modifiedCount = database.getCollection(configuration.getMongoDbFormsCollectionName()).updateMany(new Document(
      FormServiceConstants.DATABASE_OBJECTID_KEY, formId), new Document("$set", new Document(config.getFormStatusPropertyNameOut(),
      formStatus))).getModifiedCount();

    if (modifiedCount == 1) {
      return true;
    }
    return false;
  }
  
  /**
   * update matching form status by object Id.
   *
   * @return MongoCollection
   */
  public boolean updateFormXmlByObjectId(ObjectId formId, String xml) {
    MongoDatabase database = getDatabase();

    long modifiedCount = database.getCollection(configuration.getMongoDbFormsCollectionName()).updateMany(new Document(
      FormServiceConstants.DATABASE_OBJECTID_KEY, formId), new Document("$set", new Document(config.getXmlPropertyNameOut(),
      xml))).getModifiedCount();

    if (modifiedCount == 1) {
      return true;
    }
    return false;
  }


  /**
   * Store a transformed package in the mongo database.
   * Package and forms stored separately but linked by the packageIdentifier.
   *
   * @param transformedPackage package to store
   */
  public boolean storeFormsPackage(FormsPackage transformedPackage) {

    List<String> forms = transformedPackage.getForms();

    // Get package metadata
    Document packageMetaDataDocument = Document.parse(transformedPackage.getPackageMetaData());
    String packageId = packageMetaDataDocument.getString(config.getPackageIdentifierElementNameOut());
    
    //Is there already a package with this identifier?
    if (getPackageByPackageId(packageId) != null) {
      throw new DuplicatePackageException(packageId);
    }
    
    //Insert
    getPackagesCollection().insertOne(packageMetaDataDocument);
    
    if (getPackageByPackageId(packageId) != null) {
      // add each form to db
      for (String form : forms) {
        Document transformedForm = Document.parse(form);
        getFormsCollection().insertOne(transformedForm);
      }
      //check all forms are saved successfully, else remove those which were saved, if any, throw exception
      if (getFormsCollectionByPackageId(packageId).into(new ArrayList<Document>()).size() != forms.size()) {
        removePackageByPackageId(packageId);
        removeFormsCollectionByPackageId(packageId);
        throw new DatabaseException(config.getFormsPropertyNameOut());
      }
      return true;
    }
    //check package is saved successfully, else throw exception
    throw new DatabaseException(FormServiceConstants.PACKAGE_METADATA_NAME);
  }

  private MongoClient setupMongoClient() {
    String uri = configuration.getMongoDbUri();
    MongoClientURI mongoUri = new MongoClientURI(uri);
    return new MongoClient(mongoUri);
  }

  public void dropCollection(String collection) {
    MongoDatabase database = getDatabase();
    database.getCollection(collection).drop();
  }
}
