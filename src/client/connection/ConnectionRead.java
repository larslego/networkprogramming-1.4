package client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ConnectionRead implements Runnable{
    private ObjectInputStream clientInput;
    private boolean running = true;

    public ConnectionRead(Socket socket) {
        try {
            this.clientInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                } else {
                    System.out.println("[Server] Object: " + response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                this.running = false;
                break;
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
