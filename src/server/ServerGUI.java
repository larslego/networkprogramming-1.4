package server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalTime;

public class ServerGUI extends Application {
    private ChatServer chatServer;
    private TextArea consoleOutput;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        this.consoleOutput = new TextArea();
        consoleOutput.setEditable(false);

        this.chatServer = new ChatServer(4444, this);
        Thread serverThread = new Thread(this.chatServer);
        borderPane.setCenter(this.consoleOutput);

        stage.setScene(new Scene(borderPane));
        stage.setTitle("Server");
        stage.show();

        serverThread.start();
    }

    public void printInGUI(String msg) {
        this.consoleOutput.appendText("[" + String.format("%tR", LocalTime.now()) + "] " + msg + "\n");
    }

    @Override
    public void stop() {
        this.chatServer.stop();
    }
}
