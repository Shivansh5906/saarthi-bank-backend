package com.saarthi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.saarthi.dto.LoginRequest;
import com.saarthi.dto.SignupRequest;
import com.saarthi.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        return userService.signup(request);
    }
        @PostMapping("/login")
        public String login(@RequestBody LoginRequest request) {
            return userService.login(request);
    }
}
