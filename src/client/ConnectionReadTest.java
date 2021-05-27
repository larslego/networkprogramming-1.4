package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReadTest {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            System.out.println("Waiting for client");
            Socket socket = serverSocket.accept();
            System.out.println("Connected");
            //We NEED to create an output- and inputstream in this order, otherwise we will create a deadlock.
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Object o = objectInputStream.readObject();
                if (o instanceof String) {
                    System.out.println("Received [String]: " + o);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
