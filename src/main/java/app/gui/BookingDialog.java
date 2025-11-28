package app.gui;

/*
 * BookingDialog — простой диалог для ввода брони (клиент, зал, дата-время, часы).
 * Возвращает Optional<BookingInfo>.
 */

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BookingDialog {

    public static class BookingInfo {
        private final String client;
        private final int room;
        private final LocalDateTime from;
        private final int hours;

        public BookingInfo(String client, int room, LocalDateTime from, int hours) {
            this.client = client;
            this.room = room;
            this.from = from;
            this.hours = hours;
        }

        public String getClient() { return client; }
        public int getRoom() { return room; }
        public LocalDateTime getFrom() { return from; }
        public int getHours() { return hours; }
    }

    public Optional<BookingInfo> showAndWait() {
        Dialog<BookingInfo> dialog = new Dialog<>();
        dialog.setTitle("Создать бронь VIP");

        ButtonType createButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField clientField = new TextField();
        clientField.setPromptText("Имя клиента");

        TextField roomField = new TextField("1");
        TextField datetimeField = new TextField(LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH")));
        TextField hoursField = new TextField("2");

        grid.add(new Label("Клиент:"), 0, 0);
        grid.add(clientField, 1, 0);
        grid.add(new Label("Зал:"), 0, 1);
        grid.add(roomField, 1, 1);
        grid.add(new Label("Дата (YYYY-MM-DD HH):"), 0, 2);
        grid.add(datetimeField, 1, 2);
        grid.add(new Label("Часы:"), 0, 3);
        grid.add(hoursField, 1, 3);

        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(false);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String client = clientField.getText().trim();
                    if (client.isEmpty()) client = "Гость";
                    int room = Integer.parseInt(roomField.getText().trim());
                    LocalDateTime from = LocalDateTime.parse(datetimeField.getText().trim(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                    int hours = Integer.parseInt(hoursField.getText().trim());
                    return new BookingInfo(client, room, from, hours);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<BookingInfo> result = dialog.showAndWait();
        return result;
    }
}
