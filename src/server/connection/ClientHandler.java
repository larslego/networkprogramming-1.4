package server.connection;

import client.game.player.Nickname;
import server.Server;
import server.enums.LogType;
import server.interfaces.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Read from and send to client.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String nickname;

    private boolean running = true;

    private Client client;

    public ClientHandler(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, Client client) {
        try {
            this.socket = socket;
            this.objectOutputStream = oos;
            this.objectInputStream = ois;
            this.client = client;

            //Retrieve nickname
            Object o = this.objectInputStream.readObject();
            if (o instanceof Nickname) {
                this.nickname = ((Nickname) o).getNickname();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Server.appendLog(LogType.ERROR, e.getMessage());
        }
    }

    @Override
    public void run() {
        this.client.onConnect(this);
        while (this.running) {
            try {
                Object o = this.objectInputStream.readObject(); //Read object
                if (o instanceof String) { //Check what type of object we receive
                    if (((String) o).equalsIgnoreCase("quit")) {
                        Server.appendLog(LogType.INFO, this.nickname + "left the server.");
                        this.running = false;
                    }
                    Server.appendLog(LogType.INFO, this.nickname + ": " + o);
                } else if (o == null || o.equals(-1)) { //Client disconnects
                    this.client.onDisconnect(this);
                    this.running = false;
                } else {
                    Server.appendLog(LogType.ERROR, "Received invalid object!");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Server.appendLog(LogType.ERROR, e.getMessage());
                try {
                    this.socket.close();
                    Server.appendLog(LogType.ERROR, this.nickname + " connection reset");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                this.client.onDisconnect(this);
                this.running = false;
            }
        }
    }

    public void stop() {
        this.running = false;
    }

    public String getNickname() {
        return this.nickname;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return this.objectOutputStream;
    }
}
