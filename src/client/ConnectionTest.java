package client;

import client.connection.Connection;

public class ConnectionTest {
    public static void main(String[] args) {
        Connection connection = new Connection("localhost", 4444, "Lars");
        connection.connect();
        connection.sendObject("First string sent");
        connection.sendObject("Second string sent");
    }
}
