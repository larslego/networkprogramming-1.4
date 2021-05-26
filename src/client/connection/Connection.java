package client.connection;

import client.game.player.Nickname;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Runnable {
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

    public void disconnect() {
        if (!this.socket.isClosed()) {
            try {
                System.out.println("Closing socket");
                this.connectionRead.stop();
                this.socket.close();
                this.readSocketThread.join();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connect to the server and start reading on a separate thread.
     */
    @Override
    public void run() {
        try {
            this.socket = new Socket(this.hostname, this.port);
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

            //Create thread to read connection input.
            this.connectionRead = new ConnectionRead(this.socket);
            this.readSocketThread = new Thread(this.connectionRead);
            this.readSocketThread.start();

            //Send username to the server (can be used as a simple handshake).
            this.objectOutputStream.writeObject(new Nickname(this.nickname));
            this.objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }
}
