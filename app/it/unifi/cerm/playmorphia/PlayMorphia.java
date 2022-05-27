package it.unifi.cerm.playmorphia;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import play.Environment;
import play.inject.ApplicationLifecycle;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;

/**
 * @author morelli
 */
@Singleton
public class PlayMorphia {

    MongoClient mongo = null;
    Datastore datastore = null;

    MongoDatabase mongoDatabase = null;

    @Inject
    public PlayMorphia(ApplicationLifecycle lifecycle, Environment env, Config config) {
        try {
            configure(config, env.classLoader(), env.isTest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lifecycle.addStopHook(() -> {
            mongo().close();
            return CompletableFuture.completedFuture(null);
        });
    }


    private void configure(Config config, ClassLoader classLoader, boolean isTestMode) throws Exception {

        final var clientFactoryName = config.getString("playmorphia.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(clientFactoryName, config, isTestMode);
        this.mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of " + factory.getClass().getName());
        }

        this.datastore = Morphia.createDatastore(mongo, factory.getDbName());
        this.datastore.getMapper().mapPackage(factory.getModels());
        this.mongoDatabase = this.mongo.getDatabase(factory.getDbName());
    }

    @SuppressWarnings({"rawtypes"})
    protected MongoClientFactory getMongoClientFactory(String className, Config config, boolean isTestMode) throws Exception {

        if (className != null) {
            final var factoryClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            if (!MongoClientFactory.class.isAssignableFrom(factoryClass)) {
                throw new IllegalStateException("mongoClientFactory '" + className +
                        "' is not of type " + MongoClientFactory.class.getName());
            }
            Constructor constructor = null;
            try {
                constructor = factoryClass.getConstructor(Config.class);
            } catch (Exception e) {
                // can't use that one
            }
            if (constructor == null) {
                return (MongoClientFactory) factoryClass.getDeclaredConstructor().newInstance();
            }
        }
        return new MongoClientFactory(config, isTestMode);
    }


    public MongoClient mongo() {
        return this.mongo;
    }

    public Datastore datastore() {
        return this.datastore;
    }


    public MongoDatabase mongoDatabase() {
        return this.mongoDatabase;
    }
}