package app.storage;

/*
 * FileStorage — простая сериализация объектов в файлы.
 * Файлы: catalog.dat, orders.dat, bookings.dat
 */

import app.models.Catalog;
import app.models.Order;
import app.models.Booking;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String CATALOG_FILE = "catalog.dat";
    private static final String ORDERS_FILE = "orders.dat";
    private static final String BOOKINGS_FILE = "bookings.dat";

    public static void saveCatalog(Catalog catalog) {
        if (catalog == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATALOG_FILE))) {
            oos.writeObject(catalog);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения каталога: " + e.getMessage());
        }
    }

    public static Catalog loadCatalog() {
        File f = new File(CATALOG_FILE);
        if (!f.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CATALOG_FILE))) {
            return (Catalog) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки каталога: " + e.getMessage());
            return null;
        }
    }

    public static void saveOrders(List<Order> orders) {
        if (orders == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDERS_FILE))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения заказов: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Order> loadOrders() {
        File f = new File(ORDERS_FILE);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDERS_FILE))) {
            return (List<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки заказов: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveBookings(List<Booking> bookings) {
        if (bookings == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения бронирований: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Booking> loadBookings() {
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKINGS_FILE))) {
            return (List<Booking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки бронирований: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
