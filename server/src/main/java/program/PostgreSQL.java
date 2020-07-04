package program;

import command.Command;
import command.CommandWithObj;
import command.RemoveById;
import dopFiles.*;
import exceptions.FailedCheckException;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static dopFiles.User.userFromHashPassword;

public class PostgreSQL extends Thread {
    AtomicBoolean killFlag;
    final String DB_URL;
    final String USER;
    final String PASS;
    ConcurrentLinkedQueue<Command> commands = new ConcurrentLinkedQueue<>();

    public PostgreSQL(String url, String user, String pass, AtomicBoolean killFlag) {
        DB_URL = url;
        USER = user;
        PASS = pass;
        this.killFlag = killFlag;
    }
    public void add(Command com) {
        commands.add(com);
    }
    public void run(){
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            while (!killFlag.get()) {
                if (!commands.isEmpty()) {
                    Command com = commands.poll();
                    switch (com.getCurrent()) {
                        case ADD:
                            addSQL((CommandWithObj) com, connection);
                            break;
                        case UPDATE:
                            updateSQL((CommandWithObj) com, connection);
                            break;
                        case REMOVE_BY_ID:
                            removeByIdSQL((RemoveById) com, connection);
                            break;
                        case CLEAR:
                            clearSQL(com, connection);
                            break;
                        case EXECUTE_SCRIPT:
                            //executeScriptSQL(com, connection);
                            break;
                        case REGISTER:
                            registerSQL(com, connection);
                            break;
                        default:
                            Writer.writeln("Неизвестная комманда в потоке SQL");
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            Writer.writeln("Не удалось установить соединение с Базой Данных.");
            e.printStackTrace();
        }
        killFlag.set(true);
    }

    public void setRoutes(Map<User, List<Route>> map) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)){
            for (User user : map.keySet()) {
                try (Statement statement = connection.createStatement()) {
                    ResultSet rs = statement.executeQuery("SELECT * FROM routes WHERE user_name = '" + user.login + "';");
                    while (rs.next()) {
                        try {
                            map.get(user).add(parseData(rs));
                        } catch (DateTimeParseException | FailedCheckException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Соединение прервано.");
                    e.printStackTrace();
                    killFlag.set(true);
                }
            }
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            e.printStackTrace();
            killFlag.set(true);
        }
    }

    private Route parseData(ResultSet rs) throws DateTimeParseException, SQLException, FailedCheckException {
        Route route = new Route();
        int id = Utils.routeIdCheck.checker(rs.getInt("id"));
        route.setId(id);

        route.setName(Utils.routeNameCheck.checker(rs.getString("name")));

        int cx = Utils.coordinatesXCheck.checker(rs.getInt("coordinates_x"));
        Long cy = Utils.coordinatesYCheck.checker(rs.getLong("coordinates_y"));
        route.setCoordinates(new Coordinates(cx, cy));

        ZonedDateTime dateTime = ZonedDateTime.parse(rs.getString("creationdate"));
        route.setCreationDate(dateTime);

        Long fromX = Utils.locationXYZCheck.checker(rs.getLong("from_x"));
        if (!rs.wasNull()) {
            Long fromY = Utils.locationXYZCheck.checker(rs.getLong("from_y"));
            long fromZ = Utils.locationXYZCheck.checker(rs.getLong("from_z"));
            String fromName = Utils.locationNameCheck.checker(rs.getString("from_name"));
            route.setFrom(new Location(fromX, fromY, fromZ, rs.wasNull() ? null : fromName));
        }
        Long toX = Utils.locationXYZCheck.checker(rs.getLong("to_x"));
        Long toY = Utils.locationXYZCheck.checker(rs.getLong("to_y"));
        long toZ = Utils.locationXYZCheck.checker(rs.getLong("to_z"));
        String toName = Utils.locationNameCheck.checker(rs.getString("to_name"));
        route.setTo(new Location(toX, toY, toZ, rs.wasNull() ? null : toName));
        Long distance = Utils.routeDistanceCheck.checker(rs.getLong("distance"));
        if (!rs.wasNull()) {
            Long dis = Utils.routeDistanceCheck.checker(distance);
            route.setDistance(dis);
        }
        return route;
    }

    public Map<User, List<Route>> getMapOfUsers() {
        Map<User, List<Route>> map = new ConcurrentHashMap<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)){
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM users");
                while (rs.next()) {
                    String login = rs.getString("login");
                    String hash = rs.getString("hash");
                    map.put(userFromHashPassword(login,hash), new CopyOnWriteArrayList<>());
                }
            } catch (SQLException e) {
                System.out.println("Соединение прервано.");
                e.printStackTrace();
                killFlag.set(true);
            }
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            e.printStackTrace();
            killFlag.set(true);
        }
        return map;
    }

    public int getIds() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)){
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT nextval('ids');");
                if (rs.next()) {
                    int id = (int) rs.getLong(1);
                    statement.execute("SELECT setval('ids', " + id + ", false);");
                    return id;
                }
            } catch (SQLException e) {
                System.out.println("Соединение прервано.");
                killFlag.set(true);
            }
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            killFlag.set(true);
        }
        return 0;
    }

    private void registerSQL(Command com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, hash) VALUES (? , ?);")) {
            statement.setString(1, com.getUser().login);
            statement.setString(2, com.getUser().hashPassword);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            killFlag.set(true);
        }
    }

    private void clearSQL(Command com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM routes WHERE user_name = ?;")) {
            statement.setString(1, com.getUser().login);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            killFlag.set(true);
        }
    }

    private void removeByIdSQL(RemoveById com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM routes WHERE id = ? and user_name = ?;")) {
            statement.setInt(1, com.returnObj());
            statement.setString(2, com.getUser().login);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            killFlag.set(true);
        }
    }

    private void updateSQL(CommandWithObj com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE routes SET (user_name, name, coordinates_x, coordinates_y, creationdate, from_x, from_y, from_z, from_name, to_x, to_y, to_z, to_name, distance) = ((?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?)) WHERE id = ? AND user_name = ?;")) {
            statement.setString(1, com.getUser().login);
            statement.setString(2, com.returnObj().getName());
            statement.setInt(3, com.returnObj().getCoordinates().getX());
            statement.setLong(4, com.returnObj().getCoordinates().getY());
            statement.setString(5, com.returnObj().getCreationDate().toString());
            if (com.returnObj().getFrom() != null) {
                statement.setLong(6, com.returnObj().getFrom().getX());
                statement.setLong(7, com.returnObj().getFrom().getY());
                statement.setLong(8, com.returnObj().getFrom().getZ());
                if (com.returnObj().getFrom().getName() != null)
                    statement.setString(9, com.returnObj().getFrom().getName());
                else
                    statement.setNull(9, Types.VARCHAR);
            } else {
                statement.setNull(6,Types.BIGINT);
                statement.setNull(7,Types.BIGINT);
                statement.setNull(8,Types.BIGINT);
                statement.setNull(9,Types.VARCHAR);
            }
            statement.setLong(10, com.returnObj().getTo().getX());
            statement.setLong(11, com.returnObj().getTo().getY());
            statement.setLong(12, com.returnObj().getTo().getZ());
            if (com.returnObj().getTo().getName() != null)
                statement.setString(13, com.returnObj().getTo().getName());
            else
                statement.setNull(13, Types.VARCHAR);
            if (com.returnObj().getDistance() != null)
                statement.setLong(14, com.returnObj().getDistance());
            else
                statement.setNull(14, Types.BIGINT);
            statement.setInt(15, com.returnObj().getId());
            statement.setString(16, com.getUser().login);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            killFlag.set(true);
        }
    }

    private void addSQL(CommandWithObj com, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO routes (user_name, name, coordinates_x, coordinates_y, creationdate, from_x, from_y, from_z, from_name, to_x, to_y, to_z, to_name, distance) VALUES ((?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?));")) {
            statement.setString(1, com.getUser().login);
            statement.setString(2, com.returnObj().getName());
            statement.setInt(3, com.returnObj().getCoordinates().getX());
            statement.setLong(4, com.returnObj().getCoordinates().getY());
            statement.setString(5, com.returnObj().getCreationDate().toString());
            if (com.returnObj().getFrom() != null) {
                statement.setLong(6, com.returnObj().getFrom().getX());
                statement.setLong(7, com.returnObj().getFrom().getY());
                statement.setLong(8, com.returnObj().getFrom().getZ());
                if (com.returnObj().getFrom().getName() != null)
                    statement.setString(9, com.returnObj().getFrom().getName());
                else
                    statement.setNull(9, Types.VARCHAR);
            } else {
                statement.setNull(6,Types.BIGINT);
                statement.setNull(7,Types.BIGINT);
                statement.setNull(8,Types.BIGINT);
                statement.setNull(9,Types.VARCHAR);
            }
            statement.setLong(10, com.returnObj().getTo().getX());
            statement.setLong(11, com.returnObj().getTo().getY());
            statement.setLong(12, com.returnObj().getTo().getZ());
            if (com.returnObj().getTo().getName() != null)
                statement.setString(13, com.returnObj().getTo().getName());
            else
                statement.setNull(13, Types.VARCHAR);
            if (com.returnObj().getDistance() != null)
                statement.setLong(14, com.returnObj().getDistance());
            else
                statement.setNull(14, Types.BIGINT);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("Соединение прервано.");
            e.printStackTrace();
            killFlag.set(true);
        }
    }
}
