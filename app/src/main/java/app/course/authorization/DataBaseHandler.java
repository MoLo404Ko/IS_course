package app.course.authorization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseHandler {
    private final static String host = "djudoklw.beget.tech:3306";
    private final static String db = "djudoklw_course";
    private final static String user = "djudoklw_course";
    private final static String password = "IFpknCkS2b";
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
