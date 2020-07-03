package command;

import dopFiles.User;

public class ExecuteScript extends Command {
    String script;

    public ExecuteScript(User user, String script) {
        super(user, Commands.EXECUTE_SCRIPT);
        this.script = script;
    }

    @Override
    public String toString() {
        return "ExecuteScript{" +
                "script='" + script + '\'' +
                '}';
    }

    @Override
    public String returnObj() {
        return script;
    }
}