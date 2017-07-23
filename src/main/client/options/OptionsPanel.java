package main.client.options;

import main.client.game_display.UIController;
import main.client.components.ButtonType;
import main.client.components.StretchIcon;
import main.client.components.TableButton;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
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



    //called by notifiers

    public void optionsPushed(UIController ctrl) {
        optionsFrame = new SettingsFrame(ctrl, sounds);
    }

    public void helpPushed() {
        helpFrame = new HelpFrame(standardWindowSize);
    }

    public void statsPushed() {
        //// TODO: 2/22/17  
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
    private JTextPane textDisplay;
    private JScrollPane scroller;
    private String absFilename;

    HelpFrame(Dimension size) {
        super("Info");
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
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close(true);
                setVisible(false);
                dispose();
            }
        });
        setPreferredSize(size);

        textDisplay = new JTextPane();
        textDisplay.setContentType("text/html");
        textDisplay.setEditable(false);
        scroller = new JScrollPane(textDisplay);
        this.add(scroller);
        absFilename = System.getProperty("user.dir") + "/lib/Pages/Sheeshead_Wiki.html";
        loadhtml();


        pack();
        setVisible(true);
    }

    private void loadhtml(){
        try{
            System.out.print("Loading " + absFilename + " ...");
            File file = new File(absFilename);
            textDisplay.setPage(file.toURI().toURL());
            System.out.println("done");

        } catch(NullPointerException e){
            System.out.println("could not find file: " + absFilename);
            e.printStackTrace();
            close(false);
        } catch(UnsupportedEncodingException e){
            System.out.println("could not encode " + absFilename);
            e.printStackTrace();
            close(false);
        } catch(IOException e){
            System.out.println("IO exception");
            e.printStackTrace();
            close(false);
        }
    }

    private void save(){
        StyledDocument doc = (StyledDocument) textDisplay.getDocument();
        HTMLEditorKit kit = new HTMLEditorKit();
        BufferedOutputStream out;
        try {
            out = new BufferedOutputStream(new FileOutputStream(absFilename));
            kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());

        } catch (IOException e){
            e.printStackTrace();
            close(false);
        } catch (BadLocationException e){
            e.printStackTrace();
            close(false);
        }
    }

    /**
     * closes this window
     * @param saveWork
     */
    private void close(boolean saveWork){
        if (saveWork){
            save();
            setVisible(false);
            dispose();
        }
        else{
            System.exit(1);
        }
    }
}


class StatsFrame extends JFrame {
//    private StatsTablePanel gameStats, playerStats;
//    private Dimension windowSize, tableSize;
//    private JPanel jpanel;
//    private int showingRound = 0; // TODO: 2/22/17  

