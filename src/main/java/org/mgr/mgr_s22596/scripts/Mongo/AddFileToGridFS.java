package org.mgr.mgr_s22596.scripts.Mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

import java.io.*;

public class AddFileToGridFS {
    public static void main(String[] args) throws IOException {

        File fileSmall = new File("src/main/java/org/mgr/mgr_s22596/scripts/test_small.txt");
        File fileBig = new File("src/main/java/org/mgr/mgr_s22596/scripts/test_big.txt");

        // zapełnienie plików tekstowych słowem "test" określoną liczbe razy
        writeTestToFile(fileSmall, 5_000);
        writeTestToFile(fileBig, 50_000);

        // połączenie z bazą danych MongoDB i zapisanie plików do GridFS
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
             FileInputStream streamToUploadSmall = new FileInputStream(fileSmall);
             FileInputStream streamToUploadBig = new FileInputStream(fileBig)) {


            MongoDatabase database = mongoClient.getDatabase("test");
            GridFSBucket gridFSBucket = GridFSBuckets.create(database);
            org.bson.Document fileMetadata = new org.bson.Document();
            GridFSUploadOptions options = new GridFSUploadOptions().metadata(fileMetadata);

            gridFSBucket.uploadFromStream("test_small.txt", streamToUploadSmall, options);
            gridFSBucket.uploadFromStream("test_big.txt", streamToUploadBig, options);
        }
    }

    private static void writeTestToFile(File file, int times) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (int i = 0; i < times; i++) {
                writer.println("test");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
