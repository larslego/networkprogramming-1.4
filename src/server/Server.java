package server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import server.enums.LogType;

import java.time.LocalTime;

public class Server extends Application {
    private TextArea consoleArea = new TextArea();
    private TextField consoleSendText = new TextField();
    //TODO: private ListView<Player> playerListView = new ListView<>();
    private ListView<String> playerList = new ListView<>();

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();

        borderPane.setRight(this.playerList);
        borderPane.setCenter(this.consoleArea);
        borderPane.setBottom(this.consoleSendText);

        initialize();

        Scene scene = new Scene(borderPane);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }

    private void initialize() {
        this.playerList.getItems().addAll("Tom", "Lars");
        this.consoleSendText.setPromptText("Enter a command. To see all available commands type '/help'.");
        this.consoleArea.setEditable(false);
        this.consoleSendText.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) && !this.consoleSendText.getText().isEmpty()) {
                appendLog(LogType.INFO, this.consoleSendText.getText());
                this.consoleSendText.clear();
            }
        });
    }

    public void appendLog(LogType logType, String msg) {
        this.consoleArea.appendText("\n[" + logType + "]" +
                "[" + String.format("%tR", LocalTime.now()) + "]" +
                msg);
    }
}
