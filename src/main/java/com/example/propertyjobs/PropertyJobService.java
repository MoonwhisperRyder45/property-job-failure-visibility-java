package com.example.propertyjobs;

import java.time.LocalDate;
import java.util.List;

public final class PropertyJobService {
    private final PropertyJobRules rules;
    private final InfraiErrorReporter reporter;

    public PropertyJobService(PropertyJobRules rules, InfraiErrorReporter reporter) {
        this.rules = rules;
        this.reporter = reporter;
    }

    public List<JobAlert> run(LocalDate today, List<MaintenanceRequest> requests,
                              List<TenantDocument> documents, List<InspectionReminder> inspections) {
        try {
            return rules.dueAlerts(today, requests, documents, inspections);
        } catch (RuntimeException failure) {
            reporter.capture("property-jobs", "scheduled check failed", failure);
            throw failure;
        }
    }
}
