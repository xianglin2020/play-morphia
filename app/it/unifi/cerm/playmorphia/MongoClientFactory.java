package it.unifi.cerm.playmorphia;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.typesafe.config.Config;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.inject.Inject;

/**
 * @author morelli
 * @date 21/02/19
 */
public class MongoClientFactory {

    protected Config config;
    protected boolean isTest;

    public MongoClientFactory(Config config) {
        this.config = config;
    }

    @Inject
    public MongoClientFactory(Config config, boolean isTest) {
        this.config = config;
        this.isTest = isTest;
    }

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     */
    public MongoClient createClient() {
        final var pojoCodecProvider = PojoCodecProvider
                .builder()
                .automatic(true)
                .build();

        final var codecRegistries = CodecRegistries
                .fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(pojoCodecProvider));

        var connectionString = getConnectionString();

        var mongoClientSettings = MongoClientSettings
                .builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistries)
                .build();

        return MongoClients.create(mongoClientSettings);
    }


    /**
     * Returns the database name associated with the current configuration.
     *
     * @return The database name
     */
    public String getDbName() {
        return getConnectionString().getDatabase();
    }

    protected ConnectionString getConnectionString() {
        return new ConnectionString(
                isTest
                        ? config.getString("playmorphia.test-uri")
                        : config.getString("playmorphia.uri"));
    }

    /**
     * Returns the models folder name associated with the current configuration.
     *
     * @return The models folder name
     */
    public String getModels() {
        return config.getString("playmorphia.models");
    }

}