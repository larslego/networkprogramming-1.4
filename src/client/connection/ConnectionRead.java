package client.connection;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ConnectionRead implements Runnable{
    private ObjectInputStream clientInput;
    private Connection connection;
    private boolean running = true;

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
        System.out.println("Reading from server");
        while (this.running) {
            try {
                System.out.println("Received input");
                Object response = this.clientInput.readObject();
                //Check what type of data we receive
                if (response instanceof String) {
                    System.out.println("[Server] String: " + response);
                } else if (response instanceof EOFException) {
                    System.out.println("[Server] Object: " + response);
                }
            } catch (EOFException eofException) {
                System.out.println("Server closed");
                this.running = false;
                this.connection.disconnect();
                break;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("You can ignore this exception if you intended to quit.");
                this.running = false;
                this.connection.disconnect();
                break;
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
