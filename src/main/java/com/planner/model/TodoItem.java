package com.planner.model;

import java.time.LocalDate;

/**
 * Subclass untuk item To-Do.
 * Konsep OOP: Inheritance (extends ScheduleItem), Polymorphism
 */
public class TodoItem extends ScheduleItem {

    private boolean done;
    private Priority priority;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public TodoItem(String title, LocalDate date, String note, Priority priority) {
        super(title, date, note, ItemType.TODO);
        this.done = false;
        this.priority = priority;
    }

    public TodoItem(String title, LocalDate date, String note) {
        this(title, date, note, Priority.MEDIUM);
    }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public void toggleDone() { this.done = !this.done; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    // Polymorphism - override method abstract dari parent
    @Override
    public String getDisplayColor() {
        return "#3b82f6"; // Biru
    }

    @Override
    public String getTypeLabel() {
        return "TODO";
    }

    public String getPriorityLabel() {
        if (priority == Priority.HIGH) return "HIGH";
        if (priority == Priority.LOW) return "LOW";
        return "MEDIUM";
    }
}
