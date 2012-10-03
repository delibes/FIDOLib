/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fidolib.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Steen
 */
public class MouseAdaptorCountDownPanel extends MouseAdapter {

    /*
     * Parent reference
     * 
     */
    private CountDownPanel aCountDownPanel = null;
    /**
     * 
     */
    public MouseAdaptorCountDownPanel (CountDownPanel aCountDownPanel) {
        this.aCountDownPanel = aCountDownPanel;
    }
    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON3) {
            CountDownDialog aCountDownDialog = new CountDownDialog(null, true);
            aCountDownDialog.setLocationRelativeTo(aCountDownPanel);
            aCountDownDialog.setVisible(true);

        }

    }
}