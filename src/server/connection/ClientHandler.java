package server.connection;

import client.game.player.Nickname;
import server.Server;
import server.enums.LogType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String nickname;

    private boolean running = true;

    public ClientHandler(Socket socket) {
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

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
        Server.appendLog(LogType.INFO, "ClientHandler running");
        while (this.running) {
            try {
                Object o = this.objectInputStream.readObject(); //Read object
                if (o instanceof String) { //Check what type of object we receive
                    Server.appendLog(LogType.INFO, this.nickname + ": " + o);
                } else {
                    Server.appendLog(LogType.ERROR, "Received invalid object!");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
