package com.booking.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(
	    name = "unique_booking",
	    def = "{'hotelId': 1, 'roomId': 1, 'checkInDate': 1, 'status': 1}",
	    unique = true,partialFilter = "{'status': {'$in': ['BOOKED','CONFIRMED','CHECKED_IN']}}"
)
public class Reservation {

    @Id
    private String id;
    
    @Version
    private Long version;

    private String hotelId;
    private String roomId;

    private String guestName;
    private String guestEmail;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double price;
    private String hotelName;

    private RSTATUS status;

    private boolean checkInReminderSent = false;
    private boolean checkOutReminderSent = false;
}
