/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.controller;

import ch.iisresear.dssa.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private ChatService chatService;

    @GetMapping(path = "")
    public String message(@RequestParam(value = "message") String message){
        return chatService.message(message, httpSession.getId());
    }
}
