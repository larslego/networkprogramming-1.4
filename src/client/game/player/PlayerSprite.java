package client.game.player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PlayerSprite {
    private BufferedImage body;
    private static BufferedImage[][] bodies;
    private int bodyHeight;
    private int bodyWidth;

    public PlayerSprite() {
        try {
            this.body = ImageIO.read(Player.class.getResourceAsStream("/player/male.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bodies = new BufferedImage[4][4];
        getBodyTextures();
    }

    private void getBodyTextures() {
        this.bodyWidth = 50;
        this.bodyHeight = 85;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                bodies[i][j] = this.body.getSubimage(j * this.bodyWidth, i * this.bodyHeight, this.bodyWidth, this.bodyHeight);
            }
        }
    }

    public static BufferedImage getBodyTexture(int direction, int index) {
        return bodies[direction][index];
    }

    public int getBodyHeight() {
        return this.bodyHeight;
    }

    public int getBodyWidth() {
        return this.bodyWidth;
    }
}
