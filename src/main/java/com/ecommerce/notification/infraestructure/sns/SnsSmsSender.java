package com.ecommerce.notification.infraestructure.sns;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
@RequiredArgsConstructor
public class SnsSmsSender {


    private final SnsClient snsClient;

    public void enviarSms(String mensaje, String numeroTelefono) {

        // ✅ VALIDACIÓN: Si el número es null o vacío, no enviar
        if (numeroTelefono == null || numeroTelefono.trim().isEmpty()) {
            System.out.println("⚠️ No se envió SMS: el número de teléfono es null o vacío");
            return; // Salir sin enviar
        }

        System.out.println("Enviando SMS al número: " + numeroTelefono + " con el mensaje: " + mensaje);
        PublishRequest request = PublishRequest.builder()
                .message(mensaje)
                .phoneNumber("+57" + numeroTelefono)
                .build();

        snsClient.publish(request);
    }
}
