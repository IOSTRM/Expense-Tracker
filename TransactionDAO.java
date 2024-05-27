package cz.cuni.mff.kuznietv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *  a class to get all transactions from a database
 */

public class TransactionDAO {
    public static List<Transaction> getAllTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        Connection connection = DataBaseConnection.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        try{
            ps = connection.prepareStatement("SELECT * FROM tracker");
            rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                Transaction transaction = new Transaction(id, type, description, amount);
                transactions.add(transaction);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return transactions;
    }
}
