package server.connection;

import client.game.player.Direction;
import client.game.player.Player;
import server.Server;
import server.enums.LogType;
import server.interfaces.Client;

import java.awt.geom.Point2D;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Read from and send to client.
 */
public class ClientHandler implements Runnable, server.interfaces.Server {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private Player player;

    private boolean running = true;

    private Client client;

    public ClientHandler(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, Client client) {
        try {
            this.socket = socket;
            this.objectOutputStream = oos;
            this.objectInputStream = ois;
            this.client = client;

            //Retrieve player object
            Object o = this.objectInputStream.readUnshared();
            if (o instanceof Player) {
                this.player = (Player) o;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Server.appendLog(LogType.ERROR, e.getMessage());
        }
    }

    @Override
    public void run() {
        this.client.onConnect(this, this.player);
        while (this.running) {
            try {
                if (this.socket.isClosed()) {
                    stop();
                } else {
                    Object o = this.objectInputStream.readObject(); //Read object
                    onObjectReceived(o); //Fire onObjectReceived event
                }
            } catch (IOException | ClassNotFoundException | NullPointerException e) { //Client disconnect on client side
                if (e instanceof SocketException || e instanceof EOFException) {
                    stop();
                    break;
                }
                System.out.println(getClass().getName() + e.getCause() + " in run method.");
                kickClient(this);
                e.printStackTrace();
                Server.appendLog(LogType.ERROR, e.getMessage());
            }
        }
    }

    public void stop() {
        Server.appendLog(LogType.ERROR, this.player.getNickname() + " lost connection");
        this.client.onDisconnect(this, this.player);
    }

    public String getNickname() {
        return this.player.getNickname().getNickname();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return this.objectOutputStream;
    }

    @Override
    public void kickClient(ClientHandler clientHandler) {
        clientHandler.stop();
    }

    @Override
    public synchronized void onObjectReceived(Object o) {
        if (o instanceof String) { //Check what type of object we receive
            if (((String) o).equalsIgnoreCase("quit")) {
                this.client.broadcastObjectToOthers(this, this.player.getNickname() + " left the server");
                Server.appendLog(LogType.INFO, this.player.getNickname() + " left the server.");
                this.running = false;
            } else if (((String) o).startsWith("/")) { //User sent a command
                Server.appendLog(LogType.INFO, o.toString());
            } else {
                this.client.broadcastObject(o);
            }
            Server.appendLog(LogType.INFO, (String)o);
        } else if (o == null || o.equals(-1)) { //Client disconnects
            this.client.onDisconnect(this, this.player);
            this.running = false;
        } else if (o instanceof Player) { //Player object
            Iterator<Player> i = Server.getPlayers().iterator();
            while (i.hasNext()) {
                Player player = i.next();
                if (player.getNickname().getNickname().equalsIgnoreCase(((Player) o).getNickname().getNickname())) {
                    synchronized (this) {
                        Server.removePlayer(player);
                        Server.addPlayer((Player) o);
                        Object[] src = Server.getPlayers().toArray();
                        Player[] players = Arrays.copyOf(src, src.length, Player[].class);
                        try {
                            this.objectOutputStream.reset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.client.broadcastObject(players);
                    }
                }
            }
        } else if (o instanceof Point2D) { //Player position
            Server.getPlayers().get(Server.getPlayers().indexOf(this.player)).setPosition((Point2D) o);
            synchronized (this) {
                Object[] src = Server.getPlayers().toArray();
                Player[] players = Arrays.copyOf(src, src.length, Player[].class);
                try {
                    this.objectOutputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.client.broadcastObject(players);
            }
        }  else if (o instanceof Direction) { //Player position
            Server.getPlayers().get(Server.getPlayers().indexOf(this.player)).setDirection((Direction) o);
            synchronized (this) {
                Object[] src = Server.getPlayers().toArray();
                Player[] players = Arrays.copyOf(src, src.length, Player[].class);
                try {
                    this.objectOutputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.client.broadcastObject(players);
            }
        } else {
            Server.appendLog(LogType.ERROR, "Received invalid object!");
        }
    }
}
