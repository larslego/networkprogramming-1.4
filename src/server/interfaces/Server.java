package server.interfaces;

import server.connection.ClientHandler;

public interface Server {
    /**
     * Disconnect a client from the server.
     * @param clientHandler Client to disconnect.
     */
    void kickClient(ClientHandler clientHandler);

    /**
     * This method is called when a object is received.
     * @param o Object that is received.
     */
    void onObjectReceived(Object o);
}
