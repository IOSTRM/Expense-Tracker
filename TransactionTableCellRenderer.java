package cz.cuni.mff.kuznietv;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * class for custom cell renderer of a table
 */
public class TransactionTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String type = (String) table.getValueAt(row, 1);
        if(isSelected){
            c.setForeground(Color.BLACK);
            c.setBackground(Color.ORANGE);
        }
        else
        {
            if("Income".equals(type)){
                c.setBackground(new Color(144, 238, 144));
            }
            else{
                c.setBackground(new Color(255,99,71));
            }
        }

        return c;
    }
}
