package app.services;

/*
  Тест Catalog: добавление и удаление пунктов меню.
*/

import app.models.Catalog;
import app.models.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CatalogMenuTest {

    private Catalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new Catalog();
        catalog.seedSampleData();
    }

    @Test
    void addAndRemoveMenuItem_changesMenuList() {
        System.out.println("Тест: модификация меню — старт");
        int before = catalog.menu.size();

        MenuItem newItem = new MenuItem("Test Drink", 150.0);
        catalog.menu.add(newItem);

        assertEquals(before + 1, catalog.menu.size(), "Размер меню должен увеличиться на 1");
        assertTrue(catalog.menu.contains(newItem), "Меню должно содержать добавленный пункт");

        catalog.menu.remove(newItem);
        assertEquals(before, catalog.menu.size(), "Размер меню должен вернуться к исходному");
        assertFalse(catalog.menu.contains(newItem), "Меню не должно содержать удалённый пункт");
        System.out.println("Тест: модификация меню — пройден");
    }
}
