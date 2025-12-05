package app.services;

/*
  Тесты BookingService: проверка добавления брони и детекция перекрытий.
*/

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
        System.out.println("Тест: добавление брони — старт");
        LocalDateTime from = LocalDateTime.of(2025, 12, 1, 18, 0);
        Booking b = new Booking("ClientA", from, 2, 1);
        boolean added = bookingService.addBooking(b);
        assertTrue(added, "Первая бронь на свободное время должна добавляться");
        assertEquals(1, bookings.size(), "Список броней должен содержать одну запись");
        System.out.println("Тест: добавление брони — пройден");
    }

    @Test
    void isAvailable_shouldDetectOverlap() {
        System.out.println("Тест: проверка перекрытия броней — старт");
        LocalDateTime from1 = LocalDateTime.of(2025, 12, 1, 18, 0);
        Booking b1 = new Booking("ClientA", from1, 2, 1);
        bookings.add(b1);

        LocalDateTime from2 = LocalDateTime.of(2025, 12, 1, 19, 0);
        boolean available = bookingService.isAvailable(1, from2, 2);
        assertFalse(available, "BookingService должен обнаруживать перекрытие по времени");
        System.out.println("Тест: проверка перекрытия броней — пройден");
    }
}
