package View;
import Model.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Dave on 10/02/2015.
 */
public class OptionsPanel extends JPanel{
    private ArrayList<TableButton> optionsButtons;
    private MainSound sounds;

    public OptionsPanel(final MouseListener listener, MainSound sounds){
        setLayout(new GridLayout(1,3));
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.sounds = sounds;


        optionsButtons = new ArrayList<TableButton>();
        this.addButton(new TableButton(ButtonType.NEW_GAME,"NEW GAME"),listener);
        this.addButton(new TableButton(ButtonType.HELP,"HELP"),listener);
        this.addButton(new TableButton(ButtonType.SCOREBOARD,"SCORES"),listener);

    }

    /**
     * helper method for setting up buttons
     * @param tb
     * @param ml
     */
    private void addButton(TableButton tb, MouseListener ml){
        tb.addMouseListener(ml);
        optionsButtons.add(tb);
        this.add(tb);
    }





    //called by notifiers

    public void newGamePushed(){}

    public void helpPushed(){
        new HelpPanel();
    }

    public void statsPushed(ScoreBoard scoreBoard) {
        new StatsPanel(scoreBoard);
    }
}



class HelpPanel extends JFrame{
    //helpFrame
    HelpPanel(){
        super("HELP");
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
        setPreferredSize(new Dimension(450,375));
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });
        JLabel header = new JLabel("Welcome to Sheepshead!");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        JButton ok = new JButton("ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        add(header, BorderLayout.NORTH);
        pack();
        setVisible(true);
    }

}



class StatsPanel extends JPanel{
    StatsPanel(ScoreBoard scoreboard){
        setLayout(new GridLayout(5,2));
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    //creates row with description of stat and stat on right side
    private void createRow(String s, int n){
        JLabel label = new JLabel(s);
        label.setBorder(BorderFactory.createLineBorder(Color.black));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(label);
        JLabel stat = new JLabel("" + n);
        stat.setBorder(BorderFactory.createLineBorder(Color.black));
        stat.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(stat);
    }
}
