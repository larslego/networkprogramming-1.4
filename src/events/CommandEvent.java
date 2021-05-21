package events;

import server.ChatServerTask;

public interface CommandEvent {
    boolean onCommand(ChatServerTask sender, String commandLabel, String[] args);
}
