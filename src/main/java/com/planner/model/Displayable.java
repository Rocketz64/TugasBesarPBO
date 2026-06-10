package com.planner.model;

/**
 * Interface untuk item yang bisa ditampilkan.
 * Konsep OOP: Interface
 */
public interface Displayable {
    String getDisplayColor();
    String getTypeLabel();
    String getTitle();
    String getNote();
}
