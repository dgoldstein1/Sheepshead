package View;

import Model.Card;
import Model.Game;
import Model.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private Game g;

    public SheepsheadMainFrame(Game g, final MouseListener listener) {
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

        this.g = g;
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
    private boolean initPanels(MouseListener listener, Game g) {
        logEntries = new ArrayList<LogEntry>();
        gameLog = null;
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
        if(gameLog!=null)
            gameLog.refresh(logEntries);
        if(gameLog == null && g.debuggerRunning()){ //discrep between model + view
            g.setDebuggerRunning(false);
        }
        if(options.statsDisplayed()){
            options.refesh(g.getScoreboard());
        }
        return table.refresh(g);

    }


    /* in from notifier*/
    public void playSound(SoundEffect ef){
        sounds.playEffect(ef);
    }

    /**
     * creates Jdialog for player to choose yes or no prompt
     * @param prompt message displayed to user
     * @return String (testing done in game to assert 'y' or 'no')
     */
    public String yOrN(String prompt){
        int n = JOptionPane.showConfirmDialog(this,prompt,"",JOptionPane.YES_NO_OPTION);
        if(n==0)
            return "y";
        return "n";
    }

    /**
     * displays "ok" prompt
     * @param prompt
     * @return success n
     */
    public int displayMessage(String prompt, String title){
        Object[] options = {"baa'ah"};
        int n = JOptionPane.showOptionDialog(this,
                prompt,title,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        this.log(this.getClass(),LogType.SYSTEM,prompt);
        playSound(SoundEffect.BAH);
        return n;
    }
    public Card getPlayerCard(String prompt){
        this.log(this.getClass(),LogType.SYSTEM,prompt);
        return table.getPlayerCard(prompt);
    }
    public void playerCardPushed(Card card){
        table.playerCardPushed(card);
    }
    public void log(Class c, LogType type, String s){
        logEntries.add(new LogEntry(c,type,s));
    }

    /*out to options*/
    public void helpPushed() {
        logEntries.add(new LogEntry(this.getClass(),LogType.PLAYER_INPUT,"help panel opened"));
        options.helpPushed();
    }
    public void statsPushed(ScoreBoard s) {
        logEntries.add(new LogEntry(this.getClass(),LogType.PLAYER_INPUT,"stats panel opened"));
        options.statsPushed(s);
    }
    public void settingsPushed(Game g){
        logEntries.add(new LogEntry(this.getClass(),LogType.PLAYER_INPUT,"settings panel opened"));
        options.optionsPushed(g);
    }
    public void launchDebugger(){
        if(gameLog==null){
            gameLog = new GameLogFrame(logEntries,g);
        }
    }
    public void closeDebugger(){
        if(gameLog!=null){
            gameLog.dispose();
            gameLog = null;
        }
    }


}
