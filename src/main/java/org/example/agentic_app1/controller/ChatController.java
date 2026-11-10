package org.example.agentic_app1.controller;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.example.agentic_app1.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;



    @GetMapping("/chat")
    public ResponseEntity<String> ask(@RequestBody String prompt){
        String response = chatService.generateResponse(prompt);
        return
                ResponseEntity.status(HttpStatus.SC_OK).body(response);
    }


}
