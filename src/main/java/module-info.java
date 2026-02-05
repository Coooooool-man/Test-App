module org.example.testapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires mysql.connector.j;
    requires jbcrypt;


    opens org.example.testapp.ui to javafx.fxml;


    opens org.example.testapp.model to javafx.base;


    exports org.example.testapp.ui;
}