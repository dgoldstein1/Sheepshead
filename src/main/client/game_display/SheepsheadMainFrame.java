package main.client.game_display;

import main.protocols.GameState;
import main.client.options.*;
import main.client.components.StretchIcon;
import main.protocols.PlayerData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Dave on 11/2/2015.
 * <p/>
 * Maine frame holding all relevant panels
 */
public class SheepsheadMainFrame extends JFrame {
    private Container contentPane;
    private GamePanel table;
    private OptionsPanel options;
    private GameLogFrame gameLog;
    private MainSound sounds;
    private Dimension standardSize;
    private ArrayList<LogEntry> logEntries;

    SheepsheadMainFrame(GameState g, final MouseListener listener) {
        super("Sheepshead");
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
        standardSize = new Dimension(1000, 730);
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
        sounds.setMusicMuted(true);
        sounds.setEffectsMuted(true);
        sounds.playMusic("Sounds/Music/CCRmix.wav");
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
    private boolean initPanels(MouseListener listener, GameState g) {
        logEntries = new ArrayList<LogEntry>();
        gameLog = null;
        Font standardFont = new Font("Helvetica", Font.PLAIN, 12);

        UIManager.put("Button.font", standardFont);
        UIManager.put("Label.font", standardFont);

        table = new GamePanel(g, listener);
        options = new OptionsPanel(listener, sounds);
        contentPane.add(table, BorderLayout.CENTER);
        contentPane.add(options, BorderLayout.NORTH);
        return true;
    }


    /**
     * refreshes all values in GUI from main.server
     * called by notifier
     *
     * @param g main.protocols.GameState from main.server
     * @return success
     */
    public boolean refresh(GameState g) {
        repaint();
        if (gameLog != null)
            gameLog.refresh(logEntries);
//        if (gameLog == null && g.debuggerRunning()) { //discrep between model + view
//            g.setDebuggerRunning(false);
//        }
//        if (options.statsDisplayed()) {
//            options.refesh(g.getScoreboard());
//        } // TODO: 2/22/17
        return table.refresh(g);

    }


    /* in from notifier*/
    public void playSound(SoundEffect ef) {
        sounds.playEffect(ef);
    }

    /**
     * creates Jdialog for player to choose yes or no prompt
     *
     * @param prompt message displayed to user
     * @return boolean (testing done in game to assert 'y' or 'no')
     */
    public boolean yOrN(String prompt) {
        return (JOptionPane.showConfirmDialog(this, prompt, "", JOptionPane.YES_NO_OPTION) == 0);
    }

    /**
     * displays "ok" prompt
     *
     * @param prompt
     * @return success n
     */
    public int displayMessage(String prompt, String title) {
        Object[] options = {"baa'ah"};
        int n = JOptionPane.showOptionDialog(this,
                prompt, title,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        this.log(this.getClass(), LogType.SYSTEM, prompt);
        playSound(SoundEffect.BAH);
        return n;
    }

    public int getPlayerCard(String prompt) {
        this.log(this.getClass(), LogType.SYSTEM, prompt);
        return table.getPlayerCard(prompt);
    }

    public void playerCardPushed(int card) {
        table.playerCardPushed(card);
    }

    public void log(Class c, LogType type, String s) {
        logEntries.add(new LogEntry(c, type, s));
    }

    /*out to options*/
    public void helpPushed() {
        logEntries.add(new LogEntry(this.getClass(), LogType.PLAYER_INPUT, "help panel opened"));
        options.helpPushed(); 
    }

    public void statsPushed() {//// TODO: 2/22/17
        logEntries.add(new LogEntry(this.getClass(), LogType.PLAYER_INPUT, "stats panel opened"));
//        options.statsPushed(s);
    }

    public void settingsPushed(UIController ctrl) {
        logEntries.add(new LogEntry(this.getClass(), LogType.PLAYER_INPUT, "settings panel opened"));
        options.optionsPushed(ctrl);
    }

    public void joinCreatePushed(UIController ctrl) {
        logEntries.add(new LogEntry(this.getClass(), LogType.PLAYER_INPUT, "join-create panel opened"));
        options.joinCreatePushed(ctrl);
    }

    public void launchDebugger() {//// TODO: 2/22/17  
        if (gameLog == null) {
//            gameLog = new GameLogFrame(logEntries);
        }
    }

    public void closeDebugger() {
        if (gameLog != null) {
            gameLog.dispose();
            gameLog = null;
        }
    }

    /* getters and setters */

    public GamePanel getTable() {return table;}



}