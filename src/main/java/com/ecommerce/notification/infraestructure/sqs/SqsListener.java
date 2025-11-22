package com.ecommerce.notification.infraestructure.sqs;

import com.ecommerce.notification.infraestructure.api.MailtrapApiSender;
import com.ecommerce.notification.infraestructure.ses.SesEmailSender;
import com.ecommerce.notification.infraestructure.smtp.SmtpEmailSender;
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
    private final SmtpEmailSender emailSender;
    private final MailtrapApiSender mailtrapApiSender; // <-- Hazlo final aquí

    @Value("${QUEUE_URL}")
    private String queueUrl;

    @PostConstruct
    public void escucharMensajes() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(5)
                            .waitTimeSeconds(10)
                            .build();

                    List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

                    for (Message message : messages) {
                        try {
                            EventoNotificacionDTO evento = objectMapper.readValue(message.body(), EventoNotificacionDTO.class);

                            // Log solo los eventos recibidos y cuando todo se procesa bien
                            System.out.println("[NOTIFICACION] Evento recibido y procesado: " + evento);

                            smsSender.enviarSms(evento.getMensaje(), evento.getNumeroTelefono());
//                            emailSender.enviarEmail(evento.getEmail(), evento.getTipo(), evento.getMensaje());
                            mailtrapApiSender.enviarEmail(evento.getEmail(), evento.getTipo(), evento.getMensaje());

                            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                                    .queueUrl(queueUrl)
                                    .receiptHandle(message.receiptHandle())
                                    .build());
                        } catch (Exception e) {
                            System.err.println("[ERROR] Procesando mensaje: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR POLLING SQS] " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
