package View;

import Model.Card;
import Model.CardHistory;
import Model.HandHistory;
import Model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * holds cards played by each player
 */
class CardsOnTable extends JPanel {
    private ArrayList<CardPlayedPanel> cardsOnTable;
    private Image background;

    CardsOnTable(Player[] players) {
        this.cardsOnTable = new ArrayList<CardPlayedPanel>(10);

        for (Player p : players) {
            cardsOnTable.add(new CardPlayedPanel(p));
        }
        setLayout();
        setBorder(BorderFactory.createLineBorder(Color.black));
        background = iconToImage(new StretchIcon("Textures/green_felt.jpg", false));
        setMinimumSize(new Dimension(230,230));
        setOpaque(false);

    }

    /**
     * sets up layout
     */
    private void setLayout() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        //add in top two AIplayer panels
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        add(cardsOnTable.get(2), c); //player 3

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        add(cardsOnTable.get(3), c); //player 4

        //add in card view panel
        c.weightx = 0.5;
        c.ipady = 50;
        c.gridx = 1;
        c.gridy = 1;
        add(new JLabel(), c); //empty space
        c.ipadx = 0;
        c.ipady = 0;

        //add bottom two AIplayer panels

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.5;
        add(cardsOnTable.get(1), c); //player 2


        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.5;
        add(cardsOnTable.get(4), c); //player 5

        c.gridx = 0;
        c.gridy = 3;
        c.ipady = 20;
        add(new JLabel(),c);

        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.5;
        c.ipady = 0;
        add(cardsOnTable.get(0), c); //user


    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background,0,0,null);
    }

    /**
     * converts icon to Image
     * @param icon icon
     * @return Image of icon
     * taken from stack Overflow (http://stackoverflow.com/questions/19125707/simplest-way-to-set-image-as-jpanel-background)
     */
    private Image iconToImage(Icon icon){
        if (icon instanceof ImageIcon) {
            return ((ImageIcon)icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }



    /**
     * called by timer
     * repaints panel based on updated model
     *
     * @return true if success, else return false
     */
    public boolean refresh(java.util.List<Card> cardsInModel, HandHistory handHistories) {

        //remove cards not played
        for (CardPlayedPanel cpp : cardsOnTable) {
            if (cpp.getCard() != null) {
                if (!cardsInModel.contains(cpp.getCard())) { //card showing on table but not in model
                    cpp.setCardAsNull();
                }
            }
        }
        //add numberFaceDownCards not already displayed
        for (CardHistory ch : handHistories.getHand()) {
            if (!showing(ch.card())) {
                addCardToTable(ch);
            }
        }

        repaint();
        return true;

    }


    /**
     * helper function
     *
     * @param c card to check
     * @return true if showing selected card, false otherwise
     */
    private boolean showing(Card c) {
        for (CardPlayedPanel cpp : cardsOnTable) {
            if (cpp.getCard() != null) { //check for nullpointer
                if (cpp.getCard().equals(c)) return true;
            }
        }
        return false;
    }

    /**
     * adds card to hand in null spot
     *
     * @param ch cardHistory to add
     * @throws IndexOutOfBoundsException hand is full
     */
    private void addCardToTable(CardHistory ch) {
        for (CardPlayedPanel cpp : cardsOnTable) {
            if (cpp.getPlayer().equals(ch.playerBy())) {
                cpp.setCard(ch.card());
                return;
            }
        }
        throw new IndexOutOfBoundsException("NO PLAYER FOUND IN ADDING CARD TO TABLE");
    }

    //panel for individual card Played
    class CardPlayedPanel extends JPanel {
        private TableButton card;
        private Player player;

        CardPlayedPanel(Player p) {
            player = p;
            card = new TableButton(ButtonType.CARD_PLAYED);
            this.add(card);
            setOpaque(false);
        }

        public Card getCard() {
            return card.card();
        }

        public Player getPlayer() {
            return player;
        }

        public void setCardAsNull() {
            card.setAsNull();
        }

        public void setCard(Card c) {
            card.setCard(c);
        }


    }
}
