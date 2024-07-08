package org.mgr.mgr_s22596;

import com.mongodb.client.MongoClient;
import jakarta.annotation.PostConstruct;
import org.mgr.mgr_s22596.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;

@SpringBootApplication
public class MgrS22596Application {

    @Autowired
    private MongoConfig mongoConfig;

    @Autowired
    private MongoClient mongoClient;

    public static void main(String[] args) {
        SpringApplication.run(MgrS22596Application.class, args);
    }

    @PostConstruct
    public void setUp() {
        try {
            if (mongoConfig.databaseFound(mongoClient, AppConfig.MONGO_DB_NAME)) {
                System.out.println("GridFS database already exists. Skipping setup.");
            } else {
                System.out.println("Creating GridFS database...");
                MongoConfig.setUpGridFSDatabase();
                System.out.println("GridFS database created successfully!");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}