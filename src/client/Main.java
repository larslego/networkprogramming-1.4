package client;

import client.game.Game;
import client.game.player.Nickname;
import client.game.player.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
        Scene scene = new Scene(borderPane);
        this.game = new Game(borderPane, scene);
        borderPane.setCenter(game.getCanvas());
        borderPane.setTop(connectionBar());

        this.portText.setText("4444");

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setTitle("Mooie game");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox connectionBar() {
        this.hostnameText.setPromptText("Server adress/hostname");
        this.portText.setPromptText("Server port");
        this.usernameText.setPromptText("Username");
        ColorPicker colorPicker = new ColorPicker(Color.BLUE);

        //Check for valid input when the join button is pressed.
        this.joinButton.setOnAction(e -> {
            String hostname = "";
            int port = 0;
            String nickname = "";
            Color color = colorPicker.getValue();

            if (!this.hostnameText.getText().isEmpty()) { hostname = this.hostnameText.getText(); }
            if (!this.portText.getText().isEmpty()) { port = Integer.parseInt(this.portText.getText()); }
            if (!this.usernameText.getText().isEmpty()) { nickname = this.usernameText.getText(); }

            //If host input is valid we can start the game.
            if (!hostname.isEmpty() && port != 0 && !nickname.isEmpty()) {
                this.game.start(new Player(
                        new Nickname(nickname),
                        new Point2D.Double(0, 0),
                        new java.awt.Color( //Convert JavaFX Color to AWT Color.
                            (float) color.getRed(),
                            (float) color.getGreen(),
                            (float) color.getBlue(),
                            (float) color.getOpacity())),
                        hostname, port);
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(this.hostnameText, this.portText, this.usernameText, colorPicker, this.joinButton);
        return hBox;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.game.stop();
    }

    public static void chatMessageReceived(String msg) {
        System.out.println(msg);
    }
}
