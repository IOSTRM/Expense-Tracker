package cz.cuni.mff.kuznietv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * a class which guarantees a connection to the created database. Nothing special inside it, basic way of creating a connection.
 */

public class DataBaseConnection
{
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/expensetracker";
    private static final String USER = "root";
    private static final String PASSWORD = "vova";

    public static Connection getConnection()
    {
        Connection connection = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        } catch (ClassNotFoundException | SQLException ex){
            System.out.println(ex);
        }
        return connection;
    }


}
