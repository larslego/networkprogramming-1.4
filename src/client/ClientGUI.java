package client;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private ChatClient chatClient;
    private TextArea serverOutput;
    private boolean loggedIn = false;
    private TextField userInput;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        this.serverOutput = new TextArea();
        this.userInput = new TextField();
        serverOutput.setEditable(false);
        this.userInput.setDisable(true);
        this.serverOutput.setDisable(true);

        this.chatClient = new ChatClient("localhost", 4444, "lars", this);
        borderPane.setCenter(this.serverOutput);

        //User info
        borderPane.setTop(loginBox());

        this.userInput = new TextField();
        this.userInput.setOnKeyPressed(event -> {
            if (loggedIn) {
                if (event.getCode() == KeyCode.ENTER) {
                    this.chatClient.chat(userInput.getText());
                    userInput.clear();
                }
            }
        });

        borderPane.setBottom(this.userInput);

        stage.setScene(new Scene(borderPane));
        stage.show();
        stage.setTitle("Client");
    }

    public Node loginBox() {
        HBox hBox = new HBox();
        TextField userNameField = new TextField();
        TextField hostField = new TextField();
        TextField portField = new TextField();
        Button login = new Button("Login");
        login.setOnAction(event -> {
            hBox.setDisable(true);
            this.userInput.setDisable(false);
            this.serverOutput.setDisable(false);
            this.loggedIn = true;

//            this.chatClient.setHostname(hostField.getText());
//            this.chatClient.setNickname(userNameField.getText());
//            this.chatClient.setPort(Integer.parseInt(portField.getText()));
            this.chatClient.start();
        });

        hBox.getChildren().addAll(new Label("Username: "), userNameField, new Label("Hostname: "), hostField, new Label("Port: "), portField, login);
        return hBox;
    }

    public void printInGUI(String msg) {
        this.serverOutput.appendText(msg + "\n");
    }

    @Override
    public void stop() {
        System.out.println("Closing client...");
        this.chatClient.close();
    }
}
