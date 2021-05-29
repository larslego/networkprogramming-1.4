package client;

import client.connection.Connection;
import client.game.player.Nickname;
import client.game.player.Player;

import java.awt.geom.Point2D;
import java.util.Scanner;

public class ConnectionTest {
    public static void main(String[] args) {
        Connection connection = new Connection("localhost", 4444, new Player(new Nickname("Lars"), new Point2D.Double(0, 0)));
        Thread thread = new Thread(connection);
        if (!connection.connect()) {
            return;
        }
        //thread.start(); //Also connects to the server
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
