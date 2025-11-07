package app.models;

import java.io.Serializable;
import java.util.*;

public class Catalog implements Serializable {
    private static final long serialVersionUID = 1L;
    public List<Hookah> hookahs = new ArrayList<>();
    public List<Flavor> flavors = new ArrayList<>();
    public List<MenuItem> menu = new ArrayList<>();
    // incompatible tastes map
    public Map<String, Set<String>> incompatible = new HashMap<>();

    public void seedSampleData() {
        if (hookahs.isEmpty()) {
            hookahs.add(new Hookah("Классический большой", HookahType.CLASSIC, 700));
            hookahs.add(new Hookah("Электро-стиль", HookahType.ELECTRONIC, 900));
            hookahs.add(new Hookah("Дизайнерский Deluxe", HookahType.DESIGNER, 1500));
        }
        if (flavors.isEmpty()) {
            Collections.addAll(flavors, new Flavor("Виноград"), new Flavor("Яблоко"), new Flavor("Мята"), new Flavor("Лимон"), new Flavor("Кола"), new Flavor("Ваниль"), new Flavor("Карамель"), new Flavor("Шоколад"));
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
        incompatible.computeIfAbsent(a, k->new HashSet<>()).add(b);
        incompatible.computeIfAbsent(b, k->new HashSet<>()).add(a);
    }

    public boolean areFlavorsCompatible(List<Flavor> list) {
        for (int i=0;i<list.size();i++){
            for (int j=i+1;j<list.size();j++){
                String a = list.get(i).name;
                String b = list.get(j).name;
                if (incompatible.containsKey(a) && incompatible.get(a).contains(b)) return false;
            }
        }
        return true;
    }
}
