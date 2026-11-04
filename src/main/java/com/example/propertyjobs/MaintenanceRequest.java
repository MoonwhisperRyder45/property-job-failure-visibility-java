package com.example.propertyjobs;

import java.time.LocalDate;

public record MaintenanceRequest(String id, String property, String description, boolean open, LocalDate dueDate) {}
