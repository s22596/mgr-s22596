package org.mgr.mgr_s22596.service;

import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.model.FileStructure;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DataBaseService {
    List<FileStructure> getFiles();

    List<FileStructure> getTestFiles();

    long downloadFile(String id, HttpServletResponse response);

    long saveFile(MultipartFile file) throws IOException;

    long deleteFile(String fileName) throws IOException;

    default long getDuration(long StartTime, long endTime) {
        return (endTime - StartTime) / 1_000_000;
    }
}