    StatsFrame(Dimension size) {
//        super("Stats");
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
//        setPreferredSize(size);
//
//        this.windowSize = size;
//        this.tableSize = new Dimension(windowSize.width, windowSize.height / 3);
//        setContentPane(new JLabel(new StretchIcon("Pictures/old_farm.jpg", false)));
//        gameStats = playerStats = null;
//        setLayout(new FlowLayout());
//
//        //add stats panels to jpanel
//        jpanel = new JPanel(new BorderLayout());
//        jpanel.setOpaque(false);
//        initGameStats(scorekeeper);
//        initPlayerStats(scorekeeper);
//        jpanel.add(gameStats, BorderLayout.NORTH);
//        jpanel.add(playerStats, BorderLayout.SOUTH);
//        this.add(jpanel);
//        setPreferredSize(size);
//
//        pack();
//        setVisible(true);

    }
//
//    /**
//     * initializes table with scorekeeper values
//     */
//    private void initGameStats(ScoreBoard sb) {
//
//        String[][] data = parseData(sb);
//        String[] columns = parseColumns(sb);
//        if (data[0].length != columns.length) {
//            JOptionPane.showMessageDialog(this,"columns and data not compatible" +
//                    "       colums = " + columns.length + " but data[0]" + data[0].length);
//            this.dispose(); //exit
//        }
//
//        gameStats = new StatsTablePanel(tableSize, data, columns);
//
//    }
//
//    /**
//     * stat panel for player-specific stats
//     *
//     * @param sb
//     */
//    private void initPlayerStats(ScoreBoard sb) {
//        String[][] data = sb.getPlayerStats();
//        playerStats = new StatsTablePanel(tableSize, data, new String[]{"main.protocols.PlayerData Stats", ""});
//
//    }
//
//    /**
//     * @param sb Scoreboard of curr game
//     * @return list of players in game as strings
//     */
//    private String[] parseColumns(ScoreBoard sb) {
//        String[] columns = new String[6];
//        columns[0] = "Round";
//        for (int i = 1; i < 6; i++) {
//            columns[i] = sb.getNonStaticPlayers()[i - 1].getUsername();
//        }
//        return columns;
//    }
//
//    /**
//     * given sb gets round points for game
//     *
//     * @param sb Scoreboard
//     * @return String[][] of round data where:
//     * String[round][playerId+1] = roundPoint
//     */
//    private String[][] parseData(ScoreBoard sb) {
//        String[][] data = new String[sb.roundsPlayed()][6];
//        int[] roundPlayerPoints;
//
//        for (int round = 0; round < sb.roundsPlayed(); round++) {
//            data[round][0] = round + 1 + ""; //set column number
//            roundPlayerPoints = sb.getListOfRound().get(sb.roundsPlayed() - round - 1).getPlayerScores();
//            for (int playerId = 1; playerId < 6; playerId++) {
//                data[round][playerId] = roundPlayerPoints[playerId - 1] + "";
//            }
//        }
//        return data;
//    }
//
//    /**
//     * called by frame to refresh stats
//     *
//     * @return success
//     */
//    public boolean refresh(ScoreBoard sb) {
//        if(showingRound != sb.currRound()){
//            gameStats.updateTableData(parseData(sb));
//            playerStats.updateTableData(sb.getPlayerStats());
//            showingRound = sb.currRound();
//        }
//        return true;
//    }


}

class SettingsFrame extends JFrame {
    JPanel settings;
    JCheckBox playEffects, playMusic, launchDebugger;
    JSlider gameSpeed;
    JButton applyChanges;
    MainSound sounds;
    UIController ctrl;
    JTextField enterName;

    SettingsFrame(UIController ctrl, MainSound sounds) {
        super("Stats");
        setContentPane(new JLabel(new StretchIcon("Textures/wood.jpg", false)));
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
        setPreferredSize(new Dimension(269, 325));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
            }
        });
        setLayout(new BorderLayout());
        this.ctrl = ctrl;
        this.sounds = sounds;
        init();
        pack();
        setVisible(true);
    }

    /**
     * inits components of this frame
     */
    private void init() {

        //initiatlization
        playEffects = new JCheckBox("");
        playEffects.setHorizontalAlignment(JCheckBox.CENTER);
        playEffects.setSelected(!sounds.effectsMuted());
        playMusic = new JCheckBox("");
        playMusic.setHorizontalAlignment(JCheckBox.CENTER);
        playMusic.setSelected(!sounds.musicMuted());
        launchDebugger = new JCheckBox("");
        launchDebugger.setHorizontalAlignment(JCheckBox.CENTER);
        launchDebugger.setSelected(ctrl.debuggerRunning);
        enterName = new JTextField(ctrl.currPlayerData.name);


        applyChanges = new JButton("apply changes");

        //apply changes + button
        applyChanges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sounds.setEffectsMuted(!playEffects.isSelected());
                sounds.setMusicMuted(!playMusic.isSelected());
                ctrl.debuggerRunning= launchDebugger.isSelected();
                ctrl.currPlayerData.name = enterName.getText();
                setVisible(false);
                dispose();
            }
        });

        //add to JPabels / frame
        settings = new JPanel(new GridLayout(5, 2));
        settings.setOpaque(false);
        addLabel(settings, "Play Sound Effects: ");
        settings.add(playEffects);
        addLabel(settings, "Play Music: ");
        settings.add(playMusic);
//        addLabel(settings, "Game Speed (%)");
//        settings.add(gameSpeed);
        addLabel(settings, "Run Debugger: ");
        settings.add(launchDebugger);
        addLabel(settings, "username: ");
        settings.add(enterName);

        add(settings, BorderLayout.CENTER);
        add(applyChanges, BorderLayout.SOUTH);

    }

    private void addLabel(JPanel jpanel, String s) {
        JLabel label = new JLabel(s, SwingConstants.CENTER);
        label.setFont(new Font("Helvetica", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setOpaque(false);
        jpanel.add(label);
    }


}