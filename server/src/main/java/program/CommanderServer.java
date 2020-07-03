package program;

import command.Command;
import dopFiles.*;
import exceptions.FailedCheckException;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс - обработчик команд с консоли
 */

public class CommanderServer {
    /**
     * Обработка команд, вводимых с консоли
     */
    public static Writer switcher(Command com, Collection c) {
        switch (com.getCurrent()) {
            case HELP:
                return help();
            case INFO:
                return info(c);
            case SHOW:
                return show(c, com);
            case ADD:
                return add(c, com);
            case UPDATE:
                return update(c, com);
            case REMOVE_BY_ID:
                return removeById(c, com);
            case CLEAR:
                return clear(c, com);
            case EXECUTE_SCRIPT:
                return executeScript(c, com);
            case ADD_IF_MIN:
                return addIfMin(c, com);
            case REMOVE_GREATER:
                return removeGreater(c, com);
            case REMOVE_LOWER:
                return removeLower(c, com);
            case AVERAGE_OF_DISTANCE:
                return averageOfDistance(c, com);
            case MIN_BY_CREATION_DATE:
                return minByCreationDate(c, com);
            case PRINT_FIELD_ASCENDING_DISTANCE:
                return printFieldAscendingDistance(c, com);
            case LOGIN:
                return login(c, com);
            case REGISTER:
                return register(c, com);
            default:
                Writer.writeln("Такой команды нет");
        }
        return new Writer();
    }

    private static Writer register(Collection c, Command com) {
        Writer w = new Writer();
        if (!c.isLoginUsed(com.getUser().login)) {
            c.map.put(com.getUser(), new LinkedList<>());
            w.addToList(true, "Пользователь успешно зарегестрирован.");
        }
        else {
            w.addToList(true, "Пользователь с таким логином уже создан.");
            w.addToList(true, "Попробуйте, пожалуйста, другой логин.");
        }

        w.addToList(false,"end");
        return w;
    }

