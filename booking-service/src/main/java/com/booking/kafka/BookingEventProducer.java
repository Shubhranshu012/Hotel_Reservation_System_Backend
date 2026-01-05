package com.booking.kafka;


import com.booking.dto.BookingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class BookingEventProducer {

    private static final String TOPIC = "booking-events";
    private static final String CB_NAME = "kafkaCB";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "kafkaFallback")
    public void sendEvent(BookingEvent event) {
        try {
            String message = mapper.writeValueAsString(event);
            kafkaTemplate
                    .send(TOPIC, event.getReservationId(), message)
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void kafkaFallback(BookingEvent event, Throwable ex) {
        System.out.println("Kafka unavailable" + event.getReservationId());
    }
}

