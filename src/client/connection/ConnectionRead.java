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
        while (this.running) {
            try {
                if (this.clientInput.available() > 0) {
                    Object response = this.clientInput.readObject();
                    //Check what type of data we receive
                    if (response instanceof String) {
                        System.out.println("Received: String: " + response);
                    } else {
                        System.out.println("Received: Object: " + response);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
