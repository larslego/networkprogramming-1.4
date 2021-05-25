package client;

import client.game.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    private TextField hostnameText = new TextField();
    private TextField portText = new TextField();
    private TextField usernameText = new TextField();
    private Button joinButton = new Button("Join");

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        Game game = new Game(borderPane);
        game.run();
        borderPane.setCenter(game.getCanvas());
        borderPane.setTop(connectionBar());

        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("Mooie game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox connectionBar() {
        this.hostnameText.setPromptText("Server adress/hostname");
        this.portText.setPromptText("Server port");
        this.usernameText.setPromptText("Username");
        this.joinButton.setOnAction(e -> {
            //TODO: Implement connecting to server.
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(this.hostnameText, this.portText, this.usernameText, this.joinButton);
        return hBox;
    }
}
