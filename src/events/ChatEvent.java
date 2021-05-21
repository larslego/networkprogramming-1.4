package events;

public interface ChatEvent extends ChatMessageEvent, OnLeaveEvent, OnJoinEvent, CommandEvent {
}
