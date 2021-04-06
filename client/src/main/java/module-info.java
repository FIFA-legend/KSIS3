module by.bsuir {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.web;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens by.bsuir to javafx.fxml;
    opens by.bsuir.entity to com.fasterxml.jackson.databind;
    exports by.bsuir;
}