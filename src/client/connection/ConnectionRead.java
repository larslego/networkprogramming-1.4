package client.connection;

import client.Main;
import client.game.Game;
import client.game.player.Player;
import client.interfaces.ChatMessage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionRead implements Runnable, ChatMessage {
    private ObjectInputStream clientInput;
    private Connection connection;
    private boolean running = false;

    public ConnectionRead(Socket socket, Connection connection) {
        try {
            this.clientInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connection = connection;
    }

    @Override
    public void run() {
        this.running = true;
        System.out.println("Reading from server");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Object response = this.clientInput.readObject();
                //Check what type of data we receive
                if (response instanceof String) {
                    System.out.println("[Server] String: " + response);
                    onReceive((String) response);
                } else if (response instanceof EOFException) {
                    System.out.println("[Server] Object: " + response);
                } else if (response instanceof Player[]) {
                    Game.updatePlayerList((Player[]) response);
                }
            } catch (EOFException eofException) {
                System.out.println("Server closed");
                this.running = false;
                this.connection.disconnect();
            } catch (SocketException e) {
                if (e.getMessage().equalsIgnoreCase("Socket closed")) {
                    break;
                } else {
                    e.printStackTrace();
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
                this.running = false;
                this.connection.disconnect();
            }
        }
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void onReceive(String msg) {
        Main.chatMessageReceived(msg);
    }
}
