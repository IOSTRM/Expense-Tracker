package cz.cuni.mff.kuznietv;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Custom table header renderer with gradient background
 */
public class GradientHeaderRenderer extends JLabel implements TableCellRenderer {

    private final Color startColor = new Color(192,192,192);
    private final Color endColor = new Color(50,50,50);

    public GradientHeaderRenderer(){
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD,22));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, Color.YELLOW),
                BorderFactory.createEmptyBorder(2, 5, 2, 5))
        );
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setText(value.toString());
        return this;
    }
    @Override
    protected void paintComponent(Graphics g){

        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        GradientPaint gradientPaint = new GradientPaint(
                0, 0, startColor,width, 0, endColor);

        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, width, height);

        super.paintComponent(g);
    }

}
