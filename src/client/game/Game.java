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
    private final ResizableCanvas canvas;
    private final FXGraphics2D g2d;

    //Input
    private final GameInputManager gameInputManager;

    //Player
    private Player player;
    public static Player[] playerList; //This list contains all players on the server.

    //Connection
    private Connection connection;

    public Game(BorderPane borderPane) {
        this.canvas = new ResizableCanvas(g -> draw(), borderPane);
        this.g2d = new FXGraphics2D(canvas.getGraphicsContext2D());
        borderPane.setCenter(this.canvas);

        this.gameInputManager = new GameInputManager();
    }

    public void start(Player player, String hostname, int port) {
        this.player = player;
        playerList = new Player[] { this.player };
        this.connection = new Connection(hostname, port, player);
        run();
    }

    public void run() {
        this.canvas.setFocusTraversable(true); //Enable keylisteners on canvas.
        this.canvas.setOnMouseMoved(e -> this.canvas.requestFocus());
        this.canvas.setOnKeyPressed(e -> this.gameInputManager.setKeyPressed(e.getCode()));
        this.canvas.setOnKeyReleased(e -> this.gameInputManager.setKeyReleased(e.getCode()));

        if (!this.connection.connect()) {
            System.out.println("Could not open a connection!");
            //TODO: Add Alert to warn user about not being able to connect.
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

        this.g2d.translate(this.canvas.getWidth() / 2, this.canvas.getHeight() / 2);
        this.g2d.setColor(Color.GREEN);
        this.g2d.fillRect(0, 0, 40, 40); //Top left of square is 0, 0.
        this.g2d.setColor(Color.WHITE);

        //Draw every player on the server (including your own player).
        if (playerList != null) {
            AffineTransform newTransform = new AffineTransform();
            for (Player player : playerList) {
                if (player != null) {
                    if (player.equals(this.player)) {
                        newTransform.translate(-player.getPosition().getX(), -player.getPosition().getY());
                    }
                    player.draw(this.g2d);
                }
            }
            this.g2d.setTransform(newTransform);
        }
    }

    public ResizableCanvas getCanvas() {
        return this.canvas;
    }

    @Override
    public void update(double deltaTime) {
        if (this.player != null) {
            int playerX = 0;
            int playerY = 0;
            int speed = 1;

            //Set different speed when player is sprinting.
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.SHIFT)) {
                speed = 2;
            }

            //Check direction controls.
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
                this.player.update(deltaTime);
                this.connection.sendObject(this.player);
            }
        }
    }

    public static void updatePlayerList(Player[] players) {
        playerList = players;
    }

    public void stop() {
        this.connection.disconnect();
    }
}
