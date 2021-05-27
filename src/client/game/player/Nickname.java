package client.game.player;

import java.io.Serializable;

public class Nickname implements Serializable {
    private String nickname;

    public Nickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() { return this.nickname; }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
