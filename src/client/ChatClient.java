package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient implements Runnable {
    //Server info
    private String hostname;
    private int port;
    private Socket socket;

    //Client
    private String nickname;
    private DataInputStream clientInput;
    private DataOutputStream clientOutput;
    private ChatClientTask chatClientTask;

    //Gui
    private ClientGUI gui;

    public ChatClient(String hostname, int port, String nickname, ClientGUI gui) {
        this.hostname = hostname;
        this.port = port;
        this.nickname = nickname;
        this.gui = gui;
    }

    public void start() {
        try {
            this.socket = new Socket(hostname, port);
            this.clientInput = new DataInputStream(this.socket.getInputStream());
            this.clientOutput = new DataOutputStream(this.socket.getOutputStream());

            String serverResponse = this.clientInput.readUTF();
            this.gui.printInGUI("Server: " + serverResponse);
            System.out.println("Server: " + serverResponse);
            this.clientOutput.writeUTF(this.nickname); //Send nickname to server

        } catch (IOException e) {
            this.gui.printInGUI("Could not connect to server\n" + e.getMessage());
            System.out.println("Could not connect to server: " + e.getMessage());
        }

        this.chatClientTask = new ChatClientTask(this.socket, this);
        Thread thread = new Thread(this.chatClientTask);
        thread.start();
    }

    public void chat(String msg) {
        try {
            this.clientOutput.writeUTF(msg);
            this.clientOutput.flush();
        } catch (IOException e) {
            System.out.println("Could not send message: " + e.getMessage());
        }
    }

    public void close() {
        try {
            this.clientOutput.writeUTF("_/user_leave_");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.chatClientTask.setRunning(false);

        try {
            this.clientInput.close();
            this.clientOutput.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ClientGUI getGui() {
        return this.gui;
    }

    @Override
    public void run() {

    }
}
