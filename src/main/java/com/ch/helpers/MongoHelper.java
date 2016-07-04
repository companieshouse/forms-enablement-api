package com.ch.helpers;

import com.ch.configuration.FormsServiceConfiguration;
import com.ch.model.FormsPackage;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by elliott.jenkins on 30/06/2016.
 */
public class MongoHelper {
    private final MongoClient client;
    private final FormsServiceConfiguration config;

    public MongoHelper(FormsServiceConfiguration config) {
        this.config = config;
        this.client = setupMongoClient();
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
        return client.getDatabase(config.getMongoDbName());
    }

    /**
     * Get the packages collection.
     *
     * @return MongoCollection
     */
    public MongoCollection<Document> getPackagesCollection() {
        MongoDatabase database = getDatabase();
        return database.getCollection(config.getMongoDbPackagesCollectionName());
    }

    /**
     * Get the forms collection.
     *
     * @return MongoCollection
     */
    public MongoCollection<Document> getFormsCollection() {
        MongoDatabase database = getDatabase();
        return database.getCollection(config.getMongoDbFormsCollectionName());
    }

    /**
     * Store a transformed package in the mongo database.
     * Package and forms stored separately but linked by the packageIdentifier.
     *
     * @param transformedPackage package to store
     */
    public void storeFormsPackage(FormsPackage transformedPackage) {
        // add package to db
        Document packageMetaDataDocument = Document.parse(transformedPackage.getPackageMetaData());
        getPackagesCollection().insertOne(packageMetaDataDocument);

        // add each form to db
        for (String form : transformedPackage.getForms()) {
            Document transformedForm = Document.parse(form);
            getFormsCollection().insertOne(transformedForm);
        }
    }

    private MongoClient setupMongoClient() {
        String uri = config.getMongoDbUri();
        MongoClientURI mongoUri = new MongoClientURI(uri);
        return new MongoClient(mongoUri);
    }
}
