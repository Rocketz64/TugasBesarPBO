package com.planner.util;

import com.planner.model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FileStorageManager implements StorageManager {
    private final String filePath = "planner_data.txt";

    @Override
    public void save(List<ScheduleItem> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (ScheduleItem item : items) {
                if (item instanceof TodoItem) {
                    TodoItem t = (TodoItem) item;
                    writer.write(String.format("TODO|%s|%s|%s|%b|%s\n",
                            t.getTitle(), t.getDate(), t.getNote(), t.isDone(), t.getPriority()));
                } else if (item instanceof EventItem) {
                    EventItem e = (EventItem) item;
                    String timeStr = e.getStartTime() != null ? e.getStartTime().toString() : "";
                    writer.write(String.format("EVENT|%s|%s|%s|%s|%s\n",
                            e.getTitle(), e.getDate(), e.getNote(), timeStr, e.getLocation()));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    @Override
    public List<ScheduleItem> load() {
        List<ScheduleItem> items = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return items;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\|", -1);
                if (tokens.length < 5) continue;

                String type = tokens[0];
                String title = tokens[1];
                LocalDate date = LocalDate.parse(tokens[2]);
                String note = tokens[3];

                if ("TODO".equals(type)) {
                    boolean isDone = Boolean.parseBoolean(tokens[4]);
                    TodoItem.Priority priority = TodoItem.Priority.valueOf(tokens[5].trim());
                    TodoItem todo = new TodoItem(title, date, note, priority);
                    if (isDone) todo.setDone(true);
                    items.add(todo);
                } else if ("EVENT".equals(type)) {
                    String timeStr = tokens[4];
                    String location = tokens[5];
                    LocalTime time = null;
                    try {
                        if (timeStr != null && !timeStr.isBlank()) {
                            time = LocalTime.parse(timeStr);
                        }
                    } catch (Exception ignored) {}

                    EventItem event = new EventItem(title, date, note, time, location);
                    items.add(event);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load data: " + e.getMessage());
        }
        return items;
    }
}