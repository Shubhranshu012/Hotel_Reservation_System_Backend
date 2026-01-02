package com.booking.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    private String id;

    private String hotelId;
    private String roomId;

    private String guestName;
    private String guestEmail;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double price;

    private RSTATUS status;
}
