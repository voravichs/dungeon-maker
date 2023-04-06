package dungeon.dungeonmaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Starts the application, initializing the FXML and CSS files.
 * @author bainrow
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("rootpane.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),1200,675);
        String css = App.class.getResource("stylesheet.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle("Dungeon Maker");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}