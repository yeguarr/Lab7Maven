package command;

import dopFiles.User;

public class RemoveById extends Command {
    Integer id;

    public RemoveById(User user, Integer id) {
        super(user, Commands.REMOVE_BY_ID);
        this.id = id;
    }

    @Override
    public String toString() {
        return "RemoveById{" +
                "id=" + id +
                '}';
    }

    @Override
    public Integer returnObj() {
        return id;
    }
}