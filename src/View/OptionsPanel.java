package View;

import Model.Game;
import Model.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dave on 10/02/2015.
 */
public class OptionsPanel extends JPanel {
    private ArrayList<TableButton> optionsButtons;
    private MainSound sounds;
    private Dimension standardWindowSize;
    private StatsFrame statsFrame;
    private HelpFrame helpFrame;
    private SettingsFrame optionsFrame;
    private MouseListener listener;

    public OptionsPanel(final MouseListener listener, MainSound sounds) {
        setLayout(new GridLayout(1, 3));
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.sounds = sounds;
        this.listener = listener;
        standardWindowSize = new Dimension(800,600);

        optionsButtons = new ArrayList<TableButton>();

        initButton(ButtonType.SETTINGS, " ", listener);
        initButton(ButtonType.HELP, " ", listener);
        initButton(ButtonType.SCOREBOARD, " ", listener);
    }

    /**
     * private method for adding buttons
     * @param type
     * @param name
     * @param listener
     */
    private void initButton(ButtonType type, String name, MouseListener listener){
        TableButton tb = new TableButton(type, name);
        if(type.equals(ButtonType.SETTINGS)){
            tb.setIcon(new StretchIcon("Icons/settings.png", true));
        }
        else if(type.equals(ButtonType.HELP)){
            tb.setIcon(new StretchIcon("Icons/about.png",true));
        }
        else if(type.equals(ButtonType.SCOREBOARD)){
            tb.setIcon(new StretchIcon("Icons/stats.png",true));
        }
        tb.addMouseListener(listener);
        optionsButtons.add(tb);
        this.add(tb);
    }

    public boolean refesh(ScoreBoard sb) {
        if (statsFrame != null) {
            statsFrame.refresh(sb);
        }
        return true;

    }


    //called by notifiers

    public void optionsPushed(Game g) {
        optionsFrame = new SettingsFrame(g, sounds);
    }

    public void helpPushed() {
        helpFrame = new HelpFrame(standardWindowSize);
    }

    public void statsPushed(ScoreBoard scoreBoard) {
        statsFrame = new StatsFrame(scoreBoard, standardWindowSize);
    }

    //getters / setters
    public boolean statsDisplayed() {
        return statsFrame != null;
    }

    public boolean helpDisplayed() {
        return helpFrame != null;
    }

    public boolean optionsDisplayed(){
        return optionsFrame!=null;
    }

}


class HelpFrame extends JFrame {

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
        setContentPane(new JLabel(new StretchIcon("Sheep_Photos/rolling_hills.jpg", false)));
        setPreferredSize(size);
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

        JTextArea about = new JTextArea("" +
                "Sheapshead is a very complicated game originating in XXX.... \n" +
                "I learned the game visiting family in the Mid-West.  For more information \n" +
                "on rules and history, see: \n \n" +
                "\t https://en.wikipedia.org/wiki/Sheepshead_%28game%29 \n" +
                "\t https://www.pagat.com/schafk/shep.html \n" +
                "\t http://www.tc.umn.edu/~jds/games/sheepshead/sheepshead-rules.pdf \n"
        );
        about.setFont(new Font("Helvetica",Font.BOLD,12));
        about.setEditable(false);
        about.setOpaque(false);

        JTextArea copyright = new JTextArea("" +
                "This project was completed as part of a culminating capstone project at Macalester College, \n" +
                "2016.  For more info contact David goldstein at Dgoldstein01@gmail.com"
        );
        copyright.setFont(new Font("Helvetica",Font.ITALIC,12));
        copyright.setEditable(false);
        copyright.setOpaque(false);

        JPanel infoHolder = new JPanel(new GridLayout(3,1));
        infoHolder.setOpaque(false);

        infoHolder.add(about);
        infoHolder.add(copyright);

        infoHolder.setOpaque(false);
        add(header, BorderLayout.NORTH);
        add(infoHolder,BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

}


class StatsFrame extends JFrame {
    private StatsTablePanel gameStats, playerStats;
    private Dimension windowSize, tableSize;
    private JPanel jpanel;
    private int showingRound = 0;

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
        this.tableSize = new Dimension(windowSize.width, windowSize.height / 3);
        setContentPane(new JLabel(new StretchIcon("Sheep_Photos/rolling_hills2.jpg", false)));
        gameStats = playerStats = null;
        setLayout(new FlowLayout());

