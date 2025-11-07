package app.ui;

import app.models.*;
import app.services.*;
import app.storage.FileStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    Catalog catalog;
    List<Order> orders;
    List<Booking> bookings;
    OrderService orderService;
    BookingService bookingService;
    Scanner sc = new Scanner(System.in);
    boolean isAdmin = false;
    final String ADMIN_PASS = "admin";

    public ConsoleApp() {
        catalog = FileStorage.loadCatalog();
        if (catalog==null) { catalog = new Catalog(); catalog.seedSampleData(); FileStorage.saveCatalog(catalog); }
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
                case "1": cmdCreateOrder(); break;
                case "2": cmdListCatalog(); break;
                case "3": cmdManageCatalog(); break;
                case "4": cmdBookVIP(); break;
                case "5": cmdViewOrders(); break;
                case "6": cmdViewBookings(); break;
                case "login": cmdLogin(); break;
                case "exit":
                    System.out.println("Выход. Сохранение данных...");
                    FileStorage.saveCatalog(catalog);
                    FileStorage.saveOrders(orders);
                    FileStorage.saveBookings(bookings);
                    return;
                default: System.out.println("Неизвестная команда. Введите номер пункта меню или 'exit'.");
            }
        }
    }

    private void printHeader() {
        System.out.println("================ Автоматизированная кассовая система кальянного бара ================");
        System.out.println("Сборка: консольная версия (этап 1). Данные сохраняются в локальные файлы.");
        System.out.println("Для доступа к админ-функциям введите команду 'login' в главном меню.");
    }
    private void printMainMenu() {
        System.out.println("\nГлавное меню:\n1. Оформить заказ\n2. Посмотреть каталог\n3. Управление каталогом (admin)\n4. Забронировать VIP-зал\n5. Просмотреть заказы\n6. Просмотреть бронирования\nlogin. Вход (админ)\nexit. Выход");
        System.out.print("Выберите действие: ");
    }

    private void cmdCreateOrder() {
        System.out.print("Имя клиента: ");
        String client = sc.nextLine().trim();
        if (client.isEmpty()) client = "Гость";
        Order o = orderService.createOrder(client);

        System.out.print("Добавить кальян? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            Hookah h = chooseHookah();
            if (h!=null) { o.hookah = h; o.flavors = chooseFlavors();
                if (!catalog.areFlavorsCompatible(o.flavors)) {
                    System.out.println("Выбранные вкусы несовместимы! Отмена добавления кальяна.");
                    o.hookah = null; o.flavors.clear();
                }
            }
        }
        System.out.print("Добавить напитки/блюда из меню? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            List<MenuItem> chosen = chooseMenuItems();
            o.items.addAll(chosen);
        }
        orderService.placeOrder(o);
        System.out.println("Заказ оформлен:\n" + o);
    }

    private Hookah chooseHookah() {
        System.out.println("Доступные кальяны:");
        for (int i=0;i<catalog.hookahs.size();i++) System.out.printf("%d) %s\n", i+1, catalog.hookahs.get(i));
        System.out.print("Выберите номер или пусто для отмены: ");
        String s = sc.nextLine().trim(); if (s.isEmpty()) return null;
        try { int id = Integer.parseInt(s); if (id>=1 && id<=catalog.hookahs.size()) return catalog.hookahs.get(id-1); }
        catch (Exception e) {}
        System.out.println("Неверный выбор."); return null;
    }

    private List<Flavor> chooseFlavors() {
        List<Flavor> chosen = new ArrayList<>();
        System.out.println("Доступные вкусы (введите номера через запятую, например: 1,3,4):");
        for (int i=0;i<catalog.flavors.size();i++) System.out.printf("%d) %s\n", i+1, catalog.flavors.get(i));
        System.out.print("Выберите: ");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) return chosen;
        String[] parts = line.split("[,;\\s]+");
        for (String p: parts) {
            try { int id = Integer.parseInt(p); if (id>=1 && id<=catalog.flavors.size()) chosen.add(catalog.flavors.get(id-1)); }
            catch (Exception e) {}
        }
        return chosen;
    }

    private List<MenuItem> chooseMenuItems() {
        List<MenuItem> chosen = new ArrayList<>();
        while (true) {
            System.out.println("Меню:");
            for (int i=0;i<catalog.menu.size();i++) System.out.printf("%d) %s\n", i+1, catalog.menu.get(i));
            System.out.print("Введите номер для добавления в заказ (или пусто чтобы закончить): ");
            String s = sc.nextLine().trim(); if (s.isEmpty()) break;
            try { int id = Integer.parseInt(s); if (id>=1 && id<=catalog.menu.size()) chosen.add(catalog.menu.get(id-1)); else System.out.println("Неверный номер"); }
            catch (Exception e) { System.out.println("Ошибка ввода"); }
        }
        return chosen;
    }

    private void cmdListCatalog() {
        System.out.println("\n-- КАТАЛОГ КАЛЬЯНОВ --"); for (Hookah h: catalog.hookahs) System.out.println(" - " + h);
        System.out.println("\n-- ВКУСЫ --"); for (Flavor f: catalog.flavors) System.out.println(" - " + f);
        System.out.println("\n-- МЕНЮ (напитки/блюда) --"); for (MenuItem mi: catalog.menu) System.out.println(" - " + mi);
    }

    private void cmdManageCatalog() {
        if (!isAdmin) { System.out.println("Требуется вход администратором (команда 'login')."); return; }
        while (true) {
            System.out.println("\nУправление каталогом: 1-addHookah 2-addFlavor 3-addMenu 4-removeHookah 5-removeFlavor 6-removeMenu 7-back");
            System.out.print("Выберите: ");
            String c = sc.nextLine().trim();
            if (c.equals("1")) { System.out.print("Название кальяна: "); String name = sc.nextLine(); System.out.print("Тип (CLASSIC/ELECTRONIC/DESIGNER): "); String t = sc.nextLine().trim(); System.out.print("Цена: "); String p = sc.nextLine().trim(); try { HookahType ht = HookahType.valueOf(t.toUpperCase()); double pr = Double.parseDouble(p); catalog.hookahs.add(new Hookah(name, ht, pr)); FileStorage.saveCatalog(catalog); System.out.println("Кальян добавлен."); } catch (Exception e) { System.out.println("Ошибка данных."); }
            } else if (c.equals("2")) { System.out.print("Название вкуса: "); String name = sc.nextLine().trim(); if (!name.isEmpty()) { catalog.flavors.add(new Flavor(name)); FileStorage.saveCatalog(catalog); System.out.println("Вкус добавлен."); } }
            else if (c.equals("3")) { System.out.print("Название блюда/напитка: "); String name = sc.nextLine(); System.out.print("Цена: "); String price = sc.nextLine(); try { double pr = Double.parseDouble(price); catalog.menu.add(new MenuItem(name, pr)); FileStorage.saveCatalog(catalog); System.out.println("Позиция добавлена."); } catch (Exception e) { System.out.println("Ошибка цены."); } }
            else if (c.equals("4")) { for (int i=0;i<catalog.hookahs.size();i++) System.out.println((i+1)+") "+catalog.hookahs.get(i)); System.out.print("Номер для удаления: "); String s = sc.nextLine(); try { int id = Integer.parseInt(s); if (id>=1 && id<=catalog.hookahs.size()) { catalog.hookahs.remove(id-1); FileStorage.saveCatalog(catalog); System.out.println("Удалено."); } } catch (Exception e) { System.out.println("Ошибка."); } }
            else if (c.equals("5")) { for (int i=0;i<catalog.flavors.size();i++) System.out.println((i+1)+") "+catalog.flavors.get(i)); System.out.print("Номер для удаления: "); String s = sc.nextLine(); try { int id = Integer.parseInt(s); if (id>=1 && id<=catalog.flavors.size()) { catalog.flavors.remove(id-1); FileStorage.saveCatalog(catalog); System.out.println("Удалено."); } } catch (Exception e) { System.out.println("Ошибка."); } }
            else if (c.equals("6")) { for (int i=0;i<catalog.menu.size();i++) System.out.println((i+1)+") "+catalog.menu.get(i)); System.out.print("Номер для удаления: "); String s = sc.nextLine(); try { int id = Integer.parseInt(s); if (id>=1 && id<=catalog.menu.size()) { catalog.menu.remove(id-1); FileStorage.saveCatalog(catalog); System.out.println("Удалено."); } } catch (Exception e) { System.out.println("Ошибка."); } }
            else if (c.equals("7")) { break; }
            else System.out.println("Неизвестная команда.");
        }
    }

    private void cmdBookVIP() {
        System.out.print("Имя клиента для брони: "); String client = sc.nextLine().trim(); if (client.isEmpty()) client = "Гость";
        System.out.print("Выберите зал (номер, по умолчанию 1): "); String sroom = sc.nextLine().trim(); int room = 1; try { if (!sroom.isEmpty()) room = Integer.parseInt(sroom); } catch (Exception e) { room = 1; }
        System.out.print("Дата и время начала брони (формат YYYY-MM-DD HH, например 2025-10-20 19): "); String datetime = sc.nextLine().trim();
        LocalDateTime from;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            from = LocalDateTime.parse(datetime, fmt);
        } catch (Exception e) { System.out.println("Неверный формат даты."); return; }
        System.out.print("Длительность в часах: "); String sh = sc.nextLine().trim(); int hours = 2; try { hours = Integer.parseInt(sh); } catch (Exception e) { hours = 2; }
        Booking b = new Booking(client, from, hours, room);
        if (bookingService.addBooking(b)) System.out.println("Бронь создана: " + b);
        else System.out.println("Выбранное время занято.");
    }

    private void cmdViewOrders() {
        if (orders.isEmpty()) System.out.println("Заказов пока нет.");
        else for (Order o: orders) System.out.println(o);
    }
    private void cmdViewBookings() {
        if (bookings.isEmpty()) System.out.println("Бронирований пока нет.");
        else for (Booking b: bookings) System.out.println(b);
    }

    private void cmdLogin() {
        System.out.print("Пароль администратора: ");
        String p = sc.nextLine().trim();
        if (p.equals(ADMIN_PASS)) { isAdmin = true; System.out.println("Вход как администратор успешен."); }
        else System.out.println("Неверный пароль.");
    }
}
