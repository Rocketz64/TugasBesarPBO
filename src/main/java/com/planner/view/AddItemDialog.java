package com.planner.view;

import com.planner.model.TodoItem;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Dialog untuk menambah item baru.
 * Konsep OOP: Encapsulation, Composition (menggunakan Dialog JavaFX)
 */
public class AddItemDialog extends Dialog<AddItemDialog.ItemResult> {

    public static class ItemResult {
        private final String type;
        private final String title;
        private final LocalDate date;
        private final String note;
        private final String time;
        private final String location;
        private final TodoItem.Priority priority;

        public ItemResult(String type, String title, LocalDate date, String note,
                          String time, String location, TodoItem.Priority priority) {
            this.type = type;
            this.title = title;
            this.date = date;
            this.note = note;
            this.time = time;
            this.location = location;
            this.priority = priority;
        }

        public String type() { return type; }
        public String title() { return title; }
        public LocalDate date() { return date; }
        public String note() { return note; }
        public String time() { return time; }
        public String location() { return location; }
        public TodoItem.Priority priority() { return priority; }
    }

    public AddItemDialog(LocalDate defaultDate) {
        setTitle("Add New Item");
        setHeaderText(null);

        getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType addButton = new ButtonType("Add →", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(addButton, cancelButton);

        // Form
        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: #111111;");

        // Type selector
        Label typeLabel = new Label("TYPE");
        typeLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 10px; -fx-font-family: 'Courier New';");
        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rdTodo = new RadioButton("To-Do");
        rdTodo.setToggleGroup(typeGroup);
        rdTodo.setSelected(true);
        rdTodo.setStyle("-fx-text-fill: #3b82f6; -fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        RadioButton rdEvent = new RadioButton("Event");
        rdEvent.setToggleGroup(typeGroup);
        rdEvent.setStyle("-fx-text-fill: #ef4444; -fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        javafx.scene.layout.HBox typeBox = new javafx.scene.layout.HBox(16, rdTodo, rdEvent);

        // Grid form
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: transparent;");

        Label lTitle = styledLabel("TITLE");
        TextField fTitle = styledField("What's the task?");
        Label lDate = styledLabel("DATE");
        DatePicker fDate = new DatePicker(defaultDate);
        fDate.setStyle("-fx-background-color:#0d0d0d; -fx-border-color:#2a2a2a; -fx-font-family:'Courier New'; -fx-pref-width: 200;");
        Label lNote = styledLabel("NOTE");
        TextField fNote = styledField("Keterangan tambahan...");
        Label lTime = styledLabel("TIME (hh:mm)");
        TextField fTime = styledField("e.g. 09:00");
        Label lLoc = styledLabel("LOCATION");
        TextField fLoc = styledField("e.g. Ruang B101");
        Label lPriority = styledLabel("PRIORITY");
        ComboBox<TodoItem.Priority> fPriority = new ComboBox<>();
        fPriority.getItems().addAll(TodoItem.Priority.values());
        fPriority.setValue(TodoItem.Priority.MEDIUM);
        fPriority.setStyle("-fx-background-color:#0d0d0d; -fx-border-color:#2a2a2a; -fx-text-fill:#e0e0e0; -fx-font-family:'Courier New';");

        grid.add(lTitle, 0, 0); grid.add(fTitle, 1, 0);
        grid.add(lDate, 0, 1); grid.add(fDate, 1, 1);
        grid.add(lNote, 0, 2); grid.add(fNote, 1, 2);

        // Dynamic fields based on type
        Runnable updateForm = () -> {
            boolean isTodo = rdTodo.isSelected();
            grid.getChildren().removeAll(lTime, fTime, lLoc, fLoc, lPriority, fPriority);
            if (isTodo) {
                grid.add(lPriority, 0, 3);
                grid.add(fPriority, 1, 3);
            } else {
                grid.add(lTime, 0, 3);
                grid.add(fTime, 1, 3);
                grid.add(lLoc, 0, 4);
                grid.add(fLoc, 1, 4);
            }
        };

        rdTodo.setOnAction(e -> updateForm.run());
        rdEvent.setOnAction(e -> updateForm.run());
        updateForm.run();

        form.getChildren().addAll(typeLabel, typeBox, grid);
        getDialogPane().setContent(form);
        getDialogPane().setPrefWidth(380);

        // Result converter
        setResultConverter(btn -> {
            if (btn == addButton) {
                String type = rdTodo.isSelected() ? "todo" : "event";
                LocalDate date = fDate.getValue() != null ? fDate.getValue() : LocalDate.now();
                return new ItemResult(
                        type, fTitle.getText(), date, fNote.getText(),
                        fTime.getText(), fLoc.getText(), fPriority.getValue()
                );
            }
            return null;
        });
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #555; -fx-font-size: 10px; -fx-font-family: 'Courier New';");
        return l;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#0d0d0d; -fx-border-color:#2a2a2a; -fx-text-fill:#e0e0e0; -fx-font-family:'Courier New'; -fx-font-size:13px; -fx-pref-width:200;");
        return tf;
    }

    public Optional<ItemResult> showAndWaitResult() {
        return showAndWait();
    }
}
