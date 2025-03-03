package com.ruffyw.freelanceflow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Main extends Application {
    private Label timeLabel; //TimeLabel initialieseren
    private Button startTimerButton; //Button initialieseren
    private LocalDateTime startTime; //LocalDateTIme initialieseren
    private Timeline timeline; //TimeLine initialieseren
    private boolean isRunning = false;
    private ListView<String> timeList;

    @Override
    public void start(Stage primaryStage) {
        initDatabase();
    TabPane tabPane = createTabs();
    Scene scene = new Scene(tabPane, 500, 400);
    primaryStage.setTitle("FreelanceFlow");
    primaryStage.setScene(scene);
    primaryStage.show();
    }

    private TabPane createTabs() {
        startTimerButton = new Button("Timer starten"); // Timer-Button einfügen
        timeLabel = new Label("Zeit heute: 00:00:00"); // Time Label einfügen
        startTimerButton.setOnAction(e -> toggleTimer()); // On Click, Timer startet
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime())); // Programm Refresh für Timer
        timeline.setCycleCount(Timeline.INDEFINITE);

        VBox timeBox = new VBox(10, startTimerButton, timeLabel); // Deklariere timeBox
        timeList = new ListView<>(); // Initialisiere Klassenvariable
        timeList.setPrefHeight(100); //höhe setzen
        timeBox.getChildren().add(timeList);
        updateTimeList(timeList); // Lade Daten beim Start
        timeBox.setSpacing(15);

        TabPane tabPane = new TabPane();
        Tab timeTab = new Tab("Zeit", timeBox);
        timeTab.setClosable(false);

        // KundenTab
        VBox customerBox = new VBox(10);
        Button bill = new Button("Rechnung erstellen");
        Label billStatus = new Label("Keine Rechnung");
        bill.setOnAction(e ->{
        billStatus.setText("Rechnung wird erstellt");
        createInvoice();});
        customerBox.getChildren().addAll(
                new Label("Kundenliste noch in der Entwicklung"),
                new Label("Noch in Entwicklung"),
                bill,
                billStatus
        );
        Tab customersTab = new Tab("Kunden", customerBox);
        customersTab.setClosable(false);

        // TasksTab
        VBox tasksBox = new VBox(10);
        tasksBox.getChildren().add(new TextField("Aufgabe eingeben"));
        tasksBox.setSpacing(15);
        Tab tasksTab = new Tab("Aufgaben", tasksBox);
        tasksTab.setClosable(false);

        // StatsTab
        VBox statsBox = new VBox(10);
        statsBox.getChildren().add(new Label("StatistikBereich - bald verfügbar"));
        Tab statsTab = new Tab("Statistik", statsBox);
        statsTab.setClosable(false);

        tabPane.getTabs().addAll(timeTab, customersTab, tasksTab, statsTab);

        return tabPane; // Einziges return am Ende
    }

    private void toggleTimer() {
        if (!isRunning) {
            startTime = LocalDateTime.now();
            startTimerButton.setText("Timer stoppen");
            timeline.play();
            isRunning = true;
        } else {
            LocalDateTime endTime = LocalDateTime.now();
            startTimerButton.setText("Timer starten");
            timeline.stop();
            isRunning = false;
            saveTime(startTime, endTime); // Speichere die Zeit
            updateTimeList(timeList);
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

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:timelog.db"))
        {
         Statement stmt = conn.createStatement();
         stmt.execute("CREATE TABLE IF NOT EXISTS time_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, start_time TEXT, end_time TEXT)");
         System.out.println("Datenbank und Tabelle erstellt");
        }catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void saveTime(LocalDateTime start, LocalDateTime end) {
        int logCount = getLogCount();
        if (logCount >= 10) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Limit erreicht");
            alert.setHeaderText("Maximale Einträge in der kostenlosen Version");
            alert.setContentText("Bitte upgraden Sie für unbegrenzte Einträge.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:timelog.db")){
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO time_logs (start_time, end_time) VALUES  (?, ?)");
            pstmt.setString(1, start.toString());
            pstmt.setString(2, end.toString());
            pstmt.executeUpdate();
            System.out.println("Zeit gespeichert");
        }catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    private void printLogs() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:timelog.db")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM time_logs");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Start: " + rs.getString("start_time") + ", End: " + rs.getString("end_time"));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void updateTimeList(ListView<String> timeList) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:timelog.db")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM time_logs");
            timeList.getItems().clear();
            while (rs.next()) {
                String entry = "ID: " + rs.getInt("id") + ", Start: " + rs.getString("start_time") + ", End: " + rs.getString("end_time");
                timeList.getItems().add(entry);
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private void createInvoice() {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Rechnung");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Timer-Daten:");
            contentStream.newLineAtOffset(0, -20);

            // Daten aus time_logs einfügen
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:timelog.db")) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM time_logs");
                while (rs.next()) {
                    String entry = "ID: " + rs.getInt("id") + ", Start: " + rs.getString("start_time") + ", End: " + rs.getString("end_time");
                    contentStream.showText(entry);
                    contentStream.newLineAtOffset(0, -20);
                }
            } catch (SQLException e) {
                System.out.println("Datenbankfehler: " + e.getMessage());
            }

            contentStream.endText();
            contentStream.close();
            document.save("rechnung.pdf");
            System.out.println("Rechnung erstellt");
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private int getLogCount() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:timelog.db")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM time_logs");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
        return 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}