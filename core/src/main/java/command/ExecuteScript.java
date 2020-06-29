package command;

public class ExecuteScript extends Command {
    String script;

    public ExecuteScript(String script) {
        super(Commands.EXECUTE_SCRIPT);
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