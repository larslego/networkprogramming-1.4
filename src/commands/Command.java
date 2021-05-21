package commands;

public enum Command {
    NULL("null"),
    STOP("stop"),
    HELP("help"),
    LIST("list"),
    SAVE("save"),
    BROADCAST("broadcast"),
    KICK("kick");

    private String name;

    Command(String name) {
        this.name = name;
    }

    public static Command getCommand(String command) {
        Command cmd = Command.NULL;
        for (Command c : values()) {
            if (c.name.equalsIgnoreCase(command)) {
                cmd = c;
                break;
            }
        }
        return cmd;
    }

    public static String list() {
        StringBuilder list = new StringBuilder("Available commands:");
        for (Command cmd : values()) {
            if (cmd != NULL) {
                list.append("\n");
                list.append("/");
                list.append(cmd.toString());
            }
        }
        return list.toString();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
