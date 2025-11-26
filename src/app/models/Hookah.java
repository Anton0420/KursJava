package app.models;

/*
 * Hookah — описание кальяна (id, название, тип, цена).
 */

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

    @Override
    public String toString() {
        return name + " (" + type + ") — " + String.format("%.2f \u20BD", price);
    }
}
