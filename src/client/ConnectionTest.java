package client;

import client.connection.Connection;

import java.util.Scanner;

public class ConnectionTest {
    public static void main(String[] args) {
        Connection connection = new Connection("localhost", 4444, "Lars");
        Thread thread = new Thread(connection);
        thread.start(); //Also connects to the server

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        connection.sendObject("Test 1");
        connection.sendObject("Test 2");
        connection.sendObject("Test 3");

        while (true) {
            System.out.println("Send a message: ");
            String msg = scanner.nextLine();

            if (msg.equalsIgnoreCase("quit")) {
                connection.disconnect();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Disconnected from server");
                break;
            } else {
                connection.sendObject(msg);
            }
        }

        connection.disconnect();
    }
}
