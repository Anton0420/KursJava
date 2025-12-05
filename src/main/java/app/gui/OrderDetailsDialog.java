package app.gui;

/*
 * OrderDetailsDialog — диалог для  ввода данных клиента перед сохранением заказа.
 */

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.regex.Pattern;

public class OrderDetailsDialog {

    public static class OrderDetails {
        private final String name;
        private final String phone;
        private final String email;

        public OrderDetails(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
    }

    private static final Pattern PHONE = Pattern.compile("^\\+?\\d{7,15}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public Optional<OrderDetails> showAndWait() {
        Dialog<OrderDetails> dialog = new Dialog<>();
        dialog.setTitle("Данные для заказа");

        ButtonType okType = new ButtonType("Подтвердить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("ФИО");

        TextField phoneField = new TextField();
        phoneField.setPromptText("+79991234567");

        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Телефон:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        Node okButton = dialog.getDialogPane().lookupButton(okType);
        okButton.setDisable(true);

        Runnable validate = () -> {
            String n = nameField.getText().trim();
            String p = phoneField.getText().trim();
            String e = emailField.getText().trim();
            boolean ok = !n.isEmpty()
                    && PHONE.matcher(p).matches()
                    && EMAIL.matcher(e).matches();
            okButton.setDisable(!ok);
        };

        nameField.textProperty().addListener((obs, o, n) -> validate.run());
        phoneField.textProperty().addListener((obs, o, n) -> validate.run());
        emailField.textProperty().addListener((obs, o, n) -> validate.run());

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt == okType) {
                return new OrderDetails(nameField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim());
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
