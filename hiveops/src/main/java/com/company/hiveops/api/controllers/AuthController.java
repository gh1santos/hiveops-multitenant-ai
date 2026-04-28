package com.company.hiveops.api.controllers;

import com.company.hiveops.api.dtos.AuthRequest;
import com.company.hiveops.api.dtos.AuthResponse;
import com.company.hiveops.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        String token = jwtService.generateToken(request.email(), request.companyId());
        return new AuthResponse(token);
    }
}