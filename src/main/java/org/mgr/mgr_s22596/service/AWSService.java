package org.mgr.mgr_s22596.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.model.FileStructure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AWSService implements DataBaseService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    private AmazonS3 s3Client;

    @PostConstruct
    private void initializeAmazon() {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }

    @Override
    public List<FileStructure> getFiles() {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        return result.getObjectSummaries().stream()
                .map(s3ObjectSummary -> {
                    FileStructure fileStructure = new FileStructure();
                    fileStructure.setId(s3ObjectSummary.getKey());
                    fileStructure.setName(s3ObjectSummary.getKey());
                    return fileStructure;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FileStructure> getTestFiles() {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        return result.getObjectSummaries().stream()
                .filter(s -> s.getKey().toLowerCase().startsWith("test"))
                .map(s3ObjectSummary -> {
                    FileStructure fileStructure = new FileStructure();
                    fileStructure.setId(s3ObjectSummary.getKey());
                    fileStructure.setName(s3ObjectSummary.getKey());
                    return fileStructure;
                })
                .collect(Collectors.toList());
    }


    @Override
    public long downloadFile(String key, HttpServletResponse response) {
        long startTime = System.nanoTime();
        S3Object s3Object = s3Client.getObject(bucketName, key);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + key + "\"");

        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }

    @Override
    public long saveFile(MultipartFile file) throws IOException {
        long startTime = System.nanoTime();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest request = new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata);
        s3Client.putObject(request);

        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }

    @Override
    public long deleteFile(String key) {
        long startTime = System.nanoTime();
        s3Client.deleteObject(bucketName, key);
        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }
}
