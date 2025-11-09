package com.saarthi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.saarthi.dto.LoginRequest;
import com.saarthi.dto.SignupRequest;
import com.saarthi.service.UserService;

import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // Service se token aaya
        String token = userService.login(request);

        return ResponseEntity.ok(
            Map.of("token", token) // FRONTEND ko {"token":"xxxxx"} milega
        );
    }
}
