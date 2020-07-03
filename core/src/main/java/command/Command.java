package command;

import dopFiles.User;

import java.io.Serializable;

public class Command implements Serializable {
    Commands current;

    public User getUser() {
        return user;
    }

    User user;

    public Command(User user, Commands com) {
        current = com;
        this.user = user;
    }

    public Commands getCurrent() {
        return current;
    }

    public Object returnObj() {
        return null;
    }

    @Override
    public String toString() {
        return "Command{" +
                "current=" + current +
                '}';
    }
}