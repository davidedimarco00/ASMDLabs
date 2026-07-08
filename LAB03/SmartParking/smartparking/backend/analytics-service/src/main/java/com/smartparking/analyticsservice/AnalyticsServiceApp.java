package com.smartparking.analyticsservice;

import com.mongodb.client.MongoClient;
import com.smartparking.analyticsservice.application.ports.TicketingServicePort;
import com.smartparking.analyticsservice.infrastructure.adapters.TicketingServiceAdapter;
import com.smartparking.analyticsservice.infrastructure.persistence.AnalyticsRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class AnalyticsServiceApp {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceApp.class);

    @Autowired
    private MongoClient mongoClient;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:paymentservicedb}")
    private String databaseName;

    @Bean
    public AnalyticsRepositoryImpl analyticsRepository(MongoClient mongoClient) {

        logger.info("🔧 Initializing AnalyticsRepository with database: {}", databaseName);
        return new AnalyticsRepositoryImpl(mongoClient);
    }

    @Bean
    public TicketingServicePort ticketingServicePort() {
        logger.info("🔧 Initializing TicketingServiceAdapter");
        return new TicketingServiceAdapter();
    }

    public static void main(String[] args) {
        logger.info("🚀 Starting Analytics Service...");
        SpringApplication.run(AnalyticsServiceApp.class, args);
        logger.info("✅ Analytics Service started successfully!");
    }
}