        //add stats panels to jpanel
        jpanel = new JPanel(new BorderLayout());
        jpanel.setOpaque(false);
        initGameStats(scoreboard);
        initPlayerStats(scoreboard);
        jpanel.add(gameStats, BorderLayout.NORTH);
        jpanel.add(playerStats, BorderLayout.SOUTH);
        this.add(jpanel);
        setPreferredSize(size);

        pack();
        setVisible(true);

    }

    /**
     * initializes table with scoreboard values
     */
    private void initGameStats(ScoreBoard sb) {

        String[][] data = parseData(sb);
        String[] columns = parseColumns(sb);
        if (data[0].length != columns.length) {
            new PopUpFrame("columns and data not compatible" +
                    "       colums = " + columns.length + " but data[0]" + data[0].length);
            this.dispose(); //exit
        }

        gameStats = new StatsTablePanel(tableSize, data, columns);

    }

    /**
     * stat panel for player-specific stats
     *
     * @param sb
     */
    private void initPlayerStats(ScoreBoard sb) {
        String[][] data = sb.getPlayerStats();
        playerStats = new StatsTablePanel(tableSize, data, new String[]{"Player Stats", ""});

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
            }
        }
        return data;
    }

    /**
     * called by frame to refresh stats
     *
     * @return success
     */
    public boolean refresh(ScoreBoard sb) {
        if(showingRound != sb.currRound()){
            gameStats.updateTableData(parseData(sb));
            playerStats.updateTableData(sb.getPlayerStats());
            showingRound = sb.currRound();
        }
        return true;
    }


}

class SettingsFrame extends JFrame {
    JPanel settings, apply;
    JCheckBox playEffects, playMusic, launchDebugger;
    JSlider gameSpeed;
    JButton applyChanges;
    MainSound sounds;
    Game g;

    SettingsFrame(Game g, MainSound sounds) {
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
        setPreferredSize(new Dimension(269,275));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });
        setLayout(new BorderLayout());
        this.g = g;
        this.sounds = sounds;
        init();

        pack();
        setVisible(true);
    }

    /**
     * inits components of this frame
     */
    private void init(){
        //initiatlization
        playEffects = new JCheckBox("");
        playEffects.setHorizontalAlignment(JCheckBox.CENTER);
        playEffects.setSelected(!sounds.effectsMuted());
        playMusic = new JCheckBox("");
        playMusic.setHorizontalAlignment(JCheckBox.CENTER);
        playMusic.setSelected(!sounds.musicMuted());
        gameSpeed = new JSlider(1,70,g.getGameSpeed());
        launchDebugger = new JCheckBox("");
        launchDebugger.setHorizontalAlignment(JCheckBox.CENTER);
        launchDebugger.setSelected(g.debuggerRunning());

        applyChanges = new JButton("apply changes");

        //apply changes + button
        applyChanges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sounds.setEffectsMuted(!playEffects.isSelected());
                sounds.setMusicMuted(!playMusic.isSelected());
                g.setDebuggerRunning(launchDebugger.isSelected());
                g.setGameSpeed(gameSpeed.getValue());
                setVisible(false);
                dispose();
            }
        });

        //add to JPabels / frame
        settings = new JPanel(new GridLayout(4,2));
        addLabel(settings,"Play Sound Effects: ");
        settings.add(playEffects);
        addLabel(settings,"Play Music: ");
        settings.add(playMusic);
        addLabel(settings,"Game Speed (%)");
        settings.add(gameSpeed);
        addLabel(settings,"Run Debugger: ");
        settings.add(launchDebugger);

        add(settings, BorderLayout.CENTER);
        add(applyChanges,BorderLayout.SOUTH);

    }

    private void addLabel(JPanel jpanel,String s){
        JLabel label = new JLabel(s, SwingConstants.CENTER);
        jpanel.add(label);
    }


}