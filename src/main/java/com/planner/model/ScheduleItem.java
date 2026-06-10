package com.planner.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Abstract base class untuk semua item jadwal.
 * Konsep OOP: Abstraction, Encapsulation
 */
public abstract class ScheduleItem implements Displayable, Comparable<ScheduleItem> {

    private final String id;
    private String title;
    private LocalDate date;
    private String note;
    private ItemType type;

    public enum ItemType {
        TODO, EVENT
    }

    public ScheduleItem(String title, LocalDate date, String note, ItemType type) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.date = date;
        this.note = note;
        this.type = type;
    }

    // Getters & Setters - Encapsulation
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public ItemType getType() { return type; }

    // Abstract method - wajib diimplementasi subclass (Abstraction)
    public abstract String getDisplayColor();
    public abstract String getTypeLabel();

    // Polymorphism via interface Comparable
    @Override
    public int compareTo(ScheduleItem other) {
        return this.date.compareTo(other.date);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", type, title, date);
    }
}
