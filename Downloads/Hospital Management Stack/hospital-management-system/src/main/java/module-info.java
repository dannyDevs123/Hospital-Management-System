module com.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires java.sql;
    requires org.apache.pdfbox;
    requires org.apache.commons.csv;

    // This gives JavaFX deep reflection access to your packages:
    opens com.hospital to javafx.graphics, javafx.fxml;
    opens com.hospital.controller to javafx.fxml;
    opens com.hospital.model to javafx.base, javafx.fxml; 
    opens com.hospital.util to javafx.fxml;
}