package dopFiles;

import exceptions.FailedCheckException;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс, который хранится в коллекции
 */

public class Route implements Comparable<Route>, Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле может быть null
    private Location to; //Поле не может быть null
    private Long distance; //Поле может быть null, Значение поля должно быть больше 1

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "program.Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }


    /**
     * Сравнение объектов. Сравнение объектов идет в первую очередь по имени, потом по дистанции
     */
    @Override
    public int compareTo(Route route) {
        int result = getName().compareTo(route.getName());

        if (result == 0 && getDistance() != null && route.getDistance() != null) {
            result = getDistance().compareTo(route.getDistance());
        }
        return result;
    }
}