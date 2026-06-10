package com.planner.controller;

import com.planner.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller untuk menghubungkan Model dan View.
 * Konsep OOP: Encapsulation, Single Responsibility
 */
public class PlannerController {

    private final PlannerRepository repo;
    private final ObservableList<ScheduleItem> observableItems;

    public PlannerController() {
        this.repo = PlannerRepository.getInstance();
        this.observableItems = FXCollections.observableArrayList(repo.getAll());
    }

    public void addTodo(String title, LocalDate date, String note, TodoItem.Priority priority) {
        if (title == null || title.isBlank()) return;
        TodoItem item = new TodoItem(title, date, note, priority);
        repo.add(item);
        refreshObservable();
    }

    public void addEvent(String title, LocalDate date, String note, String timeStr, String location) {
        if (title == null || title.isBlank()) return;
        LocalTime time = null;
        try {
            if (timeStr != null && !timeStr.isBlank()) {
                time = LocalTime.parse(timeStr);
            }
        } catch (Exception ignored) {}
        EventItem item = new EventItem(title, date, note, time, location);
        repo.add(item);
        refreshObservable();
    }

    public void removeItem(ScheduleItem item) {
        repo.remove(item);
        refreshObservable();
    }

    public void toggleTodoDone(TodoItem item) {
        item.toggleDone();
        repo.update(item);
        refreshObservable();
    }

    public List<ScheduleItem> getItemsByDate(LocalDate date) {
        return repo.getByDate(date);
    }

    public List<ScheduleItem> getUpcoming(int days) {
        return repo.getUpcoming(days);
    }

    public boolean hasTodoOnDate(LocalDate date) {
        return repo.hasTodoOnDate(date);
    }

    public boolean hasEventOnDate(LocalDate date) {
        return repo.hasEventOnDate(date);
    }

    public int countTodos() { return repo.countTodos(); }
    public int countEvents() { return repo.countEvents(); }
    public int countDoneTodos() { return repo.countDoneTodos(); }

    public ObservableList<ScheduleItem> getObservableItems() {
        return observableItems;
    }

    private void refreshObservable() {
        observableItems.setAll(repo.getAll());
    }
}
