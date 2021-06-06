package client.game.player;

import client.interfaces.Drawable;
import client.interfaces.Updateble;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable, Updateble, Drawable {
    //private static final long serialVersionUID = -192837465;
    private Nickname nickname;
    private Point2D position;
    private Color color;
    private Direction direction;

    public Player(Nickname nickname, Point2D position, Color color) {
        this.nickname = nickname;
        this.position = position;
        this.color = color;
        this.direction = Direction.SOUTH;
    }

    @Override
    public void draw(FXGraphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(this.nickname.toString(),
                (int) this.position.getX() - (25 - (this.nickname.getNickname().length() / 2)),
                (int) this.position.getY() - 85);
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
