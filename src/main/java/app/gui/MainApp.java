package app.gui;

/*
 * MainApp — точка входа графического приложения (JavaFX).
 * Использует MainWindow.getRoot() (Parent) для создания Scene.
 */

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        MainWindow mainWindow = new MainWindow();
        Parent root = mainWindow.getRoot();
        Scene scene = new Scene(root, 1000, 600);

        stage.setTitle("Автоматизированная касса кальянного бара — GUI");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
