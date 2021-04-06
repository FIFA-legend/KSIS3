package by.bsuir;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage st) throws IOException {
        stage = st;
        Scene scene = new Scene(loadFXML());
        st.getIcons().add(new Image(App.class.getResourceAsStream("Server.jpeg")));
        st.setResizable(false);
        st.setTitle("Storage");
        st.setScene(scene);
        st.show();
    }

    private static Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main_window.fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}