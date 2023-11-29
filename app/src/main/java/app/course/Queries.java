package app.course;

public class Queries {
    /* -------------------------------- АВТОРИЗАЦИЯ И РЕГИСТРАЦИЯ --------------------------------*/
    public static String getLogin(String login) {
        String query = "SELECT login FROM user WHERE login = " + "\'" + login + "\'";
        return query;
    }

    public static String getPassword(String login) {
        String query = "SELECT password FROM user WHERE login = " + "\'" + login + "\'";
        return query;
    }

    public static String newUser(String login, String password, String name) {
        String query = "INSERT INTO user (name, login, password) VALUES (\'" + name + "\',\'" + login + "\',\'" + password + "\')";
        return query;
    }

    public static String getIdUser(String login) {
        String query = "SELECT ID_user FROM user where login = " + "\'" + login + "\'";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* ------------------------------------------- MAIN ------------------------------------------*/
    public static String getAmounts(User user) {
        String query = "SELECT name_accounts FROM accounts WHERE ID_USER = " + User.getUser();
        return query;
    }

    public static String setDefaultAmounts(User user) {
        String query = "INSERT INTO accounts VALUES (" + user.getID_user() + "," + "\'Основной счет\'" +
                "," + 0 + "," + 1;
        return query;
    }

}
