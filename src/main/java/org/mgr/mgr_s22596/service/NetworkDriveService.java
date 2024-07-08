package org.mgr.mgr_s22596.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkDriveService extends GeneralService {

    public List<String> getFiles(String path) {
        File directoryPath = new File(path);
        String[] contents = directoryPath.list();
        List<String> files = new ArrayList<>();
        List<String> folders = new ArrayList<>();
        if (contents == null) {
            return new ArrayList<>();
        }
        for (String content : contents) {
            if (content.charAt(0) == '.') {
                continue;
            }
            if (new File(directoryPath + "\\" + content).isFile()) {
                files.add("file " + content);
            } else {
                folders.add("folder " + content);
            }
        }

        List<String> allFiles = new ArrayList<>(folders);
        allFiles.addAll(files);
        return allFiles;
    }

    public void downloadFile(String path, String fileName, HttpServletResponse response) throws IOException {
        File fileToDownload = new File(path + "\\" + fileName);
        if (fileToDownload.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            Files.copy(fileToDownload.toPath(), response.getOutputStream());
            response.getOutputStream().flush();

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public List<String> searchFiles(String path, String query) {
        List<String> allFiles = getFiles(path);
        return searchFiles(allFiles, query);
    }


}
