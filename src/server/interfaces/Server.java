package server.interfaces;

import server.connection.ClientHandler;

public interface Server {
    /**
     * Send object to every client.
     * @param o Object to send.
     */
    void broadcastObject(Object o);

    /**
     * Disconnect a client from the server.
     * @param clientHandler Client to disconnect.
     */
    void disconnectClient(ClientHandler clientHandler);
}
