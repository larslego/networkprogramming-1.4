package client.game;

import client.interfaces.Updateble;
import javafx.scene.input.KeyCode;

import java.util.HashSet;

public class GameInputManager {
    private HashSet<KeyCode> keysPressed;

    public GameInputManager() {
        this.keysPressed = new HashSet<>();
    }

    public boolean isPressed(KeyCode keyCode) {
        return this.keysPressed.contains(keyCode);
    }

    public void setKeyPressed(KeyCode keyCode) {
        this.keysPressed.add(keyCode);
    }

    public void setKeyReleased(KeyCode keyCode) {
        this.keysPressed.remove(keyCode);
    }

    public HashSet<KeyCode> getKeysPressed() {
        return this.keysPressed;
    }
}
