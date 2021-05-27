package client.interfaces;

public interface Client {
    /**
     * Connect to server.
     */
    boolean connect();

    /**
     * Disconnect from server.
     */
    void disconnect();

    /**
     * Send object to server.
     */
    void sendObject(Object o);
}
