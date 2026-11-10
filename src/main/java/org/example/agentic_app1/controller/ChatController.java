package org.example.agentic_app1.controller;

import lombok.RequiredArgsConstructor;
import org.example.agentic_app1.services.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;




}
