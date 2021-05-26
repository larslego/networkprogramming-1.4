package server.connection;

import client.game.player.Nickname;
import server.Server;
import server.enums.LogType;
import server.interfaces.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wait for client to join. If a client joins it will create a new ClientHandler on a separate thread.
 */
public class Connection implements Runnable, Client {
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<ClientHandler> clients = new ArrayList<>();

    private boolean running = true;
    private final int maxPlayerCount = 8;

    public Connection(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(this.maxPlayerCount);
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            Server.appendLog(LogType.INFO, "Started server on port " + this.port + ".");
            while (this.running) {
                Socket client = this.serverSocket.accept();
                if (this.clients.size() <= this.maxPlayerCount) { //Allow the user to join the server.
                    ClientHandler clientHandler = new ClientHandler(client,
                            new ObjectOutputStream(client.getOutputStream()),
                            new ObjectInputStream(client.getInputStream()),
                            this);
                    this.threadPool.execute(clientHandler);
                } else { //Decline user and send a string to tell the user that the server is full.
                    ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                    try {
                        Object o = objectInputStream.readObject();
                        if (o instanceof Nickname) {
                            Server.appendLog(LogType.INFO, ((Nickname) o).getNickname() + " tried to join the server, but the server is full.");
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Object received from client is invalid.");
                        Server.appendLog(LogType.ERROR, "Object received from client is invalid.");
                    }
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                    objectOutputStream.writeObject("Server full!");
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    client.close();
                }
            }

            //Disconnect every client
            for (ClientHandler clientHandler : this.clients) {
                sendObject(clientHandler, null);
            }
            //Close threads
            this.threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("You can ignore this exception if you stopped the server.");
        }
    }

    public List<ClientHandler> getClients() {
        return this.clients;
    }

    public void stop() {
        this.running = false;
        this.clients.forEach(ClientHandler::stop);
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("You can ignore this exception if you stopped the server.");
        }
    }

    @Override
    public void sendObject(ClientHandler clientHandler, Object o) {
        try {
            clientHandler.getObjectOutputStream().writeObject(o);
            clientHandler.getObjectOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            Server.appendLog(LogType.ERROR, e.getMessage());
        }
    }

    @Override
    public void onConnect(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
        Server.appendLog(LogType.INFO, clientHandler.getNickname() + " joined the server.");
        this.sendObject(clientHandler, "Welcome to the server");
    }

    @Override
    public void onDisconnect(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
        Server.appendLog(LogType.INFO, clientHandler.getNickname() + " has left the server.");
    }
}