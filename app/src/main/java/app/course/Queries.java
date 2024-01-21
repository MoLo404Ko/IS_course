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

    public static String addNewAccount() {
        String query = "INSERT INTO accounts (ID_user, name_accounts, sum, ID_currency) VALUES (?, ?, ?, ?)";
        return query;
    }

    /* -------------------------------------------------------------------------------------------*/

    /* ------------------------------------------- MAIN ------------------------------------------*/
    public static String getIdAccount() {
        String query = "SELECT accounts.id_accounts FROM accounts WHERE accounts.ID_user = ? and " +
                "accounts.name_accounts = ?";
        return query;
    }

    public static String getHistoryMapIncome() {
        String query = "SELECT date_entry, history_income.name_income, sum, bg_income, income_icon, income.ID_income FROM history_income, income WHERE history_income.id_income = " +
                "income.ID_income and ID_user = ?";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* --------------------------------------- FRAGMENT GENERAL ----------------------------------*/
    public static String getCategoryIncome() {
        String query = "SELECT income_sum, name_income, income_icon, bg_income, ID_income " +
                "FROM income where ID_user = ?";
        return query;
    }

    public static String getCategoryExpense() {
        String query = "SELECT income_sum, name_income, income_icon, bg_expense " +
                "FROM expense where ID_user = ?";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/


    /* ------------------------------------ FRAGMENT_INCOME --------------------------------------*/
    public static String addNewIncomeCategory() {
        String query = "INSERT INTO income (ID_user, income_sum, name_income, income_icon, " +
                "bg_income, ID_accounts) VALUES (?,?,?,?,?,?)";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* ---------------------------------- FRAGMENT_SUB_CATEGORY ----------------------------------*/
    public static String updateSumOfCategory() {
        String query = "UPDATE income SET income_sum = ? WHERE ID_income = ?";
        return query;
    }

    public static String removeItemFromSubCategory() {
        String query = "DELETE FROM sub_category_income where name_sub_category = ? and ID_income = ?";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/

    /* ---------------------------------- DIALOG_ADD_SUB_CATEGORY --------------------------------*/
    public static String addSubCategory() {
        String query = "INSERT INTO sub_category_income (ID_income, name_sub_category, sub_sum) " +
                "VALUES (?,?,?)";
        return query;
    }

    public static String addHistoryItem() {
        String query = "INSERT INTO history_income (date_entry, name_income, sum, ID_income)" +
                " VALUES (?,?,?,?)";
        return query;
    }

    public static String getNewIdCategories(String names) {
        String query = "SELECT ID_income FROM income WHERE ID_user = ? and name_income IN (" +
                names + ")";
        return query;
    }

    public static String updateSumSubCategory() {
        String query = "UPDATE sub_category_income SET sub_sum = ? WHERE ID_income = ? and name_sub_category = ?";
        return query;
    }

    /* -------------------------------------------------------------------------------------------*/

    /* ----------------------------------- FRAGMENT_HISTORY --------------------------------------*/
    public static String getSubCategoriesById(String part_of_query) {
        String query = "SELECT name_sub_category, sub_sum, income.ID_income " +
                "FROM sub_category_income, income WHERE income.ID_income = sub_category_income.ID_income and " +
                "sub_category_income.ID_income IN (" + part_of_query + ")";
        return query;
    }
    /* -------------------------------------------------------------------------------------------*/
}
