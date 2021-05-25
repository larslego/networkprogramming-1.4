package client.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    //Server details
    private Socket socket;
    private String hostname;
    private int port;

    //Client details
    private String nickname;
    //private Player player; //TODO: Implement player class

    //Socket IO
    private ObjectOutputStream dataOutputStream;

    //Threads
    private ConnectionRead connectionRead;
    private Thread readSocketThread;

    public Connection(String hostname, int port, String nickname) {
        this.hostname = hostname;
        this.port = port;
        this.nickname = nickname;
    }

    /**
     * Connect to the server and start reading on a separate thread.
     */
    public void connect() {
        try {
            this.socket = new Socket(this.hostname, this.port);
            this.dataOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

            //Create thread to read connection input.
            this.connectionRead = new ConnectionRead(this.socket);
            this.readSocketThread = new Thread(this.connectionRead);
            this.readSocketThread.start();

            //Send username to the server (can be used as a simple handshake).
            this.dataOutputStream.writeObject(this.nickname);
            this.dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendObject(Object o) {
        if (this.socket != null && this.dataOutputStream != null) {
            try {
                this.dataOutputStream.writeObject(o);
                this.dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (!this.socket.isClosed()) {
            try {
                this.connectionRead.setRunning(false);
                this.readSocketThread.join();
                this.socket.close();
                this.dataOutputStream = null;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