    private static Writer login(Collection c, Command com) {
        Writer w = new Writer();
        if (c.isUserInMap(com.getUser()))
            w.addToList(true, "Вы вошли под логином: " + com.getUser().login);
        else {
            w.addToList(true, "Не удаётся войти.");
            w.addToList(true, "Пожалуйста, проверьте правильность написания логина и пароля.");
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Показывает информацию по всем возможным командам
     */
    public static Writer help() {
        Writer w = new Writer();
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
                        "print_field_ascending_distance : вывести значения поля distance в порядке возрастания\n" +
                        "login : авторизоваться под определенным пользователем\n" +
                        "register : зарегестрировать пользователя"
        );

        w.addToList(false,"end");
        return w;
    }

    /**
     * Показывает информацию о коллекции
     */
    public static Writer info(Collection collection) {
        Writer w = new Writer();
        w.addToList(true, "Тип коллекции: " + collection.map.getClass().getName());
        w.addToList(true, "Колличество зарегестрированных пользователей: " + collection.map.size());
        w.addToList(true, "Коллеция создана: " + collection.getDate());

        w.addToList(false,"end");
        return w;
    }
    /**
     * Выводит все элементы списка
     */
    public static Writer show(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            for (User user : c.map.keySet()) {
                w.addToList(true, "Все добавленные элементы поользователя: " + user.login);
                if (c.map.get(user).isEmpty())
                    w.addToList(true, "В коллекции нет элементов");
                else
                    c.map.get(user).forEach(r -> w.addToList(true, r.toString()));
            }
        }

        w.addToList(false,"end");
        return w;
    }
    /**
     * Выводит значения поля distance в порядке возрастания
     */
    public static Writer printFieldAscendingDistance(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            if (list.size() > 0)
                list.stream().filter(r -> r.getDistance() != null).map(Route::getDistance).sorted().forEach(dis -> w.addToList(true, dis));
            else
                w.addToList(true, "В доступной вам коллекции нет элементов");
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * выводит объект из коллекции, значение поля creationDate которого является минимальным
     */
    public static Writer minByCreationDate(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            if (list.size() > 0)
                w.addToList(true, list.stream().min(Comparator.comparing(Route::getCreationDate)).get());
            else
                w.addToList(true, "В доступной вам коллекции нет элементов");
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Выводит среднее значение поля distance
     */
    public static Writer averageOfDistance(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            if (list.size() > 0)
                w.addToList(true, "Среднее значение distance: " + list.stream().filter(r -> r.getDistance() != null).mapToDouble(Route::getDistance).average().orElse(Double.NaN));
            else
                w.addToList(true, "В доступной вам коллекции нет элементов");
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Удаляет все элементы коллекции, которые меньше чем заданный
     */
    public static Writer removeLower(Collection c, Command com) {
        Writer w = new Writer();
        int id = c.getRandId();
        Route newRoute = routeWithId((Route) com.returnObj(), id);
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            list.stream().filter(route -> route.compareTo(newRoute) < 0).forEach(route -> w.addToList(true, "Удален элемент с id: " + route.getId()));
            list.removeIf(route -> route.compareTo(newRoute) < 0);
            Collections.sort(list);
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Удаляет все элементы коллекции, которые больше чем заданный
     */
    public static Writer removeGreater(Collection c, Command com) {
        Writer w = new Writer();
        int id = c.getRandId();
        Route newRoute = routeWithId((Route) com.returnObj(), id);
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            list.stream().filter(route -> route.compareTo(newRoute) > 0).forEach(route -> w.addToList(true, "Удален элемент с id: " + route.getId()));
            list.removeIf(route -> route.compareTo(newRoute) > 0);
            Collections.sort(list);
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public static Writer addIfMin(Collection c, Command com) {
        Writer w = new Writer();
        int id = c.getRandId();
        Route newRoute = routeWithId((Route) com.returnObj(), id);
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            if (newRoute.compareTo(list.get(0)) < 0) {
                list.add(newRoute);
                w.addToList(true, "Элемент успешно добавлен");
            } else w.addToList(true, "Элемент не является минимальным в списке");
            Collections.sort(list);
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public static Writer executeScript(Collection c, Command command) {
        Writer w = new Writer();
        /*String s = (String) command.returnObj();
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

        } catch (exceptions.IncorrectFileNameException e) {
            w.addToList(true, "\u001B[31m" + "Неверное имя файла" + "\u001B[0m");
        } catch (exceptions.EndOfFileException e) {
            w.addToList(true, "\u001B[31m" + "Неожиданный конец файла " + s + "\u001B[0m");
            RecursionHandler.removeLast();
        } catch (FileNotFoundException e) {
            w.addToList(true, "\u001B[31m" + "Файл не найден" + "\u001B[0m");
        } catch (FailedCheckException | NumberFormatException e) {
            w.addToList(true, "\u001B[31m" + "Файл содержит неправильные данные" + "\u001B[0m");
            RecursionHandler.removeLast();
        }
*/
        w.addToList(false,"end");
        return w;
    }

    /**
     * Удаляет все элементы из коллекции
     */
    public static Writer clear(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            list.clear();
            w.addToList(true, "Доступная вам коллекция очищена");
        }

        w.addToList(false, "end");
        return w;
    }

    /**
     * Удаляет все элементы по его id
     */
    public static Writer removeById(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            Integer id = (Integer) com.returnObj();
            Route route = null;
            for (Route r : list) {
                if (r.getId().equals(id)) {
                    route = r;
                }
            }
            if (route == null) {
                w.addToList(true, "Такого в дотупной вам коллекции элемента нет");

                w.addToList(false, "end");
                return w;
            }
            list.remove(route);
            w.addToList(true, "Элемент с id: " + id + " успешно удален");
            Collections.sort(list);
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Перезаписывает элемент списка с указанным id
     */
    public static Writer update(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            int id = ((Route) com.returnObj()).getId();
            Route route = null;
            for (Route r : list) {
                if (r.getId().equals(id)) {
                    route = r;
                }
            }
            if (route == null) {
                w.addToList(true, "Такого в дотупной вам коллекции элемента нет");

                w.addToList(false, "end");
                return w;
            }
            list.set(list.indexOf(route), (Route) com.returnObj());
            Collections.sort(list);
            w.addToList(true, "Элемент с id: " + id + " успешно изменен");
        }

        w.addToList(false,"end");
        return w;
    }

    /**
     * Добавляет элемент в список
     */
    public static Writer add(Collection c, Command com) {
        Writer w = new Writer();
        List<Route> list = properUser(w, com.getUser(), c);
        if (list != null) {
            int id = c.getRandId();
            list.add(routeWithId((Route) com.returnObj(), id));
            Collections.sort(list);
            w.addToList(true, "Элемент с id: " + id + " успешно добавлен");
        }

        w.addToList(false,"end");
        return w;
    }

    public static Route routeWithId(Route r, int id) {
        r.setId(id);
        return r;
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

