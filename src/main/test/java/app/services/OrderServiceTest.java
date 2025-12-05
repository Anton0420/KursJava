package app.services;

/*
  Тесты OrderService: создание заказа и сохранение в список.
*/

import app.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

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
    void createOrder_shouldReturnNonNullOrder() {
        System.out.println("Тест: создание заказа — старт");
        Order o = orderService.createOrder("UnitTestClient");
        assertNotNull(o, "createOrder должен вернуть объект Order");
        assertEquals("UnitTestClient", o.clientName, "Имя клиента должно совпадать");
        System.out.println("Тест: создание заказа — пройден");
    }

    @Test
    void placeOrder_shouldAddOrderToList() {
        System.out.println("Тест: сохранение заказа — старт");
        Order o = orderService.createOrder("Client2");
        if (!catalog.hookahs.isEmpty()) {
            o.hookah = catalog.hookahs.get(0);
        }
        orderService.placeOrder(o);
        assertFalse(orders.isEmpty(), "orders должен содержать хотя бы один элемент после placeOrder");
        assertEquals("Client2", orders.get(0).clientName, "Имя сохранённого заказа должно совпадать");
        System.out.println("Тест: сохранение заказа — пройден");
    }
}
