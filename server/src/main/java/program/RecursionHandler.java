package program;

import java.util.Stack;

/**
 * Класс, предотвращающий рекурсии
 */
public class RecursionHandler {
    private static Stack<String> usedFiles = new Stack<>();

    public static void addToFiles(String s) {
        usedFiles.push(s);
    }

    public static void removeLast() {
        usedFiles.pop();
    }

    public static boolean isContains(String s) {
        return usedFiles.search(s) == -1;
    }
}