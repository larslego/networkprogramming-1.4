package client.connection;

import client.game.player.Nickname;
import client.interfaces.Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Connection implements Runnable, Client {
    //Server details
    private Socket socket;
    private String hostname;
    private int port;

    //Client details
    private String nickname;
    //private Player player; //TODO: Implement player class

    //Socket IO
    private ObjectOutputStream objectOutputStream;

    //Threads
    private ConnectionRead connectionRead;
    private Thread readSocketThread;

    public Connection(String hostname, int port, String nickname) {
        this.hostname = hostname;
        this.port = port;
        this.nickname = nickname;
    }

    @Override
    public void sendObject(Object o) {
        if (this.socket != null && this.objectOutputStream != null) {
            try {
                this.objectOutputStream.writeObject(o);
                this.objectOutputStream.flush();
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
            this.connectionRead = new ConnectionRead(this.socket, this);
            this.readSocketThread = new Thread(this.connectionRead);
            this.readSocketThread.start();

            //Send username to the server (can be used as a simple handshake).
            this.objectOutputStream.writeObject(new Nickname(this.nickname));
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
            try {
                System.out.println("Closing socket");
                if (this.connectionRead != null) {
                    this.connectionRead.stop();
                }
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.readSocketThread != null) {
            try {
                this.readSocketThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connect to the server and start reading on a separate thread.
     */
    @Override
    public void run() {
    }
}
