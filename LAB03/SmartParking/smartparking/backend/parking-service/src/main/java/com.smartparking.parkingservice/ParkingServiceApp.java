package com.smartparking.parkingservice;

import com.mongodb.client.MongoClient;

import com.smartparking.parkingservice.application.ParkingServiceApiImpl;
import com.smartparking.parkingservice.application.ports.ParkingServiceAPI;
import com.smartparking.parkingservice.application.ports.ParkingServiceRepository;
import com.smartparking.parkingservice.infrastracture.persistence.ParkingRepositoryImpl;

import com.smartparking.parkingservice.llm.service.LlmParkingDecisionService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class ParkingServiceApp {

    private static final Logger logger = LoggerFactory.getLogger(ParkingServiceApp.class);

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
        SpringApplication.run(ParkingServiceApp.class, args);
        logger.info("✅ Parking Service started successfully with Spring MongoDB + Vert.x ");
    }

    @PostConstruct
    public void printConfiguration() {
        logger.info("🔧 [Parking Service Configuration]");
        logger.info(" • Application Name : {}", appName);
        logger.info(" • Hostname         : {}", hostname);
        logger.info(" • Server Port      : {}", port);
        logger.info(" • MongoDB URI      : {}", mongoUri);
        logger.info(" • Eureka Zone      : {}", eurekaZone);
        logger.info("──────────────────────────────────────────────");
    }

    @Bean
    public ParkingServiceRepository ParkingRepository(MongoClient mongoClient) {
        return new ParkingRepositoryImpl(mongoClient);
    }

    @Bean
    public ParkingServiceAPI parkingServiceAPI(ParkingServiceRepository repository) {
        return new ParkingServiceApiImpl(repository);
    }



}
