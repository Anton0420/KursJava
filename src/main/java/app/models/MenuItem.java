package app.models;

/*
 * MenuItem — позиция меню (напиток/блюдо).
 */

import java.io.Serializable;
import java.util.UUID;

public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public UUID id;
    public String name;
    public double price;

    public MenuItem(String name, double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " — " + String.format("%.2f \u20BD", price);
    }
}
