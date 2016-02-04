package View;

import Model.Card;
import Model.Game;
import Model.ScoreBoard;
import Model.Value;

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
        setContentPane(new JLabel(new StretchIcon("Textures/wood.jpg", false)));
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
        repaint();
        return table.refresh(g);

    }


    /* in from notifier*/

    /**
     * creates Jdialog for player to choose yes or no prompt
     * @param prompt message displayed to user
     * @return String (testing done in game to assert 'y' or 'no')
     */
    public String yOrN(String prompt){
        int n = JOptionPane.showConfirmDialog(
                this,
                prompt,
                "",
                JOptionPane.YES_NO_OPTION
        );
        if(n==0)
            return "y";
        return "n";
    }
    public Card getPlayerCard(String prompt){
        return table.getPlayerCard(prompt);
    }
    public void playerCardPushed(Card card){
        table.playerCardPushed(card);
    }

    /*out to options*/
    public void helpPushed() {
        options.helpPushed();
    }

    public void statsPushed(ScoreBoard s) {
        options.statsPushed(s);
    }

    /*out to table*/


}
