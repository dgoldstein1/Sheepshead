package main.client.game_display;

/**
 * Created by Dave on 11/12/2015.
 */

import main.client.components.ButtonType;
import main.client.components.TableButton;
import main.protocols.GameState;
import main.protocols.PlayerData;
import main.server.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.*;

/**
 * displays player hand
 */
class PlayerHandPanel extends JPanel {
    private CardBox cardsDisplayed; //buttons of numberFaceDownCards in hand
    private boolean readyForPlayerInput, partnerDisplayed, pickerDislayed;;
    private int playerCardEntered;
    private FadeLabel promptDisplayer;
    private float direction = -0.05f;

    PlayerHandPanel(String name,MouseListener listener) {
        setLayout(new BorderLayout());
        initLabels();
        cardsDisplayed = new CardBox(listener,name);
        this.add(cardsDisplayed,BorderLayout.CENTER);
        setOpaque(false);

    }

    /**
     * inits buttons
     */
    private void initLabels(){
        promptDisplayer = addLabel("",BorderLayout.NORTH);
        readyForPlayerInput = false;
        playerCardEntered = -1;
    }



    /**
     * internal method to add labels to box
     * @param startingText
     * @return new JLabel made
     */
    private FadeLabel addLabel(String startingText, String layout){
        FadeLabel label = new FadeLabel(startingText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Helvetica",Font.ITALIC,18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label,layout);
        return label;
    }

    /**
     * called by timer
     * repaints panel based on updated model
     *
     * @return true if success, else return false
     */
    public boolean refresh(GameState g) {

        ArrayList<Integer> cardsInHand = g.cardsInHand;
        PlayerData p = g.players.get(0);
        int points = p.points;

        //refresh username
        if(!p.name.equals(cardsDisplayed.playerUsernameLabel.getText())){
            cardsDisplayed.playerUsernameLabel.setText(p.name);
        }

        //remove numberFaceDownCards not in hand
        for (TableButton tb : cardsDisplayed.cards) {
            if (!cardsInHand.contains(tb.card())) { //table button not in card model
                tb.setAsNull();
            }
        }
        //add numberFaceDownCards not already displayed
        for (int c : cardsInHand) {
            if (!showing(c)) {
                addCardToHand(c);
            }
        }
        if(points!=cardsDisplayed.pointsDisplayed){
            cardsDisplayed.pointsDisplayer.setText("| Points: " + points);
            cardsDisplayed.pointsDisplayed = points;
        }

        runPromptAnimations();

        //update partner + picker displayed
        if(p.partner && !partnerDisplayed){//partner not displayed
            cardsDisplayed.getPickerPartnerLabel().setText(cardsDisplayed.getPickerPartnerLabel().getText() + "(PARTNER)");
            cardsDisplayed.getPickerPartnerLabel().setForeground(Color.orange);
            partnerDisplayed = true;
        }
        if(p.picker && !pickerDislayed){//picker not displayed
            cardsDisplayed.getPickerPartnerLabel().setText(cardsDisplayed.getPickerPartnerLabel().getText() + "(PICKER)");
            cardsDisplayed.getPickerPartnerLabel().setForeground(Color.MAGENTA);
            pickerDislayed = true;
        }
        else if((!p.partner && partnerDisplayed) ||
                !p.picker && pickerDislayed){//partner or picker should not be displayed
            cardsDisplayed.getPickerPartnerLabel().setText("            ");
            partnerDisplayed = pickerDislayed = false;
        }

        repaint();

        return true;
    }

    /**
     * runs animation for prompt displayer
     * fades in if direction has been set to positive
     * fades out if firection has been set to negative
     */
    private void runPromptAnimations(){
        float alpha = promptDisplayer.getAlpha();
        alpha += direction;
        if (alpha < 0) {//fade in
            alpha = 0;
        } else if (alpha > 1) {//fade out
            alpha = 1;
        }
        promptDisplayer.setAlpha(alpha);


    }

    /**
     * helper function
     *
     * @param c card to check
     * @return true if showing selected card, false otherwise
     */
    private boolean showing(int c) {
        for (TableButton tb : cardsDisplayed.cards) {
            if (tb.card() != -1) {
                if (tb.card() == c) return true;
            }
        }
        return false;
    }

    /**
     * adds card to hand in null spot
     *
     * @param c Card to add
     * @throws IndexOutOfBoundsException hand is full
     */
    private void addCardToHand(int c) {
        for (TableButton tb : cardsDisplayed.cards) {
            if (tb.card() == -1) {
                tb.setCard(c);
                return;
            }
        }
        throw new IndexOutOfBoundsException("ADDING CARD TO FULL PLAYER HAND");
    }

    /**
     * in from model, runs on application thread
     * @param prompt displayed to ask for Card
     * @return Card
     */
    public int getPlayerCard(String prompt){
        if(direction < 0)
            direction *= -1; //make positive fade variable

        readyForPlayerInput = true;
        int temp = -1;
        promptDisplayer.setText(prompt);
        try{
            while(playerCardEntered==-1){
                Thread.sleep(100);
            }
            //card selected by user
            temp = playerCardEntered;
            playerCardEntered = -1;
            readyForPlayerInput = false;

        } catch(InterruptedException e){
            e.printStackTrace();
            System.exit(1);
        }

        if(direction > 0)
            direction *= -1; //make negative

        return temp;

    }

    /**
     * in from view on EDT
     * @param c card selected
     */
    public void setSelectedCard(int c){
        synchronized (this) { //critical section with getPlayerCard()
            if(readyForPlayerInput) {
                playerCardEntered = c;
            }
        }
    }

    /**
     * used in testing to make sure correct cards are being displayed
     * @return
     */
    public ArrayList<Integer> getPlayerCards() {
        ArrayList<Integer> cardsAsInt = new ArrayList<Integer>();
        for (TableButton c : cardsDisplayed.cards)
            // '-1' signifies not being displayed
            if (c.card() != -1) cardsAsInt.add(c.card());
        return cardsAsInt;
    }


}


class CardBox extends JPanel{
    public ArrayList<TableButton> cards;
    public JLabel pointsDisplayer, pickerPartnerLabel;
    public JLabel playerUsernameLabel;
    public int pointsDisplayed;


