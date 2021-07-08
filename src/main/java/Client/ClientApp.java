package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL startView = new URL("file","","src/main/resources/basicCloudView.fxml");
        Parent parent = FXMLLoader.load(startView);
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
