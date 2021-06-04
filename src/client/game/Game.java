package client.game;

import client.connection.Connection;
import client.game.player.Direction;
import client.game.player.Player;
import client.game.player.PlayerSprite;
import client.interfaces.Updateble;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;
import javafx.scene.control.TextField;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Game implements Updateble {
    private BorderPane borderPane;
    private final ResizableCanvas canvas;
    private final FXGraphics2D g2d;

    //Input
    private final GameInputManager gameInputManager;

    //Player
    private Player player;
    public Player[] playerList; //This list contains all players on the server.
    private PlayerSprite playerSprite;

    //Chatbox
    private Chatbox chatbox;
    private Scene scene;

    //Connection
    private Connection connection;

    public Game(BorderPane borderPane, Scene scene) {
        this.borderPane = borderPane;
        this.scene = scene;
        this.canvas = new ResizableCanvas(g -> draw(), this.borderPane);
        this.g2d = new FXGraphics2D(canvas.getGraphicsContext2D());
        this.borderPane.setCenter(this.canvas);

        this.gameInputManager = new GameInputManager();
        this.playerSprite = new PlayerSprite();
    }

    public void start(Player player, String hostname, int port) {
        this.player = player;
        playerList = new Player[] { this.player };
        this.connection = new Connection(hostname, port, player, this);

        //chat
        this.chatbox = new Chatbox(this.connection, this.player.getNickname());
        this.borderPane.setRight(this.chatbox.getChatVBox());

        run();
    }

    public void run() {
        if (!this.connection.connect()) {
            System.out.println("Could not open a connection!");
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Could not connect to the server.",
                    ButtonType.OK);
            alert.show();
            return;
        }

        this.canvas.setFocusTraversable(true); //Enable keylisteners on canvas.
        this.canvas.setOnMouseMoved(e -> this.canvas.requestFocus());
        this.canvas.setOnKeyPressed(e -> this.gameInputManager.setKeyPressed(e.getCode()));
        this.canvas.setOnKeyReleased(e -> this.gameInputManager.setKeyReleased(e.getCode()));

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
                    if (this.playerSprite != null) {
                        g2d.drawImage(PlayerSprite.getBodyTexture(player.getDirection().getValue(), 0),
                                (int) player.getPosition().getX() - (this.playerSprite.getBodyWidth() / 2),
                                (int) player.getPosition().getY() - (this.playerSprite.getBodyHeight()),
                                null);
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
        if (this.gameInputManager.isPressed(KeyCode.ENTER)) {
            if (this.borderPane != null) {
                TextField chatField = (TextField) this.scene.lookup("#chatInputField");
                chatField.requestFocus();
                this.gameInputManager.getKeysPressed().clear();
            }
        }

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
                this.player.setDirection(Direction.NORTH);
                playerY -= speed;
            }
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.S)) {
                this.player.setDirection(Direction.SOUTH);
                playerY += speed;
            }
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.A)) {
                this.player.setDirection(Direction.WEST);
                playerX -= speed;
            }
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.D)) {
                this.player.setDirection(Direction.EAST);
                playerX += speed;
            }

            Point2D oldPos = this.player.getPosition();
            if (playerX != 0 || playerY != 0) {
                this.player.setPosition(new Point2D.Double(this.player.getPosition().getX() + playerX,
                        this.player.getPosition().getY() + playerY));
                this.player.update(deltaTime);
            }

            //TODO: Improve when to send player position.
//            if (!this.player.getPosition().equals(oldPos)) {
                this.connection.sendObject(this.player.getPosition());
//            }
        }
    }

    public void updatePlayerList(Player[] players) {
        ArrayList<Player> old = new ArrayList<>(Arrays.asList(playerList));
        ArrayList<Player> list = new ArrayList<>(Arrays.asList(players));
        for (Player p : list) {
            if (old.size() < list.size()) {
                if (!old.contains(p)) {
                    System.out.println("Adding player");
                    old.add(p);
                }
            } else if (old.size() > list.size()){
                System.out.println("removing player");
                old.remove(p);
            }
        }

        for (Player lPlayer : list) {
            for (Player oPlayer : old) {
                if (lPlayer.equals(oPlayer)) {
                    oPlayer.setPosition(lPlayer.getPosition());
                }
            }
        }

        playerList = old.toArray(new Player[old.size()]);

//        for (int i = 0; i <= players.length - 1; i++) {
////            if (players[i].equals(this.player)) {
////                System.out.println("First if");
////                players[i] = this.player;
////            }
////
////            if (players[i].equals(playerList[i])) {
////                System.out.println("Second if");
////                playerList[i].setPosition(players[i].getPosition());
////            }
//
//            for (int j = 0; j <= playerList.length - 1; j++) {
//                if (players[i].equals(playerList[j])) { //Add player if necessary
//                    System.out.println("Player: " + playerList[j].getNickname() + ", " + playerList[j].getPosition());
//                    playerList[j].setPosition(players[i].getPosition());
//                } else {
//                    list.add(players[i]);
//                }
//            }
//
//            System.out.println("PlayerList: " + playerList.length);
//            System.out.println("Players: " + players.length);
//        }
//        playerList = (Player[]) list.toArray();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void stop() {
        if (this.connection != null) {
            this.connection.disconnect();
        }
    }
}
