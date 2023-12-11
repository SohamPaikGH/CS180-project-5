import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
/**
 * Button Renderer
 *
 * This class defines the custom ButtonRenderer used in the StoreApplication class.
 * It is used to add buttons and their graphics to a column of cells in a JTable in Java Swing. This class
 * is an extension of the DefaultCellEditor class in Java Swing. This class extends JButton and implements
 * the TableCellRenderer interface.
 *
 * @author Sean Kim, Soham Paik, Yash Patel, CS 18000 Black, lab sec l17
 *
 * @version December 11, 2023
 */
class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // Makes table cell look like a button
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value==null) ? "":value.toString());
        return this;
    }
}