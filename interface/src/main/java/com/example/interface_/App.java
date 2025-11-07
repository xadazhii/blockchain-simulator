package com.example.interface_;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.IOException;
import java.security.Security;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();

        Controller controller = fxmlLoader.getController();
        stage.setOnCloseRequest(event -> controller.shutdown());

        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/interface_/style.css")).toExternalForm());

        stage.setTitle("Blockchain Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        launch(args);
    }
}