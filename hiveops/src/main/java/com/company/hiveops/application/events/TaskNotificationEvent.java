package com.company.hiveops.application.events;

import java.util.UUID;

public record TaskNotificationEvent(UUID companyId, String message) {}
