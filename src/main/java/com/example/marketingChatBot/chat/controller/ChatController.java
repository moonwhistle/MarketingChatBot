package com.example.marketingChatBot.chat.controller;

import com.example.marketingChatBot.chat.controller.dto.request.ChatRequest;
import com.example.marketingChatBot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
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
    public String chat(@RequestBody ChatRequest request) {
        System.out.println(chatService.searchRelatedData(request));
        return "ss";
    }
}
