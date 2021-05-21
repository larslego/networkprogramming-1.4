package server;

import events.ChatEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ChatServerTask implements Runnable {
    private ChatServer server;
    private Socket socket;
    private DataInputStream clientInput;
    private DataOutputStream clientOutput;
    private boolean running = true;

    private String clientName;

    private ChatEvent chatMessageEvent;

    public ChatServerTask(Socket socket, ChatServer server, ChatEvent chatEvents) {
        this.socket = socket;
        this.server = server;
        this.chatMessageEvent = chatEvents;

        try {
            this.clientInput = new DataInputStream(this.socket.getInputStream());
            this.clientOutput = new DataOutputStream(this.socket.getOutputStream());
            this.clientOutput.writeUTF("Welcome to the server.");
            this.clientOutput.writeUTF("nick"); //Ask nickname from client
            this.clientName = this.clientInput.readUTF(); //Read nickname from client
            this.chatMessageEvent.onJoin(this.clientName + " joined the server!");
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

                    if (response.equalsIgnoreCase("_/user_leave_")) { //This will notify the server that this client disconnects.
                        this.chatMessageEvent.onLeave(this.clientName + " left the server!", this);
                        break;
                    }

                    if (response.startsWith("/")) { //Check if the msg is a command.
                        if (response.contains(" ")) {
                            String[] args = response.split(" ");
                            this.chatMessageEvent.onCommand(this, response.substring(1, response.indexOf(' ')), Arrays.copyOfRange(args, 1, args.length));
                        } else {
                            this.chatMessageEvent.onCommand(this, response.substring(1), null);
                        }
                    } else {
                        this.chatMessageEvent.msgToAll(this.clientName + ": " + response);
                    }

                    this.server.getGui().printInGUI(this.clientName + ": " + response);
                    System.out.println(this.clientName + ": " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendChatMessage(String msg) {
        try {
            this.clientOutput.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.socket.close();
            this.clientInput.close();
            this.clientOutput.close();
            this.running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }
}
