package com.ruffyw.freelanceflow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        timeLabel = new Label("Zeit heute: 00:00:00");

        startTimerButton.setOnAction(e -> toggleTimer());


        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        VBox timeBox = new VBox(10, startTimerButton, timeLabel);
        timeBox.setSpacing(15);

        //Tabs
        TabPane tabPane = new TabPane();
        Tab timeTab = new Tab("Zeit", timeBox);
        timeTab.setClosable(false);


        //KundenTab
        VBox customerBox = new VBox(10);
        Button bill = new Button("Rechnung erstellen");
        Label billStatus = new Label("Keine Rechnung");
        bill.setOnAction(e -> billStatus.setText("Rechnung wird erstellt"));
        customerBox.getChildren().addAll(
                new Label("Kundenliste noch in der Entwicklung"),
                new Label("Noch in Entwicklung"),
                bill,
                billStatus
        );
        Tab customersTab = new Tab("Kunden", customerBox);
        customersTab.setClosable(false);

        //TasksTab
        VBox tasksBox = new VBox(10);
        tasksBox.getChildren().add(new TextField("Aufgabe eingeben"));
        Tab tasksTab = new Tab("Aufgaben", tasksBox);
        tasksTab.setClosable(false);

        //StatsTab
        VBox statsBox = new VBox(10);
        statsBox.getChildren().add(new Label("StatistikBereich - bald verfügbar"));
        Tab statsTab = new Tab("Statistik", statsBox);
        statsTab.setClosable(false);


        tabPane.getTabs().addAll(timeTab, customersTab, tasksTab, statsTab);

        Scene scene = new Scene(tabPane, 500, 400);
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