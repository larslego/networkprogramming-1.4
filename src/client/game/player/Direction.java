package client.game.player;

public enum Direction {
    NORTH(3), EAST(2), SOUTH(0), WEST(1);

    private int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
