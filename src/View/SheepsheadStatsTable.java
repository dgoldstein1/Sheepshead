package View;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Dave on 12/16/2015.
 */
public class SheepsheadStatsTable extends JTable {
    private Dimension initialSize;
    private Container container;


    public SheepsheadStatsTable(Container c,Dimension windowSize, String[][] data, String[] columnNames) {
        super(data,columnNames);

        //set initialSize
        int width,height;
        width = windowSize.width -  windowSize.width / 6;
        height = windowSize.height / 2;
        initialSize = new Dimension(width,height);
        setSize(initialSize);


        //add scroller
        JScrollPane scroller = new JScrollPane(this);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        c.add(scroller, CENTER_ALIGNMENT);
        this.container = c;


        this.initialSize = initialSize;
        this.setPreferredSize(initialSize);
        setOpaque(false);
    }


    /**
     //creates row with description of stat and stat on right side
     * @param s Stat name
     * @param n stat or initialSize of dashed line
     */
    public void writeValues(String s, int n){

    }


    @Override
    public void paint(Graphics g){
        int width,height;
        width = container.getWidth();
        height = container.getHeight();
        setSize(width,height);
    }



}
