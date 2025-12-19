package ru.netology.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.backend.dto.LoginRequest;
import ru.netology.backend.dto.JwtResponse;
import ru.netology.backend.exception.InvalidCredentialsException;
import ru.netology.backend.exception.UnauthorizedException;
import ru.netology.backend.service.AuthService;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) throws InvalidCredentialsException {
        String token = authService.login(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) throws UnauthorizedException {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
}