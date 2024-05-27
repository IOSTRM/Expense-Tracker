package cz.cuni.mff.kuznietv;

import java.util.List;

/** a class which counts total value of income of a data base */

public class TransactionValuesCalculation {

    /** method to get all Incomes from a database. It chooses those fields
     * which has Income in their type
     * @param transactions list of transactions
     * @return sum of all incomes from transactions
     */
    public static double getTotalIncomes(List<Transaction> transactions){
        double totalIncome = 0;
        for(Transaction transaction : transactions){
            if("Income".equals(transaction.getType())){
                totalIncome += transaction.getAmount();
            }
        }

        return totalIncome;
    }

    /**
     * method to get all expenses from a database.
     * It chooses those fields which have Expense in their type
     * @param transactions list of all transactions in database
     * @return sum of all expenses in transactions
     */

    public static double getTotalExpenses(List<Transaction> transactions){
        double totalExpenses = 0;
        for(Transaction transaction : transactions){
            if("Expense".equals(transaction.getType())){
                totalExpenses += transaction.getAmount();
            }
        }

        return totalExpenses;
    }

    /**
     * a method which counts a total value, i.e. difference between Incomes and Expenses
     * @param transactions list of transactions
     * @return income - expense
     */

    public static double getTotalValue(List<Transaction> transactions){
        double income = getTotalIncomes(transactions);
        double expense = getTotalExpenses(transactions);
        return income - expense;
    }
}
