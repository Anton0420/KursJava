package app.services;

import app.models.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    private List<Booking> bookings;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookings = new ArrayList<>();
        bookingService = new BookingService(bookings);
    }

    @Test
    void addBooking_shouldReturnTrueWhenAvailable() {
        LocalDateTime from = LocalDateTime.of(2025, 12, 1, 18, 0);
        Booking b = new Booking("КлиентA", from, 2, 1);
        boolean added = bookingService.addBooking(b);
        assertTrue(added, "Первая бронь на свободное время должна добавляться");
        assertEquals(1, bookings.size());
    }

    @Test
    void isAvailable_shouldDetectOverlap() {
        LocalDateTime from1 = LocalDateTime.of(2025, 12, 1, 18, 0);
        Booking b1 = new Booking("КлиентA", from1, 2, 1);
        bookings.add(b1);

        LocalDateTime from2 = LocalDateTime.of(2025, 12, 1, 19, 0);
        boolean available = bookingService.isAvailable(1, from2, 2);
        assertFalse(available, "BookingService должен обнаруживать пересечение по времени");
    }
}
