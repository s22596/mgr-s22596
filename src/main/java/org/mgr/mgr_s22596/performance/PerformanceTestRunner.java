package org.mgr.mgr_s22596.performance;

import org.mgr.mgr_s22596.model.FileStructure;
import org.mgr.mgr_s22596.service.DataBaseService;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class PerformanceTestRunner {

    private final DataBaseService service;

    public PerformanceTestRunner(DataBaseService service) {
        this.service = service;
    }

    public DataBaseService getService() {
        return service;
    }


    public List<MultipartFile> generateTestFiles(int count, int sizeInKB) throws IOException {
        List<MultipartFile> files = new ArrayList<>();
        int targetSize = sizeInKB * 1024;
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            StringBuilder contentBuilder = new StringBuilder(targetSize);
            for (int j = 0; j < targetSize; j++) {
                char randomChar = (char) ('a' + random.nextInt(26));
                contentBuilder.append(randomChar);
            }

            byte[] content = contentBuilder.toString().getBytes();

            MultipartFile file = new MockMultipartFile(
                    "file" + i,
                    "test_file" + i + ".txt",
                    "text/plain",
                    new ByteArrayInputStream(content)
            );
            files.add(file);
        }

        return files;
    }



    public void runConcurrentUpload(List<MultipartFile> files) throws InterruptedException {
        System.out.println("=== TEST RÓWNOLEGŁEGO WYSYŁANIA PLIKU ===");
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Long>> tasks = new ArrayList<>();

        for (MultipartFile file : files) {
            tasks.add(() -> {
                try {
                    return service.saveFile(file);
                } catch (IOException e) {
                    return 0L;
                }
            });
        }

        long start = System.nanoTime();
        List<Future<Long>> results = executor.invokeAll(tasks);
        long end = System.nanoTime();
        executor.shutdown();

        List<Long> times = new ArrayList<>();
        for (Future<Long> future : results) {
            try {
                times.add(future.get());
            } catch (Exception ignored) {}
        }

        double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
        System.out.println("Czas całkowity: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Średni czas wysyłania pliku: " + avg + " ms");
        System.out.println("=========================================");
    }

    public void runConcurrentDownload(List<FileStructure> files) throws InterruptedException {
        System.out.println("=== TEST RÓWNOLEGŁEGO POBIERANIA PLIKU ===");
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Long>> tasks = new ArrayList<>();

        for (FileStructure file : files) {
            tasks.add(() -> service.downloadFile(file.getId(), new MockHttpServletResponse()));
        }

        long start = System.nanoTime();
        List<Future<Long>> results = executor.invokeAll(tasks);
        long end = System.nanoTime();
        executor.shutdown();

        List<Long> durations = new ArrayList<>();
        for (Future<Long> future : results) {
            try {
                durations.add(future.get());
            } catch (Exception ignored) {}
        }

        double avg = durations.stream().mapToLong(Long::longValue).average().orElse(0);
        System.out.println("Liczba plików: " + files.size());
        System.out.println("Czas całkowity: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Średni czas pobierania pliku: " + avg + " ms");
        System.out.println("=========================================");
    }




    public void runConcurrentDelete(List<FileStructure> files) throws InterruptedException {
        System.out.println("=== TEST RÓWNOLEGŁEGO USUWANIA PLIKU ===");
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Long>> tasks = new ArrayList<>();

        for (FileStructure file : files) {
            tasks.add(() -> {
                try {
                    long start = System.nanoTime();
                    service.deleteFile(file.getId());
                    long end = System.nanoTime();
                    return (end - start) / 1_000_000;
                } catch (IOException e) {
                    return 0L;
                }
            });
        }

        long start = System.nanoTime();
        List<Future<Long>> results = executor.invokeAll(tasks);
        long end = System.nanoTime();
        executor.shutdown();

        List<Long> times = new ArrayList<>();
        for (Future<Long> future : results) {
            try {
                times.add(future.get());
            } catch (Exception ignored) {}
        }

        double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
        System.out.println("Liczba plików: " + files.size());
        System.out.println("Czas całkowity: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Średni czas usuwania pliku: " + avg + " ms");
        System.out.println("=========================================");
    }

}
