package app.services;

/*
  Тест OrderService: проверка расчёта итоговой суммы заказа.
*/

import app.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceCalculateTotalTest {

    private Catalog catalog;
    private List<Order> orders;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        catalog = new Catalog();
        catalog.seedSampleData();
        orders = new ArrayList<>();
        orderService = new OrderService(catalog, orders);
    }

    @Test
    void calculateTotal_withHookahAndMenuItems_calculatesCorrectly() {
        System.out.println("Тест: расчёт счёта — старт");
        Order o = orderService.createOrder("TestClient");

        assertFalse(catalog.hookahs.isEmpty(), "В каталоге должен быть хотя бы один кальян");
        assertTrue(catalog.menu.size() >= 2, "В каталоге должно быть минимум 2 пункта меню");

        Hookah h = catalog.hookahs.get(0);
        MenuItem m1 = catalog.menu.get(0);
        MenuItem m2 = catalog.menu.get(1);

        o.hookah = h;
        o.items.add(m1);
        o.items.add(m2);

        double total = orderService.calculateTotal(o);
        double expected = h.price + m1.price + m2.price;
        assertEquals(expected, total, 0.001, "Итоговая сумма должна быть суммой позиций");

        System.out.println("Рассчитанная сумма: " + total + " (ожидалось: " + expected + ")");
        System.out.println("Тест: расчёт счёта — пройден");
    }
}
