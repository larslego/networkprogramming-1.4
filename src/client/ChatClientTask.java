package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientTask implements Runnable {
    private ChatClient chatClient;
    private Socket socket;
    private DataInputStream clientInput;
    private boolean running = true;

    public ChatClientTask(Socket socket, ChatClient chatClient) {
        this.socket = socket;
        this.chatClient = chatClient;
        try {
            this.clientInput = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                if (this.clientInput.available() > 0) {
                    String response = this.clientInput.readUTF();
                    this.chatClient.getGui().printInGUI(response);
                    System.out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
