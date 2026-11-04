package com.example.propertyjobs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PropertyJobRules {
    public List<JobAlert> dueAlerts(LocalDate today, List<MaintenanceRequest> requests,
                                    List<TenantDocument> documents, List<InspectionReminder> inspections) {
        List<JobAlert> alerts = new ArrayList<>();
        for (MaintenanceRequest request : requests) {
            if (request.open() && !request.dueDate().isAfter(today)) {
                alerts.add(new JobAlert("maintenance-request", request.property(), request.description()));
            }
        }
        for (TenantDocument document : documents) {
            if (document.expiresOn().isBefore(today)) {
                alerts.add(new JobAlert("tenant-document", document.property(), document.documentType() + " expired"));
            }
        }
        for (InspectionReminder inspection : inspections) {
            if (!inspection.remindOn().isAfter(today)) {
                alerts.add(new JobAlert("inspection-reminder", inspection.property(), "inspection reminder due"));
            }
        }
        return List.copyOf(alerts);
    }
}
