package com.krce.mobilebackend.controller;

import com.krce.mobilebackend.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) { this.chatService = chatService; }

    @PostMapping("/ask")
    public ChatService.ChatResponse ask(@Valid @RequestBody AskRequest request) {
        return chatService.ask(request.question(), request.live());
    }

    public record AskRequest(@NotBlank String question, boolean live) {}
}
