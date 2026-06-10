package com.planner.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Subclass untuk item Event/Acara.
 * Konsep OOP: Inheritance (extends ScheduleItem), Polymorphism
 */
public class EventItem extends ScheduleItem {

    private LocalTime startTime;
    private LocalTime endTime;
    private String location;

    public EventItem(String title, LocalDate date, String note, LocalTime startTime, String location) {
        super(title, date, note, ItemType.EVENT);
        this.startTime = startTime;
        this.location = location;
    }

    public EventItem(String title, LocalDate date, String note) {
        this(title, date, note, null, "");
    }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTimeDisplay() {
        if (startTime == null) return "";
        String result = startTime.toString();
        if (endTime != null) result += " - " + endTime.toString();
        return result;
    }

    // Polymorphism - override method abstract dari parent
    @Override
    public String getDisplayColor() {
        return "#ef4444"; // Merah
    }

    @Override
    public String getTypeLabel() {
        return "EVENT";
    }
}
