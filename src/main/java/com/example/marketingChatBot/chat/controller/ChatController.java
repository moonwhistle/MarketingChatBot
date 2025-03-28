package com.example.marketingChatBot.chat.controller;

import com.example.marketingChatBot.chat.controller.dto.Response.ChatResponse;
import com.example.marketingChatBot.chat.controller.dto.request.ChatRequest;
import com.example.marketingChatBot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.getAnswer(request));
    }
}
