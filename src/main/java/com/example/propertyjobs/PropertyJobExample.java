package com.example.propertyjobs;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.List;

public final class PropertyJobExample {
    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 10, 23);
        PropertyJobService service = new PropertyJobService(new PropertyJobRules(),
                new InfraiErrorReporter(HttpClient.newHttpClient(), System.getenv("INFRAI_API_KEY")));
        List<JobAlert> alerts = service.run(today,
                List.of(new MaintenanceRequest("m-17", "Maple Court", "repair boiler", true, today)),
                List.of(new TenantDocument("d-04", "Maple Court", "lease", today.plusDays(30))),
                List.of(new InspectionReminder("i-08", "Maple Court", today)));
        alerts.forEach(alert -> System.out.println(alert.jobName() + " | " + alert.property() + " | " + alert.reason()));
    }
}
