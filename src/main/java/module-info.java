module com.example.cpuscheduler {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.example.cpuscheduler to javafx.fxml;
    exports com.example.cpuscheduler;
}