package dopFiles;

import exceptions.EndOfFileException;

import java.util.Scanner;

//ирир

/**
 * Абстрактный класс, предназначенный для считаывания команд с консоли и считывания команд из файла
 */

public abstract class AbstractReader implements AutoCloseable {
    /**
     * Поле типа Scanner, предназначенное для считывания строки либо из файла, либо из консоли
     */
    protected Scanner scan;

    /**
     * protected конструктор
     */
    protected AbstractReader() {
    }

    public static String[] splitter(String line) {
        String[] s = line.split(" ", 2);
        if (s.length == 2) {
            s[1] = s[1].trim();
            return s;
        } else
            return new String[]{s[0], ""};
    }

    public static Boolean parseBoolean(String s) {
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t") || s.equals("1") || s.equalsIgnoreCase("y"))
            return true;
        else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f") || s.equals("0") || s.equalsIgnoreCase("n"))
            return false;
        throw new NumberFormatException();
    }

    /**
     * @return Возвращает последнюю строку
     */
    public abstract String read(Writer w) throws EndOfFileException;

    public void close() {
        scan.close();
    }
}



