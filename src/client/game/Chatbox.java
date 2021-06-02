package client.game;

import client.connection.Connection;
import client.game.player.Nickname;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalTime;

public class Chatbox {
    private Nickname nickname;
    private VBox chatVBox;
    private static TextArea textArea;
    private TextField chatInputField;
    private Connection connection;

    public Chatbox(Connection connection, Nickname nickname) {
        this.chatVBox = new VBox();
        textArea = new TextArea();
        textArea.setEditable(false);
        this.chatInputField = new TextField();
        this.connection = connection;
        this.nickname = nickname;

        //inputfield
        this.chatInputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String msg = this.nickname.getNickname() + ": " + this.chatInputField.getText();

                //does not work yet
                this.connection.sendObject(msg);
                this.chatInputField.clear();
            }
        });

        textArea.setWrapText(true);
        this.chatInputField.setPromptText("Type a message here");
        this.chatInputField.setId("chatInputField");

        //setup VBox
        VBox.setVgrow(textArea, Priority.ALWAYS);
        this.chatVBox.setMaxWidth(250.0);
        this.chatVBox.setAlignment(Pos.BOTTOM_CENTER);
        this.chatVBox.getChildren().addAll(textArea, this.chatInputField);
    }

    public VBox getChatVBox() {
        return this.chatVBox;
    }

    static public void apendText(String msg) {
        textArea.appendText("[" + String.format("%tR", LocalTime.now()) + "] " +msg + "\n");
    }
}
