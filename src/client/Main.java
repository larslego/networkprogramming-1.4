package client;

import client.game.Game;
import client.interfaces.Updateble;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static void main(String[] args) {
        /*List<Updateble> updatebles = new ArrayList<>();
        Updateble game = new Game();

        updatebles.add(game);

        boolean running = true;
        while (running) {
            for (Updateble updateble : updatebles) {
                updateble.update();
            }
        }*/

        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        Game game = new Game(borderPane);
        game.run();
        borderPane.setCenter(game.getCanvas());
        primaryStage.show();
    }
}
