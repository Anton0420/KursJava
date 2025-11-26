package app.ui;

/*
 * ConsoleApp — консольный интерфейс для работы с кассой.
 * Поддерживает: оформление заказа, просмотр каталога,
 * управление каталогом (admin), бронирование, просмотр заказов/бронирований.
 */

import app.models.*;
import app.services.*;
import app.storage.FileStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private Catalog catalog;
    private List<Order> orders;
    private List<Booking> bookings;
    private OrderService orderService;
    private BookingService bookingService;

    private Scanner sc = new Scanner(System.in);
    private boolean isAdmin = false;
    private final String ADMIN_PASS = "admin";

    public ConsoleApp() {
        catalog = FileStorage.loadCatalog();
        if (catalog == null) {
            catalog = new Catalog();
            catalog.seedSampleData();
            FileStorage.saveCatalog(catalog);
        }

        orders = FileStorage.loadOrders();
        bookings = FileStorage.loadBookings();

        orderService = new OrderService(catalog, orders);
        bookingService = new BookingService(bookings);
    }

    public void run() {
        printHeader();

        while (true) {
            printMainMenu();
            String cmd = sc.nextLine().trim();

            switch (cmd) {
                case "1":
                    createOrder();
                    break;
                case "2":
                    printCatalog();
                    break;
                case "3":
                    manageCatalog();
                    break;
                case "4":
                    bookVip();
                    break;
                case "5":
                    viewOrders();
                    break;
                case "6":
                    viewBookings();
                    break;
                case "login":
                    login();
                    break;
                case "exit":
                    shutdown();
                    return;
                default:
                    System.out.println("Неизвестная команда. Попробуйте снова.");
            }
        }
    }

    private void printHeader() {
        System.out.println("======================================================");
        System.out.println(" Автоматизированная кассовая система кальянного бара ");
        System.out.println(" Консольная версия — этап 1 (файловое хранение данных) ");
        System.out.println(" Для админ-доступа введите команду: login ");
        System.out.println("======================================================");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("Главное меню:");
        System.out.println("1. Оформить заказ");
        System.out.println("2. Посмотреть каталог");
        System.out.println("3. Управление каталогом (admin)");
        System.out.println("4. Забронировать VIP-зал");
        System.out.println("5. Просмотреть заказы");
        System.out.println("6. Просмотреть бронирования");
        System.out.println("login. Вход (админ)");
        System.out.println("exit. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createOrder() {
        System.out.print("Имя клиента: ");
        String client = sc.nextLine().trim();
        if (client.isEmpty()) client = "Гость";

        Order o = orderService.createOrder(client);

        System.out.print("Добавить кальян? (y/n): ");
        String addHookah = sc.nextLine().trim();
        if ("y".equalsIgnoreCase(addHookah)) {
            Hookah h = chooseHookah();
            if (h != null) {
                o.hookah = h;
                o.flavors = chooseFlavors();
                if (!catalog.areFlavorsCompatible(o.flavors)) {
                    System.out.println("Выбранные вкусы несовместимы. Кальян не добавлен.");
                    o.hookah = null;
                    o.flavors.clear();
                }
            }
        }

        System.out.print("Добавить напитки/блюда? (y/n): ");
        String addItems = sc.nextLine().trim();
        if ("y".equalsIgnoreCase(addItems)) {
            List<MenuItem> chosen = chooseMenuItems();
            o.items.addAll(chosen);
        }

        orderService.placeOrder(o);
        System.out.println("Заказ оформлен:");
        System.out.println(o);
    }

    private Hookah chooseHookah() {
        System.out.println("Доступные кальяны:");
        for (int i = 0; i < catalog.hookahs.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, catalog.hookahs.get(i));
        }
        System.out.print("Выберите номер (или Enter для отмены): ");
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return null;

        try {
            int id = Integer.parseInt(s);
            if (id >= 1 && id <= catalog.hookahs.size()) {
                return catalog.hookahs.get(id - 1);
            }
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Неверный выбор.");
        return null;
    }

    private List<Flavor> chooseFlavors() {
        List<Flavor> chosen = new ArrayList<>();
        System.out.println("Доступные вкусы (введите номера через запятую, например: 1,3,4):");
        for (int i = 0; i < catalog.flavors.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, catalog.flavors.get(i));
        }
        System.out.print("Выберите: ");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) return chosen;

        String[] parts = line.split("[,;\\s]+");
        for (String p : parts) {
            try {
                int id = Integer.parseInt(p);
                if (id >= 1 && id <= catalog.flavors.size()) {
                    chosen.add(catalog.flavors.get(id - 1));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return chosen;
    }

    private List<MenuItem> chooseMenuItems() {
        List<MenuItem> chosen = new ArrayList<>();
        while (true) {
            System.out.println("Меню:");
            for (int i = 0; i < catalog.menu.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, catalog.menu.get(i));
            }
            System.out.print("Введите номер для добавления (Enter — закончить): ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) break;
            try {
                int id = Integer.parseInt(s);
                if (id >= 1 && id <= catalog.menu.size()) {
                    chosen.add(catalog.menu.get(id - 1));
                } else {
                    System.out.println("Неверный номер.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода.");
            }
        }
        return chosen;
    }

    private void printCatalog() {
        System.out.println();
        System.out.println("-- КАТАЛОГ КАЛЬЯНОВ --");
        for (Hookah h : catalog.hookahs) {
            System.out.println(" - " + h);
        }
        System.out.println();
        System.out.println("-- ВКУСЫ --");
        for (Flavor f : catalog.flavors) {
            System.out.println(" - " + f);
        }
        System.out.println();
        System.out.println("-- МЕНЮ (напитки/блюда) --");
        for (MenuItem mi : catalog.menu) {
            System.out.println(" - " + mi);
        }
    }

    private void manageCatalog() {
        if (!isAdmin) {
            System.out.println("Требуется админ-доступ. Введите 'login' в главном меню.");
            return;
        }

        while (true) {
            System.out.println();
            System.out.println("Управление каталогом:");
            System.out.println("1. Добавить кальян");
            System.out.println("2. Добавить вкус");
            System.out.println("3. Добавить позицию меню");
            System.out.println("4. Удалить кальян");
            System.out.println("5. Удалить вкус");
            System.out.println("6. Удалить позицию меню");
            System.out.println("7. Назад");
            System.out.print("Выберите: ");

            String c = sc.nextLine().trim();
            if ("1".equals(c)) {
                System.out.print("Название кальяна: ");
                String name = sc.nextLine();
                System.out.print("Тип (CLASSIC/ELECTRONIC/DESIGNER): ");
                String t = sc.nextLine().trim().toUpperCase();
                System.out.print("Цена: ");
                String p = sc.nextLine().trim();
                try {
                    HookahType ht = HookahType.valueOf(t);
                    double pr = Double.parseDouble(p);
                    catalog.hookahs.add(new Hookah(name, ht, pr));
                    FileStorage.saveCatalog(catalog);
                    System.out.println("Кальян добавлен.");
                } catch (Exception e) {
                    System.out.println("Ошибка данных. Кальян не добавлен.");
                }
            } else if ("2".equals(c)) {
                System.out.print("Название вкуса: ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty()) {
                    catalog.flavors.add(new Flavor(name));
                    FileStorage.saveCatalog(catalog);
                    System.out.println("Вкус добавлен.");
                }
            } else if ("3".equals(c)) {
                System.out.print("Название блюда/напитка: ");
                String name = sc.nextLine();
                System.out.print("Цена: ");
                String price = sc.nextLine();
                try {
                    double pr = Double.parseDouble(price);
                    catalog.menu.add(new MenuItem(name, pr));
                    FileStorage.saveCatalog(catalog);
                    System.out.println("Позиция добавлена.");
                } catch (Exception e) {
                    System.out.println("Неверная цена.");
                }
            } else if ("4".equals(c)) {
                for (int i = 0; i < catalog.hookahs.size(); i++) {
                    System.out.println((i + 1) + ") " + catalog.hookahs.get(i));
                }
                System.out.print("Номер для удаления: ");
                String s = sc.nextLine();
                try {
                    int id = Integer.parseInt(s);
                    if (id >= 1 && id <= catalog.hookahs.size()) {
                        catalog.hookahs.remove(id - 1);
                        FileStorage.saveCatalog(catalog);
                        System.out.println("Удалено.");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка.");
                }
            } else if ("5".equals(c)) {
                for (int i = 0; i < catalog.flavors.size(); i++) {
                    System.out.println((i + 1) + ") " + catalog.flavors.get(i));
                }
                System.out.print("Номер для удаления: ");
                String s = sc.nextLine();
                try {
                    int id = Integer.parseInt(s);
                    if (id >= 1 && id <= catalog.flavors.size()) {
                        catalog.flavors.remove(id - 1);
                        FileStorage.saveCatalog(catalog);
                        System.out.println("Удалено.");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка.");
                }
            } else if ("6".equals(c)) {
                for (int i = 0; i < catalog.menu.size(); i++) {
                    System.out.println((i + 1) + ") " + catalog.menu.get(i));
                }
                System.out.print("Номер для удаления: ");
                String s = sc.nextLine();
                try {
                    int id = Integer.parseInt(s);
                    if (id >= 1 && id <= catalog.menu.size()) {
                        catalog.menu.remove(id - 1);
                        FileStorage.saveCatalog(catalog);
                        System.out.println("Удалено.");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка.");
                }
            } else if ("7".equals(c)) {
                break;
            } else {
                System.out.println("Неизвестная команда.");
            }
        }
    }

    private void bookVip() {
        System.out.print("Имя клиента для брони: ");
        String client = sc.nextLine().trim();
        if (client.isEmpty()) client = "Гость";

        System.out.print("Выберите зал (номер, по умолчанию 1): ");
        String sroom = sc.nextLine().trim();
        int room = 1;
        try {
            if (!sroom.isEmpty()) room = Integer.parseInt(sroom);
        } catch (Exception e) {
            room = 1;
        }

        System.out.print("Дата и время начала брони (YYYY-MM-DD HH), например 2025-10-20 19: ");
        String datetime = sc.nextLine().trim();
        LocalDateTime from;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            from = LocalDateTime.parse(datetime, fmt);
        } catch (Exception e) {
            System.out.println("Неверный формат даты.");
            return;
        }

        System.out.print("Длительность в часах: ");
        String sh = sc.nextLine().trim();
        int hours = 2;
        try {
            hours = Integer.parseInt(sh);
        } catch (Exception e) {
            hours = 2;
        }

        Booking b = new Booking(client, from, hours, room);
        if (bookingService.addBooking(b)) {
            System.out.println("Бронь создана: " + b);
        } else {
            System.out.println("Выбранное время занято.");
        }
    }

    private void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("Заказов пока нет.");
            return;
        }
        for (Order o : orders) {
            System.out.println(o);
        }
    }

    private void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("Бронирований пока нет.");
            return;
        }
        for (Booking b : bookings) {
            System.out.println(b);
        }
    }

    private void login() {
        System.out.print("Пароль администратора: ");
        String p = sc.nextLine().trim();
        if (p.equals(ADMIN_PASS)) {
            isAdmin = true;
            System.out.println("Вход как администратор успешен.");
        } else {
            System.out.println("Неверный пароль.");
        }
    }

    private void shutdown() {
        System.out.println("Выход. Сохранение данных...");
        FileStorage.saveCatalog(catalog);
        FileStorage.saveOrders(orders);
        FileStorage.saveBookings(bookings);
    }
}
