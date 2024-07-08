package org.mgr.mgr_s22596.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Strona Główna");
        model.addAttribute("content", "Witamy na stronie głównej!");
        return "index";
    }
}
