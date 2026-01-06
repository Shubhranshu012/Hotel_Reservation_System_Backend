package com.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BookingNotificationConsumer {

	@Autowired
    private EmailService emailService;

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            Map<String, String> event = mapper.readValue(message, Map.class);

            String eventType = event.get("eventType");
            String email = event.get("guestEmail");

            if ("BOOKING_CONFIRMED".equals(eventType)) {
                handleBookingConfirmed(event, email);
            }

            if ("BOOKING_CANCELLED".equals(eventType)) {
                handleBookingCancelled(event, email);
            }
            if ("CHECK_IN_REMINDER".equals(eventType)) {
            	sendCheckInReminder(event, email);
            }

            if ("CHECK_OUT_REMINDER".equals(eventType)) {
            	sendCheckOutReminder(event, email);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBookingConfirmed(Map<String, String> event, String email) {

        String body =
                "Your booking is CONFIRMED ✅\n\n" +
                "Reservation ID: " + event.get("reservationId") + "\n" +
                "Hotel ID: " + event.get("hotelId") + "\n" +
                "Room ID: " + event.get("roomId") + "\n" +
                "Check-in: " + event.get("checkIn") + "\n" +
                "Check-out: " + event.get("checkOut");

        emailService.sendEmail(email, "Booking Confirmed", body);
    }

    private void handleBookingCancelled(Map<String, String> event, String email) {

        String body =
                "Your booking has been CANCELLED ❌\n\n" +
                "Reservation ID: " + event.get("reservationId") + "\n" +
                "Hotel ID: " + event.get("hotelId") + "\n" +
                "Room ID: " + event.get("roomId");

        emailService.sendEmail(email, "Booking Cancelled", body);
    }
    private void sendCheckInReminder(Map<String, String> event, String email) {
        String body =
                "Reminder: Your check-in is tomorrow! ✅\n\n" +
                "Reservation ID: " + event.get("reservationId") + "\n" +
                "Hotel ID: " + event.get("hotelId") + "\n" +
                "Room ID: " + event.get("roomId") + "\n" +
                "Check-in: " + event.get("checkIn") + "\n" +
                "Check-out: " + event.get("checkOut");

        emailService.sendEmail(email, "Check-in Reminder", body);
    }
    private void sendCheckOutReminder(Map<String, String> event, String email) {
        String body =
                "Reminder: Your check-out is tomorrow! ❌\n\n" +
                "Reservation ID: " + event.get("reservationId") + "\n" +
                "Hotel ID: " + event.get("hotelId") + "\n" +
                "Room ID: " + event.get("roomId") + "\n" +
                "Check-in: " + event.get("checkIn") + "\n" +
                "Check-out: " + event.get("checkOut");

        emailService.sendEmail(email, "Check-out Reminder", body);
    }
}
