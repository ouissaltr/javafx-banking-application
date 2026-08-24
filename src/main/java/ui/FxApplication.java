package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // FXML laden (aus src/main/resources/)
        Parent root = FXMLLoader.load(getClass().getResource("/Mainview.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("PrivateBank – Mainview");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
