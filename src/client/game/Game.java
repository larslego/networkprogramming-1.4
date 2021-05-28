package client.game;

import client.interfaces.Updateble;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.BorderPane;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Game implements Updateble {
    private ResizableCanvas canvas;
    private FXGraphics2D g2d;
    private BorderPane borderPane;

    //Input
    private GameInputManager gameInputManager;

    public Game(BorderPane borderPane) {
        this.borderPane = borderPane;
        this.canvas = new ResizableCanvas(g -> draw(), this.borderPane);
        this.g2d = new FXGraphics2D(canvas.getGraphicsContext2D());
        this.borderPane.setCenter(this.canvas);

        this.gameInputManager = new GameInputManager();
    }

    public void run() {
        this.canvas.setFocusTraversable(true); //Enable keylisteners on canvas.
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

        this.g2d.setColor(Color.blue);
        this.g2d.draw(new Rectangle2D.Double(100,100,100,100));
    }

    public ResizableCanvas getCanvas() {
        return this.canvas;
    }

    @Override
    public void update(double deltaTime) {

    }
}
