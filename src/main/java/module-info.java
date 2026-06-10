module com.planner {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.planner to javafx.graphics, javafx.fxml;
    opens com.planner.view to javafx.graphics, javafx.fxml;
    opens com.planner.controller to javafx.fxml;
    opens com.planner.model to javafx.fxml;

    exports com.planner;
    exports com.planner.view;
    exports com.planner.controller;
    exports com.planner.model;
}
