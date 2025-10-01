package com.exploration.service.postgres.dto;

import java.time.Instant;

public record PersonEvent(Long id, String name, String email, Instant updatedAt, String source) {}

