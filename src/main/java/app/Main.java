package app;

/*
 * Main — точка входа приложения.
 * Запускает консольный интерфейс.
 */

import app.ui.ConsoleApp;

public class Main {
    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.run();
    }
}
