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
    private Dimension standardWindowSize;

    public OptionsPanel(final MouseListener listener, MainSound sounds){
        setLayout(new GridLayout(1,3));
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.sounds = sounds;
        standardWindowSize = new Dimension(300,475);

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
        new HelpFrame(standardWindowSize);
    }

    public void statsPushed(ScoreBoard scoreBoard) {
        new StatsFrame(scoreBoard,standardWindowSize);
    }
}



class HelpFrame extends JFrame{
    //helpFrame
    HelpFrame(Dimension size){
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
        setPreferredSize(size);
        setLayout(new FlowLayout());

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



class StatsFrame extends JFrame{
    private JTextArea statsArea;
    private Dimension windowSize,textAreaSize;

    StatsFrame(ScoreBoard scoreboard, Dimension size){
        super("Stats");
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
        setPreferredSize(size);
        setMinimumSize(size);
        this.windowSize = size;
        setContentPane(new JLabel(new StretchIcon("Textures/wood.JPG", false)));

        //add in scroll bars
        initScrollWindow(size);

        setPreferredSize(size);
        setLayout(new FlowLayout());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });

        pack();
        setVisible(true);

    }

    /**
     * adds in stats areas and scroll bar
     * @param windowSize overall size of window
     */
    private void initScrollWindow(Dimension windowSize){
        int width,height;
        width = windowSize.width / 3;
        height = windowSize.height / 2;
        statsArea = new JTextArea();
        textAreaSize = new Dimension(width,height);
        statsArea.setPreferredSize(textAreaSize);
        statsArea.setEditable(false);
        JScrollPane scroller = new JScrollPane(statsArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getContentPane().add(scroller);

    }


    /**
     //creates row with description of stat and stat on right side
     * @param s Stat name
     * @param n stats number
     */
    private void writeRow(String s, int n){
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
