package org.mgr.mgr_s22596.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@Service
public class MongoDBService extends GeneralService {

    @Autowired
    GridFsOperations gridFsOperations;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    public List<String> listFiles(String path) {
        GridFSFindIterable queryResult;
        if (path.isEmpty()) {
            queryResult = gridFsOperations.find(
                    new BasicQuery("{ 'filename': { $regex: '^[^/]+$' } }")
            );
        } else {
            queryResult = gridFsOperations.find(
                    new BasicQuery("{ 'filename': { $regex: '^" + path + "/[^/]+$'} }")
            );
        }
        List<String> resultList = new ArrayList<>();
        for (GridFSFile file : queryResult) {
            if (file.getFilename().charAt(file.getFilename().lastIndexOf('/') + 1) != '.')
                resultList.add(file.getMetadata().get("type") + " " + file.getFilename().substring(file.getFilename().lastIndexOf('/') + 1));
        }
        return resultList;
    }

    public List<String> searchFiles(String path, String query) {
        List<String> allFiles = listFiles(path);
        return searchFiles(allFiles, query);
    }

    public void downloadFile(String currentPath, String filename, HttpServletResponse response) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(currentPath + "/" + filename)));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = downloadStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

    }
}