package app.services;

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
        Order o = orderService.createOrder("UnitTestClient");
        assertNotNull(o, "createOrder должен вернуть объект Order");
        assertEquals("UnitTestClient", o.clientName);
    }

    @Test
    void placeOrder_shouldAddOrderToList() {
        Order o = orderService.createOrder("Клиент2");
        if (!catalog.hookahs.isEmpty()) {
            o.hookah = catalog.hookahs.get(0);
        }
        orderService.placeOrder(o);
        assertFalse(orders.isEmpty(), "orders должен содержать минимум один элемент после placeOrder");
        assertEquals("Клиент2", orders.get(0).clientName);
    }
}
