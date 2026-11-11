package org.example.agentic_app1.controller;
import org.example.agentic_app1.services.SiteServerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/site")
@CrossOrigin(origins = "*") // fine for local development
public class SiteController {

    private final SiteServerService sites;

    public SiteController(SiteServerService sites) {
        this.sites = sites;
    }

    @PostMapping(value = "/start", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> start(@RequestBody String name) {
        try {
            return ResponseEntity.ok(sites.start(name.trim()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Could not start the server");
        }
    }

    @PostMapping(value = "/stop", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> stop(@RequestBody String name) {
        sites.stop(name.trim());
        return ResponseEntity.ok().build();
    }
}