package server;

import client.game.player.Player;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Application {
    private static TextArea consoleArea = new TextArea();
    private TextField consoleSendText = new TextField();
    private static ListView<Player> playerListView = new ListView<>();
    private static CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>(playerListView.getItems());

    private Connection connection;
    private Thread serverThread;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();

        borderPane.setRight(playerListView);
        borderPane.setCenter(consoleArea);
        borderPane.setBottom(this.consoleSendText);

        initialize();

        Scene scene = new Scene(borderPane);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();

    }

    private void initialize() {
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
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String string : words) {
                            if (!string.equalsIgnoreCase(words[0])) {
                                stringBuilder.append(string);
                                stringBuilder.append(" ");
                            }
                        }
                        this.connection.broadcastObject(stringBuilder.toString());
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

    public synchronized static void addPlayer(Player player) {
        Platform.runLater(() -> players.add(player));
    }

    public synchronized static void removePlayer(Player player) {
        Platform.runLater(() -> players.remove(player));
    }

    @Override
    public void stop() {
        appendLog(LogType.INFO, "Stopping server");
        System.out.println("Stopping server");
        try {
            this.connection.stop();
            this.serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped");
    }

    public synchronized static CopyOnWriteArrayList<Player> getPlayers() {
        return players;
    }
}
