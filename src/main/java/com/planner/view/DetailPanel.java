package com.planner.view;

import com.planner.controller.PlannerController;
import com.planner.model.EventItem;
import com.planner.model.ScheduleItem;
import com.planner.model.TodoItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Panel detail menampilkan item pada tanggal terpilih.
 * Konsep OOP: Encapsulation, Polymorphism
 */
public class DetailPanel extends VBox {

    private final PlannerController controller;
    private LocalDate currentDate;
    private String filterType = "all";
    private Runnable onItemChanged;

    private final Label dateLabel = new Label();
    private final VBox itemList = new VBox(6);

    public DetailPanel(PlannerController controller) {
        this.controller = controller;
        this.currentDate = LocalDate.now();
        setupUI();
        refresh();
    }

    private void setupUI() {
        getStyleClass().add("detail-panel");
        setSpacing(10);

        dateLabel.getStyleClass().add("detail-date-label");

        // Filter tabs
        HBox tabs = new HBox(4);
        Button tabAll   = filterTab("All",      "all");
        Button tabTodo  = filterTab("◼ Todo",   "todo");
        Button tabEvent = filterTab("◼ Event",  "event");
        tabTodo .setStyle("-fx-text-fill:#3b82f6;-fx-background-color:transparent;-fx-font-size:11px;-fx-font-family:'Courier New';-fx-border-color:transparent;-fx-padding:4 10 4 10;-fx-cursor:hand;");
        tabEvent.setStyle("-fx-text-fill:#ef4444;-fx-background-color:transparent;-fx-font-size:11px;-fx-font-family:'Courier New';-fx-border-color:transparent;-fx-padding:4 10 4 10;-fx-cursor:hand;");
        tabs.getChildren().addAll(tabAll, tabTodo, tabEvent);

        ScrollPane scroll = new ScrollPane(itemList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        scroll.setPrefHeight(220);

        getChildren().addAll(dateLabel, tabs, scroll);
    }

    private Button filterTab(String text, String type) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:transparent;-fx-text-fill:#555;-fx-font-size:11px;-fx-font-family:'Courier New';-fx-border-color:transparent;-fx-padding:4 10 4 10;-fx-cursor:hand;");
        btn.setOnAction(e -> { filterType = type; refresh(); });
        return btn;
    }

    public void setDate(LocalDate date) {
        this.currentDate = date;
        refresh();
    }

    public void refresh() {
        if (currentDate == null) return;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE — d MMMM yyyy", Locale.ENGLISH);
        dateLabel.setText(currentDate.format(fmt).toUpperCase());

        List<ScheduleItem> items = controller.getItemsByDate(currentDate);
        List<ScheduleItem> filtered = items.stream().filter(item -> {
            if ("todo".equals(filterType))  return item instanceof TodoItem;
            if ("event".equals(filterType)) return item instanceof EventItem;
            return true;
        }).collect(Collectors.toList());

        itemList.getChildren().clear();

        if (filtered.isEmpty()) {
            Label empty = new Label("// no items for this date");
            empty.setStyle("-fx-text-fill:#444;-fx-font-size:13px;-fx-font-family:'Courier New';");
            itemList.getChildren().add(empty);
            return;
        }

        for (ScheduleItem item : filtered) {
            itemList.getChildren().add(buildCard(item));
        }
    }

    // Polymorphism — perilaku berbeda berdasarkan tipe objek
    private HBox buildCard(ScheduleItem item) {
        if (item instanceof TodoItem) {
            return buildTodoCard((TodoItem) item);
        } else {
            return buildEventCard((EventItem) item);
        }
    }

    private HBox buildTodoCard(TodoItem todo) {
        HBox card = new HBox(10);
        card.getStyleClass().add("item-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 12, 10, 12));

        // Warna bar kiri biru
        Rectangle bar = new Rectangle(3, 36);
        bar.setArcHeight(3); bar.setArcWidth(3);
        bar.setFill(Color.web("#3b82f6"));

        // Checkbox
        CheckBox chk = new CheckBox();
        chk.setSelected(todo.isDone());
        chk.setStyle("-fx-cursor:hand;");
        chk.setOnAction(e -> {
            controller.toggleTodoDone(todo);
            refresh();
            if (onItemChanged != null) onItemChanged.run();
        });

        // Body
        Label title = new Label(todo.getTitle());
        title.getStyleClass().add(todo.isDone() ? "item-title-done" : "item-title");

        Label meta = new Label(todo.getNote() == null || todo.getNote().isBlank() ? "—" : todo.getNote());
        meta.getStyleClass().add("item-meta");

        Label tagLbl = new Label("TODO");
        tagLbl.getStyleClass().add("tag-todo");
        Label priLbl = new Label(todo.getPriorityLabel());
        priLbl.setStyle("-fx-text-fill:#888;-fx-font-size:10px;-fx-font-family:'Courier New';");

        HBox tags = new HBox(6, tagLbl, priLbl);

        VBox body = new VBox(3, title, meta, tags);
        HBox.setHgrow(body, Priority.ALWAYS);

        HBox row = new HBox(8, chk, body);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(body, Priority.ALWAYS);

        // Delete
        Button del = deleteButton();
        del.setOnAction(e -> {
            controller.removeItem(todo);
            refresh();
            if (onItemChanged != null) onItemChanged.run();
        });

        card.getChildren().addAll(bar, row, del);
        return card;
    }

    private HBox buildEventCard(EventItem event) {
        HBox card = new HBox(10);
        card.getStyleClass().add("item-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 12, 10, 12));

        // Warna bar kiri merah
        Rectangle bar = new Rectangle(3, 36);
        bar.setArcHeight(3); bar.setArcWidth(3);
        bar.setFill(Color.web("#ef4444"));

        Label icon = new Label("■");
        icon.setStyle("-fx-text-fill:#ef4444;-fx-font-size:14px;");

        Label title = new Label(event.getTitle());
        title.getStyleClass().add("item-title");

        String metaText = event.getTimeDisplay().isBlank() ? "" : event.getTimeDisplay() + "  ";
        if (!event.getLocation().isBlank()) metaText += event.getLocation();
        if (metaText.isBlank()) metaText = event.getNote() == null || event.getNote().isBlank() ? "—" : event.getNote();

        Label meta = new Label(metaText);
        meta.getStyleClass().add("item-meta");

        Label tagLbl = new Label("EVENT");
        tagLbl.getStyleClass().add("tag-event");

        VBox body = new VBox(3, title, meta, tagLbl);
        HBox.setHgrow(body, Priority.ALWAYS);

        HBox row = new HBox(8, icon, body);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(body, Priority.ALWAYS);

        Button del = deleteButton();
        del.setOnAction(e -> {
            controller.removeItem(event);
            refresh();
            if (onItemChanged != null) onItemChanged.run();
        });

        card.getChildren().addAll(bar, row, del);
        return card;
    }

    private Button deleteButton() {
        Button del = new Button("×");
        del.setStyle("-fx-background-color:transparent;-fx-text-fill:#444;-fx-font-size:16px;-fx-cursor:hand;-fx-padding:0 4 0 4;");
        return del;
    }

    public void setOnItemChanged(Runnable handler) {
        this.onItemChanged = handler;
    }

    public void setFilterType(String type) {
        this.filterType = type; // "all", "todo", or "event"
        refresh();
    }
}
