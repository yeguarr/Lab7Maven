package dopFiles;

import exceptions.EndOfFileException;
import exceptions.FailedCheckException;

import java.util.Scanner;

/**
 * Класс, считывающий строки из консоли
 */
public class Console extends AbstractReader {
    public static Console console = new Console();

    Console() {
        scan = new Scanner(System.in);
    }

    /**
     * Метод для парсинга Integer
     */
    public static Integer handlerI(String s, Checker<Integer> c) throws EndOfFileException {
        String line;

        while (true) {
            try {
                Writer.write(s);
                line = console.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(Integer.parseInt(line));
            } catch (NumberFormatException e) {
                Writer.writeln("\u001B[31m" + "Ошибка ввода, попробуйте еще раз" + "\u001B[0m");
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    /**
     * Метод для парсинга Long
     */
    public static Long handlerL(String s, Checker<Long> c) throws EndOfFileException {
        String line;

        while (true) {
            try {
                Writer.write(s);
                line = console.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(Long.parseLong(line));
            } catch (NumberFormatException e) {
                Writer.writeln("\u001B[31m" + "Ошибка ввода, попробуйте еще раз" + "\u001B[0m");
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    /**
     * Метод для парсинга String
     */
    public static String handlerS(String s, Checker<String> c) throws EndOfFileException {
        String line;
        while (true) {
            try {
                Writer.write(s);
                line = console.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(line);
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    /**
     * Метод для парсинга Boolean
     */
    public static Boolean handlerB(String s, Checker<Boolean> c) throws EndOfFileException {
        String line;

        while (true) {
            try {
                Writer.write(s);
                line = console.read();
                if (line == null)
                    throw new EndOfFileException("Преждевременный конец файла!");
                else if (line.equals(""))
                    return c.checker(null);
                return c.checker(parseBoolean(line));
            } catch (NumberFormatException e) {
                Writer.writeln("\u001B[31m" + "Ошибка ввода, попробуйте еще раз" + "\u001B[0m");
            } catch (FailedCheckException e) {
                Writer.writeln("\u001B[31m" + "Условия не соблюдены, попробуйте еще раз" + "\u001B[0m");
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public String read(Writer w) throws EndOfFileException {
        w.writeAll();
        if (scan.hasNextLine())
            return scan.nextLine();
        throw new EndOfFileException("Конец ввода косоли!");
    }

    public String read() throws EndOfFileException {
        if (scan.hasNextLine())
            return scan.nextLine();
        throw new EndOfFileException("Конец ввода косоли!");
    }
}
