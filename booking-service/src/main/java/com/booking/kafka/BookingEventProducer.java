package com.booking.kafka;


import com.booking.dto.BookingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingEventProducer {

    private static final String TOPIC = "booking-events";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void sendEvent(BookingEvent event) {
        try {
            String message = mapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getReservationId(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
