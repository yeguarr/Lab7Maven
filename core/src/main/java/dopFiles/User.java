package dopFiles;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    public String login;
    public String hashPassword;
    private transient String password; // нужен ли

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        hashPassword = Utils.sha1(password);
    }

    public static User userFromHashPassword(String login, String hashPassword) {
        User user = new User(login, "password");
        user.hashPassword = hashPassword;
        return user;
    }

    public void changeUser(String login, String password) {
        this.login = login;
        this.password = password;
        hashPassword = Utils.sha1(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) &&
                Objects.equals(hashPassword, user.hashPassword);
    }

    @Override
    public String toString() {
        return "User{" + "login='" + login + '\'' + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, hashPassword);
    }
    /**
     * Конвертирование элемента списка в удобный для сохранения формат
     */
    public String toCSVfile() {
        return login + "," + hashPassword;
    }
}
