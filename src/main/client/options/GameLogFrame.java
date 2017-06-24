package main.client.options;

import main.protocols.GameState;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by dave on 2/6/16.
 */
public class GameLogFrame extends JFrame {
    private int entriesDisplayed;
    private JTextPane logDisplay;
    private JScrollPane scroller;

    public GameLogFrame(ArrayList<LogEntry> previousLog, final GameState g){
        super("Sheepshead Log");
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


        setPreferredSize(new Dimension(400,600));
        logDisplay = new JTextPane();
        appendToPane(logDisplay,"/> launching debugger... ",Color.BLACK);
        logDisplay.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,8));
        scroller = new JScrollPane(logDisplay);
        this.add(scroller);
        logDisplay.setEditable(false);
        appendToPane(logDisplay,"done \n",Color.BLACK);
        appendToPane(logDisplay,"/> loading previous entries..\n",Color.BLACK);

        if(previousLog!=null){
            for(LogEntry logEntry : previousLog){
                this.log(logEntry);
            }
            entriesDisplayed = previousLog.size();
        } else {
            entriesDisplayed = 0;
        }
        appendToPane(logDisplay,"/> done\n",Color.BLACK);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();
                //g.setDebuggerRunning(false); //// TODO: 2/22/17
            }
        });
        pack();
        setVisible(true);

    }


    public boolean refresh(ArrayList<LogEntry> log){
        //update entries
        if(log.size() > entriesDisplayed){
            for(int i = entriesDisplayed; i < log.size() ; i++){
                log(log.get(i));
            }
            entriesDisplayed = log.size();

            //set caret at bottom
            Document doc = logDisplay.getDocument();
            logDisplay.setCaretPosition(doc.getLength());
        }

        return true;
    }

    /**
     * enters given log entry into pane
     * @param toEnter
     */
    public void log(LogEntry toEnter){
        appendToPane(logDisplay, toEnter.c + "-",Color.BLACK);

        //specify type color
        Color typeColor = Color.black;
        if(toEnter.type.equals(LogType.ERROR)) {
            typeColor = Color.RED;
        }
        else if(toEnter.type.equals(LogType.PLAYER_INPUT)) {
            typeColor = Color.GREEN;
        }
        else if(toEnter.type.equals(LogType.SYSTEM)) {
            typeColor = Color.BLUE;
        }

        appendToPane(logDisplay, toEnter.type + "/> ",Color.BLACK);
        appendToPane(logDisplay, toEnter.message + "\n",typeColor);

    }

    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        try {
            Document doc = tp.getDocument();
            doc.insertString(doc.getLength(), msg, aset);
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }

}


