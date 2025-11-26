package app.models;

/*
 * Flavor — модель вкуса табака.
 */

import java.io.Serializable;

public class Flavor implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;

    public Flavor(String name) {
        this.name = name == null ? "" : name.trim();
    }

    @Override
    public String toString() {
        return name;
    }
}
