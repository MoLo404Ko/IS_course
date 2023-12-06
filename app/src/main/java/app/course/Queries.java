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
        String query = "SELECT name_accounts FROM accounts WHERE ID_USER = " + user.getID_user();
        return query;
    }

    public static String setDefaultAmounts(User user) {
        String query = "INSERT INTO accounts (ID_user, name_accounts, sum, ID_currency) VALUES (" + user.getID_user() + "," + "\'Основной счет\'" +
                "," + 0 + "," + 1 + ")";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* --------------------------------------- FRAGMENT GENERAL ----------------------------------*/
    public static String getCategory(User user) {
        String query = "SELECT category_sum, name_category, category_icon, bg_category " +
                "FROM category_income where ID_user = " + user.getID_user();
        return query;
    }

//    public static String setDefSumCategory(User user) {
//        String query = "INSERT INTO category_sum (ID_user, category_sum, name_category) VALUES (" +
//                user.getID_user() + "," + 0 + ",\'Зарплата\')";
////                user.getID_user() + "," + 0 + ",\'Подарки\'),(" +
////                user.getID_user() + "," + 0 + ",\'Инвестиции\')";
//        return query;
//    }

    public static String countCategories(User user) {
        String query = "SELECT COUNT(*) FROM category_income WHERE ID_user = " + user.getID_user();
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/
}
