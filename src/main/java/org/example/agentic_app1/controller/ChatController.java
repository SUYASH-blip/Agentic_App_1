package org.example.agentic_app1.controller;

import lombok.RequiredArgsConstructor;
import org.example.agentic_app1.services.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, String>> ask(@RequestBody String prompt){
        return chatService.generateResponse(prompt)
                .map(chunk -> Map.of("t", chunk))
                .onErrorResume(e -> Flux.just(
                        Map.of("t", "\n\nSomething went wrong: " + e.getMessage())));
    }
}
