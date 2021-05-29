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
    private Nickname nickname;
    private Point2D position;
    private Point2D oldPosition;

    public Player(Nickname nickname, Point2D position) {
        this.nickname = nickname;
        this.position = position;
        this.oldPosition = this.position;
    }

    @Override
    public void draw(FXGraphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(this.nickname.toString(), (int) this.position.getX(), (int) this.position.getY() - 10);
        g2d.setColor(Color.BLUE);
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
        this.oldPosition = this.position;
        this.position = position;
    }

    public Point2D getOldPosition() {
        return oldPosition;
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
