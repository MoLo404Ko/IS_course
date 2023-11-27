package app.course.authorization;

public class Queries {
    public static String getLogin(String login) {
        String query = "SELECT login FROM user WHERE login = " + "\'" + login + "\'";
        return query;
    }

    public static String newUser(String login, String password, String name) {
        String query = "INSERT INTO user VALUES (\'" + login + "\',\'" + password + "\',\'" + name + "\')";
        return query;
    }
}
