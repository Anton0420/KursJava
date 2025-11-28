package app.models;

/*
 * Catalog — хранит списки кальянов, вкусов и меню;
 * также хранит пары несовместимых вкусов.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Catalog implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<Hookah> hookahs = new ArrayList<>();
    public List<Flavor> flavors = new ArrayList<>();
    public List<MenuItem> menu = new ArrayList<>();
    public Map<String, Set<String>> incompatible = new HashMap<>();

    public void seedSampleData() {
        if (hookahs.isEmpty()) {
            hookahs.add(new Hookah("Классический большой", HookahType.CLASSIC, 700));
            hookahs.add(new Hookah("Электро-стиль", HookahType.ELECTRONIC, 900));
            hookahs.add(new Hookah("Дизайнерский Deluxe", HookahType.DESIGNER, 1500));
        }

        if (flavors.isEmpty()) {
            flavors.add(new Flavor("Виноград"));
            flavors.add(new Flavor("Яблоко"));
            flavors.add(new Flavor("Мята"));
            flavors.add(new Flavor("Лимон"));
            flavors.add(new Flavor("Кола"));
            flavors.add(new Flavor("Ваниль"));
            flavors.add(new Flavor("Карамель"));
            flavors.add(new Flavor("Шоколад"));

            addIncompatibility("Мята", "Ваниль");
            addIncompatibility("Кола", "Карамель");
        }

        if (menu.isEmpty()) {
            menu.add(new MenuItem("Чай зелёный", 150));
            menu.add(new MenuItem("Капучино", 200));
            menu.add(new MenuItem("Кола 0.5", 120));
            menu.add(new MenuItem("Сэндвич", 350));
            menu.add(new MenuItem("Закуска сырная", 300));
        }
    }

    public void addIncompatibility(String a, String b) {
        if (a == null || b == null) return;
        incompatible.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        incompatible.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    }

    public boolean areFlavorsCompatible(List<Flavor> list) {
        if (list == null || list.size() < 2) return true;

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                String a = list.get(i).name;
                String b = list.get(j).name;
                Set<String> set = incompatible.get(a);
                if (set != null && set.contains(b)) return false;
            }
        }
        return true;
    }
}
