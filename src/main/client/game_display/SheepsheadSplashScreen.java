package main.client.game_display;

// SplashScreen.java
// A simple application to show a title screen in the center of the screen
// for the amount of time given in the constructor.  This class includes
// a sample main() method to test the splash screen, but it's meant for use
// with other applications.
//

import main.client.components.StretchIcon;

import java.awt.*;
import javax.swing.*;

public class SheepsheadSplashScreen extends JWindow {

    public SheepsheadSplashScreen(int d) {
        super();
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

        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.WHITE);

        // Set the window's bounds, centering the window
        int width = 350;
        int height =350;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);

        // Build the splash screen
        JLabel welcome = new JLabel("Welcome to Sheepshead!",JLabel.CENTER);
        JLabel label = new JLabel(new StretchIcon("Pictures/running_sheep.gif", false));
        JLabel loading = new JLabel("Loading...", JLabel.CENTER);
        Font font = new Font("Helvetica", Font.BOLD, 12);
        loading.setFont(font);
        welcome.setFont(font);
        content.add(loading, BorderLayout.SOUTH);
        content.add(welcome,BorderLayout.NORTH);
        content.add(label, BorderLayout.CENTER);
        Color c = new Color(217, 176, 140);
        content.setBorder(BorderFactory.createLineBorder(c, 10));

        // Display it
        setVisible(true);

        // Wait a little while, maybe while loading resources
        try { Thread.sleep(d); } catch (Exception e) {}

        setVisible(false);

        String[] options = {"Play!"};
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(80,30));
        JTextField txt = new JTextField(14);
        panel.add(txt);

        //new UIController(getPlayerInput(panel,txt,options));

    }

    private String getPlayerInput(JPanel panel,JTextField txt, String[] options) {
        JOptionPane.showOptionDialog(null, panel, "Enter Username", JOptionPane.NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
        if (txt.getText() == null || txt.getText().equals("")) {
            return getPlayerInput(panel, txt, options);
        }
        return txt.getText();

    }


}

