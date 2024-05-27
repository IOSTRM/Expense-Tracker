package cz.cuni.mff.kuznietv;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * main class of the program. It is responsible for the image of a program and logic of operations
 */

public class App {
    private JFrame frame;
    private JPanel titleBar;
    private JLabel titleLabel;
    private JLabel closeLabel;
    private JLabel minimizeLabel;
    private JPanel dashboardPanel;
    private JPanel buttonsPanel;
    private JButton addTransactionButton;
    private JButton removeTransactionButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private double totalAmount = 0.0;
    private ArrayList<String> dataPanelValues = new ArrayList<>();

    /**
     * Method creates a window of a program with buttons and labels.
     */

    public App(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(52, 73, 94)));

        titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(new Color(52,73,94));
        titleBar.setPreferredSize(new Dimension(frame.getWidth(), 30));
        frame.add(titleBar, BorderLayout.NORTH);

        titleLabel = new JLabel("Tracker");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setBounds(10,0,250,30);
        titleBar.add(titleLabel);

        closeLabel = new JLabel("x");
        closeLabel.setForeground(Color.WHITE);
        closeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        closeLabel.setBounds(frame.getWidth() - 50, 0, 30, 30);
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                System.exit(0);
            }
            @Override
            public void mouseEntered(MouseEvent e){
                closeLabel.setForeground(Color.red);
            }
            @Override
            public void mouseExited(MouseEvent e){
                closeLabel.setForeground(Color.white);
            }
        });
        titleBar.add(closeLabel);

        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        dashboardPanel.setBackground(new Color(255, 255, 255));
        frame.add(dashboardPanel,BorderLayout.CENTER);

        totalAmount = TransactionValuesCalculation.getTotalValue(TransactionDAO.getAllTransactions());
        dataPanelValues.add(String.format("-$%,.2f", TransactionValuesCalculation.getTotalExpenses(TransactionDAO.getAllTransactions())));
        dataPanelValues.add(String.format("$%,.2f", TransactionValuesCalculation.getTotalIncomes(TransactionDAO.getAllTransactions())));
        dataPanelValues.add("$"+totalAmount);

        addDataPanel("Expense", 0);
        addDataPanel("Income", 1);
        addDataPanel("Total", 2);

        addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setBackground(new Color(0, 255, 0));
        addTransactionButton.setForeground(Color.WHITE);
        addTransactionButton.setFocusPainted(false);
        addTransactionButton.setBorderPainted(false);
        addTransactionButton.setFont(new Font("Arial", Font.BOLD, 14));
        addTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTransactionButton.addActionListener((e) -> { showAddTransactionDialog(); });

        removeTransactionButton = new JButton("Remove Transaction");
        removeTransactionButton.setBackground(new Color(255, 0, 0));
        removeTransactionButton.setForeground(Color.WHITE);
        removeTransactionButton.setFocusPainted(false);
        removeTransactionButton.setBorderPainted(false);
        removeTransactionButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeTransactionButton.addActionListener((e) -> {
            removeSelectedTransaction();
        });

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BorderLayout(10, 5));
        buttonsPanel.add(addTransactionButton, BorderLayout.NORTH);
        buttonsPanel.add(removeTransactionButton, BorderLayout.SOUTH);
        dashboardPanel.add(buttonsPanel);

        // Set up the transaction table
        String[] columnNames = {"ID","Type","Description","Amount"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                // Make all cells non-editable
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        configureTransactionTable();

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        configureScrollPane(scrollPane);
        dashboardPanel.add(scrollPane);

        frame.setVisible(true);
    }

    /**
     * method which converts a string starting with $- into a double. Needed when we update sums of
     * incomes and updates
     * @param value
     * @return the number in double format
     */
    private String fixNegativeValueDisplay(double value){
        String newVal = String.format("$%.2f", value);
        if(newVal.startsWith("$-")){
            String numericPart = newVal.substring(2);
            newVal = "-$"+numericPart;
        }

        return newVal;
    }

    /**
     * method which removes selected transaction. It retrieves an amount which is needed to be removed
     * and deletes the transaction and decreases its amount from total Value and respective Income or Expense
     */

    private void removeSelectedTransaction(){
        int selectedRow = transactionTable.getSelectedRow();
        if(selectedRow != -1){
            int transactionId = (int) transactionTable.getValueAt(selectedRow, 0);
            String type = transactionTable.getValueAt(selectedRow, 1).toString();
            String amountStr = transactionTable.getValueAt(selectedRow, 3).toString();
            String temp = amountStr.replace("$", "").replace("\u00A0", "").replace(",", "");
            double amount = Double.parseDouble(temp.substring(0, temp.length() - 2));
            if(type.equals("Income")){ totalAmount -= amount; }
            else{ totalAmount += amount; }
            JPanel totalPanel = (JPanel) dashboardPanel.getComponent(2);
            totalPanel.repaint();
            int indexToUpdate = type.equals("Income") ? 1 : 0;
            String currentValue = dataPanelValues.get(indexToUpdate);
            String tempCurr = currentValue.replace("$", "").replace("\u00A0", "").replace(",", "");
            double currentAmount = Double.parseDouble(tempCurr.substring(0, tempCurr.length() - 2));
            double updatedAmount = currentAmount + (type.equals("Income") ? -amount : amount);
            if(indexToUpdate == 1){
                dataPanelValues.set(indexToUpdate, String.format("$%,.2f", updatedAmount));
            }
            else{ dataPanelValues.set(indexToUpdate, fixNegativeValueDisplay(updatedAmount)); }

            // repaint the data panel
            JPanel dataPanel = (JPanel) dashboardPanel.getComponent(indexToUpdate);
            dataPanel.repaint();
            tableModel.removeRow(selectedRow);
            removeTransactionFromDatabase(transactionId);
        }
    }

    /**
     * remove a transaction from database
     * @param transactionId
     */

    private void removeTransactionFromDatabase(int transactionId){

        try {
            Connection connection = DataBaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM tracker WHERE id = ?");
            ps.setInt(1, transactionId);
            ps.executeLargeUpdate();
            System.out.println("Transaction Removed");
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * method which creates a window for adding a new transaction
     */
    private void showAddTransactionDialog(){
        JDialog dialog = new JDialog(frame, "Add Transaction", true);
        dialog.setSize(400,250);
        dialog.setLocationRelativeTo(frame);
        JPanel dialogPanel = new JPanel(new GridLayout(4, 0, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialogPanel.setBackground(Color.LIGHT_GRAY);
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombobox = new JComboBox<>(new String[]{"Expense","Income"});
        typeCombobox.setBackground(Color.WHITE);
        typeCombobox.setBorder(BorderFactory.createLineBorder(Color.yellow));
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();
        descriptionField.setBorder(BorderFactory.createLineBorder(Color.yellow));

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        amountField.setBorder(BorderFactory.createLineBorder(Color.yellow));

        // Create and configure the "Add" button
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(0, 0, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener((e) -> {
            addTransaction(typeCombobox, descriptionField, amountField);
        });
        dialogPanel.add(typeLabel);
        dialogPanel.add(typeCombobox);
        dialogPanel.add(descriptionLabel);
        dialogPanel.add(descriptionField);
        dialogPanel.add(amountLabel);
        dialogPanel.add(amountField);
        dialogPanel.add(new JLabel());
        dialogPanel.add(addButton);

        DataBaseConnection.getConnection();

        dialog.add(dialogPanel);
        dialog.setVisible(true);

    }

    /**
     * method which adds a transaction to the database
     * @param typeCombobox
     * @param descriptionField
     * @param amountField
     */
    private void addTransaction(JComboBox<String> typeCombobox, JTextField descriptionField, JTextField amountField){
        String type = (String) typeCombobox.getSelectedItem();
        String description = descriptionField.getText();
        String amount = amountField.getText();
        // convert a string into double. the following pattern is used several times
        double newAmount = Double.parseDouble(amount.replace("$", "").replace("\u00A0", "").replace(",", ""));
        if(type.equals("Income")){ totalAmount += newAmount; }
        else{ totalAmount -= newAmount; }
        JPanel totalPanel = (JPanel) dashboardPanel.getComponent(2);
        totalPanel.repaint();
        int indexToUpdate = type.equals("Income") ? 1 : 0;
        String currentValue = dataPanelValues.get(indexToUpdate);
        String temp = currentValue.replace("$", "").replace("\u00A0", "").replace(",", "");
        double currentAmount = Double.parseDouble(temp.substring(0, temp.length() - 2));
        double updatedAmount = currentAmount + (type.equals("Income") ? newAmount : -newAmount);
        // Update the data panel with the new amount
        if(indexToUpdate == 1){ // income
            dataPanelValues.set(indexToUpdate, String.format("$%,.2f", updatedAmount));
        }
        else{ dataPanelValues.set(indexToUpdate, fixNegativeValueDisplay(updatedAmount)); }
        JPanel dataPanel = (JPanel) dashboardPanel.getComponent(indexToUpdate);
        dataPanel.repaint();
        try {
            Connection connection = DataBaseConnection.getConnection();
            String insertQuery = "INSERT INTO tracker (type, description, amount) VALUES (?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insertQuery);

            ps.setString(1, type);
            ps.setString(2, description);
            ps.setDouble(3, Double.parseDouble(amount));
            ps.executeUpdate();
            System.out.println("Data inserted successfully.");

            tableModel.setRowCount(0);
            populateTableTransactions();

        } catch (SQLException ex) {
            System.out.println("Error - Data not inserted.");
        }
    }

    /**
     * method which adds a new data to the table
     */
    private void populateTableTransactions(){

        for(Transaction transaction : TransactionDAO.getAllTransactions()){
            Object[] rowData = { transaction.getId(), transaction.getType(),
                    transaction.getDescription(), transaction.getAmount() };
            tableModel.addRow(rowData);
        }
    }

    /**
     * method which configures a transaction table
     */
    private void configureTransactionTable(){
        transactionTable.setBackground(new Color(236,240,241));
        transactionTable.setRowHeight(30);
        transactionTable.setShowGrid(false);
        transactionTable.setBorder(null);
        transactionTable.setFont(new Font("Arial",Font.ITALIC,16));
        transactionTable.setDefaultRenderer(Object.class, new TransactionTableCellRenderer());
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        populateTableTransactions();

        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setForeground(Color.red);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 18));
        tableHeader.setDefaultRenderer(new GradientHeaderRenderer());
    }

    /**
     * method which configures a scrollpane, which is needed in case if we have a lot of transactions, and we want to see them all.
     * @param scrollPane
     */
    private void configureScrollPane(JScrollPane scrollPane){

        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(750, 300));

    }
    /**
     * mrthod to add a data panel
     * @param title title of a panel
     * @param index index of a panel
     */
    private void addDataPanel(String title, int index){
        JPanel dataPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if(title.equals("Total")) {
                    drawDataPanel(g2d, title, fixNegativeValueDisplay(totalAmount), getWidth(), getHeight());
                } else{
                    drawDataPanel(g2d, title, dataPanelValues.get(index), getWidth(), getHeight());
                }
            }
        };
        dataPanel.setLayout(new GridLayout(2, 1));
        dataPanel.setPreferredSize(new Dimension(170, 100));
        dataPanel.setBackground(new Color(255,255,255));
        dataPanel.setBorder(new LineBorder(new Color(149,165,166),2));
        dashboardPanel.add(dataPanel);
    }

    /**
     * method to draw a data panel
     * @param g graphics
     * @param title title of a panel
     * @param value value of a cell
     * @param width width of a panel
     * @param height height of a panel
     */
    private void drawDataPanel(Graphics g, String title, String value, int width, int height){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(new Color(255,255,255));
        g2d.fillRoundRect(0, 0, width, height, 20, 20);
        g2d.setColor(new Color(236,240,241));
        g2d.fillRect(0, 0, width, 40);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(title, 20, 30);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(value, 20, 75);
    }

    /**
     * main method which runs the program
     * @param args
     */
    public static void main(String[] args){
        new App();
    }
}
