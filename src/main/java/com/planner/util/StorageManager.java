package com.planner.util;

import com.planner.model.ScheduleItem;
import java.util.List;

public interface StorageManager {
    void save(List<ScheduleItem> items);
    List<ScheduleItem> load();
}