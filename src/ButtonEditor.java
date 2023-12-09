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
            if (action.equals("Edit Message") || action.equals("Delete Message")) {
                if (!storeApplication.msgTable.getValueAt(storeApplication.msgTable.getSelectedRow(), 0).equals(storeApplication.recipientSelection.getSelectedItem())) {
                    if (action.equals("Edit Message")) {
                        storeApplication.writer.println("Edit Message");
                        storeApplication.writer.println(storeApplication.conversationID);
                        storeApplication.writer.println(storeApplication.recipientName);
                        storeApplication.writer.println(storeApplication.msgTable.getSelectedRow());
                        storeApplication.writer.println(storeApplication.msgTable.getValueAt(storeApplication.msgTable.getSelectedRow(), 1));
                        storeApplication.writer.flush();
                    } else {
                        storeApplication.writer.println("Delete Message");
                        storeApplication.writer.println(storeApplication.conversationID);
                        storeApplication.writer.println(storeApplication.recipientName);
                        storeApplication.writer.println(storeApplication.msgTable.getSelectedRow());
                        storeApplication.writer.flush();
                    }
                }
                storeApplication.writer.println("Conversation");
                storeApplication.writer.println(storeApplication.conversationID);
                storeApplication.writer.println(storeApplication.recipientName);
                storeApplication.writer.flush();

                try {
                    String messageCountLine = storeApplication.reader.readLine();
                    int messageCount = Integer.parseInt(messageCountLine);

                    Object[][] messageData = new Object[messageCount][4];
                    for (int i = 0; i < messageCount; i++) {
                        messageData[i][0] = storeApplication.reader.readLine();
                        messageData[i][1] = storeApplication.reader.readLine();
                        if (!messageData[i][0].equals(storeApplication.recipientName)) {
                            messageData[i][2] = "Edit";
                            messageData[i][3] = "Delete";
                        }
                    }

                    TableModel messagesDataTable = storeApplication.getMessagesTable(messageData);
                    storeApplication.msgTable.setModel(messagesDataTable);

                    storeApplication.msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    storeApplication.msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                    storeApplication.msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    storeApplication.msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                    storeApplication.msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    storeApplication.msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                    storeApplication.msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                    storeApplication.msgTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                            storeApplication,
                            "Edit Message"));

                    storeApplication.msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                    storeApplication.msgTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                            storeApplication,
                            "Delete Message"));

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (storeApplication.isCustomer) {

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