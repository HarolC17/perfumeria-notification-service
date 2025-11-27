# Notification Service - Mi Perfumería

Microservicio de notificaciones por correo electrónico para la aplicación Mi Perfumería.

> 📦 Parte del proyecto [Mi Perfumería](https://github.com/HarolC17/mi-perfumeria-app)

## Descripción

Servicio que escucha eventos de AWS SQS y envía notificaciones automáticas por correo electrónico utilizando Mailtrap.

## Tecnologías

- Java 17
- Spring Boot 3.x
- AWS SQS (consumidor de mensajes)
- Mailtrap SMTP
- REST API

## Funcionamiento

1. **Order Service** publica evento en cola SQS
2. **Notification Service** consume el mensaje
3. Se envía correo automático al cliente vía Mailtrap

## Tipos de Notificaciones

| Evento | Correo Enviado |
|--------|----------------|
| Orden creada | Confirmación de orden |
| Orden enviada | Notificación de envío |
| Orden cancelada | Notificación de cancelación |

## Instalación

git clone https://github.com/HarolC17/notification-service.git
cd notification-service
