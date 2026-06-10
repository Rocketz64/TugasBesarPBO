package com.planner;

import com.planner.controller.PlannerController;
import com.planner.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point aplikasi Planner PBO 2026.
 * Konsep OOP: semua konsep dirangkum di sini:
 *   - Abstraction  : ScheduleItem (abstract class)
 *   - Encapsulation: semua class dengan private fields + getter/setter
 *   - Inheritance  : TodoItem & EventItem extends ScheduleItem
 *   - Polymorphism : getDisplayColor(), getTypeLabel(), Comparable
 *   - Interface    : Displayable
 *   - Collection   : ArrayList<ScheduleItem> di PlannerRepository
 *   - Modularisasi : package model, view, controller, util
 *   - GUI          : JavaFX
 */
public class PlannerApp extends Application {

    @Override
    public void start(Stage stage) {
        PlannerController controller = new PlannerController();
        MainView mainView = new MainView(controller);

        Scene scene = new Scene(mainView, 900, 640);

        // Load CSS dark theme
        scene.getStylesheets().add(
                getClass().getResource("/com/planner/css/dark.css").toExternalForm()
        );

        stage.setTitle("Planner — PBO 2026");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(580);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
