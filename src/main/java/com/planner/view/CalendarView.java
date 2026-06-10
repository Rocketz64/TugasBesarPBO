package com.planner.view;

import com.planner.controller.PlannerController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Komponen tampilan kalender.
 * Konsep OOP: Composition, Encapsulation
 */
public class CalendarView extends VBox {

    private final PlannerController controller;
    private LocalDate viewMonth;
    private LocalDate selectedDate;
    private Consumer<LocalDate> onDateSelected;

    private final GridPane calGrid = new GridPane();
    private final Label monthLabel = new Label();

    private String filterType = "all";

    private static final String[] DAY_HEADERS = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    public CalendarView(PlannerController controller) {
        this.controller = controller;
        this.viewMonth = LocalDate.now().withDayOfMonth(1);
        this.selectedDate = LocalDate.now();

        setupUI();
        render();
    }

    public void setFilterType(String type) {
        this.filterType = type;
        render(); // Re-render the calendar to update the dots immediately
    }

    private void setupUI() {
        setSpacing(8);
        setPadding(new Insets(0, 0, 12, 0));

        // Month navigation
        Button prev = new Button("‹");
        prev.getStyleClass().add("month-nav-btn");
        Button next = new Button("›");
        next.getStyleClass().add("month-nav-btn");
        monthLabel.getStyleClass().add("month-label");

        prev.setOnAction(e -> { viewMonth = viewMonth.minusMonths(1); render(); });
        next.setOnAction(e -> { viewMonth = viewMonth.plusMonths(1); render(); });

        HBox nav = new HBox(12, prev, monthLabel, next);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(0, 0, 8, 0));

        // Day headers
        for (int i = 0; i < 7; i++) {
            Label hdr = new Label(DAY_HEADERS[i]);
            hdr.getStyleClass().add("cal-header-cell");
            hdr.setMinWidth(70);
            hdr.setAlignment(Pos.CENTER);
            calGrid.add(hdr, i, 0);
        }

        calGrid.setHgap(4);
        calGrid.setVgap(4);

        getChildren().addAll(nav, calGrid);
    }

    public void render() {
        // Update month label
        String monthName = viewMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        monthLabel.setText(monthName + " " + viewMonth.getYear());

        // Remove day cells (keep headers at row 0)
        calGrid.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);

        LocalDate today = LocalDate.now();
        int firstDayOfWeek = viewMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = viewMonth.lengthOfMonth();
        int prevMonthDays = viewMonth.minusDays(1).lengthOfMonth();

        int col = 0, row = 1;

        // Previous month padding
        for (int i = firstDayOfWeek - 1; i >= 0; i--) {
            LocalDate d = viewMonth.minusMonths(1).withDayOfMonth(prevMonthDays - i);
            calGrid.add(buildDayCell(d, true), col++, row);
        }

        // Current month
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate d = viewMonth.withDayOfMonth(day);
            calGrid.add(buildDayCell(d, false), col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }

        // Next month padding
        int nextMonthDay = 1;
        while (col < 7 && col > 0) {
            LocalDate d = viewMonth.plusMonths(1).withDayOfMonth(nextMonthDay);
            calGrid.add(buildDayCell(d, true), col++, row);
        }
    }

    private VBox buildDayCell(LocalDate date, boolean otherMonth) {
        VBox cell = new VBox(2);
        cell.getStyleClass().add("day-cell");
        if (otherMonth) cell.getStyleClass().add("day-cell-other");

        LocalDate today = LocalDate.now();
        boolean isToday = date.equals(today);
        boolean isSelected = date.equals(selectedDate);

        if (isSelected) cell.getStyleClass().add("day-cell-selected");
        else if (isToday) cell.getStyleClass().add("day-cell-today");

        // Day number
        Label num = new Label(String.valueOf(date.getDayOfMonth()));
        num.getStyleClass().add("day-num");
        if (isSelected) num.getStyleClass().add("day-num-selected");
        else if (isToday) num.getStyleClass().add("day-num-today");

        // Indicator dots
        HBox dots = new HBox(3);
        dots.setAlignment(Pos.CENTER_LEFT);
        if (!otherMonth) {
            if (("all".equals(filterType) || "todo".equals(filterType)) && controller.hasTodoOnDate(date)) {
                dots.getChildren().add(makeDot("#3b82f6"));
            }
            if (("all".equals(filterType) || "event".equals(filterType)) && controller.hasEventOnDate(date)) {
                dots.getChildren().add(makeDot("#ef4444"));
            }
        }

        cell.getChildren().addAll(num, dots);
        cell.setAlignment(Pos.TOP_LEFT);
        cell.setMinWidth(70);
        cell.setMinHeight(60);
        cell.setMaxHeight(60);

        if (!otherMonth) {
            cell.setOnMouseClicked(e -> {
                selectedDate = date;
                render();
                if (onDateSelected != null) onDateSelected.accept(date);
            });
        }

        return cell;
    }

    private Circle makeDot(String colorHex) {
        Circle c = new Circle(3.5);
        c.setFill(Color.web(colorHex));
        return c;
    }

    public void setOnDateSelected(Consumer<LocalDate> handler) {
        this.onDateSelected = handler;
    }

    public LocalDate getSelectedDate() { return selectedDate; }

    public void refresh() { render(); }
}
