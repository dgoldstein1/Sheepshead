package View;

import Model.Game;
import Model.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Dave on 11/2/2015.
 * <p/>
 * Maine frame holding all relevant panels
 */
public class SheepsheadMainFrame extends JFrame {
    private Container contentPane;
    private GamePanel table;
    private OptionsPanel options;
    private MainSound sounds;
    private Dimension standardSize;

    public SheepsheadMainFrame(Game g, final MouseListener listener) {
        super("Sheaphead");
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
        standardSize = new Dimension(1000,730);
        setPreferredSize(standardSize);
        setMinimumSize(standardSize);
        setContentPane(new JLabel(new StretchIcon("Textures/wood.JPG", false)));
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });

        contentPane = getContentPane();
        sounds = new MainSound();
        initPanels(listener, g);
        pack();
        setVisible(true);
    }

    /**
     * creates and adds panels to this frame
     *
     * @param listener mouselistener
     * @param g        game from model
     * @return success
     */
    private boolean initPanels(MouseListener listener, Game g) {
        Font standardFont = new Font("Helvetica",Font.PLAIN,12);

        UIManager.put("Button.font", standardFont);
        UIManager.put("Label.font", standardFont);

        table = new GamePanel(g, listener);
        options = new OptionsPanel(listener, sounds);
        contentPane.add(table, BorderLayout.CENTER);
        contentPane.add(options, BorderLayout.NORTH);
        return true;
    }


    /**
     * refreshes all values in GUI from model
     * called by notifier
     *
     * @param g Game from model
     * @return success
     */
    public boolean refresh(Game g) {
        return table.refresh(g);
    }


    /* in from notifier*/


    /*out to options*/
    public void helpPushed() {
        options.helpPushed();
    }

    public void statsPushed(ScoreBoard s) {
        options.statsPushed(s);
    }

    /*out to table*/


}