    public CardBox( MouseListener listener, String playerName){
        cards = new ArrayList<TableButton>(8);

        pickerPartnerLabel = addLabel("          ");
        pickerPartnerLabel.setForeground(Color.YELLOW);
        pickerPartnerLabel.setFont(new Font("Helvetica",Font.ITALIC,12));

        playerUsernameLabel =  addLabel(playerName);
        pointsDisplayed = 0;
        pointsDisplayer = addLabel("| Points: " + pointsDisplayed);

        //initButtons numberFaceDownCards as buttons and initButtons 6 null buttons
        for (int i = 0; i < 8; i++) {
            TableButton card = new TableButton(ButtonType.PLAYERCARD);
            card.addMouseListener(listener);
            cards.add(i, card);
            this.add(cards.get(i)); //put on panel

        }
        setOpaque(false);
    }

    /**
     * internal method to add labels to box
     * @param startingText
     * @return new JLabel made
     */
    private JLabel addLabel(String startingText){
        JLabel label = new JLabel(startingText);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
        return label;
    }

    public JLabel getPickerPartnerLabel(){
        return pickerPartnerLabel;
    }



}

class FadeLabel extends JLabel {

    private float alpha;
    private BufferedImage background;

    /**
     * image which fades in an out using Alpha
     * taken from : http://stackoverflow.com/questions/13203415/how-to-add-fade-fade-out-effects-to-a-jlabel
     */
    public FadeLabel(String text) {
        setText(text);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setAlpha(1f);
    }

    public void setAlpha(float value) {
        if (alpha != value) {
            float old = alpha;
            alpha = value;
            firePropertyChange("alpha", old, alpha);
            repaint();
        }
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public Dimension getPreferredSize() {
        return background == null ? super.getPreferredSize() : new Dimension(background.getWidth(), background.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        // This is one of the few times I would directly override paint
        // This makes sure that the entire paint chain is now using
        // the alpha composite, including borders and child components
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
        super.paint(g2d);
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // This is one of the few times that doing this before the super call
        // will work...
        if (background != null) {
            int x = (getWidth() - background.getWidth()) / 2;
            int y = (getHeight() - background.getHeight()) / 2;
            g.drawImage(background, x, y, this);
        }
        super.paintComponent(g);
    }
}


