package program;

import command.Command;
import command.CommandWithObj;
import command.Commands;
import command.RemoveById;
import dopFiles.*;
import exceptions.EndOfFileException;
import exceptions.FailedCheckException;
import exceptions.IncorrectFileNameException;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Класс - обработчик команд с консоли
 */

public class Commander {

    /**
     * Обработка команд, вводимых с консоли
     */
    public static boolean switcher(Writer w, AbstractReader reader, Collection c, String s1, String s2, PostgreSQL sql, User user) throws EndOfFileException, FailedCheckException, NumberFormatException {
        switch (s1) {
            case ("help"):
                help(w);
                break;
            case ("info"):
                info(w, c);
                break;
            case ("show"):
                show(w, c, user);
                break;
            case ("add"):
                add(w, reader, c, s2, sql, user);
                break;
            case ("update"):
                update(w, reader, c, s2, sql, user);
                break;
            case ("remove_by_id"):
                removeById(w, c, s2, sql, user);
                break;
            case ("clear"):
                clear(w, c, sql, user);
                break;
            case ("execute_script"):
                return executeScript(w, c, s2, sql, user);
            case ("add_if_min"):
                addIfMin(w, reader, c, s2, sql, user);
                break;
            case ("remove_greater"):
                removeGreater(w, reader, c, s2, sql, user);
                break;
            case ("remove_lower"):
                removeLower(w, reader, c, s2, sql, user);
                break;
            case ("average_of_distance"):
                averageOfDistance(w, c, user);
                break;
            case ("min_by_creation_date"):
                minByCreationDate(w, c, user);
                break;
            case ("print_field_ascending_distance"):
                printFieldAscendingDistance(w, c, user);
                break;
            default:
                w.addToList(true, "Такой команды нет");
        }
        return true;
    }

    /**
     * Показывает информацию по всем возможным командам
     */
    public static void help(Writer w) {
        w.addToList(true,
                "help : вывести справку по доступным командам\n" +
                        "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "add {element} : добавить новый элемент в коллекцию\n" +
                        "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_by_id id : удалить элемент из коллекции по его id\n" +
                        "clear : очистить коллекцию\n" +
                        "save : сохранить коллекцию в файл\n" +
                        "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "exit : завершить программу (без сохранения в файл)\n" +
                        "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                        "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                        "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                        "average_of_distance : вывести среднее значение поля distance для всех элементов коллекции\n" +
                        "min_by_creation_date : вывести любой объект из коллекции, значение поля creationDate которого является минимальным\n" +
                        "print_field_ascending_distance : вывести значения поля distance в порядке возрастания"
        );
    }

    /**
     * Показывает информацию о коллекции
     */
    public static void info(Writer w, Collection collection) {
        w.addToList(true, "Тип коллекции: " + collection.map.getClass().getName());
        w.addToList(true, "Колличество зарегестрированных пользователей: " + collection.map.size());
        w.addToList(true, "Коллеция создана: " + collection.getDate());
    }

