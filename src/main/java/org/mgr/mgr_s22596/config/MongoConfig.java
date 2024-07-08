package org.mgr.mgr_s22596.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.mgr.mgr_s22596.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(AppConfig.MONGO_DB_ADDRESS);
    }

    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory factory, MappingMongoConverter converter) {
        return new GridFsTemplate(factory, converter);
    }

    @Override
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoCustomConversions conversions, MongoMappingContext context) {
        return new MappingMongoConverter(factory, context);
    }

    @Bean
    public GridFSBucket gridFSBucket(MongoDatabaseFactory mongoDatabaseFactory) {
        return GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
    }

    public Boolean databaseFound(MongoClient mongoClient, String databaseName) {
        for (String s : mongoClient.listDatabaseNames()) {
            if (s.equals(databaseName)) {
                return true;
            }
        }
        return false;
    }

    public static void setUpGridFSDatabase() throws FileNotFoundException {
        try (MongoClient mongoClient = MongoClients.create(AppConfig.MONGO_DB_ADDRESS)) {
            MongoDatabase database = mongoClient.getDatabase(AppConfig.MONGO_DB_NAME);
            GridFSBucket gridFSBucket = GridFSBuckets.create(database);

            uploadDirectoryToGridFS(gridFSBucket, AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_USER_DISK_NAME, "");
        }
    }

    private static void uploadDirectoryToGridFS(GridFSBucket gridFSBucket, String folderPath, String parentPath) throws FileNotFoundException {
        File folder = new File(folderPath);
        for (File file : folder.listFiles(file -> !file.isHidden())) {
            String relativePath = parentPath.isEmpty() ? file.getName() : parentPath + "/" + file.getName();
            if (file.isDirectory()) {
                org.bson.Document folderMetadata = new org.bson.Document("path", relativePath).append("type", "folder");
                gridFSBucket.uploadFromStream(relativePath, new ByteArrayInputStream(new byte[0]), new GridFSUploadOptions().metadata(folderMetadata));
                uploadDirectoryToGridFS(gridFSBucket, file.getAbsolutePath(), relativePath);
            } else {
                FileInputStream streamToUploadFrom = new FileInputStream(file);
                org.bson.Document fileMetadata = new org.bson.Document("path", relativePath).append("type", "file");
                GridFSUploadOptions options = new GridFSUploadOptions().metadata(fileMetadata);
                gridFSBucket.uploadFromStream(relativePath, streamToUploadFrom, options);
            }
        }
    }
}