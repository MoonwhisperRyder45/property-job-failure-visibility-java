package com.example.propertyjobs;

import java.time.LocalDate;

public record InspectionReminder(String id, String property, LocalDate remindOn) {}
