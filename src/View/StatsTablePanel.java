package View;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dave on 12/16/2015.
 */
public class StatsTablePanel extends JPanel {
    private int columnValue = -1;
    private int columnNewValue = -1;
    private JTable table;
    private String[] columnNames;

    public StatsTablePanel(Dimension initialSize, String[][] data, String[] columnNames) {
        this.setPreferredSize(initialSize);
        this.setOpaque(false);
        this.columnNames = columnNames;

        DefaultTableModel model = new DefaultTableModel(data, columnNames){

            @Override
            public boolean isCellEditable(int row, int column){
                return false; //make non-editable by mouse + keys
            }

        };
        table = new JTable(model);
        table.setOpaque(false);
        makeDragHook();
        JScrollPane js=new JScrollPane(table);
        js.setVisible(true);
        js.setOpaque(false);
        add(js);

    }

    /**
     * make it so first column cannot be moved
     */
    private void makeDragHook(){
        table.getColumnModel().addColumnModelListener(new TableColumnModelListener()
        {
            public void columnAdded(TableColumnModelEvent e) {}

            public void columnMarginChanged(ChangeEvent e) {}

            public void columnMoved(TableColumnModelEvent e)
            {
                if (columnValue == -1)
                    columnValue = e.getFromIndex();

                columnNewValue = e.getToIndex();
            }

            public void columnRemoved(TableColumnModelEvent e) {}

            public void columnSelectionChanged(ListSelectionEvent e) {}
        });

        table.getTableHeader().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (columnValue != -1 && (columnValue == 0 || columnNewValue == 0))
                    table.moveColumn(columnNewValue, columnValue);

                columnValue = -1;
                columnNewValue = -1;
            }
        });
    }


    /**
     * updates table based on new Data
     * @param data
     * @return success
     */
    public boolean updateTableData(String[][] data){
        DefaultTableModel model = new DefaultTableModel(data, columnNames){

            @Override
            public boolean isCellEditable(int row, int column){
                return false; //make non-editable by mouse + keys
            }

        };
        table.setModel(model);
        this.repaint();
        return true;
    }

}
