package org.mgr.mgr_s22596.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.model.FileStructure;
import org.mgr.mgr_s22596.service.MSSQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class MSSQLController extends DatabaseController {

    private final MSSQLService mssqlService;

    @Autowired
    public MSSQLController(MSSQLService mssqlService) {
        this.mssqlService = mssqlService;
    }

    @Override
    protected List<FileStructure> getFiles() {
        return mssqlService.getFiles();
    }

    @Override
    protected List<FileStructure> getTestFiles() {
        return mssqlService.getTestFiles();
    }

    @GetMapping(value = {"/MSSQL"})
    public String listFiles(Model model) {
        return super.showFiles(model, "MSSQL");
    }

    @Override
    protected long downloadFile(HttpServletResponse response, String streamId) throws IOException {
        return mssqlService.downloadFile(streamId, response);
    }

    @GetMapping("/MSSQL/download/{streamId}")
    protected void downloadFile(@PathVariable String streamId, HttpServletResponse response) throws IOException {
        super.updateDownloadDuration(streamId, response);
    }

    @PostMapping("/MSSQL/download")
    public String downloadFile(RedirectAttributes redirectAttributes) throws IOException {
        return super.postDownloadDuration(redirectAttributes, "/MSSQL");
    }


    @PostMapping("/MSSQL/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        return super.handleUpload(file, redirectAttributes, "/MSSQL");
    }

    @Override
    protected long saveFile(MultipartFile file) {
        return mssqlService.saveFile(file);
    }

    @Override
    protected long deleteFile(String streamId) throws IOException {
        return mssqlService.deleteFile(streamId);
    }

    @PostMapping("/MSSQL/delete/{streamId}")
    public String deleteFile(@PathVariable("streamId") String streamId, RedirectAttributes redirectAttributes) throws IOException {
        return super.handleDelete(streamId, redirectAttributes, "/MSSQL");
    }
}

