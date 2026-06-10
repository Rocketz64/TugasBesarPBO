package com.planner.model;

import com.planner.util.FileStorageManager;
import com.planner.util.StorageManager;
import com.planner.util.PlannerObserver; // Import interface observer

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository untuk menyimpan dan mengelola semua ScheduleItem.
 * Konsep OOP: Encapsulation, Generic Collection (ArrayList<T>)
 */
public class PlannerRepository {

    // Generic collection - konsep koleksi PBO
    private final List<ScheduleItem> items = new ArrayList<>();

    // Singleton pattern
    private static PlannerRepository instance;

    // Menghubungkan kembali File Storage Manager
    private final StorageManager storageManager = new FileStorageManager();

    // List untuk menampung elemen UI yang mendaftar sebagai subscriber (Observer Pattern)
    private final List<PlannerObserver> observers = new ArrayList<>();

    private PlannerRepository() {
        // 1. Ambil data lama yang tersimpan di berkas lokal 'planner_data.txt' saat aplikasi dibuka
        List<ScheduleItem> loadedData = storageManager.load();
        if (loadedData != null) {
            this.items.addAll(loadedData);
        }

        // 2. Jika berkas kosong atau belum pernah ada data, gunakan data contoh (seeds)
        if (this.items.isEmpty()) {
            seedData();
            storageManager.save(this.items); // Langsung amankan data awal ke file
        }
    }

    public static synchronized PlannerRepository getInstance() {
        if (instance == null) {
            instance = new PlannerRepository();
        }
        return instance;
    }

    // --- MANAJEMEN OBSERVER PATTERN ---
    public void registerObserver(PlannerObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(PlannerObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (PlannerObserver observer : observers) {
            observer.onPlannerDataChanged();
        }
    }

    // --- MUTASI DATA (MEMPERBARUI FILE & BROADCAST KE UI) ---
    public void add(ScheduleItem item) {
        if (item == null) return;
        items.add(item);

        storageManager.save(items); // MASALAH TERATASI: Simpan perubahan ke text file secara permanen
        notifyObservers();          // Beritahu UI untuk langsung menggambar ulang layar
    }

    public void remove(ScheduleItem item) {
        if (item == null) return;
        items.remove(item);

        storageManager.save(items); // MASALAH TERATASI: Simpan perubahan ke text file secara permanen
        notifyObservers();          // Beritahu UI untuk langsung menggambar ulang layar
    }

    public void update(ScheduleItem item) {
        storageManager.save(items); // MASALAH TERATASI: Simpan perubahan status (seperti centang Done) ke file
        notifyObservers();          // Beritahu UI untuk langsung menggambar ulang layar
    }

    // --- QUERY METHODS ---
    public List<ScheduleItem> getAll() {
        List<ScheduleItem> sorted = new ArrayList<>(items);
        Collections.sort(sorted); // Pakai Comparable dari ScheduleItem
        return sorted;
    }

    public List<ScheduleItem> getByDate(LocalDate date) {
        return items.stream()
                .filter(item -> item.getDate().equals(date))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<TodoItem> getAllTodos() {
        return items.stream()
                .filter(item -> item instanceof TodoItem)
                .map(item -> (TodoItem) item)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<EventItem> getAllEvents() {
        return items.stream()
                .filter(item -> item instanceof EventItem)
                .map(item -> (EventItem) item)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<ScheduleItem> getUpcoming(int days) {
        LocalDate today = LocalDate.now();
        LocalDate until = today.plusDays(days);
        return items.stream()
                .filter(item -> !item.getDate().isBefore(today) && !item.getDate().isAfter(until))
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean hasItemsOnDate(LocalDate date) {
        return items.stream().anyMatch(item -> item.getDate().equals(date));
    }

    public boolean hasTodoOnDate(LocalDate date) {
        return items.stream().anyMatch(item -> item.getDate().equals(date) && item instanceof TodoItem);
    }

    public boolean hasEventOnDate(LocalDate date) {
        return items.stream().anyMatch(item -> item.getDate().equals(date) && item instanceof EventItem);
    }

    public int countTodos() {
        return (int) items.stream().filter(i -> i instanceof TodoItem).count();
    }

    public int countEvents() {
        return (int) items.stream().filter(i -> i instanceof EventItem).count();
    }

    public int countDoneTodos() {
        return (int) items.stream()
                .filter(i -> i instanceof TodoItem && ((TodoItem) i).isDone())
                .count();
    }

    // Data awal cadangan jika berkas penyimpanan kosong
    private void seedData() {
        LocalDate today = LocalDate.now();
        // Memasukkan langsung ke objek 'items' agar tidak memicu overhead penyimpanan berulang kali saat inisialisasi awal
        items.add(new TodoItem("Buat class diagram UML", today, "Sebelum mulai coding"));
        items.add(new TodoItem("Push code ke GitHub", today.plusDays(2), "Commit semua perubahan", TodoItem.Priority.HIGH));
        items.add(new EventItem("Presentasi PBO Kelompok", today.plusDays(1), "Persiapan 5 menit",
                java.time.LocalTime.of(9, 0), "Ruang B101"));
        items.add(new TodoItem("Review materi Polimorfisme", today.plusDays(4), "Baca slide P07"));
        items.add(new EventItem("UAS Pemrograman Berorientasi Objek", today.plusDays(17), "Bawa kartu ujian",
                java.time.LocalTime.of(8, 0), "Gedung C"));
        items.add(new TodoItem("Finalisasi laporan PBO", today.plusDays(16), "Format PDF", TodoItem.Priority.HIGH));
    }
}