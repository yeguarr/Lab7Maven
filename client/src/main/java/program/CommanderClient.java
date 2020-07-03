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

    /**
     * Обработка команд, вводимых с консоли
     */
    public static Command switcher(User user, String s1, String s2) throws EndOfFileException {
        switch (s1) {
            case ("help"):
                return new Command(user, Commands.HELP);
            case ("info"):
                return new Command(user, Commands.INFO);
            case ("show"):
                return new Command(user, Commands.SHOW);
            case ("add"):
                return add(user, s2);
            case ("update"):
                return update(user, s2);
            case ("remove_by_id"):
                return removeById(user, s2);
            case ("clear"):
                return new Command(user,Commands.CLEAR);
            case ("execute_script"):
                return new ExecuteScript(user, s2);
            case ("exit"):
                return new Command(user, Commands.EXIT);
            case ("add_if_min"):
                return addIfMin(user, s2);
            case ("remove_greater"):
                return removeGreater(user, s2);
            case ("remove_lower"):
                return removeLower(user, s2);
            case ("average_of_distance"):
                return new Command(user, Commands.AVERAGE_OF_DISTANCE);
            case ("min_by_creation_date"):
                return new Command(user, Commands.MIN_BY_CREATION_DATE);
            case ("print_field_ascending_distance"):
                return new Command(user, Commands.PRINT_FIELD_ASCENDING_DISTANCE);
            case("login"):
                return login(user);
            case("register"):
                return register(user);
            default:
                Writer.writeln("Такой команды нет");
        }
        return null;
    }

    private static Command register(User user) throws EndOfFileException {
        String login = Console.handlerS("Введите логин: ", Utils.loginCheck);
        String password = Console.handlerS("Введите пароль: ", Utils.passwordCheck);
        user.changeUser(login, password);
        return new Command(user, Commands.REGISTER);
    }

    private static Command login(User user) throws EndOfFileException {
        String login = Console.handlerS("Введите логин: ", Utils.loginCheck);
        String password = Console.handlerS("Введите пароль: ", Utils.passwordCheck);
        user.changeUser(login, password);
        return new Command(user, Commands.LOGIN);
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static Command removeLower(User user, String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(user, Commands.REMOVE_LOWER, newRoute);
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static Command removeGreater(User user, String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(user, Commands.REMOVE_GREATER, newRoute);
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static Command addIfMin(User user, String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(user, Commands.ADD_IF_MIN, newRoute);
    }

    /**
     * Удаляет все элементы по его id
     */
    public static Command removeById(User user, String s) throws EndOfFileException {
        int id;
        try {
            id = Utils.routeIdCheck.checker(Integer.parseInt(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = Console.handlerI("Введите int id: ", Utils.routeIdCheck);
        }
        return new RemoveById(user, id);
    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static Command update(User user, String s) throws EndOfFileException {
        int id;
        try {
            id = Utils.routeIdCheck.checker(Integer.parseInt(s));
        } catch (NumberFormatException | FailedCheckException e) {
            id = Console.handlerI("Введите int id: ", Utils.routeIdCheck);
        }
        String name = Console.handlerS("Введите String: name", Utils.routeNameCheck);
        Route newRoute = toAddWithoutId(name);
        newRoute.setId(id);
        return new CommandWithObj(user, Commands.UPDATE, newRoute);
    }

    /**
     * Добавляет элемент в список
     */
    public static Command add(User user, String s) throws EndOfFileException {
        Route newRoute = toAddWithoutId(s);
        return new CommandWithObj(user, Commands.ADD, newRoute);
    }

    public static Route toAddWithoutId(String s) throws EndOfFileException {
        Route route = new Route();
        route.setId(null);
        try {
            Utils.routeNameCheck.checker(s);
            Writer.writeln("Поле name: " + s);
        } catch (FailedCheckException e) {
            s = Console.handlerS("Введите String name, диной больше 0: ", Utils.routeNameCheck);
        }
        route.setName(s);

        Writer.writeln("Ввoд полей Coordinates");
        int cx = Console.handlerI("      Введите int x, не null: ", Utils.coordinatesXCheck);
        Long cy = Console.handlerL("     Введите Long y, величиной больше -765: ", Utils.coordinatesYCheck);
        route.setCoordinates(new Coordinates(cx, cy));

        ZonedDateTime creationTime = ZonedDateTime.now();
        route.setCreationDate(creationTime);

        Writer.writeln("Ввoд полей Location to");
        Long x = Console.handlerL("     Введите Long x, не null: ", Utils.locationXYZCheck);
        long y = Console.handlerL("     Введите long y, не null: ", Utils.locationXYZCheck);
        long z = Console.handlerL("     Введите long z, не null: ", Utils.locationXYZCheck);
        String name = Console.handlerS("     Введите поле name, длиной меньше 867: ", Utils.locationNameCheck);
        route.setTo(new Location(x, y, z, name));

        Writer.writeln("Является ли From null'ом?");
        if (!Console.handlerB("     Введите Bool: ", Utils.boolCheck)) {
            Writer.writeln("Ввoд полей Location from");
            x = Console.handlerL("     Введите Long x, не null: ", Utils.locationXYZCheck);
            y = Console.handlerL("     Введите long y, не null: ", Utils.locationXYZCheck);
            z = Console.handlerL("     Введите long z, не null: ", Utils.locationXYZCheck);
            name = Console.handlerS("     Введите поле name, длиной меньше 867: ", Utils.locationNameCheck);
            route.setFrom(new Location(x, y, z, name));
        } else
            route.setFrom(null);


        Long distance = Console.handlerL("Введите Long distance, величиной больше 1:", Utils.routeDistanceCheck);
        route.setDistance(distance);

        return route;
    }
}
