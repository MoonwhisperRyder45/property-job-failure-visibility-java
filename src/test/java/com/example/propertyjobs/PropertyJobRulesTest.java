package com.example.propertyjobs;

import java.time.LocalDate;
import java.util.List;

public final class PropertyJobRulesTest {
    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 10, 23);
        List<JobAlert> alerts = new PropertyJobRules().dueAlerts(today,
                List.of(new MaintenanceRequest("m-17", "Maple Court", "repair boiler", true, today)),
                List.of(new TenantDocument("d-04", "Maple Court", "lease", today.plusDays(30))),
                List.of(new InspectionReminder("i-08", "Maple Court", today)));
        if (alerts.size() != 2 || !alerts.get(0).jobName().equals("maintenance-request")
                || !alerts.get(1).jobName().equals("inspection-reminder")) {
            throw new AssertionError("expected the open request and today's inspection reminder");
        }
        System.out.println("PASS: due job decision returns two alerts");
    }
}
