package com.ecommerce.notification.infraestructure.sqs;

import com.ecommerce.notification.infraestructure.ses.SesEmailSender;
import com.ecommerce.notification.infraestructure.sns.SnsSmsSender;
import com.ecommerce.notification.infraestructure.sqs.dto.EventoNotificacionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class SqsListener {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SnsSmsSender smsSender;
    private final SesEmailSender emailSender;

    @Value("${QUEUE_URL}")
    private String queueUrl;

    @PostConstruct
    public void escucharMensajes() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(10)
                        .build();

                List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

                for (Message message : messages) {
                    try {
                        EventoNotificacionDTO evento = objectMapper.readValue(message.body(), EventoNotificacionDTO.class);

                        System.out.println("📩 Evento recibido: " + evento);
                        System.out.println("📞 Número de teléfono: " + evento.getNumeroTelefono());

                        // ✅ Intentar enviar SMS (si es null, simplemente no lo enviará)
                        smsSender.enviarSms(evento.getMensaje(), evento.getNumeroTelefono());

                        // emailSender.enviarEmail(evento.getEmail(), evento.getTipo(), evento.getMensaje());

                        // ✅ Eliminar mensaje de la cola SIEMPRE (incluso si el SMS no se envió)
                        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build());

                        System.out.println("✅ Mensaje procesado y eliminado de la cola");

                    } catch (Exception e) {
                        System.err.println("❌ Error al procesar mensaje: " + e.getMessage());
                        e.printStackTrace();

                        // ⚠️ OPCIONAL: Puedes decidir si eliminar el mensaje para evitar reintentos infinitos
                    }
                }
            }
        });
    }
}