module com.example.interface_ {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.example.server;
    requires org.bouncycastle.provider;

    opens com.example.interface_ to javafx.fxml;
    exports com.example.interface_;
}