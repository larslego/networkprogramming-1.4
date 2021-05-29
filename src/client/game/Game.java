package client.game;

import client.connection.Connection;
import client.game.player.Player;
import client.interfaces.Updateble;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Game implements Updateble {
    private ResizableCanvas canvas;
    private FXGraphics2D g2d;
    private BorderPane borderPane;

    //Input
    private GameInputManager gameInputManager;

    //Player
    private Player player;
    public static Player[] playerList; //This list contains all players on the server.
    private Camera camera;

    //Connection
    private Connection connection;

    public Game(BorderPane borderPane) {
        this.borderPane = borderPane;
        this.canvas = new ResizableCanvas(g -> draw(), this.borderPane);
        this.g2d = new FXGraphics2D(canvas.getGraphicsContext2D());
        this.borderPane.setCenter(this.canvas);

        this.gameInputManager = new GameInputManager();
    }

    public void start(Player player, String hostname, int port) {
        this.player = player;
        playerList = new Player[] { this.player };
//        playerList = new Player[] {};
        this.connection = new Connection(hostname, port, player);
        this.camera = new Camera(this.canvas, g -> draw(), this.g2d);
        run();
    }

    public void run() {
        this.canvas.setFocusTraversable(true); //Enable keylisteners on canvas.
        this.canvas.setOnMouseMoved(e -> this.canvas.requestFocus());
        this.canvas.setOnKeyPressed(e -> this.gameInputManager.setKeyPressed(e.getCode()));
        this.canvas.setOnKeyReleased(e -> this.gameInputManager.setKeyReleased(e.getCode()));

        if (!this.connection.connect()) {
            System.out.println("Could not open a connection!");
            return;
        }

        new AnimationTimer() {
            long last = -1;
            @Override
            public void handle(long now) {
                if(last == -1)
                    last = now;
                update((now - last) / 1000000000.0);
                last = now;
                draw();
            }
        }.start();
    }

    public void draw() {
        this.g2d.clearRect(0, 0, (int) this.canvas.getWidth(), (int) this.canvas.getHeight());
        this.g2d.setBackground(Color.white);
        AffineTransform oldTransform = this.g2d.getTransform();

        //Camera movement, TODO: Not working yet
        if (this.camera != null) {
            this.g2d.setTransform(this.camera.getTransform((int) this.canvas.getWidth(), (int) this.canvas.getHeight()));
        }

        this.g2d.setTransform(oldTransform);
        this.g2d.setColor(Color.GREEN);
        this.g2d.fillRect(0, 0, 40, 40);
        this.g2d.setColor(Color.WHITE);

        //Draw every player on the server (including your own player).
        if (playerList != null) {
            AffineTransform newTransform = new AffineTransform();
            for (Player player : playerList) {
                if (player != null) {
                    //newTransform.translate(player.getPosition().getX(),
                    //       player.getPosition().getY());
                    player.draw(this.g2d);
                }
                //this.g2d.setTransform(newTransform);
                //this.g2d.translate(this.player.getPosition().getX(), this.player.getPosition().getY());
            }
        }

        this.g2d.setTransform(oldTransform);
    }

    public ResizableCanvas getCanvas() {
        return this.canvas;
    }

    @Override
    public void update(double deltaTime) {
        if (this.player != null) {
            int playerX = 0;
            int playerY = 0;
            int speed;

            if (this.gameInputManager.getKeysPressed().contains(KeyCode.SHIFT)) {
                speed = 2;
            } else {
                speed = 1;
            }

            if (this.gameInputManager.getKeysPressed().contains(KeyCode.W)) {
                playerY -= speed;
            }
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.S)) {
                playerY += speed;
            }
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.A)) {
                playerX -= speed;
            }
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.D)) {
                playerX += speed;
            }

            if (playerX != 0 || playerY != 0) {
                this.player.setPosition(new Point2D.Double(this.player.getPosition().getX() + playerX,
                        this.player.getPosition().getY() + playerY));
                System.out.println("Current player pos: " + this.player.getPosition());
                this.player.update(deltaTime);
                this.connection.sendObject(this.player);
                System.out.println("Player updated and sent to server.");
            }
            this.camera.setCenterPoint(this.player.getPosition());
            this.camera.update(deltaTime);
        }
    }

    public static void updatePlayerList(Player[] players) {
        playerList = players;
        System.out.println("New player list");
        for (Player player : players) {
            System.out.println("Player: " + player + ", " + player.getPosition() + "\t" + player.getOldPosition());
        }
        System.out.println("Updated players");
    }

    public void stop() {
        this.connection.disconnect();
    }
}
