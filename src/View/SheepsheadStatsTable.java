package View;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Dave on 12/16/2015.
 */
public class SheepsheadStatsTable extends JTable {
    private Dimension initialSize;


    public SheepsheadStatsTable(JFrame frame,Dimension windowSize, String[][] data, String[] columnNames) {
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
        frame.add(scroller, CENTER_ALIGNMENT);


        this.setPreferredSize(initialSize);
        setOpaque(false);
    }


}
