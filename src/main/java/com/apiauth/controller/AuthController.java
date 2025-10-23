package com.apiauth.controller;

import com.apiauth.service.AuthService;
import com.apiauth.service.AuthService.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> body) {
        ServiceResult result = authService.signup(body);
        return ResponseEntity.status(result.status()).body(result.body());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        ServiceResult result = authService.login(body);
        return ResponseEntity.status(result.status()).body(result.body());
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recover(@RequestBody Map<String, String> body) {
        ServiceResult result = authService.recover(body);
        return ResponseEntity.status(result.status()).body(result.body());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        ServiceResult result = authService.logout(readToken(authorization));
        return ResponseEntity.status(result.status()).body(result.body());
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        ServiceResult result = authService.me(readToken(authorization));
        return ResponseEntity.status(result.status()).body(result.body());
    }

    private String readToken(String header) {
        if (header == null) {
            return null;
        }
        if (header.startsWith("SDWork ")) {
            return header.substring("SDWork ".length()).trim();
        }
        return header.trim();
    }
}
