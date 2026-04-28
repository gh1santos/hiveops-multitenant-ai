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

    // Endpoint público (liberado no SecurityConfig) para você gerar seu token de teste
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        // Aqui você faria a validação de usuário/senha no futuro.
        // Por enquanto, confiamos na requisição para gerar o token do Tenant.
        String token = jwtService.generateToken(request.email(), request.companyId());
        return new AuthResponse(token);
    }
}