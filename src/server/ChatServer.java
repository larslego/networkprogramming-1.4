package server;

import commands.Command;
import events.ChatEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer implements ChatEvent, Runnable {
    private int port;
    private ServerSocket serverSocket;
    private ArrayList<ChatServerTask> chatServerTasks;

    private boolean running = true;

    private ServerGUI gui;

    public ChatServer(int port, ServerGUI application) {
        this.port = port;
        this.chatServerTasks = new ArrayList<>();
        this.gui = application;
    }

    @Override
    public void run() {
        start();
    }

    private void start() {
        try {
            this.serverSocket = new ServerSocket(this.port);

            while (this.running) {
                Socket client = this.serverSocket.accept();
                ChatServerTask chatServerTask = new ChatServerTask(client, this, this);
                Thread thread = new Thread(chatServerTask);
                thread.start();
                this.chatServerTasks.add(chatServerTask);
            }
        } catch (IOException e) {
            if (this.running) {
                this.gui.printInGUI("Could not start the server\n" + e.getMessage());
                System.out.println("Could not start server: " + e.getMessage());
            } else {
                this.gui.printInGUI("Server closed");
                System.out.println("Server closed");
            }
        }
    }

    public void stop() {
        this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.chatServerTasks.forEach(ChatServerTask::stop);
    }

    @Override
    public void msgToAll(String message) {
        for (ChatServerTask c : this.chatServerTasks) {
            c.sendChatMessage(message);
        }
    }

    public ServerGUI getGui() {
        return this.gui;
    }

    @Override
    public void onJoin(String msg) {
        msgToAll(msg);
        this.gui.printInGUI(msg);
    }

    @Override
    public void onLeave(String msg, ChatServerTask chatServerTask) {
        this.chatServerTasks.remove(chatServerTask);
        msgToAll(msg);
        System.out.println(msg);
        this.gui.printInGUI(msg);
    }

    @Override
    public boolean onCommand(ChatServerTask sender, String commandLabel, String[] args) {
        Command command = Command.getCommand(commandLabel.toUpperCase());
        switch (command) {
            case STOP:
                msgToAll("Server closing...");
                stop();
                return true;
            case SAVE:
                return true;
            case HELP:
                sender.sendChatMessage(Command.list());
                return true;
            case LIST:
                StringBuilder clientList = new StringBuilder();
                clientList.append("Online clients: ");
                for (ChatServerTask c : this.chatServerTasks) {
                    clientList.append("\n");
                    clientList.append(" - ");
                    clientList.append(c.getClientName());
                }
                sender.sendChatMessage(clientList.toString());
                return true;
            case KICK:
                return true;
            case BROADCAST:
                StringBuilder bc = new StringBuilder("[Broadcast]");
                for (String word : args) {
                    bc.append(" ");
                    bc.append(word);
                }
                msgToAll(bc.toString());
                return true;
            case NULL:
                sender.sendChatMessage("\'" + commandLabel + "\' does not exist. Type /help to see available commands.");
                return false;
            default:
                sender.sendChatMessage("\'" + commandLabel + "\' does not exist. Type /help to see available commands.");
                return false;
        }
    }
}
