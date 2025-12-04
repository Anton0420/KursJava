package app.gui;

/*
 * MainWindow — простое окно управления.
 * Исправления:
 *  - использую полностью квалифицированный app.models.MenuItem, чтобы избежать конфликта с javafx.scene.control.MenuItem
 *  - getRoot() возвращает javafx.scene.Parent (BorderPane), чтобы Scene принимал его корректно
 */

import app.models.Catalog;
import app.models.Hookah;
import app.models.Order;
import app.models.Booking;
import app.services.OrderService;
import app.services.BookingService;
import app.storage.FileStorage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow {
    private BorderPane root;

    // бизнес-объекты
    private Catalog catalog;
    private List<Order> orders;
    private List<Booking> bookings;
    private OrderService orderService;
    private BookingService bookingService;

    // UI элементы
    private ListView<Hookah> hookahList;
    private ListView<app.models.MenuItem> menuList;
    private ListView<app.models.Flavor> flavorList;
    private TextArea ordersArea;
    private Label statusLabel;

    public MainWindow() {
        loadData();
        initServices();
        buildUI();
    }

    private void loadData() {
        catalog = FileStorage.loadCatalog();
        if (catalog == null) {
            catalog = new Catalog();
            catalog.seedSampleData();
            FileStorage.saveCatalog(catalog);
        }
        orders = FileStorage.loadOrders();
        bookings = FileStorage.loadBookings();
    }

    private void initServices() {
        orderService = new OrderService(catalog, orders);
        bookingService = new BookingService(bookings);
    }

    public Parent getRoot() {
        return root;
    }

    private void buildUI() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        Label header = new Label("Касса кальянного бара — GUI");
        header.setFont(Font.font(18));
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(0,0,10,0));

        hookahList = new ListView<>(FXCollections.observableArrayList(catalog.hookahs));
        hookahList.setPrefWidth(320);
        VBox leftBox = new VBox(new Label("Кальяны:"), hookahList);
        leftBox.setSpacing(6);
        root.setLeft(leftBox);

        flavorList = new ListView<>(FXCollections.observableArrayList(catalog.flavors));
        flavorList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        flavorList.setPrefHeight(200);

        menuList = new ListView<>(FXCollections.observableArrayList(catalog.menu));
        menuList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        menuList.setPrefHeight(200);

        VBox centerBox = new VBox(new Label("Вкусы (мн. выбор):"), flavorList,
                new Label("Меню (мн. выбор):"), menuList);
        centerBox.setSpacing(8);
        centerBox.setPadding(new Insets(0, 10, 0, 10));
        root.setCenter(centerBox);

        Button btnCreateOrder = new Button("Создать заказ");
        Button btnShowOrders = new Button("Показать заказы");
        Button btnBookVIP = new Button("Забронировать VIP");
        statusLabel = new Label("Статус: готов");

        btnCreateOrder.setOnAction(e -> onCreateOrder());
        btnShowOrders.setOnAction(e -> onShowOrders());
        btnBookVIP.setOnAction(e -> onBookVIP());

        VBox rightBox = new VBox(10, btnCreateOrder, btnShowOrders, btnBookVIP, statusLabel);
        rightBox.setPrefWidth(220);
        root.setRight(rightBox);

        ordersArea = new TextArea();
        ordersArea.setEditable(false);
        ordersArea.setPrefRowCount(8);
        root.setBottom(ordersArea);
        BorderPane.setMargin(ordersArea, new Insets(10, 0, 0, 0));
    }

    private void onCreateOrder() {
        Hookah selected = hookahList.getSelectionModel().getSelectedItem();
        ObservableList<app.models.Flavor> chosenFlavors = flavorList.getSelectionModel().getSelectedItems();
        ObservableList<app.models.MenuItem> chosenMenu = menuList.getSelectionModel().getSelectedItems();

        Order o = orderService.createOrder("GUI-клиент"); // временно

        if (selected != null) {
            o.hookah = selected;
            o.flavors.addAll(chosenFlavors);
            if (!catalog.areFlavorsCompatible(o.flavors)) {
                statusLabel.setText("Статус: выбранные вкусы несовместимы. Кальян не добавлен.");
                o.hookah = null;
                o.flavors.clear();
            }
        }

        if (chosenMenu != null && !chosenMenu.isEmpty()) {
            o.items.addAll(chosenMenu);
        }

        if (o.hookah == null && o.items.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Пустой заказ");
            a.setHeaderText(null);
            a.setContentText("Вы ничего не выбрали. Закрыть заказ?");
            var res = a.showAndWait();
            if (res.isEmpty() || res.get() != ButtonType.OK) {
                return;
            } else {
                statusLabel.setText("Статус: заказ отменён");
                return;
            }
        }

        OrderDetailsDialog d = new OrderDetailsDialog();
        var infoOpt = d.showAndWait();
        if (infoOpt.isEmpty()) {
            statusLabel.setText("Статус: оформление заказа отменено пользователем");
            return;
        }
        var info = infoOpt.get();
        o.clientName = info.getName();

        orderService.placeOrder(o);

        FileStorage.saveOrders(orders);
        statusLabel.setText("Статус: заказ создан (id=" + o.id.toString().substring(0,8) + ")");
        updateOrdersArea();
    }

    private void onShowOrders() {
        updateOrdersArea();
        statusLabel.setText("Статус: показаны заказы");
    }

    private void updateOrdersArea() {
        String text = orders.stream()
                .map(Order::toString)
                .collect(Collectors.joining("\n-----------------\n"));
        ordersArea.setText(text);
    }

    private void onBookVIP() {
        BookingDialog dlg = new BookingDialog();
        var result = dlg.showAndWait();
        if (result.isPresent()) {
            var info = result.get();
            String client = info.getClient();
            int room = info.getRoom();
            LocalDateTime from = info.getFrom();
            int hours = info.getHours();

            Booking b = new Booking(client, from, hours, room);
            boolean ok = bookingService.addBooking(b);
            if (ok) {
                FileStorage.saveBookings(bookings);
                statusLabel.setText("Статус: бронь создана (id=" + b.id.toString().substring(0,8) + ")");
            } else {
                statusLabel.setText("Статус: выбранное время занято");
            }
        }
    }
}
