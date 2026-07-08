package com.smartparking.ticketingservice;

import com.mongodb.client.MongoClient;

import com.smartparking.ticketingservice.application.TicketingServiceApiImpl;
import com.smartparking.ticketingservice.application.ports.TicketingRepository;
import com.smartparking.ticketingservice.application.ports.TicketingServiceAPI;
import com.smartparking.ticketingservice.infrastracture.persistence.TicketingRepositoryImpl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class TicketingServiceApp {

    private static final Logger logger = LoggerFactory.getLogger(TicketingServiceApp.class);

    @Autowired
    private MongoClient mongoClient;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String port;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${eureka.client.service-url.defaultZone}")
    private String eurekaZone;

    @Value("${eureka.instance.hostname}")
    private String hostname;

    public static void main(String[] args) {
        SpringApplication.run(TicketingServiceApp.class, args);
        logger.info("✅ Ticketing Service started successfully with Spring MongoDB + Vert.x ");
    }

    @PostConstruct
    public void printConfiguration() {
        logger.info("🔧 [Ticketing Service Configuration]");
        logger.info(" • Application Name : {}", appName);
        logger.info(" • Hostname         : {}", hostname);
        logger.info(" • Server Port      : {}", port);
        logger.info(" • MongoDB URI      : {}", mongoUri);
        logger.info(" • Eureka Zone      : {}", eurekaZone);
        logger.info("──────────────────────────────────────────────");
    }

    @Bean
    public TicketingRepository ticketingRepository(MongoClient mongoClient) {
        return new TicketingRepositoryImpl(mongoClient);
    }

    @Bean
    public TicketingServiceAPI ticketingServiceAPI(TicketingRepository repository) {
        return new TicketingServiceApiImpl(repository);
    }
}
