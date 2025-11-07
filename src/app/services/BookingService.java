package app.services;

import app.models.Booking;
import app.storage.FileStorage;

import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    List<Booking> bookings;
    int vipRooms = 1;
    public BookingService(List<Booking> bookings) { this.bookings = bookings; }

    public boolean isAvailable(int vipId, LocalDateTime from, int hours) {
        LocalDateTime to = from.plusHours(hours);
        for (Booking b: bookings) {
            if (b.vipRoomId != vipId) continue;
            LocalDateTime bFrom = b.from;
            LocalDateTime bTo = bFrom.plusHours(b.hours);
            if (from.isBefore(bTo) && to.isAfter(bFrom)) return false;
        }
        return true;
    }
    public boolean addBooking(Booking b) {
        if (!isAvailable(b.vipRoomId, b.from, b.hours)) return false;
        bookings.add(b);
        FileStorage.saveBookings(bookings);
        return true;
    }
}
