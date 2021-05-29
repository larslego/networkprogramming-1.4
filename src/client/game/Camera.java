package client.game;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.ScrollEvent;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.Resizable;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Camera {
	private Point2D centerPoint;
    private double zoom = 1;
    private double rotation = 0;
    private Canvas canvas;
    private Resizable resizable;
    private FXGraphics2D g2d;
    private GameInputManager inputManager;

    public Camera(Canvas canvas, Resizable resizable, FXGraphics2D g2d) {
        this.canvas = canvas;
        this.resizable = resizable;
        this.g2d = g2d;
        this.inputManager = new GameInputManager();
        this.centerPoint = new Point2D.Double(-79 * 32,-38 * 32);

        canvas.setOnScroll(this::mouseScroll);
        canvas.setOnKeyPressed(e -> this.inputManager.setKeyPressed(e.getCode()));
        canvas.setOnKeyReleased(e -> this.inputManager.setKeyReleased(e.getCode()));
    }

    public AffineTransform getTransform(int windowWidth, int windowHeight)  {
        AffineTransform tx = new AffineTransform();
        tx.translate(windowWidth / 2, windowHeight / 2);
        tx.scale(this.zoom, this.zoom);
        tx.translate(this.centerPoint.getX(), this.centerPoint.getY());
        tx.rotate(this.rotation);
        return tx;
    }

    private void mouseScroll(ScrollEvent e) {
        this.zoom *= (1 + e.getDeltaY()/250.0f);
        this.resizable.draw(this.g2d);
    }

    public void update(double deltatime) {
        //Check if the zoom level is within limits.
        if (this.zoom <= 0.2) {
            this.zoom = 0.2;
        } else if (this.zoom >= 2.0) {
            this.zoom = 2.0;
        }
    }

    public void setCenterPoint(Point2D centerPoint) {
        this.centerPoint = centerPoint;
    }
}
