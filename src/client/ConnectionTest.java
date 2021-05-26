package client;

import client.connection.Connection;

import java.util.Scanner;

public class ConnectionTest {
    public static void main(String[] args) {
        Connection connection = new Connection("localhost", 4444, "Lars");
        Thread thread = new Thread(connection);
        thread.start(); //Also connects to the server
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Send a message: ");
            String msg = scanner.nextLine();

            if (msg.equalsIgnoreCase("quit")) {
                break;
            } else {
                connection.sendObject(msg);
            }
        }

        System.out.println("Disconnecting...");
        connection.disconnect();
        try {
            System.out.println("Joining threads");
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected from server");
    }
}
