package server.interfaces;

import server.connection.ClientHandler;

import java.util.List;

public interface Client {
    /**
     * This method is called when a client connects.
     */
    void onConnect(ClientHandler clientHandler);

    /**
     * This method is called when a client disconnects.
     */
    void onDisconnect(ClientHandler clientHandler);

    /**
     * This method is used to send an object to the client.
     * @param o Object to be sent.
     */
    void sendObject(ClientHandler clientHandler, Object o);

    /**
     * Send object to every client.
     * @param o Object to send.
     */
    void broadcastObject(Object o);
}
