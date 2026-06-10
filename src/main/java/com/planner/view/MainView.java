package com.planner.view;

import com.planner.controller.PlannerController;
import com.planner.model.ScheduleItem;
import com.planner.model.PlannerRepository;
import com.planner.util.PlannerObserver; // Import interface baru
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Layout Jendela Utama - Implementasi konsep Observer UI Component.
 */
public class MainView extends BorderPane implements PlannerObserver {

    private final PlannerController controller;
    private CalendarView calendarView;
    private DetailPanel detailPanel;
    private Label statTodo, statEvent, statDone;
    private VBox upcomingList;

    public MainView(PlannerController controller) {
        this.controller = controller;
        buildUI();

        // --- DAFTARKAN VIEW INI KE REPOSITORY ---
        PlannerRepository.getInstance().registerObserver(this);
    }

    private void buildUI() {
        setStyle("-fx-background-color: #0d0d0d;");
        setTop(buildTitleBar());
        setLeft(buildSidebar());
        setCenter(buildCenter());
    }

    private HBox buildTitleBar() {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("titlebar");
        bar.setAlignment(Pos.CENTER_LEFT);

        for (String color : new String[]{"#ff5f57", "#febc2e", "#28c840"}) {
            javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(6);
            dot.setFill(javafx.scene.paint.Color.web(color));
            bar.getChildren().add(dot);
        }

        Label title = new Label("  PLANNER — PBO 2026");
        title.getStyleClass().add("app-title");
        bar.getChildren().add(title);
        return bar;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);

        Label viewsLabel = new Label("VIEWS");
        viewsLabel.getStyleClass().add("sidebar-label");

        Button btnAll = navButton("● All Items", null);
        btnAll.setOnAction(e -> {
            if (calendarView != null) calendarView.setFilterType("all");
            if (detailPanel != null) detailPanel.setFilterType("all");
        });

        Button btnTodo = navButton("● To-Do List", "#3b82f6");
        btnTodo.setOnAction(e -> {
            if (calendarView != null) calendarView.setFilterType("todo");
            if (detailPanel != null) detailPanel.setFilterType("todo");
        });

        Button btnEvent = navButton("● Events", "#ef4444");
        btnEvent.setOnAction(e -> {
            if (calendarView != null) calendarView.setFilterType("event");
            if (detailPanel != null) detailPanel.setFilterType("event");
        });

        Label statsLabel = new Label("STATISTICS");
        statsLabel.getStyleClass().add("sidebar-label");
        statsLabel.setPadding(new Insets(12, 0, 4, 0));

        statTodo = statCard("TODO", String.valueOf(controller.countTodos()), "#3b82f6");
        statEvent = statCard("EVENT", String.valueOf(controller.countEvents()), "#ef4444");
        statDone = statCard("DONE", String.valueOf(controller.countDoneTodos()), "#4ade80");

        Label upLabel = new Label("UPCOMING");
        upLabel.getStyleClass().add("sidebar-label");
        upLabel.setPadding(new Insets(12, 0, 4, 0));

        upcomingList = new VBox(4);
        refreshUpcoming();

        sidebar.getChildren().addAll(
                viewsLabel, btnAll, btnTodo, btnEvent,
                statsLabel, statTodo, statEvent, statDone,
                upLabel, upcomingList
        );
        return sidebar;
    }

    private Button navButton(String text, String accentColor) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        if (accentColor != null) {
            btn.setStyle("-fx-text-fill: " + accentColor + ";");
        }
        return btn;
    }

    private Label statCard(String labelText, String value, String color) {
        Label lbl = new Label(value + " " + labelText);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-family: 'Courier New'; " +
                "-fx-background-color: #111; -fx-border-color: #1e1e1e; -fx-border-width: 1; " +
                "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10 6 10;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    private VBox buildCenter() {
        VBox center = new VBox(12);
        center.setPadding(new Insets(16));
        center.setStyle("-fx-background-color: #0d0d0d;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Item");
        addBtn.getStyleClass().add("add-btn");
        addBtn.setOnAction(e -> openAddDialog());

        header.getChildren().addAll(spacer, addBtn);

        calendarView = new CalendarView(controller);
        calendarView.setOnDateSelected(date -> detailPanel.setDate(date));

        detailPanel = new DetailPanel(controller);

        center.getChildren().addAll(header, calendarView, detailPanel);
        return center;
    }

    private void openAddDialog() {
        LocalDate date = calendarView.getSelectedDate();
        AddItemDialog dialog = new AddItemDialog(date);
        dialog.showAndWaitResult().ifPresent(result -> {
            if (result.type().equals("todo")) {
                controller.addTodo(result.title(), result.date(), result.note(), result.priority());
            } else {
                controller.addEvent(result.title(), result.date(), result.note(), result.time(), result.location());
            }
        });
    }

    // --- REAKSI REAKTIF OTOMATIS SAAT MODEL BERUBAH ---
    @Override
    public void onPlannerDataChanged() {
        if (calendarView != null) calendarView.refresh();
        if (detailPanel != null) detailPanel.refresh();
        refreshStats();
        refreshUpcoming();
    }

    private void refreshStats() {
        statTodo.setText(controller.countTodos() + " TODO");
        statEvent.setText(controller.countEvents() + " EVENT");
        statDone.setText(controller.countDoneTodos() + " DONE");
    }

    private void refreshUpcoming() {
        upcomingList.getChildren().clear();
        List<ScheduleItem> upcoming = controller.getUpcoming(7);
        if (upcoming.isEmpty()) {
            Label none = new Label("// none");
            none.setStyle("-fx-text-fill: #444; -fx-font-size: 11px; -fx-font-family: 'Courier New';");
            upcomingList.getChildren().add(none);
            return;
        }
        for (ScheduleItem item : upcoming.stream().limit(5).collect(java.util.stream.Collectors.toList())) {
            String color = item.getDisplayColor();
            String title = item.getTitle().length() > 20
                    ? item.getTitle().substring(0, 20) + "…"
                    : item.getTitle();
            Label lbl = new Label("• " + title);
            lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px; -fx-font-family: 'Courier New';");
            upcomingList.getChildren().add(lbl);
        }
    }
}