package com.ruffyw.freelanceflow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main extends Application {
    private Label timeLabel;
    private Button startTimerButton;
    private LocalDateTime startTime;
    private Timeline timeline;
    private boolean isRunning = false;

    @Override
    public void start(Stage primaryStage) {
        startTimerButton = new Button("Timer starten");
        Button createInvoiceButton = new Button("Rechnung erstellen");
        timeLabel = new Label("Zeit heute: 00:00:00");

        startTimerButton.setOnAction(e -> toggleTimer());
        createInvoiceButton.setOnAction(e -> System.out.println("Rechnung wird erstellt"));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        VBox root = new VBox(10, startTimerButton, createInvoiceButton, timeLabel);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("FreelanceFlow");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void toggleTimer() {
        if (!isRunning) {
            startTime = LocalDateTime.now();
            startTimerButton.setText("Timer stoppen");
            timeline.play();
            isRunning = true;
        } else {
            startTimerButton.setText("Timer starten");
            timeline.stop();
            isRunning = false;
        }
    }

    private void updateTime() {
        if (isRunning) {
            long seconds = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            timeLabel.setText(String.format("Zeit heute: %02d:%02d:%02d", hours, minutes, secs));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}