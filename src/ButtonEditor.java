import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

class ButtonEditor extends DefaultCellEditor {
    protected JButton btn;
    private String lbl;
    private boolean clicked;
    String action;
    StoreApplication storeApplication;
    String newStoreName;
    String newStoreDesc;

    public ButtonEditor(JCheckBox checkBox, StoreApplication storeApplication, String action) {
        super(checkBox);

        this.storeApplication = storeApplication;
        this.action = action;

        btn = new JButton();
        btn.setOpaque(true);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            btn.setForeground(table.getSelectionForeground());
            btn.setBackground(table.getSelectionBackground());
        } else {
            btn.setForeground(table.getForeground());
            btn.setBackground(table.getBackground());
        }
        lbl = (value==null) ? "": value.toString();
        btn.setText(lbl);
        clicked = true;

        return btn;
    }

    @Override
    public Object getCellEditorValue() {

        if (clicked) {

            if (storeApplication.isCustomer) {

                if (action.equals("Contact Store")) {
                    EventQueue.invokeLater(storeApplication::createStoreContactWindow);
                }

            } else {

                if (action.equals("Save Store Data")) {
                    int row = storeApplication.getjTable1().getSelectedRow();
                    newStoreName = storeApplication.getjTable1().getValueAt(row, 0).toString();
                    newStoreDesc = storeApplication.getjTable1().getValueAt(row, 1).toString();

                    storeApplication.writer.println("Save Store Data");
                    System.out.println("Save Store Data");
                    storeApplication.writer.println(storeApplication.ID);
                    storeApplication.writer.println(row);
                    storeApplication.writer.println(newStoreName);
                    storeApplication.writer.println(newStoreDesc);
                    storeApplication.writer.flush();

                    try {
                        String line = storeApplication.reader.readLine();

                        if (line.equals("Success")) {
                            JOptionPane.showMessageDialog(null, "Success!", "Message",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else if (line.equals("Name Exists")) {
                            JOptionPane.showMessageDialog(null, "Name already exists. Please try again!", "Error",
                                    JOptionPane.ERROR_MESSAGE);

                        } else if (line.equals("Name Blank")) {
                            JOptionPane.showMessageDialog(null, "Name is blank. Please try again!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (action.equals("Delete Store")) {
                    int row = storeApplication.getjTable1().getSelectedRow();
                    System.out.println(row);

                    storeApplication.writer.println("Delete Store");
                    storeApplication.writer.println(storeApplication.ID);
                    storeApplication.writer.println(row);
                    storeApplication.writer.flush();

                    JOptionPane.showMessageDialog(null, "Store deleted!", "Message",
                            JOptionPane.INFORMATION_MESSAGE);

                    TableModel tableModel1 = storeApplication.getTableModel();

                    storeApplication.jTable1.setModel(tableModel1);

                    if (!storeApplication.isCustomer) {
                        storeApplication.jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        storeApplication.jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        storeApplication.jTable1.getColumnModel().getColumn(1).setPreferredWidth(550);
                        storeApplication.jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
                        storeApplication.jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Save Store Data"));

                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Delete Store"));

                    } else {
                        storeApplication.jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        storeApplication.jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        storeApplication.jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
                        storeApplication.jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
                        storeApplication.jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Contact Store"));
                    }
                    storeApplication.jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));


                }

            }

        }
        clicked = false;
        return lbl;
    }
    public Object getCellEditorValue2() {

        if (clicked) {
            EventQueue.invokeLater(storeApplication::createSearchUserWindow);

        }
        clicked = false;
        return lbl;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

}