package com.ecommerce.notification.infraestructure.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class MailtrapApiSender {

    @Value("${MAILTRAP_API_TOKEN}")
    private String apiToken;

    @Value("${MAILTRAP_INBOX_ID}")
    private String inboxId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarEmail(String to, String subject, String mensaje) {
        String apiUrl = "https://send.api.mailtrap.io/api/send";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", Map.of("email", "notificaciones@demomailtrap.co", "name", "Notificador"));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("text", mensaje);
        payload.put("category", "Notificacion");
        payload.put("inbox_id", Long.parseLong(inboxId));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            System.out.println("[MAILTRAP API] Envío: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("[MAILTRAP API] Error: " + e.getMessage());
        }
    }
}
