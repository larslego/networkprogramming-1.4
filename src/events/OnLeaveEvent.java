package events;

import server.ChatServerTask;

public interface OnLeaveEvent {
    void onLeave(String msg, ChatServerTask chatServerTask);
}
