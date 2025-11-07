package app.models;

import java.io.Serializable;
import java.util.UUID;

public class Hookah implements Serializable {
    private static final long serialVersionUID = 1L;
    public UUID id;
    public String name;
    public HookahType type;
    public double price;
    public Hookah(String name, HookahType type, double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.price = price;
    }
    public String toString() { return String.format("%s (%s) — %.2f \u20BD", name, type, price); }
}
