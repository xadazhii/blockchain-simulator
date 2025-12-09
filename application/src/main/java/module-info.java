module com.example.interface_ {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.example.server;
    requires org.bouncycastle.provider;

    opens com.xadazhii.application to javafx.fxml;
    exports com.xadazhii.application;
}