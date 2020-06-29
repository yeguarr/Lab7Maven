package program;

import command.*;
import dopFiles.*;
import exceptions.EndOfFileException;
import exceptions.FailedCheckException;

import java.time.ZonedDateTime;

/**
 * Класс - обработчик команд с консоли
 */

public class CommanderClient {
    public static Checker<Boolean> boolCheck = (Boolean B) -> {
        if (B != null) return B;
        else throw new FailedCheckException();
    };

    /**
     * Обработка команд, вводимых с консоли
     */
    public static Command switcher(String s1, String s2) throws EndOfFileException {
        switch (s1) {
            case ("help"):
                return new Command(Commands.HELP);
            case ("info"):
                return new Command(Commands.INFO);
            case ("show"):
                return new Command(Commands.SHOW);
            case ("add"):
                return add(s2);
            case ("update"):
                return update(s2);
            case ("remove_by_id"):
                return removeById(s2);
            case ("clear"):
                return new Command(Commands.CLEAR);
            case ("execute_script"):
                return new ExecuteScript(s2);
            case ("exit"):
                return new Command(Commands.EXIT);
            case ("add_if_min"):
                return addIfMin(s2);
            case ("remove_greater"):
                return removeGreater(s2);
            case ("remove_lower"):
                return removeLower(s2);
            case ("average_of_distance"):
                return new Command(Commands.AVERAGE_OF_DISTANCE);
            case ("min_by_creation_date"):
                return new Command(Commands.MIN_BY_CREATION_DATE);
            case ("print_field_ascending_distance"):
                return new Command(Commands.PRINT_FIELD_ASCENDING_DISTANCE);
            default:
                Writer.writeln("Такой команды нет");
        }
        return null;
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static Command removeLower(String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(Commands.REMOVE_LOWER, newRoute);
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static Command removeGreater(String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(Commands.REMOVE_GREATER, newRoute);
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static Command addIfMin(String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(Commands.ADD_IF_MIN, newRoute);
    }

    /**
     * Удаляет все элементы по его id
     */
    public static Command removeById(String s) throws EndOfFileException {
        int id;
        try {
            id = Route.idCheck.checker(Integer.parseInt(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = Console.handlerI("Введите int id: ", Route.idCheck);
        }
        return new RemoveById(id);
    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static Command update(String s) throws EndOfFileException {
        int id;
        try {
            id = Route.idCheck.checker(Integer.parseInt(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = Console.handlerI("Введите int id: ", Route.idCheck);
        }
        String name = Console.handlerS("Введите String: name", Route.nameCheck);
        Route newRoute = toAddWithoutId(name);
        newRoute.setId(id);
        return new CommandWithObj(Commands.UPDATE, newRoute);
    }

    /**
     * Добавляет элемент в список
     */
    public static Command add(String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(Commands.ADD, newRoute);
    }

    public static Route toAddWithoutId(String s) throws EndOfFileException {
        Route route = new Route();
        route.setId(null);
        try {
            Route.nameCheck.checker(s);
            Writer.writeln("Поле name: " + s);
        } catch (FailedCheckException e) {
            s = Console.handlerS("Введите String name, диной больше 0: ", Route.nameCheck);
        }
        route.setName(s);

        Writer.writeln("Ввoд полей program.Coordinates");
        int cx = Console.handlerI("      Введите int x, не null: ", Coordinates.xCheck);
        Long cy = Console.handlerL("     Введите Long y, величиной больше -765: ", Coordinates.yCheck);
        route.setCoordinates(new Coordinates(cx, cy));

        ZonedDateTime creationTime = ZonedDateTime.now();
        route.setCreationDate(creationTime);

        Writer.writeln("Ввoд полей program.Location to");
        Long x = Console.handlerL("     Введите Long x, не null: ", Location.xyzCheck);
        long y = Console.handlerL("     Введите long y, не null: ", Location.xyzCheck);
        long z = Console.handlerL("     Введите long z, не null: ", Location.xyzCheck);
        String name = Console.handlerS("     Введите поле name, длиной меньше 867: ", Location.nameCheck);
        route.setTo(new Location(x, y, z, name));

        Writer.writeln("Является ли From null'ом?");
        if (!Console.handlerB("     Введите Bool: ", boolCheck)) {
            Writer.writeln("Ввoд полей program.Location from");
            x = Console.handlerL("     Введите Long x, не null: ", Location.xyzCheck);
            y = Console.handlerL("     Введите long y, не null: ", Location.xyzCheck);
            z = Console.handlerL("     Введите long z, не null: ", Location.xyzCheck);
            name = Console.handlerS("     Введите поле name, длиной меньше 867: ", Location.nameCheck);
            route.setFrom(new Location(x, y, z, name));
        } else
            route.setFrom(null);


        Long distance = Console.handlerL("Введите Long distance, величиной больше 1:", Route.distanceCheck);
        route.setDistance(distance);

        return route;
    }
}
