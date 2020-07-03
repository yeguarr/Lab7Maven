package program;

import dopFiles.Route;
import dopFiles.User;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс для хранения и обработки LinkedList
 */
public class Collection {

    /**
     * Список, в котором хранятся элементы типа program.Route
     */
    public Map<User, List<Route>> map = new ConcurrentHashMap<>();
    /**
     * Дата создания списка
     */
    private Date date = new Date();

    /**
     * Метод, возвращающий список, удобный для сохранения в формат CSV
     */
    public static Collection startFromSave(String[] args) {
        if (args.length > 0) {
            File saveFile = new File(args[0]);
            if (saveFile.exists()) {
                SaveManagement.setFile(saveFile);
                return SaveManagement.listFromSave();
            }
        }
        else {
            File saveFile = new File("save.csv");
            if (saveFile.exists()) {
                SaveManagement.setFile(saveFile);
                return SaveManagement.listFromSave();
            }
        }
        return new Collection();
    }

    public boolean isUserInMap(User user) {
        if (user.login.equals("login")) return false;
        return map.containsKey(user);
    }

    public boolean isLoginUsed(String login) {
        if (login.equals("login")) return true;
        for (User user : map.keySet()) {
            if (user.login.equals(login))
                return true;
        }
        return false;
    }

    /**
     * Метод, осуществляющий поиск элемента по id
     */
    public Route searchById(Integer id) {
        for (User user: map.keySet()) {
            for (Route r : map.get(user)) {
                if (r.getId().equals(id))
                    return r;
            }
        }
        return null;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Метод, возвращающий уникальный id
     */
    public int getRandId() {
        int id;
        do {
            id = (int) (1 + Math.random() * (Integer.MAX_VALUE - 1));
        } while (this.searchById(id) != null);
        return id;
    }
}
