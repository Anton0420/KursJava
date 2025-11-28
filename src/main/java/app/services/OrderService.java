package app.services;

/*
 * OrderService — логика создания и размещения заказов.
 */

import app.models.Catalog;
import app.models.Order;
import app.storage.FileStorage;

import java.util.List;

public class OrderService {
    private Catalog catalog;
    private List<Order> orders;

    public OrderService(Catalog catalog, List<Order> orders) {
        this.catalog = catalog;
        this.orders = orders;
    }

    public Order createOrder(String clientName) {
        return new Order(clientName);
    }

    public double calculateTotal(Order o) {
        if (o == null) return 0.0;

        double sum = 0.0;
        if (o.hookah != null) {
            sum += o.hookah.price;
        }
        if (o.items != null) {
            for (var mi : o.items) {
                sum += mi.price;
            }
        }
        o.total = sum;
        return sum;
    }

    public void placeOrder(Order o) {
        if (o == null) return;
        calculateTotal(o);
        orders.add(o);
        FileStorage.saveOrders(orders);
    }
}
