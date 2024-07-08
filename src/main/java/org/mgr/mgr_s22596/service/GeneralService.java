package org.mgr.mgr_s22596.service;

import java.util.ArrayList;
import java.util.List;

public class GeneralService {

    public List<String> searchFiles(List<String> allFiles, String query) {
        if (query == null || query.isEmpty()) {
            return allFiles;
        }
        List<String> filteredFiles = new ArrayList<>();
        for (String file : allFiles) {
            if (file.toLowerCase().contains(query.toLowerCase())) {
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }
}
