package com.booking.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import com.booking.dto.BookingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BookingEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private BookingEventProducer bookingEventProducer;

    private BookingEvent bookingEvent;
    private ObjectMapper mapper;
    @BeforeEach
    void setUp() {
    	bookingEventProducer.setMapper(new ObjectMapper());  
        bookingEvent = new BookingEvent();
        bookingEvent.setReservationId("123");
        bookingEvent.setEventType("TEST_EVENT");
    }

    @Test
    void sendEvent() throws Exception {
        
        String expectedMessage = new ObjectMapper().writeValueAsString(bookingEvent);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(mock(CompletableFuture.class));
        
        bookingEventProducer.sendEvent(bookingEvent);

        verify(kafkaTemplate, times(1)).send("booking-events", "123", expectedMessage);
    }

    @Test
    void errorSendEvent() {
       
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Kafka error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingEventProducer.sendEvent(bookingEvent);
        });
        assertEquals("java.lang.RuntimeException: Kafka error", exception.getMessage());
    }

    @Test
    void testKafka() {
        bookingEventProducer.kafkaFallback(bookingEvent, new Throwable("Test exception"));
    }
}