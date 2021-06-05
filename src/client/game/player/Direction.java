package client.game.player;

import java.io.Serializable;

public enum Direction implements Serializable {
    NORTH(3), EAST(2), SOUTH(0), WEST(1);

    private int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
