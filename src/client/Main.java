package client;

import client.game.Game;
import client.game.player.Nickname;
import client.game.player.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.geom.Point2D;

public class Main extends Application {
    private TextField hostnameText = new TextField();
    private TextField portText = new TextField();
    private TextField usernameText = new TextField();
    private Button joinButton = new Button("Join");

    private Game game;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        this.game = new Game(borderPane);
        borderPane.setCenter(game.getCanvas());
        borderPane.setTop(connectionBar());

        //PRESET FOR TESTING
        this.hostnameText.setText("localhost");
        this.portText.setText("4444");
        this.usernameText.setText("Lars");

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
            String hostname = "";
            int port = 0;
            String nickname = "";

            if (!this.hostnameText.getText().isEmpty()) { hostname = this.hostnameText.getText(); }
            if (!this.portText.getText().isEmpty()) { port = Integer.parseInt(this.portText.getText()); }
            if (!this.usernameText.getText().isEmpty()) { nickname = this.usernameText.getText(); }

            if (!hostname.isEmpty() && port != 0 && !nickname.isEmpty()) {
                this.game.start(new Player(new Nickname(nickname), new Point2D.Double(0, 0)), hostname, port);
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(this.hostnameText, this.portText, this.usernameText, this.joinButton);
        return hBox;
    }
}
