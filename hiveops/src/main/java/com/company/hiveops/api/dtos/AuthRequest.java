package com.company.hiveops.api.dtos;

import java.util.UUID;

public record AuthRequest(String email, UUID companyId) {}
