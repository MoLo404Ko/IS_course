package app.course.authorization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseHandler {
    private final static String host = "sql12.freesqldatabase.com:3306";
    private final static String db = "sql12663365";
    private final static String user = "sql12663365";
    private final static String password = "rGhvb6ITkK";
    private static DataBaseHandler dataBaseHandler = new DataBaseHandler();

    public Connection connect(Connection conn) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?connectionTimeZone=UTC",
                user, password);
        return conn;
    }

    public void closeConnect(Connection conn) throws SQLException {
        conn.close();
    };

    public static DataBaseHandler getDataBaseHadler() {
        return dataBaseHandler;
    }
}
