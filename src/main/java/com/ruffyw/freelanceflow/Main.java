package com.ruffyw.freelanceflow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button startTimerButton = new Button("Timer starten");
        Button createInvoiceButton = new Button("Rechnung erstellen");

        startTimerButton.setOnAction(e -> System.out.println("Timer gestartet"));
        createInvoiceButton.setOnAction(e -> System.out.println("Rechnung wird erstellt"));

        VBox root = new VBox(10, startTimerButton, createInvoiceButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("FreelanceFlow");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}