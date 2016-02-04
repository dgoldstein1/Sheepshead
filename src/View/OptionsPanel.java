package View;

import Model.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Dave on 10/02/2015.
 */
public class OptionsPanel extends JPanel {
    private ArrayList<TableButton> optionsButtons;
    private MainSound sounds;
    private Dimension standardWindowSize;

    public OptionsPanel(final MouseListener listener, MainSound sounds) {
        setLayout(new GridLayout(1, 3));
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.sounds = sounds;
        standardWindowSize = new Dimension(800,500);

        optionsButtons = new ArrayList<TableButton>();
        this.addButton(new TableButton(ButtonType.NEW_GAME, "NEW GAME"), listener);
        this.addButton(new TableButton(ButtonType.HELP, "HELP"), listener);
        this.addButton(new TableButton(ButtonType.SCOREBOARD, "SCORES"), listener);

    }

    /**
     * helper method for setting up buttons
     *
     * @param tb
     * @param ml
     */
    private void addButton(TableButton tb, MouseListener ml) {
        tb.addMouseListener(ml);
        optionsButtons.add(tb);
        this.add(tb);
    }


    //called by notifiers

    public void newGamePushed() {
    }

    public void helpPushed() {
        new HelpFrame(standardWindowSize);
    }

    public void statsPushed(ScoreBoard scoreBoard) {
        new StatsFrame(scoreBoard, standardWindowSize);
    }
}


class HelpFrame extends JFrame {
    //helpFrame
    HelpFrame(Dimension size) {
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


class StatsFrame extends JFrame {
    private SheepsheadStatsTable statsArea;
    private Dimension windowSize, textAreaSize;

    StatsFrame(ScoreBoard scoreboard, Dimension size) {
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
        this.windowSize = size;
        setContentPane(new JLabel(new StretchIcon("Textures/wood.jpg", false)));

        //add in scroll bars
        initScrollWindow(scoreboard);

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
     * initializes table with scoreboard values
     */
    private void initScrollWindow(ScoreBoard sb) {

        String[][] data = parseData(sb);
        String[] columns = parseColumns(sb);
        if (data[0].length != columns.length) {
            new PopUpFrame("columns and data not compatible" +
                    "       colums = " + columns.length + " but data[0]" + data[0].length);
            this.dispose(); //exit
        }

        statsArea = new SheepsheadStatsTable(this, windowSize, data, columns);

    }

    /**
     * @param sb Scoreboard of curr game
     * @return list of players in game as strings
     */
    private String[] parseColumns(ScoreBoard sb) {
        String[] columns = new String[6];
        columns[0] = "Round";
        for (int i = 1; i < 6; i++) {
            columns[i] = sb.getNonStaticPlayers()[i - 1].getUsername();
        }
        return columns;
    }

    /**
     * given sb gets round points for game
     *
     * @param sb Scoreboard
     * @return String[][] of round data where:
     * String[round][playerId+1] = roundPoint
     */
    private String[][] parseData(ScoreBoard sb) {
        String[][] data = new String[sb.roundsPlayed()][6];
        int[] roundPlayerPoints;

        for (int round = 0; round < sb.roundsPlayed(); round++) {

            data[round][0] = round + 1 + ""; //set column number

            roundPlayerPoints = sb.getListOfRound().get(sb.roundsPlayed() - round - 1).getPlayerScores();

            for (int playerId = 1; playerId < 6; playerId++) {

                data[round][playerId] = roundPlayerPoints[playerId - 1] + "";
               // System.out.print(data[round][playerId] + " - ");
            }
            System.out.println("");
        }

        return data;
    }



}
