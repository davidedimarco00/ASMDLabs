package com.smartparking.paymentservice;

import com.mongodb.client.MongoClient;
import com.smartparking.paymentservice.application.PaymentServiceAPIImpl;
import com.smartparking.paymentservice.application.ports.PaymentServiceAPI;
import com.smartparking.paymentservice.application.ports.TicketingServicePort;
import com.smartparking.paymentservice.infrastracture.adapters.TicketingServiceAdapter;
import com.smartparking.paymentservice.infrastracture.persistence.PaymentRepositoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApp {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceApp.class);

    @Autowired
    private MongoClient mongoClient;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:paymentservicedb}")
    private String databaseName;

    @Bean
    public PaymentRepositoryImpl paymentRepository(MongoClient mongoClient) {
        logger.info("🔧 Initializing PaymentRepository with database: {}", databaseName);
        return new PaymentRepositoryImpl(mongoClient);
    }

    @Bean
    public TicketingServicePort ticketingServicePort() {
        logger.info("🔧 Initializing TicketingServiceAdapter");
        return new TicketingServiceAdapter();
    }

    @Bean
    public PaymentServiceAPI paymentService(PaymentRepositoryImpl paymentRepository,
                                            TicketingServicePort ticketingServicePort) {
        logger.info("🔧 Initializing PaymentService");
        return new PaymentServiceAPIImpl(paymentRepository, ticketingServicePort);
    }

    public static void main(String[] args) {
        logger.info("🚀 Starting Payment Service...");
        SpringApplication.run(PaymentServiceApp.class, args);
        logger.info("✅ Payment Service started successfully");
    }
}
