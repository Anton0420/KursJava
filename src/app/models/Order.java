package app.models;

/*
 * Order — модель заказа (клиент, кальян, вкусы, позиции меню, итог).
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    public UUID id;
    public LocalDateTime created;
    public String clientName;

    public Hookah hookah;
    public List<Flavor> flavors = new ArrayList<>();
    public List<MenuItem> items = new ArrayList<>();
    public double total;

    public Order(String clientName) {
        this.id = UUID.randomUUID();
        this.created = LocalDateTime.now();
        this.clientName = clientName == null ? "Гость" : clientName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Заказ ").append(id.toString().substring(0, 8))
                .append(" — ").append(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .append("\n");
        sb.append("Клиент: ").append(clientName).append("\n");

        if (hookah != null) {
            sb.append("Кальян: ").append(hookah).append("\n");
            sb.append("Вкусы: ").append(flavors).append("\n");
        }

        if (!items.isEmpty()) {
            sb.append("Напитки/Блюда:\n");
            for (MenuItem mi : items) {
                sb.append("  - ").append(mi).append("\n");
            }
        }

        sb.append(String.format("Итого: %.2f \u20BD\n", total));
        return sb.toString();
    }
}
