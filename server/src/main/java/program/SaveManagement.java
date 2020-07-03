package program;

import dopFiles.*;
import exceptions.FailedCheckException;
import exceptions.IncorrectFileNameException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.SplittableRandom;

/**
 * Класс, оперирующий с файлами
 */

public class SaveManagement {
    private static File file;

    public static void setFile(File file) {
        SaveManagement.file = file;
    }

    /**
     * Сохранение файла в CSV формат
     */
    public static void saveToFile(Collection c) {
        if (file == null)
            file = new File("save.csv");
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (User user : c.map.keySet()) {
                fileWriter.write(user.toCSVfile() + "\n");
            }
        } catch (IOException e) {
            Writer.writeln("Ошибка доступа к файлу");
        }
    }

    /**
     * Возвращает коллекцию из сохраненного файла
     */
    public static Collection listFromSave() {
        Collection collection = new Collection();
        try (Scanner scan = new Scanner(file)) {
            String[] args;
            for (int lineNum = 1; scan.hasNext(); lineNum++) {
                try {
                    String line = scan.nextLine();
                    args = line.split(",", 2);
                    String login = Utils.loginCheck.checker(args[0]);
                    if (collection.isLoginUsed(login))
                        throw new FailedCheckException();
                    String hashPassword = (args[1]); //todo сделать проверку хэша
                    collection.map.put(User.userFromHashPassword(login, hashPassword), new LinkedList<>()); // я не знаю, насколько это правильно
                } catch (ArrayIndexOutOfBoundsException | FailedCheckException e) {
                    Writer.writeln("\u001B[31m" + "Ошибка чтения файла, строка: " + "\u001B[0m" + lineNum);
                }
            }
        } catch (FileNotFoundException e) {
            Writer.writeln("\u001B[31m" + "Ошибка доступа к файлу" + "\u001B[0m");
        }
        return collection;
    }
    /*public static Collection listFromSave() {
        Collection collection = new Collection();
        try (Scanner scan = new Scanner(file)) {
            String[] args;
            for (int lineNum = 1; scan.hasNext(); lineNum++) {
                try {
                    String line = scan.nextLine();
                    args = line.split(",", 14);

                    Route route = new Route();
                    int id = Utils.routeIdCheck.checker(Integer.parseInt(args[0]));
                    if (collection.searchById(id) == null)
                        route.setId(id);
                    else {
                        Writer.writeln("Получен неверный id");
                        throw new FailedCheckException();
                    }

                    route.setName(Utils.routeNameCheck.checker(args[1]));

                    int cx = Utils.coordinatesXCheck.checker(Integer.parseInt(args[2]));
                    Long cy = Utils.coordinatesYCheck.checker(Long.parseLong(args[3]));
                    route.setCoordinates(new Coordinates(cx, cy));

                    ZonedDateTime dateTime = ZonedDateTime.parse(args[4]);
                    route.setCreationDate(dateTime);
                    if (!args[5].equals("null")) {
                        Long fromX = Utils.locationXYZCheck.checker(Long.parseLong(args[5]));
                        Long fromY = Utils.locationXYZCheck.checker(Long.parseLong(args[6]));
                        long fromZ = Utils.locationXYZCheck.checker(Long.parseLong(args[7]));
                        route.setFrom(new Location(fromX, fromY, fromZ, args[8]));
                    }

                    Long toX = Utils.locationXYZCheck.checker(Long.parseLong(args[9]));
                    Long toY = Utils.locationXYZCheck.checker(Long.parseLong(args[10]));
                    long toZ = Utils.locationXYZCheck.checker(Long.parseLong(args[11]));
                    route.setTo(new Location(toX, toY, toZ, args[12]));
                    if (!args[13].equals("null")) {

                        Long dis = Utils.routeDistanceCheck.checker(Long.parseLong(args[13]));
                        route.setDistance(dis);
                    }
                    collection.list.add(route);
                } catch (ArrayIndexOutOfBoundsException | DateTimeParseException | NumberFormatException | FailedCheckException e) {
                    Writer.writeln("\u001B[31m" + "Ошибка чтения файла, строка: " + "\u001B[0m" + lineNum);
                }
            }
        } catch (FileNotFoundException e) {
            Writer.writeln("\u001B[31m" + "Ошибка доступа к файлу" + "\u001B[0m");
        }
        Collections.sort(collection.list);
        return collection;
    }*/
}
