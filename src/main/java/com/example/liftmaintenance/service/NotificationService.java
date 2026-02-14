package com.example.liftmaintenance.service;

import com.example.liftmaintenance.model.AlertRecipient;
import com.example.liftmaintenance.model.Lift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AlertRecipientService alertRecipientService;

    @Value("${lift.notification.email.from}")
    private String fromEmail;

    public void sendMaintenanceReminder(Lift lift, int daysBefore) {
        String method = determineNotificationMethod(lift);
        
        switch (method.toUpperCase()) {
            case "EMAIL":
                sendEmailReminder(lift, daysBefore);
                break;
            case "WHATSAPP":
                sendWhatsAppReminder(lift, daysBefore);
                break;
            case "SMS":
                sendSMSReminder(lift, daysBefore);
                break;
            default:
                sendEmailReminder(lift, daysBefore);
        }
    }

    private String determineNotificationMethod(Lift lift) {
        if (lift.getReminderSettings() != null && !lift.getReminderSettings().isEmpty()) {
            return lift.getReminderSettings().get(0).getNotificationMethod();
        }
        return "EMAIL";
    }

    private void sendEmailReminder(Lift lift, int daysBefore) {
        String maintenanceDate = lift.getNextMaintenanceDate() != null 
            ? lift.getNextMaintenanceDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            : "soon";
        
        // Email to client (if client email exists)
        if (lift.getClientEmail() != null && !lift.getClientEmail().isEmpty()) {
            try {
                SimpleMailMessage clientMessage = new SimpleMailMessage();
                clientMessage.setFrom(fromEmail);
                clientMessage.setTo(lift.getClientEmail());
                clientMessage.setSubject("Lift Maintenance Reminder - " + lift.getName());
                clientMessage.setText(String.format(
                    "Dear %s,\n\n" +
                    "This is a reminder that your lift '%s' at %s, %s requires maintenance in %d day(s).\n\n" +
                    "Scheduled Maintenance Date: %s\n" +
                    "Maintenance Interval: %d months\n\n" +
                    "Please contact your service provider to schedule the maintenance.\n\n" +
                    "Best regards,\nLift Maintenance System",
                    lift.getClientName(),
                    lift.getName(),
                    lift.getBuilding(),
                    lift.getArea(),
                    daysBefore,
                    maintenanceDate,
                    lift.getMaintenanceInterval()
                ));
                mailSender.send(clientMessage);
                System.out.println("Client email reminder sent to: " + lift.getClientEmail());
            } catch (Exception e) {
                System.err.println("Failed to send client email: " + e.getMessage());
            }
        }
        
        // Email to global alert recipients (company/operations team)
        List<AlertRecipient> alertRecipients = alertRecipientService.getActiveRecipients();
        if (!alertRecipients.isEmpty()) {
            for (AlertRecipient recipient : alertRecipients) {
                if (recipient.getEmail() != null && !recipient.getEmail().trim().isEmpty()) {
                    try {
                        SimpleMailMessage alertMessage = new SimpleMailMessage();
                        alertMessage.setFrom(fromEmail);
                        alertMessage.setTo(recipient.getEmail().trim());
                        alertMessage.setSubject("[ALERT] Lift Maintenance Due - " + lift.getName());
                        alertMessage.setText(String.format(
                            "MAINTENANCE ALERT\n\n" +
                            "Lift: %s\n" +
                            "Location: %s, %s\n" +
                            "Client: %s\n" +
                            "Client Phone: %s\n" +
                            "Client Email: %s\n" +
                            "Maintenance Due: %s\n" +
                            "Reminder: %d day(s) before\n\n" +
                            "Please ensure maintenance is scheduled.\n\n" +
                            "Lift Maintenance System",
                            lift.getName(),
                            lift.getBuilding(),
                            lift.getArea(),
                            lift.getClientName(),
                            lift.getClientPhone(),
                            lift.getClientEmail() != null ? lift.getClientEmail() : "N/A",
                            maintenanceDate,
                            daysBefore
                        ));
                        mailSender.send(alertMessage);
                        System.out.println("Alert email sent to: " + recipient.getName() + " <" + recipient.getEmail() + ">");
                    } catch (Exception e) {
                        System.err.println("Failed to send alert email to " + recipient.getEmail() + ": " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("No active alert recipients configured. Skipping company notifications.");
        }
    }

    private void sendWhatsAppReminder(Lift lift, int daysBefore) {
        if (lift.getClientPhone() == null || lift.getClientPhone().isEmpty()) {
            System.out.println("No phone number for lift: " + lift.getName());
            return;
        }

        // For WhatsApp, you'll need to integrate with Twilio or similar service
        // This is a placeholder implementation
        String message = String.format(
            "Hi %s, this is a reminder that your lift '%s' at %s requires maintenance in %d day(s). " +
            "Please schedule with your service provider.",
            lift.getClientName(),
            lift.getName(),
            lift.getBuilding(),
            daysBefore
        );

        System.out.println("WhatsApp message to " + lift.getClientPhone() + ": " + message);
        System.out.println("NOTE: WhatsApp integration requires Twilio API. Please add your Twilio credentials.");
        
        // TODO: Implement Twilio integration
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(
        //     new PhoneNumber("whatsapp:" + lift.getClientPhone()),
        //     new PhoneNumber("whatsapp:" + TWILIO_PHONE),
        //     message
        // ).create();
    }

    private void sendSMSReminder(Lift lift, int daysBefore) {
        if (lift.getClientPhone() == null || lift.getClientPhone().isEmpty()) {
            System.out.println("No phone number for lift: " + lift.getName());
            return;
        }

        String message = String.format(
            "Lift Maintenance Reminder: %s at %s needs maintenance in %d day(s). Please schedule service.",
            lift.getName(),
            lift.getBuilding(),
            daysBefore
        );

        System.out.println("SMS to " + lift.getClientPhone() + ": " + message);
        System.out.println("NOTE: SMS integration requires provider (Twilio, AWS SNS, etc.). Please add credentials.");
    }
}
