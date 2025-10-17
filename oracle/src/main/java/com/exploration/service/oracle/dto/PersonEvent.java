package com.exploration.service.oracle.dto;

import java.time.Instant;

public record PersonEvent(
    String id, 
    String name, 
    String email, 
    Instant updatedAt, 
    String source,
    String operationType) 
{}
