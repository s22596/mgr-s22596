package org.mgr.mgr_s22596.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.mgr.mgr_s22596.model.FileStructure;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.util.List;

@Service
public class MSSQLService implements DataBaseService {

    private final JdbcTemplate jdbcTemplate;
    private final String SELECT_ALL_FILES =
            "SELECT stream_id, name FROM dbo.ArchFileTable";
    private final String SELECT_TEST_FILES =
            "SELECT stream_id, name FROM dbo.ArchFileTable WHERE name LIKE 'test%'";
    private final String SELECT_FILE =
            "SELECT name, file_stream FROM dbo.ArchFileTable WHERE stream_id = ?";
    private final String INSERT_FILE =
            "INSERT INTO dbo.ArchFileTable (name, file_stream) VALUES (?, ?)";
    private final String DELETE_FILE =
            "DELETE FROM dbo.ArchFileTable WHERE stream_id = ?";

    @Autowired
    public MSSQLService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FileStructure> getFiles() {
        return jdbcTemplate.query(SELECT_ALL_FILES, (rs, rowNum) -> {
            FileStructure file = new FileStructure();
            file.setId(rs.getString("stream_id"));
            file.setName(rs.getString("name"));
            return file;
        });
    }

    @Override
    public List<FileStructure> getTestFiles() {
        return jdbcTemplate.query(SELECT_TEST_FILES, (rs, rowNum) -> {
            FileStructure file = new FileStructure();
            file.setId(rs.getString("stream_id"));
            file.setName(rs.getString("name"));
            return file;
        });
    }


    @Override
    public long downloadFile(String streamId, HttpServletResponse response) {
        long startTime = System.nanoTime();
        jdbcTemplate.query(SELECT_FILE, new Object[]{streamId}, rs -> {
            String fileName = rs.getString("name");
            InputStream inputStream = rs.getBinaryStream("file_stream");

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        long endTime = System.nanoTime();
        return getDuration(startTime, endTime);
    }

    @Override
    public long saveFile(MultipartFile file) {
        long startTime = System.nanoTime();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_FILE);
            ps.setString(1, file.getOriginalFilename());
            try {
                ps.setBinaryStream(2, file.getInputStream(), (int) file.getSize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
        return getDuration(startTime, System.nanoTime());
    }

    @Override
    public long deleteFile(String streamId) {
        long startTime = System.nanoTime();
        jdbcTemplate.update(DELETE_FILE, streamId);
        return getDuration(startTime, System.nanoTime());
    }

}
