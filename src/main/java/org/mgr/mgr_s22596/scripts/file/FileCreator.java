package org.mgr.mgr_s22596.scripts.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class FileCreator {
    private static final long[] FILE_SIZES = {
            1024 * 1024,          // 1 MB
            10 * 1024 * 1024,     // 10 MB
            100 * 1024 * 1024,    // 100 MB
            1024 * 1024 * 1024    // 1 GB
    };

    private static final String filePath = "src\\main\\java\\org\\mgr\\mgr_s22596\\scripts\\file\\";

    private static final String[] FILE_NAMES = {
            filePath + "file_1MB.txt",
            filePath + "file_10MB.txt",
            filePath + "file_100MB.txt",
            filePath + "file_1GB.txt"
    };

    public static void main(String[] args) {
        for (int i = 0; i < FILE_SIZES.length; i++) {
            createFile(FILE_NAMES[i], FILE_SIZES[i]);
        }
    }

    private static void createFile(String fileName, long targetSize) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Random random = new Random();
            long bytesWritten = 0;

            while (bytesWritten < targetSize) {
                char randomChar = (char) ('a' + random.nextInt(26));
                writer.write(randomChar);
                bytesWritten++;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}