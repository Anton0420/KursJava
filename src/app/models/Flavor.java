package app.models;

import java.io.Serializable;

public class Flavor implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public Flavor(String name) { this.name = name.trim(); }
    public String toString() { return name; }
}
