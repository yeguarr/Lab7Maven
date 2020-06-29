package program;

import dopFiles.*;
import exceptions.EndOfFileException;
import exceptions.FailedCheckException;
import exceptions.IncorrectFileNameException;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;

/**
 * Класс - обработчик команд с консоли
 */

public class Commander {

    public static Checker<Boolean> boolCheck = (Boolean B) -> {
        if (B != null) return B;
        else throw new FailedCheckException();
    };

    /**
     * Обработка команд, вводимых с консоли
     */
    public static boolean switcher(Writer w, AbstractReader reader, Collection c, String s1, String s2) throws EndOfFileException, FailedCheckException, NumberFormatException {
        switch (s1) {
            case ("help"):
                help(w);
                break;
            case ("info"):
                info(w, c);
                break;
            case ("show"):
                show(w, c);
                break;
            case ("add"):
                add(w, reader, c, s2);
                break;
            case ("update"):
                update(w, reader, c, s2);
                break;
            case ("remove_by_id"):
                removeById(w, c, s2);
                break;
            case ("clear"):
                clear(w, c);
                break;
            case ("execute_script"):
                return executeScript(w, c, s2);
            case ("add_if_min"):
                addIfMin(w, reader, c, s2);
                break;
            case ("remove_greater"):
                removeGreater(w, reader, c, s2);
                break;
            case ("remove_lower"):
                removeLower(w, reader, c, s2);
                break;
            case ("average_of_distance"):
                averageOfDistance(w, c);
                break;
            case ("min_by_creation_date"):
                minByCreationDate(w, c);
                break;
            case ("print_field_ascending_distance"):
                printFieldAscendingDistance(w, c);
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
        w.addToList(true, "Тип коллекции: " + collection.list.getClass().getName());
        w.addToList(true, "Колличество элементов: " + collection.list.size());
        w.addToList(true, "Коллеция создана: " + collection.getDate());
    }

    /**
     * Выводит значения поля distance в порядке возрастания
     */
    public static void printFieldAscendingDistance(Writer w, Collection c) {
        if (c.list.size() > 0)
            c.list.stream().filter(r -> r.getDistance() != null).map(Route::getDistance).sorted().forEach(route -> w.addToList(true, route));
        else
            w.addToList(true, "В коллекции нет элементов");
    }

    /**
     * выводит объект из коллекции, значение поля creationDate которого является минимальным
     */
    public static void minByCreationDate(Writer w, Collection c) {
        if (c.list.size() > 0)
            w.addToList(true, c.list.stream().min(Comparator.comparing(Route::getCreationDate)).get());
        else
            w.addToList(true, "В коллекции нет элементов");
    }

    /**
     * Выводит среднее значение поля distance
     */
    public static void averageOfDistance(Writer w, Collection c) {
        if (c.list.size() > 0)
            w.addToList(true, "Среднее значение distance: " + c.list.stream().filter(r -> r.getDistance() != null).mapToDouble(Route::getDistance).average().orElse(Double.NaN));
        else
            w.addToList(true, "В коллекции нет элементов");
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static void removeLower(Writer w, AbstractReader reader, Collection c, String s) throws FailedCheckException, EndOfFileException {
        int id = c.getRandId();
        Route newRoute = toAdd(w, reader, id, s);
        c.list.stream().filter(route -> route.compareTo(newRoute) < 0).forEach(route -> w.addToList(true, "Удален элемент с id: " + route.getId()));
        c.list.removeIf(route -> route.compareTo(newRoute) < 0);
        Collections.sort(c.list);
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static void removeGreater(Writer w, AbstractReader reader, Collection c, String s) throws FailedCheckException, EndOfFileException {
        int id = c.getRandId();
        Route newRoute = toAdd(w, reader, id, s);
        c.list.stream().filter(route -> route.compareTo(newRoute) > 0).forEach(route -> w.addToList(true, "Удален элемент с id: " + route.getId()));
        c.list.removeIf(route -> route.compareTo(newRoute) > 0);
        Collections.sort(c.list);
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static void addIfMin(Writer w, AbstractReader reader, Collection c, String s) throws FailedCheckException, EndOfFileException {
        int id = c.getRandId();
        Route newRoute = toAdd(w, reader, id, s);
        if (newRoute.compareTo(c.list.getFirst()) < 0) {
            c.list.add(newRoute);
        } else w.addToList(true, "Элемент не является минимальным в списке");
        Collections.sort(c.list);
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public static boolean executeScript(Writer w, Collection c, String s) {
        boolean programIsWorking = true;
        try (Reader reader = new Reader(s)) {
            if (RecursionHandler.isContains(s)) {
                RecursionHandler.addToFiles(s);
                String[] com;
                w.addToList(false, "\u001B[33m" + "Чтение команды в файле " + s + ": " + "\u001B[0m");
                String line = reader.read(w);
                while (line != null && programIsWorking) {
                    com = AbstractReader.splitter(line);
                    programIsWorking = Commander.switcher(w, reader, c, com[0], com[1]);
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
    public static void clear(Writer w, Collection c) {
        c.list.clear();
        w.addToList(true, "Коллекция очищена");
    }

    /**
     * Удаляет элемент по его id
     */
    public static void removeById(Writer w, Collection c, String s) throws FailedCheckException {
        int id = Route.idCheck.checker(Integer.parseInt(s));
        Route r = c.searchById(id);
        if (r == null) {
            w.addToList(true, "Такого элемента нет");
            return;
        }
        c.list.remove(r);
        Collections.sort(c.list);
        w.addToList(true, "Элемент с id: " + id + " успешно удален");

    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static void update(Writer w, AbstractReader reader, Collection c, String s) throws EndOfFileException, FailedCheckException {

        int id = Route.idCheck.checker(Integer.parseInt(s));
        Route r = c.searchById(id);
        if (r == null) {
            w.addToList(true, "Такого элемента нет");
            return;
        }
        String name = Route.nameCheck.checker(reader.read(w));
        c.list.set(c.list.indexOf(r), toAdd(w, reader, id, name));
        Collections.sort(c.list);
    }

    /**
     * Выводит все элементы списка
     */
    public static void show(Writer w, Collection c) {
        if (c.list.isEmpty())
            w.addToList(true, "В коллекции нет элементов");
        else
            c.list.forEach(route -> w.addToList(true, route));

    }

    /**
     * Добавляет элемент в список
     */
    public static void add(Writer w, AbstractReader reader, Collection c, String s) throws FailedCheckException, EndOfFileException {
        int id = c.getRandId();
        c.list.add(toAdd(w, reader, id, s));
        Collections.sort(c.list);
    }

    public static Route toAdd(Writer w, AbstractReader reader, int id, String s) throws FailedCheckException, EndOfFileException {

        Route route = new Route();
        route.setId(id);

        route.setName(Route.nameCheck.checker(s));

        w.addToList(true, "Ввoд полей Coordinates");
        w.addToList(false, "      Введите int x, не null: ");
        int cx = Coordinates.xCheck.checker(Integer.parseInt(reader.read(w)));
        w.addToList(false, "     Введите Long y, величиной больше -765: ");
        Long cy = Coordinates.yCheck.checker(Long.parseLong(reader.read(w)));
        route.setCoordinates(new Coordinates(cx, cy));

        ZonedDateTime creationTime = ZonedDateTime.now();
        route.setCreationDate(creationTime);

        w.addToList(true, "Ввoд полей Location to");
        w.addToList(false, "     Введите Long x, не null: ");
        Long x = Location.xyzCheck.checker(Long.parseLong(reader.read(w)));
        w.addToList(false, "     Введите Long y, не null: ");
        long y = Location.xyzCheck.checker(Long.parseLong(reader.read(w)));
        w.addToList(false, "     Введите Long z, не null: ");
        long z = Location.xyzCheck.checker(Long.parseLong(reader.read(w)));
        w.addToList(false, "     Введите поле name, длиной меньше 867: ");
        String name = Location.nameCheck.checker(reader.read(w));
        route.setTo(new Location(x, y, z, name));

        w.addToList(true, "Является ли From null'ом?");
        w.addToList(false, "     Введите Bool: ");
        if (!boolCheck.checker(AbstractReader.parseBoolean(reader.read(w)))) {
            w.addToList(true, "Ввoд полей Location from");
            w.addToList(false, "     Введите Long x, не null: ");
            x = Location.xyzCheck.checker(Long.parseLong(reader.read(w)));
            w.addToList(false, "     Введите Long y, не null: ");
            y = Location.xyzCheck.checker(Long.parseLong(reader.read(w)));
            w.addToList(false, "     Введите Long z, не null: ");
            z = Location.xyzCheck.checker(Long.parseLong(reader.read(w)));
            w.addToList(false, "     Введите поле name, длиной меньше 867: ");
            name = Location.nameCheck.checker(reader.read(w));
            route.setFrom(new Location(x, y, z, name));
        } else
            route.setFrom(null);

        w.addToList(false, "Введите Long distance, величиной больше 1:");
        Long distance = Route.distanceCheck.checker(Long.parseLong(reader.read(w)));
        route.setDistance(distance);

        return route;
    }
}
