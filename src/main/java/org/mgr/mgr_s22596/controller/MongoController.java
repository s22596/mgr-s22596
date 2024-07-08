package org.mgr.mgr_s22596.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mgr.mgr_s22596.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class MongoController {

    private final MongoDBService mongoDBService;
    private String currentPath = "";

    @Autowired
    public MongoController(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @GetMapping(value = {"/Mongo", "/Mongo/", "/Mongo/{pathUrl}"})
    public String listFiles(Model model, @PathVariable(required = false) String pathUrl) {
        if (pathUrl != null) {
            if (currentPath.isEmpty())
                currentPath = pathUrl;
            else
                currentPath += "/" + pathUrl;
        }
        List<String> files = mongoDBService.listFiles(currentPath);
        model.addAttribute("selectedPath", currentPath);
        model.addAttribute("files", files);
        return "Mongo";
    }

    @GetMapping("/searchMongo")
    @ResponseBody
    public List<String> searchFiles(@RequestParam String query) {
        return mongoDBService.searchFiles(currentPath, query);
    }

    @GetMapping("/goUpMongo")
    public String goUp() {
        if (!currentPath.contains("/")) {
            currentPath = "";
        } else {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
        }
        return "redirect:/Mongo";
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public boolean ifBasePath() {
        return currentPath.isEmpty();
    }

    @GetMapping("/downloadFileMongo")
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            mongoDBService.downloadFile(currentPath, fileName, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}