    /**
     * Выводит значения поля distance в порядке возрастания
     */
    public static void printFieldAscendingDistance(Writer w, Collection c, User user) {
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            if (list.size() > 0)
                list.stream().filter(r -> r.getDistance() != null).map(Route::getDistance).sorted().forEach(dis -> w.addToList(true, dis));
            else
                w.addToList(true, "В доступной вам коллекции нет элементов");
        }
    }

    /**
     * выводит объект из коллекции, значение поля creationDate которого является минимальным
     */
    public static void minByCreationDate(Writer w, Collection c, User user) {
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            if (list.size() > 0)
                w.addToList(true, list.stream().min(Comparator.comparing(Route::getCreationDate)).get());
            else
                w.addToList(true, "В доступной вам коллекции нет элементов");
        }
    }

    /**
     * Выводит среднее значение поля distance
     */
    public static void averageOfDistance(Writer w, Collection c, User user) {
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            if (list.size() > 0)
                w.addToList(true, "Среднее значение distance: " + list.stream().filter(r -> r.getDistance() != null).mapToDouble(Route::getDistance).average().orElse(Double.NaN));
            else
                w.addToList(true, "В доступной вам коллекции нет элементов");
        }
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static void removeLower(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws FailedCheckException, EndOfFileException {
        Route newRoute = toAdd(w, reader, 0, s);
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            list.stream().filter(route -> route.compareTo(newRoute) < 0).forEach(route -> w.addToList(true, "Удален элемент с id: " + route.getId()));

            list.removeIf(route -> {
                boolean bool = route.compareTo(newRoute) < 0;
                if (bool)
                    sql.add(new RemoveById(user, newRoute.getId()));
                return bool;
            });
        }
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static void removeGreater(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws FailedCheckException, EndOfFileException {
        Route newRoute = toAdd(w, reader, 0, s);
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            list.stream().filter(route -> route.compareTo(newRoute) > 0).forEach(route -> w.addToList(true, "Удален элемент с id: " + route.getId()));
            list.removeIf(route -> {
                boolean bool = route.compareTo(newRoute) > 0;
                if (bool)
                    sql.add(new RemoveById(user, newRoute.getId()));
                return bool;
            });
        }
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static void addIfMin(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws FailedCheckException, EndOfFileException {
        int id = c.getNextId();
        Route newRoute = toAdd(w, reader, id, s);
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            if (newRoute.compareTo(list.stream().sorted().findFirst().orElse(newRoute)) < 0) {
                list.add(newRoute);
                w.addToList(true, "Элемент успешно добавлен");
                sql.add(new CommandWithObj(user, Commands.ADD, newRoute));
            } else w.addToList(true, "Элемент не является минимальным в списке");
        }
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public static boolean executeScript(Writer w, Collection c, String s, PostgreSQL sql, User user) {
        boolean programIsWorking = true;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                String line = reader.read(w);
                while (line != null && programIsWorking) {
                    com = AbstractReader.splitter(line);
                    programIsWorking = Commander.switcher(w, reader, c, com[0], com[1], sql, user);
                    w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                    line = reader.read(w);
                }
                RecursionHandler.removeLast();
            } else
                w.addToList(true, "\u001B[31m" + "Найдено повторение" + "\u001B[0m");

        } catch (IncorrectFileNameException e) {
            w.addToList(true, "\u001B[31m" + "Неверное имя файла" + "\u001B[0m");
        } catch (EndOfFileException e) {
            w.addToList(true, "\u001B[31m" + "Неожиданный конец файла " + s + "\u001B[0m");
            RecursionHandler.removeLast();
        } catch (FileNotFoundException e) {
            w.addToList(true, "\u001B[31m" + "Файл не найден" + "\u001B[0m");
        } catch (FailedCheckException | NumberFormatException e) {
            w.addToList(true, "\u001B[31m" + "Файл содержит неправильные данные" + "\u001B[0m");
            RecursionHandler.removeLast();
        }
        return programIsWorking;
    }

    /**
     * Удаляет все элементы из коллекции
     */
    public static void clear(Writer w, Collection c, PostgreSQL sql, User user) {
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            list.clear();
            w.addToList(true, "Доступная вам коллекция очищена");
            sql.add(new Command(user, Commands.CLEAR));
        }
    }

    /**
     * Удаляет элемент по его id
     */
    public static void removeById(Writer w, Collection c, String s, PostgreSQL sql, User user) throws FailedCheckException {
        int id = Utils.routeIdCheck.checker(Integer.parseInt(s));
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            Route route = c.searchById(id);
            if (route == null) {
                w.addToList(true, "Такого в дотупной вам коллекции элемента нет");

                w.addToList(false, "end");
                return;
            }
            list.remove(route);
            w.addToList(true, "Элемент с id: " + id + " успешно удален");
            sql.add(new RemoveById(user, id));
        }
    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static void update(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws EndOfFileException, FailedCheckException {
        int id = Utils.routeIdCheck.checker(Integer.parseInt(s));
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            Route route = null;
            for (Route r : list) {
                if (r.getId().equals(id)) {
                    route = r;
                }
            }
            if (route == null) {
                w.addToList(true, "Такого в дотупной вам коллекции элемента нет");

                w.addToList(false, "end");
                return;
            }
            String name = Utils.routeNameCheck.checker(reader.read(w));
            Route r = toAdd(w, reader, id, name);
            list.set(list.indexOf(route), r);
            w.addToList(true, "Элемент с id: " + id + " успешно изменен");
            sql.add(new CommandWithObj(user, Commands.UPDATE, r));
        }
    }

    /**
     * Выводит все элементы списка
     */
    public static void show(Writer w, Collection c, User user) {
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            for (User u : c.map.keySet()) {
                w.addToList(true, "Все добавленные элементы поользователя: " + u.login);
                if (c.map.get(u).isEmpty())
                    w.addToList(true, "В коллекции нет элементов");
                else
                    c.map.get(u).forEach(r -> w.addToList(true, r.toString()));
            }
        }
    }

    /**
     * Добавляет элемент в список
     */
    public static void add(Writer w, AbstractReader reader, Collection c, String s, PostgreSQL sql, User user) throws FailedCheckException, EndOfFileException {
        List<Route> list = properUser(w, user, c);
        if (list != null) {
            int id = c.getNextId();
            Route r = toAdd(w, reader, id, s);
            list.add(r);
            w.addToList(true, "Элемент с id: " + id + " успешно добавлен");
            sql.add(new CommandWithObj(user, Commands.ADD, r));
        }
    }

    public static Route toAdd(Writer w, AbstractReader reader, int id, String s) throws FailedCheckException, EndOfFileException {

        Route route = new Route();
        route.setId(id);

        route.setName(Utils.routeNameCheck.checker(s));

        w.addToList(true, "Ввoд полей Coordinates");
        w.addToList(false, "      Введите int x, не null: ");
        int cx = Utils.coordinatesXCheck.checker(Integer.parseInt(reader.read(w)));
        w.addToList(false, "     Введите Long y, величиной больше -765: ");
        Long cy = Utils.coordinatesYCheck.checker(Long.parseLong(reader.read(w)));
        route.setCoordinates(new Coordinates(cx, cy));

        ZonedDateTime creationTime = ZonedDateTime.now();
        route.setCreationDate(creationTime);

        w.addToList(true, "Ввoд полей Location to");
        w.addToList(false, "     Введите Long x, не null: ");
        Long x = Utils.locationXYZCheck.checker(Long.parseLong(reader.read(w)));
        w.addToList(false, "     Введите Long y, не null: ");
        long y = Utils.locationXYZCheck.checker(Long.parseLong(reader.read(w)));
        w.addToList(false, "     Введите Long z, не null: ");
        long z = Utils.locationXYZCheck.checker(Long.parseLong(reader.read(w)));
        w.addToList(false, "     Введите поле name, длиной меньше 867: ");
        String name = Utils.locationNameCheck.checker(reader.read(w));
        route.setTo(new Location(x, y, z, name));

        w.addToList(true, "Является ли From null'ом?");
        w.addToList(false, "     Введите Bool: ");
        if (!Utils.boolCheck.checker(AbstractReader.parseBoolean(reader.read(w)))) {
            w.addToList(true, "Ввoд полей Location from");
            w.addToList(false, "     Введите Long x, не null: ");
            x = Utils.locationXYZCheck.checker(Long.parseLong(reader.read(w)));
            w.addToList(false, "     Введите Long y, не null: ");
            y = Utils.locationXYZCheck.checker(Long.parseLong(reader.read(w)));
            w.addToList(false, "     Введите Long z, не null: ");
            z = Utils.locationXYZCheck.checker(Long.parseLong(reader.read(w)));
            w.addToList(false, "     Введите поле name, длиной меньше 867: ");
            name = Utils.locationNameCheck.checker(reader.read(w));
            route.setFrom(new Location(x, y, z, name));
        } else
            route.setFrom(null);

        w.addToList(false, "Введите Long distance, величиной больше 1:");
        Long distance = Utils.routeDistanceCheck.checker(Long.parseLong(reader.read(w)));
        route.setDistance(distance);

        return route;
    }
    public static List<Route> properUser(Writer w, User user, Collection collection) {
        if (collection.isUserInMap(user))
            return collection.map.get(user);
        else {
            w.addToList(true, "Не удаётся выполнить команду.");
            w.addToList(true, "Пожалуйста, проверьте правильность написания логина и пароля.");
        }
        return null;
    }

}
