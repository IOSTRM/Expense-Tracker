package cz.cuni.mff.kuznietv;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * class for creating a custom scroll bar
 */
public class CustomScrollBarUI extends BasicScrollBarUI {
    private Color thumbColor = new Color(189,195,199);
    private Color trackColor = new Color(236,240,241);
    @Override
    protected void configureScrollBarColors(){
        super.configureScrollBarColors();

    }
    @Override
    protected JButton createDecreaseButton(int orientation){
        return createEmptyButton();
    }
    @Override
    protected JButton createIncreaseButton(int orientation){
        // Create an empty button for the increase button
        return createEmptyButton();
    }
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds){
        g.setColor(thumbColor);
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
    }
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds){
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    private JButton createEmptyButton(){
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        return button;
    }

}
