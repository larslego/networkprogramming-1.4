package server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import server.connection.ClientHandler;
import server.connection.Connection;
import server.enums.LogType;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;

public class Server extends Application {
    private static TextArea consoleArea = new TextArea();
    private TextField consoleSendText = new TextField();
    //TODO: private ListView<Player> playerListView = new ListView<>();
    private ListView<String> playerList = new ListView<>();

    private Connection connection;
    private Thread serverThread;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();

        borderPane.setRight(this.playerList);
        borderPane.setCenter(consoleArea);
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
        consoleArea.setEditable(false);
        this.consoleSendText.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) && !this.consoleSendText.getText().isEmpty()) {
                String text = this.consoleSendText.getText();
                appendLog(LogType.INFO, text);

                //Command processing
                String[] words = text.split(" ");
                if (words.length > 1) {
                    if (words[0].equalsIgnoreCase("say")) {
                        for (ClientHandler c : this.connection.getClients()) {
                            this.connection.sendObject(c, Arrays.stream(words).skip(0).toString());
                        }
                    } else if (words[0].equalsIgnoreCase("kick")) {
                        Iterator<ClientHandler> iterator = this.connection.getClients().iterator();
                        while (iterator.hasNext()) {
                            ClientHandler c = iterator.next();
                            if (c.getNickname().equalsIgnoreCase(words[1])) {
                                c.kickClient(c);
                                break;
                            }
                        }
                    }
                }

                this.consoleSendText.clear();
            }
        });

        this.connection = new Connection(4444);
        this.serverThread = new Thread(this.connection);
        this.serverThread.start();
    }

    public static void appendLog(LogType logType, String msg) {
        consoleArea.appendText("[" + logType + "]" +
                "[" + String.format("%tR", LocalTime.now()) + "] " +
                msg + "\n");
    }

    @Override
    public void stop() {
        appendLog(LogType.INFO, "Stopping server");
        System.out.println("Stopping server");
        System.out.println("Thread count: " + Thread.activeCount());
        try {
            this.connection.stop();
            this.serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped");
    }
}
