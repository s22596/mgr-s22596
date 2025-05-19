package org.mgr.mgr_s22596.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.model.FileStructure;
import org.mgr.mgr_s22596.service.AWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class AWSController extends DatabaseController {

    private final AWSService awsService;

    @Autowired
    public AWSController(AWSService awsService) {
        this.awsService = awsService;
    }

    @Override
    protected List<FileStructure> getFiles() {
        return awsService.getFiles();
    }

    @Override
    protected List<FileStructure> getTestFiles() {
        return awsService.getTestFiles();
    }

    @GetMapping("/AWS")
    public String listFiles(Model model) {
        return super.showFiles(model, "/AWS");
    }

    @Override
    protected long downloadFile(HttpServletResponse response, String fileName) throws IOException {
        return awsService.downloadFile(fileName, response);
    }

    @GetMapping("/AWS/download/{fileName}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        super.updateDownloadDuration(fileName, response);
    }

    @PostMapping("/AWS/download")
    public String downloadFile(RedirectAttributes redirectAttributes) throws IOException {
        return super.postDownloadDuration(redirectAttributes, "/AWS");
    }

    @Override
    protected long saveFile(MultipartFile file) throws IOException {
        return awsService.saveFile(file);
    }

    @PostMapping("/AWS/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        return super.handleUpload(file, redirectAttributes, "/AWS");
    }

    @Override
    protected long deleteFile(String fileName) throws IOException {
        return awsService.deleteFile(fileName);
    }

    @PostMapping("/AWS/delete/{fileName}")
    public String deleteFile(@PathVariable String fileName, RedirectAttributes redirectAttributes) throws IOException {
        return super.handleDelete(fileName, redirectAttributes, "/AWS");
    }
}
