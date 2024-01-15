module com.example.ultimatetictactoe {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires static lombok;

    opens com.example.ultimatetictactoe to javafx.fxml;
    exports com.example.ultimatetictactoe;
}