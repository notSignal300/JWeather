module org.example.jweather {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires eu.hansolo.tilesfx;
    requires org.json;

    opens org.example.jweather to javafx.fxml;
    exports org.example.jweather;
}