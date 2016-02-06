package View;

/**
 * Created by Dave on 11/12/2015.
 */

import Model.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * displays player hand
 */
class PlayerHandPanel extends JPanel {
    private CardBox cardsDisplayed; //buttons of numberFaceDownCards in hand
    private boolean readyForPlayerInput;
    private Card playerCardEntered;
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
        playerCardEntered = null;
    }



    /**
     * internal method to add labels to box
     * @param startingText
     * @return new JLabel made
     */
    private FadeLabel addLabel(String startingText, String layout){
        FadeLabel label = new FadeLabel(null,startingText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Helvetica",Font.BOLD,18));
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
    public boolean refresh(java.util.List<Card> cardsInHand,int points) {
        //remove numberFaceDownCards not in hand
        for (TableButton tb : cardsDisplayed.cards) {
            if (!cardsInHand.contains(tb.card())) { //table button not in card model
                tb.setAsNull();
            }
        }
        //add numberFaceDownCards not already displayed
        for (Card c : cardsInHand) {
            if (!showing(c)) {
                addCardToHand(c);
            }
        }
        if(points!=cardsDisplayed.pointsDisplayed){
            cardsDisplayed.pointsDisplayer.setText("| Points: " + points);
            cardsDisplayed.pointsDisplayed = points;
        }

        runPromptAnimations();

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
    private boolean showing(Card c) {
        for (TableButton tb : cardsDisplayed.cards) {
            if (tb.card() != null) { //check for nullpointer
                if (tb.card().equals(c)) return true;
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
    private void addCardToHand(Card c) {
        for (TableButton tb : cardsDisplayed.cards) {
            if (tb.card() == null) {
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
    public Card getPlayerCard(String prompt){
        if(direction < 0)
            direction *= -1; //make positive fade variable

        readyForPlayerInput = true;
        Card temp = null;
        promptDisplayer.setText(prompt);
        try{
            while(playerCardEntered==null){
                Thread.sleep(100);
            }
            //card selected by user
            temp = playerCardEntered;
            playerCardEntered = null;
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
    public void setSelectedCard(Card c){
        synchronized (this) { //critical section with getPlayerCard()
            if(readyForPlayerInput) {
                playerCardEntered = c;
            }
        }
    }


}


class CardBox extends JPanel{
    public ArrayList<TableButton> cards;
    public JLabel pointsDisplayer;
    public int pointsDisplayed;

    public CardBox( MouseListener listener, String playerName){
        cards = new ArrayList<TableButton>(8);

        addLabel(playerName);
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



}

class FadeLabel extends JLabel {

    private float alpha;
    private BufferedImage background;

    /**
     * image which fades in an out using Alpha
     * taken from : http://stackoverflow.com/questions/13203415/how-to-add-fade-fade-out-effects-to-a-jlabel
     */
    public FadeLabel(String backgroundLocation, String text) {
        try {
            if(backgroundLocation!=null)
                background = ImageIO.read(getClass().getResource(backgroundLocation));
        } catch (Exception e) {
        }
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


