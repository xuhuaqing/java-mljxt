package com.example.springbootdemo.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminEntryController {

    @GetMapping({"/admin", "/admin/"})
    public String adminEntry() {
        return "forward:/admin/index.html";
    }
}
