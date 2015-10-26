package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Dave on 11/03/2015.
 * this class is a basic popup window
 * which displays a basic message for the user
 */
public class PopUpFrame extends JFrame {

    public PopUpFrame(String message) {
        super("Dave's SharkSweeper: Notification");
        init(message, 200, 125);
    }

    public PopUpFrame(String message, int xSize, int ySize) {
        super("Dave's SharkSweeper: Notification");
        init(message, xSize, ySize);
    }

    private void init(String message, int xSize, int ySize) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(xSize, ySize));
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });

        ActionListener okEvent = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        PopUpPanel popUp = new PopUpPanel(message, okEvent);
        Container contentPane = getContentPane();
        contentPane.add(popUp);
        pack();
        setVisible(true);
    }


}

class PopUpPanel extends JPanel {
    PopUpPanel(String s, final ActionListener okEvent) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel message = new JLabel(s);
        message.setHorizontalAlignment(JLabel.CENTER);
        JButton okButton = new JButton("ok");
        okButton.setHorizontalAlignment(JLabel.CENTER);
        okButton.addActionListener(okEvent);

        this.add(message, BorderLayout.CENTER);
        this.add(okButton, BorderLayout.SOUTH);
    }

}