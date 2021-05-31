package server.connection;

import client.game.player.Nickname;
import client.game.player.Player;
import server.Server;
import server.enums.LogType;
import server.interfaces.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wait for client to join. If a client joins it will create a new ClientHandler on a separate thread.
 */
public class Connection implements Runnable, Client {
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final List<ClientHandler> clients = new ArrayList<>();

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
            if (e instanceof SocketException) {
                stop();
                return;
            }
            e.printStackTrace();
            System.out.println("You can ignore this exception if you stopped the server.");
        }
    }

    public List<ClientHandler> getClients() {
        return this.clients;
    }

    public synchronized void stop() {
        this.running = false;
        Iterator<ClientHandler> iterator = this.clients.iterator();
        while (iterator.hasNext()) {
            ClientHandler c = iterator.next();
            c.stop();
        }
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
            if (!this.serverSocket.isClosed()) {
                clientHandler.getObjectOutputStream().writeObject(o);
                clientHandler.getObjectOutputStream().flush();
            }
        } catch (IOException e) {
            stop();
            e.printStackTrace();
            Server.appendLog(LogType.ERROR, e.getMessage());
        }
    }

    @Override
    public void onConnect(ClientHandler clientHandler, Player player) {
        this.clients.add(clientHandler);
        Server.addPlayer(player);
        Object[] src = Server.getPlayers().toArray();
        Player[] players = Arrays.copyOf(src, src.length, Player[].class);
        broadcastObject(players);
        Server.appendLog(LogType.INFO, clientHandler.getNickname() + " joined the server."); //Send join message to server.
        this.sendObject(clientHandler, "Welcome to the server"); //Send welcome message to the client.
    }

    @Override
    public synchronized void broadcastObject(Object o) {
        if (o instanceof String) {
            Server.appendLog(LogType.INFO, o.toString());
        }
        Iterator<ClientHandler> iterator = this.clients.iterator();
        while (iterator.hasNext()) {
            sendObject(iterator.next(), o);
        }
    }

    @Override
    public void broadcastObjectToOthers(ClientHandler clientHandler, Object o) {
        Iterator<ClientHandler> iterator = this.clients.iterator();
        while (iterator.hasNext()) {
            ClientHandler c = iterator.next();
            if (!c.equals(clientHandler)) {
                sendObject(c, o);
            }
        }
    }

    @Override
    public void onDisconnect(ClientHandler clientHandler, Player player) {
        this.clients.remove(clientHandler);
        Server.removePlayer(player);

        if (clientHandler.getSocket().isConnected()) {
            broadcastObject(clientHandler.getNickname() + " left the server.");
            try {
                clientHandler.getSocket().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
