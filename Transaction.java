package cz.cuni.mff.kuznietv;

/**
 * transaction class which describes a transaction - id, type, description, amount
 */

public class Transaction {
    private int id;
    private String type;
    private String description;
    private double amount;
    public Transaction(int id, String type, String description, double amount){
        this.id = id;
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    /**
     * method to get id of the transaction
     * @return id of the transaction
     */
    public int getId(){
        return id;
    }

    /**
     * method to get a type of the transaction
     * @return type of the transaction
     */
    public String getType(){
        return type;
    }

    /**
     * method to get a description of the transaction
     * @return
     */
    public String getDescription(){
        return description;
    }

    /**
     * method to get an amount of the transaction
     * @return
     */
    public double getAmount(){
        return amount;
    }
}
