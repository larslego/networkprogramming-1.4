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
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    Object o = objectInputStream.readObject();
                    if (o instanceof Message) {
                        System.out.println("Received [Message Object]: " + ((Message) o).getMessage());
                    } else if (o instanceof String) {
                        System.out.println("Received [String]: " + o);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
