package client.game.player;

import client.interfaces.Drawable;
import client.interfaces.Updateble;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable, Updateble, Drawable {
    private static final long serialVersionUID = -192837465;
    private transient BufferedImage image;

    {
        try {
            image = ImageIO.read(Player.class.getResourceAsStream("/player/male.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private transient BufferedImage[][] playerBaseImages;

    private Nickname nickname;
    private Point2D position;
    private Color color;

    public Player(Nickname nickname, Point2D position, Color color) {
        this.nickname = nickname;
        this.position = position;
        this.color = color;
        getPlayerImages();
    }

    private void getPlayerImages() {
        try {
            //Player texture: 35x74
            //Spacing between images: x: 15, y: 11
            this.playerBaseImages = new BufferedImage[4][4];
            int width = 35;
            int height = 74;
            System.out.println("Loading images");
//            BufferedImage image = ImageIO.read(Player.class.getResourceAsStream("/player/maleCharacter.png"));
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    this.playerBaseImages[i][j] = image.getSubimage(j * 15, i * 11, width, height);
                }
            }
            System.out.println("Images loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(FXGraphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(this.nickname.toString(), (int) this.position.getX(), (int) this.position.getY() - 10);
        g2d.setColor(this.color);

        if (this.playerBaseImages != null) {
            System.out.println("Draw image");
            g2d.drawImage(playerBaseImages[0][0/*((int) this.position.getX() / 40) % playerBaseImages[0].length*/], (int) this.position.getX(), (int) this.position.getY(), null);
        }
        g2d.draw(new Ellipse2D.Double(this.position.getX(), this.position.getY(), 10, 10));
        g2d.setColor(Color.BLACK);
    }

    @Override
    public void update(double deltaTime) {

    }

    public Nickname getNickname() {
        return this.nickname;
    }

    public Point2D getPosition() {
        return this.position;
    }

    public void setNickname(Nickname nickname) {
        this.nickname = nickname;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return this.nickname.getNickname();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return nickname.getNickname().equals(player.nickname.getNickname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
