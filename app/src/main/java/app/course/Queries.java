package app.course;

public class Queries {
    /* ------------------------------ Authorization and Registration -----------------------------*/
    public static String getLogin() {
        String query = "SELECT login FROM user WHERE login = ?";
        return query;
    }

    public static String getPassword() {
        String query = "SELECT password FROM user WHERE login = ?";
        return query;
    }

    public static String newUser() {
        String query = "INSERT INTO user (name, login, password) VALUES (?, ?, ?)";
        return query;
    }

    public static String getIdUser() {
        String query = "SELECT ID_user FROM user where login = ?";
        return query;
    }

    public static String getAmounts() {
        String query = "SELECT name_accounts FROM accounts WHERE ID_USER = ?";
        return query;
    }

    public static String countCategories(String table) {
        String query = "SELECT COUNT(*) FROM " + table  + " WHERE ID_user = ?";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* ------------------------------------------- MAIN ------------------------------------------*/
    public static String setDefaultAmounts() {
        String query = "INSERT INTO accounts (ID_user, name_accounts, sum, ID_currency) VALUES (?, ?, ?, ?)";
        return query;
    }

    public static String getSumForOneDay() {
        String query = "SELECT category_income.ID_category, SUM(sub_sum), name_category FROM category_income, " +
                "sub_category_income WHERE category_income.ID_user = ? AND " +
                "category_income.ID_category = sub_category_income.ID_category AND date_last_entry = ? " +
                "GROUP BY sub_category_income.ID_category";
        return query;
    }

    public static String getSumForRange() {
        String query = "SELECT category_income.ID_category, SUM(sub_sum), name_category FROM category_income, " +
                "sub_category_income WHERE category_income.ID_user = ? and " +
                "category_income.ID_category = sub_category_income.ID_category and date_last_entry " +
                "BETWEEN ? and ? GROUP BY category_income.ID_category";
        return query;
    }

    public static String getSumForAllTime() {
        String query = "SELECT category_income.ID_category, SUM(sub_sum), name_category FROM category_income, " +
                "sub_category_income WHERE category_income.ID_user = ? and " +
                "category_income.ID_category = sub_category_income.ID_category";
        return query;
    }

    public static String getHistoryMapIncome(String part_of_query) {
        String query = "SELECT sub_category_income.name_sub_category, history_income.sum, sub_category_income.date_last_entry, " +
                "ID_category, history_income.date_last_entry FROM sub_category_income, history_income " +
                "WHERE ID_user = ? and sub_category_income.name_sub_category = history_income.name_sub_category and " +
                "name_category IN (" + part_of_query + ") ORDER BY history_income.date_last_entry";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* --------------------------------------- FRAGMENT GENERAL ----------------------------------*/
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ОБЪЕДИНИТЬ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public static String getCategoryIncome() {
        String query = "SELECT category_sum, name_category, category_icon, bg_category, ID_category " +
                "FROM category_income where ID_user = ?";
        return query;
    }

    public static String getCategoryExpense() {
        String query = "SELECT category_sum, name_category, category_icon, bg_category " +
                "FROM category_expense where ID_user = ?";
        return query;
    }

    public static String removeIncomeCategory() {
        String query = "DELETE FROM category_income where id_user = ? and name_category in (?)";
        return query;
    }

    public static String getNewCategoryId() {
        String query = "SELECT ID_category FROM category_income WHERE ID_user = ? and name_category = ?";
        return query;
    }
    // --------------------------------------------------------------------------------------------

    public static String setDefaultCategoryIncome(User user, String name_1, String name_2, String name_3,
                                                  int icon_1, int icon_2, int icon_3, String bg_1, String bg_2, String bg_3) {
        String query = "INSERT INTO category_income (ID_user, category_sum, name_category, category_icon, bg_category) VALUES( "
                + user.getID_user() + "," + 0 + ",\'" + name_1 + "\'," + icon_1 + ",\'" + bg_1 + "\')," + "( "
                + user.getID_user() + "," + 0 + ",\'" + name_2 + "\'," + icon_2 + ",\'" + bg_2 + "\')," + "( "
                + user.getID_user() + "," + 0 + ",\'" + name_3 + "\'," + icon_3 + ",\'" + bg_3 + "\')";
        return query;
    }

    public static String setDefaultCategoryExpense(User user, String name_1, String name_2,
                                                  int icon_1, int icon_2, String bg_1, String bg_2) {
        String query = "INSERT INTO category_expense (ID_user, category_sum, name_category, category_icon, bg_category) VALUES( "
                + user.getID_user() + "," + 0 + ",\'" + name_1 + "\'," + icon_1 + ",\'" + bg_1 + "\')," + "( "
                + user.getID_user() + "," + 0 + ",\'" + name_2 + "\'," + icon_2 + ",\'" + bg_2 + "\')";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/


    /* ------------------------------------ FRAGMENT_INCOME --------------------------------------*/
    public static String getIncomeForFragmentAdd() {
        String query = "SELECT name_category, category_add_icon FROM category_income WHERE ID_user " +
                "= ID_user = ?";
        return query;
    }

    public static String addNewIncomeCategory() {
        String query = "INSERT INTO category_income (ID_user, category_sum, name_category, category_icon, bg_category) VALUES " +
                "(?,?,?,?,?)";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* ---------------------------------- FRAGMENT_SUB_CATEGORY ----------------------------------*/
    public static String getSubCategories() {
        String query = "SELECT name_sub_category, sub_sum, date_last_entry, ID_sub_category FROM category_income, sub_category_income WHERE" +
                " category_income.ID_category = sub_category_income.ID_category and sub_category_income.ID_category = ?";
        return query;
    }

    public static String updateSumOfCategory() {
        String query = "UPDATE category_income SET category_sum = ? WHERE ID_category = ?";
        return query;
    }

    public static String removeItemFromSubCategory() {
        String query = "DELETE FROM sub_category_income where name_sub_category = ? and id_category = ?";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* ---------------------------------- DIALOG_ADD_SUB_CATEGORY --------------------------------*/
    public static String addSubCategory() {
        String query = "INSERT INTO sub_category_income (ID_category, name_sub_category, sub_sum, date_last_entry) " +
                "VALUES (?,?,?,?)";
        return query;
    }

    public static String addHistoryItem() {
        String query = "INSERT INTO history_income (date_last_entry, name_sub_category ,ID_user, name_category, sum)" +
                " VALUES (?,?,?,?, ?)";
        return query;
    }

    public static String getNewIdCategories(String names) {
        String query = "SELECT ID_category FROM category_income WHERE ID_user = ? and name_category IN (" +
                names + ")";
        return query;
    }

    /* -------------------------------------------------------------------------------------------*/

    /* ----------------------------------- FRAGMENT_HISTORY --------------------------------------*/
    public static String getNameOfCategories() {
        String query = "SELECT name_category FROM category_income where ID_user = ?";
        return query;
    }

    public static String getSubCategoriesById(String part_of_query) {
        String query = "SELECT name_sub_category, sub_sum, date_last_entry, category_income.ID_category " +
                "FROM sub_category_income, category_income WHERE category_income.ID_category = sub_category_income.ID_category and " +
                "sub_category_income.ID_category IN (" + part_of_query + ")";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/
}
