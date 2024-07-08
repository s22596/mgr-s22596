package org.mgr.mgr_s22596.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.AppConfig;
import org.mgr.mgr_s22596.service.NetworkDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class NetworkDiskController {

    private final NetworkDriveService networkDriveService;
    private String path = AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_PUBLIC_DRIVE_NAME;
    private String disk = AppConfig.PJATK_PUBLIC_DRIVE_NAME;


    @Autowired
    public NetworkDiskController(NetworkDriveService networkDriveService) {
        this.networkDriveService = networkDriveService;
    }

    @GetMapping(value = {"/NetworkDisk", "/NetworkDisk/", "/NetworkDisk/{pathUrl}"})
    public String showFile(Model model, @PathVariable(required = false) String pathUrl) {
        if (pathUrl != null) {
            path += "\\" + pathUrl;
        }
        model.addAttribute("title", "Dysk " + disk);
        model.addAttribute("files", networkDriveService.getFiles(path));
        model.addAttribute("selectedPath", disk);
        return "NetworkDisk";
    }

    @PostMapping("/updatePath")
    public String updatePath(@RequestParam String pathUrl) {
        disk = pathUrl;
        String newPath = AppConfig.PJATK_BASIC_PATH + (pathUrl.equals(AppConfig.PJATK_PUBLIC_DRIVE_NAME) ? AppConfig.PJATK_PUBLIC_DRIVE_NAME : AppConfig.PJATK_USER_DISK_NAME);
        setPath(newPath);
        return "redirect:/NetworkDisk";
    }

    public String getCutPath() {
        String cutPath;
        if (path.contains(AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_PUBLIC_DRIVE_NAME)) {
            cutPath = path.substring((AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_PUBLIC_DRIVE_NAME).length());
        } else {
            cutPath = path.substring((AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_USER_DISK_NAME).length());
        }
        if (cutPath.isEmpty()) {
            return "";
        } else {
            return cutPath.substring(1);
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean ifBasePath() {
        return path.equals(AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_PUBLIC_DRIVE_NAME) ||
                path.equals(AppConfig.PJATK_BASIC_PATH + AppConfig.PJATK_USER_DISK_NAME);
    }

    @GetMapping("/goUp")
    public String goUp() {
        if (path.contains("\\")) {
            path = path.substring(0, path.lastIndexOf("\\"));
        }
        return "redirect:/NetworkDisk";
    }


    @GetMapping("/downloadFileNetworkDisk")
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            networkDriveService.downloadFile(path, fileName, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = {"/searchNetworkDisk", "/searchNetworkDisk/"})
    @ResponseBody
    public List<String> searchFiles(@RequestParam String query) {
        return networkDriveService.searchFiles(path, query);
    }
}
