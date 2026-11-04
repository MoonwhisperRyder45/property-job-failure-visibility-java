package com.example.propertyjobs;

import java.time.LocalDate;

public record TenantDocument(String id, String property, String documentType, LocalDate expiresOn) {}
