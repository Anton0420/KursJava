package app.services;

import app.models.Catalog;
import app.models.Order;
import app.storage.FileStorage;

import java.util.List;

public class OrderService {
    Catalog catalog;
    List<Order> orders;
    public OrderService(Catalog catalog, List<Order> orders) { this.catalog = catalog; this.orders = orders; }

    public Order createOrder(String clientName) {
        return new Order(clientName);
    }

    public double calculateTotal(Order o) {
        double sum = 0;
        if (o.hookah!=null) sum += o.hookah.price;
        for (var mi: o.items) sum += mi.price;
        o.total = sum;
        return sum;
    }

    public void placeOrder(Order o) {
        calculateTotal(o);
        orders.add(o);
        FileStorage.saveOrders(orders);
    }
}
