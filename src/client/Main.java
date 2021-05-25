package client;

import client.game.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        Game game = new Game(borderPane);
        game.run();
        borderPane.setCenter(game.getCanvas());

        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("Mooie game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
