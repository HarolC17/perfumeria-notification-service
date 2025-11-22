package com.ecommerce.notification.infraestructure.smtp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender {

    private final JavaMailSender mailSender;

    @Value("${MAIL_FROM}")
    private String from;

    public void enviarEmail(String to, String subject, String mensaje) {

        System.out.println("Enviando email al email: " + to + " con el mensaje: " + mensaje);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subject);
        email.setText(mensaje);
        mailSender.send(email);
    }
}
