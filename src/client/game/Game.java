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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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

    //background picture
    BufferedImage mapPicture;

    private double timerValue = 0;

    public Game(BorderPane borderPane, Scene scene) {
        this.canvas = new ResizableCanvas(g -> draw(), borderPane);
        this.borderPane = borderPane;
        this.scene = scene;
        this.g2d = new FXGraphics2D(canvas.getGraphicsContext2D());
        this.borderPane.setCenter(this.canvas);

        this.gameInputManager = new GameInputManager();

        try {
            this.mapPicture = ImageIO.read(getClass().getResourceAsStream("/map/netwerkMap.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.g2d.setBackground(Color.black);
        this.g2d.translate(this.canvas.getWidth() / 2, this.canvas.getHeight() / 2);
        this.g2d.setColor(Color.GREEN);
        this.g2d.fillRect(0, 0, 40, 40); //Top left of square is 0, 0.
        this.g2d.setColor(Color.WHITE);

        //place map
        this.g2d.drawImage(this.mapPicture, -this.mapPicture.getWidth()/2, -this.mapPicture.getHeight()/2, null);

        //Draw every player on the server (including your own player).
        if (playerList != null) {
            AffineTransform newTransform = new AffineTransform();
            for (Player player : playerList) {
                if (player != null) {
                    if (player.equals(this.player)) {
                        newTransform.translate(-player.getPosition().getX(), -player.getPosition().getY());
                    }
                    if (this.playerSprite != null) {
                        if (player.getDirection() == Direction.NORTH || player.getDirection() == Direction.SOUTH) {
                            g2d.drawImage(PlayerSprite.getBodyTexture(player.getDirection().getValue(), ((int) Math.abs(player.getPosition().getY() / 20) % 4)),
                                    (int) player.getPosition().getX() - (this.playerSprite.getBodyWidth() / 2),
                                    (int) player.getPosition().getY() - (this.playerSprite.getBodyHeight()),
                                    null);
                        } else {
                            g2d.drawImage(PlayerSprite.getBodyTexture(player.getDirection().getValue(), ((int) Math.abs(player.getPosition().getX() / 20) % 4)),
                                    (int) player.getPosition().getX() - (this.playerSprite.getBodyWidth() / 2),
                                    (int) player.getPosition().getY() - (this.playerSprite.getBodyHeight()),
                                    null);
                        }
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
            double speed = 1 * (deltaTime * 100);

            //Set different speed when player is sprinting.
            if (this.gameInputManager.getKeysPressed().contains(KeyCode.SHIFT)) {
                speed = 2 * (deltaTime * 100);
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

            movePlayer(playerX, playerY, deltaTime);

            this.timerValue += deltaTime;

            if (this.timerValue > 0.2) {
                this.connection.sendObject(this.player.getPosition());
                this.connection.sendObject(this.player.getDirection());
                this.timerValue = 0;
            }
        }
    }

    public void movePlayer(int playerX, int playerY, double deltaTime) {
        if (this.player.getPosition().getX() + 25 > (this.mapPicture.getWidth() / 2.0)) { //Right
            this.player.setPosition(new Point2D.Double((this.mapPicture.getWidth() / 2.0) - 27, this.player.getPosition().getY()));
        } else if (this.player.getPosition().getX() - 25 < -(this.mapPicture.getWidth() / 2.0)) { //Left
            this.player.setPosition(new Point2D.Double((-this.mapPicture.getWidth() / 2.0) + 27, this.player.getPosition().getY()));
        }

        if (this.player.getPosition().getY() + 25 > (this.mapPicture.getHeight() / 2.0)) { //Bottom
            this.player.setPosition(new Point2D.Double(this.player.getPosition().getX(), (this.mapPicture.getHeight() / 2.0) - 27));
        } else if (this.player.getPosition().getY() - 40 < -(this.mapPicture.getHeight() / 2.0)) { //Top
            this.player.setPosition(new Point2D.Double(this.player.getPosition().getX(), -(this.mapPicture.getHeight() / 2.0) + 42));
        }

        if (playerX != 0 || playerY != 0) {
            this.player.setPosition(new Point2D.Double(this.player.getPosition().getX() + playerX,
                    this.player.getPosition().getY() + playerY));
            this.player.update(deltaTime);
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
                if (lPlayer.equals(oPlayer) && !lPlayer.equals(this.player)) {
                    oPlayer.setPosition(lPlayer.getPosition());
                    oPlayer.setDirection(lPlayer.getDirection());
                }
            }
        }

        playerList = old.toArray(new Player[old.size()]);
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
