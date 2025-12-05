package app.services;

/*
  Интеграционный тест: создание заказа, расчёт и сохранение в список.
*/

import app.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderFlowIntegrationTest {

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
    void fullFlow_createOrder_calculateAndSave() {
        System.out.println("Тест: интеграционный сценарий — старт");
        Order o = orderService.createOrder("IntegrationClient");
        if (!catalog.hookahs.isEmpty()) {
            o.hookah = catalog.hookahs.get(0);
        }
        if (!catalog.menu.isEmpty()) {
            o.items.add(catalog.menu.get(0));
        }

        double total = orderService.calculateTotal(o);
        assertTrue(total >= 0.0, "Итоговая сумма должна быть неотрицательной");

        orderService.placeOrder(o);
        assertFalse(orders.isEmpty(), "Заказ должен сохраниться в списке заказов");

        System.out.println("Заказ создан для: " + o.clientName + ", сумма = " + total);
        System.out.println("Тест: интеграционный сценарий — пройден");
    }
}
