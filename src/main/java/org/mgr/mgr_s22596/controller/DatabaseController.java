package org.mgr.mgr_s22596.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.model.FileStructure;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

public abstract class DatabaseController {

    protected abstract List<FileStructure> getFiles();

    protected abstract List<FileStructure> getTestFiles();

    protected abstract long downloadFile(HttpServletResponse response,
                                         String fileName) throws IOException;

    protected abstract long saveFile(MultipartFile file) throws IOException;

    protected abstract long deleteFile(String fileName) throws IOException;

    protected long downloadDuration;

    public String showFiles(Model model, String viewName) {
        List<FileStructure> files = getFiles();
        model.addAttribute("files", files);
        return viewName;
    }

    public void updateDownloadDuration(String id,
                                       HttpServletResponse response)
            throws IOException {
        this.downloadDuration = downloadFile(response, id);
    }

    public String postDownloadDuration(
            RedirectAttributes redirectAttributes,
            String redirectPath) {
        redirectAttributes.addFlashAttribute(
                "downloadDuration", this.downloadDuration
        );
        return "redirect:" + redirectPath;
    }


    public String handleUpload(MultipartFile file,
                               RedirectAttributes redirectAttributes,
                               String redirectPath) throws IOException {
        long uploadDuration = saveFile(file);
        redirectAttributes.addFlashAttribute(
                "uploadDuration", uploadDuration);
        return "redirect:" + redirectPath;
    }

    public String handleDelete(String file,
                               RedirectAttributes redirectAttributes,
                               String redirectPath) throws IOException {
        long deleteDuration = deleteFile(file);
        redirectAttributes.addFlashAttribute(
                "deleteDuration", deleteDuration);
        return "redirect:" + redirectPath;
    }
}
