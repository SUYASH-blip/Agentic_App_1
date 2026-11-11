package org.example.agentic_app1.controller;

import org.example.agentic_app1.services.WebsiteBuilderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WebsiteBuilderController {

    private final WebsiteBuilderService builderService;

    public WebsiteBuilderController(WebsiteBuilderService builderService){
        this.builderService = builderService;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody String prompt){
        return builderService.generate(prompt);
    }
}
