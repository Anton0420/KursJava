package app.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    public UUID id;
    public String clientName;
    public LocalDateTime from;
    public int hours; // duration
    public int vipRoomId;
    public Booking(String clientName, LocalDateTime from, int hours, int vipRoomId) {
        this.id = UUID.randomUUID(); this.clientName = clientName; this.from = from; this.hours = hours; this.vipRoomId = vipRoomId;
    }
    public String toString() { return String.format("Бронь %s: зал %d, %s на %d ч. (id=%s)", clientName, vipRoomId, from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), hours, id.toString().substring(0,8)); }
}
