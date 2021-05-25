package server.connection;

import server.Server;
import server.enums.LogType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connection implements Runnable{
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    private boolean running = true;
    private final int maxPlayerCount = 8;

    public Connection(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(this.maxPlayerCount);
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            Server.appendLog(LogType.INFO, "Started server on port " + this.port + ".");
            int playerCount = 0;
            while (this.running) {
                if (playerCount <= this.maxPlayerCount) {
                    Socket client = this.serverSocket.accept();
                    this.threadPool.execute(new ClientHandler(client));
                    playerCount++;
                }
            }

            this.threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
