package app.models;

/*
 * Booking — бронь VIP-зала (клиент, время, длительность, зал).
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    public UUID id;
    public String clientName;
    public LocalDateTime from;
    public int hours;
    public int vipRoomId;

    public Booking(String clientName, LocalDateTime from, int hours, int vipRoomId) {
        this.id = UUID.randomUUID();
        this.clientName = clientName == null ? "Гость" : clientName;
        this.from = from;
        this.hours = hours;
        this.vipRoomId = vipRoomId;
    }

    @Override
    public String toString() {
        return "Бронь " + clientName + ": зал " + vipRoomId + ", "
                + from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + " на " + hours + " ч. (id=" + id.toString().substring(0, 8) + ")";
    }
}
