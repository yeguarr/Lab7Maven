package program;

import dopFiles.AbstractReader;
import dopFiles.Writer;
import exceptions.IncorrectFileNameException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * класс для считывания команд с файла
 */
public class Reader extends AbstractReader {
    public Reader(String file) throws IncorrectFileNameException, FileNotFoundException {
        File f = new File(file);
        if (!f.exists())
            throw new IncorrectFileNameException("Ошибка! Файл не найден!");
        scan = new Scanner(new File(file));
    }

    /**
     * считывание строки
     */
    @Override
    public String read(Writer w) /*throws EndOfFileException */ {
        if (scan.hasNextLine()) {
            String line = scan.nextLine();
            w.addToList(false, line + "\n");
            return line;
        }
        w.addToList(false, "Конец файла." + "\n");
        return null; //непроверенная неизвестность "_"
        //throw new EndOfFileException("Преждевременный конец файла!");
    }
}
