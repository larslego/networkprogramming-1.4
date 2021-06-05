package client.connection;

import client.game.Game;
import client.game.player.Player;
import client.interfaces.Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Connection implements Client {
    //Server details
    private Socket socket;
    private String hostname;
    private int port;

    //Client details
    private Player player;

    //Socket IO
    private ObjectOutputStream objectOutputStream;

    //Threads
    private ConnectionRead connectionRead;
    private Thread readSocketThread;

    private Game game;

    public Connection(String hostname, int port, Player player, Game game) {
        this.hostname = hostname;
        this.port = port;
        this.player = player;
        this.game = game;
    }

    @Override
    public void sendObject(Object o) {
        if (this.socket != null && this.objectOutputStream != null) {
            try {
                if (!this.socket.isClosed()) {
                    this.objectOutputStream.writeObject(o);
                    this.objectOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean connect() {
        try {
            this.socket = new Socket(this.hostname, this.port);
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

            //Create thread to read connection input.
            this.connectionRead = new ConnectionRead(this.socket, this, this.game);
            this.readSocketThread = new Thread(this.connectionRead);
            this.readSocketThread.start();

            //Send player object to the server.
            this.objectOutputStream.writeObject(this.player);
            this.objectOutputStream.reset();
            this.objectOutputStream.flush();
            return true;
        } catch (UnknownHostException e) {
            System.out.println("Unknown hostname");
            disconnect();
            return false;
        } catch (SocketException e) {
            System.out.println("Can't connect to server");
            disconnect();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
            System.out.println("Connection error");
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (this.socket != null) {
            if (this.connectionRead != null) {
                this.connectionRead.stop();
            }
        }

        if (this.readSocketThread != null) {
            try {
                this.readSocketThread.interrupt();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
