package com.ch.resources;

import com.ch.application.FormsServiceApplication;
import com.ch.conversion.builders.JsonBuilder;
import com.ch.conversion.config.ITransformConfig;
import com.ch.conversion.config.TransformConfig;
import com.ch.model.FormsPackage;
import com.codahale.metrics.Timer;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.auth.Auth;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Aaron.Witter on 07/03/2016.
 */
@Path("/submission")
public class FormSubmissionResource {
    private static final Timer timer = FormsServiceApplication.registry.timer("FormSubmissionResource");

    private final MongoClient client;

    public FormSubmissionResource(MongoClient client) {
        this.client = client;
    }

    /**
     * Resource to post forms from Salesforce to CHIPS.
     *
     * @return json with response from CHIPS
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postForms(@Auth
                              FormDataMultiPart multi) {
        final Timer.Context context = timer.time();
        try {
            // convert input to json
            ITransformConfig config = new TransformConfig();
            JsonBuilder builder = new JsonBuilder(config, multi);
            FormsPackage transformedPackage = builder.getTransformedPackage();

            // insert into mongodb
            // TODO: get from config, refactor
            MongoDatabase database = client.getDatabase("enablement");
            MongoCollection<Document> packagesCollection = database.getCollection("packages");
            MongoCollection<Document> formsCollection = database.getCollection("forms");

            // add package to db
            Document packageMetaDataDocument = Document.parse(transformedPackage.getPackageMetaData());
            packagesCollection.insertOne(packageMetaDataDocument);
            // add each form to db
            for (String form : transformedPackage.getForms()) {
                Document transformedForm = Document.parse(form);
                formsCollection.insertOne(transformedForm);
            }

            // return 200
            return Response.ok().build();

        } finally {
            context.stop();
        }
    }
}