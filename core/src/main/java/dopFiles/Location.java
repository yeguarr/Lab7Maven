package dopFiles;

import exceptions.FailedCheckException;

import java.io.Serializable;

/**
 * Класс - поле класса program.Route
 */

public class Location implements Serializable {
    private Long x; //Поле не может быть null
    private Long y; //Поле не может быть null
    private long z; //????
    private String name; //Длина строки не должна быть больше 867, Поле может быть null

    public Location(Long x, Long y, long z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Long getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public long getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "program.Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }
}
