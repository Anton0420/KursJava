package app.models;

import java.io.Serializable;
import java.util.UUID;

public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;
    public UUID id;
    public String name;
    public double price;
    public MenuItem(String name, double price) { this.id = UUID.randomUUID(); this.name = name; this.price = price; }
    public String toString() { return String.format("%s — %.2f \u20BD", name, price); }
}
