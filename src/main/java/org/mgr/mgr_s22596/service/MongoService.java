package org.mgr.mgr_s22596.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.mgr.mgr_s22596.model.FileStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MongoService implements DataBaseService {

    @Autowired
    GridFsOperations gridFsOperations;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public List<FileStructure> getFiles() {
        GridFSFindIterable queryResult = gridFsOperations.find(
                new BasicQuery("{ 'filename': { $regex: '^[^/]+$' } }")
        );
        List<FileStructure> resultList = new ArrayList<>();
        for (GridFSFile file : queryResult) {
            FileStructure fileStructure = new FileStructure();
            fileStructure.setId(file.getObjectId().toHexString());
            fileStructure.setName(file.getFilename());
            resultList.add(fileStructure);
        }
        return resultList;
    }

    @Override
    public List<FileStructure> getTestFiles() {
        GridFSFindIterable queryResult = gridFsOperations.find(
                new BasicQuery("{ 'filename': { $regex: '^test'} }")
        );
        List<FileStructure> resultList = new ArrayList<>();
        for (GridFSFile file : queryResult) {
            FileStructure fileStructure = new FileStructure();
            fileStructure.setId(file.getObjectId().toHexString());
            fileStructure.setName(file.getFilename());
            resultList.add(fileStructure);
        }
        return resultList;
    }

    @Override
    public long downloadFile(String objectId, HttpServletResponse response) {
        long startTime = System.nanoTime();
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(objectId))));
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + gridFSFile.getFilename() + "\"");

        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = downloadStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }

    @Override
    public long saveFile(MultipartFile file) throws IOException {
        long startTime = System.nanoTime();
        gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename());
        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }

    @Override
    public long deleteFile(String objectId) {
        long startTime = System.nanoTime();
        Query query = new Query(Criteria.where("_id").is(new ObjectId(objectId)));
        gridFsTemplate.delete(query);
        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }
}
