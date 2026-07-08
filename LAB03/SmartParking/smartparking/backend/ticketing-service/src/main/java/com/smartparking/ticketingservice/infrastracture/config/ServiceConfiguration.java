package com.smartparking.ticketingservice.infrastracture.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ServiceConfiguration {

    private static ServiceConfiguration instance;
    private final Vertx vertx;
    private JsonObject config;
    private final ConfigRetriever retriever;

    private ServiceConfiguration(Vertx vertx) {
        this.vertx = vertx;
        this.retriever = initializeRetriever();
    }

    public static synchronized ServiceConfiguration getInstance(Vertx vertx) {
        if (instance == null) {
            instance = new ServiceConfiguration(vertx);
        }
        return instance;
    }

    private ConfigRetriever initializeRetriever() {
        ConfigStoreOptions envStore = new ConfigStoreOptions()
                .setType("env")
                .setConfig(new JsonObject()
                        .put("keys", new JsonArray()
                                .add("TICKETING_SERVICE_INSTANCE_HOSTNAME")
                                .add("TICKETING_SERVICE_INSTANCE_PORT")
                                .add("TICKETING_MONGODB_URI")

                                .add("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE")
                                .add("EUREKA_INSTANCE_HOSTNAME")
                                .add("EUREKA_INSTANCE_PORT")
                        )
                );

        return ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(envStore));
    }

    public Future<JsonObject> load() {
        return retriever.getConfig()
                .onSuccess(conf -> {
                    this.config = conf;
                    System.out.println("✅ CONFIGURATION LOADED (TICKETING-SERVICE): " + this.config.encodePrettily());
                    retriever.listen(change -> {
                        this.config = change.getNewConfiguration();
                        System.out.println("🔄 Configuration updated (TICKETING-SERVICE): " + this.config.encodePrettily());
                    });
                });
    }

    public JsonObject getServiceConfig() {
        return new JsonObject()
                .put("name", config.getString("TICKETING_SERVICE_INSTANCE_HOSTNAME", "ticketing-service"))
                .put("port", config.getInteger("TICKETING_SERVICE_INSTANCE_PORT", 8084));
    }

    public JsonObject getMongoConfig() {
        return new JsonObject()
                .put("uri", config.getString("TICKETING_MONGODB_URI", "mongodb://mongo-ticketing:27017/ticketingservicedb"));
    }

    public JsonObject getEurekaConfig() {
        return new JsonObject()
                .put("url", config.getString("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://service-discovery:8761/eureka/"))
                .put("hostname", config.getString("EUREKA_INSTANCE_HOSTNAME", "service-discovery"))
                .put("port", config.getInteger("EUREKA_INSTANCE_PORT", 8761));
    }
}
