package org.mgr.mgr_s22596.controller;

import org.mgr.mgr_s22596.model.FileStructure;
import org.mgr.mgr_s22596.performance.PerformanceTestRunner;
import org.mgr.mgr_s22596.service.AWSService;
import org.mgr.mgr_s22596.service.MongoService;
import org.mgr.mgr_s22596.service.MSSQLService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/test")
public class PerformanceTestController {

    private final MongoService mongoService;
    private final MSSQLService mssqlService;
    private final AWSService awsService;

    private final int count = 99;
    private final int size = 1024 * 10;

    public PerformanceTestController(MongoService mongoService,
                                     MSSQLService mssqlService,
                                     AWSService awsService) {
        this.mongoService = mongoService;
        this.mssqlService = mssqlService;
        this.awsService = awsService;
    }

    @GetMapping("/Mongo")
    public String runMongoTests() throws IOException, InterruptedException {
        return executeTests(new PerformanceTestRunner(mongoService));
    }

    @GetMapping("/MSSQL")
    public String runMssqlTests() throws IOException, InterruptedException {
        return executeTests(new PerformanceTestRunner(mssqlService));
    }

    @GetMapping("/AWS")
    public String runAwsTests() throws IOException, InterruptedException {
        return executeTests(new PerformanceTestRunner(awsService));
    }

    private String executeTests(PerformanceTestRunner tester)
            throws IOException, InterruptedException {

        System.out.println("=== START TESTU DLA: " +
                tester.getService().getClass().getSimpleName() + " ===");

        // 1. Generowanie plików testowych
        List<MultipartFile> files = tester.generateTestFiles(count, size);

        // 2. Równoległu upload
        tester.runConcurrentUpload(files);

        // 3. Zaciągniecie wrzuconych plików
        List<FileStructure> savedFiles = tester.getService().getTestFiles();

        // 4. Równoległy download
        tester.runConcurrentDownload(savedFiles);

        // 5. Równoległe usunięcie
        tester.runConcurrentDelete(savedFiles);

        return "Testy zakończone pomyślnie dla " +
                tester.getService().getClass().getSimpleName()
                + " (" + count + " plików, " + size + " KB)";
    }
}
