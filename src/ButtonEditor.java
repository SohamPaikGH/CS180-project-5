import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
/**
 * Button Editor
 * <p>
 *     This class defines the custom ButtonEditor used in the StoreApplication class.
 *     It is used to add buttons and their functionalities to a column of cells in a JTable in Java Swing.
 *     This class is an extension of the DefaultCellEditor class in Java Swing. This adds functionality to a
 *     button added to a JTable by the ButtonRenderer class.
 *
 * @author Soham Paik, CS 180 Black
 * @version December 11, 2023
 * </p>
 */
class ButtonEditor extends DefaultCellEditor {
    protected JButton btn; // Button to add to cell
    private String lbl; // Label of button
    private boolean clicked; // Checks if button was clicked
    String action; // Specifies what the button should do
    StoreApplication storeApplication; // StoreApplication object run by the application
    String newStoreName; // If the button is set to edit a store, this contains the new store name
    String newStoreDesc; // If the button is set to edit a store, this contains the new store description

    public ButtonEditor(JCheckBox checkBox, StoreApplication storeApplication, String action) {
        // Define instance variables
        super(checkBox);

        this.storeApplication = storeApplication;
        this.action = action;

        btn = new JButton();
        btn.setOpaque(true);

        // Adds action listener to button
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    // Checks if the button was clicked
    // If so, it gets an empty value because the cell itself isn't supposed to be edited
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

    // Does something when the button is clicked
    @Override
    public Object getCellEditorValue() {

        if (clicked) {
            // If the button is clicked and its action is to edit/delete messages
            if (action.equals("Edit Message") || action.equals("Delete Message")) {
                if (!storeApplication.msgTable.getValueAt(storeApplication.msgTable.getSelectedRow(), 0).equals(storeApplication.recipientSelection.getSelectedItem())) {
                    if (action.equals("Edit Message")) {
                        // Send conversation ID, recipient name, message number, and message value to the server
                        storeApplication.writer.println("Edit Message");
                        storeApplication.writer.println(storeApplication.conversationID);
                        storeApplication.writer.println(storeApplication.recipientName);
                        storeApplication.writer.println(storeApplication.msgTable.getSelectedRow());
                        storeApplication.writer.println(storeApplication.msgTable.getValueAt(storeApplication.msgTable.getSelectedRow(), 1));
                        storeApplication.writer.flush();
                    } else {
                        // Send conversation ID, recipient name, and message value to the server
                        storeApplication.writer.println("Delete Message");
                        storeApplication.writer.println(storeApplication.conversationID);
                        storeApplication.writer.println(storeApplication.recipientName);
                        storeApplication.writer.println(storeApplication.msgTable.getSelectedRow());
                        storeApplication.writer.flush();
                    }
                }

                // Send conversation ID and recipient name to the server
                storeApplication.writer.println("Conversation");
                storeApplication.writer.println(storeApplication.conversationID);
                storeApplication.writer.println(storeApplication.recipientName);
                storeApplication.writer.flush();

                try {
                    // Reads data from server
                    String messageCountLine = storeApplication.reader.readLine();
                    int messageCount = Integer.parseInt(messageCountLine);

                    // Add message data from server to the messageData variable
                    Object[][] messageData = new Object[messageCount][4];
                    for (int i = 0; i < messageCount; i++) {
                        messageData[i][0] = storeApplication.reader.readLine();
                        messageData[i][1] = storeApplication.reader.readLine();

                        // Leave two columns for "Edit" and "Delete" buttons
                        if (!messageData[i][0].equals(storeApplication.recipientName)) {
                            messageData[i][2] = "Edit";
                            messageData[i][3] = "Delete";
                        }
                    }

                    // Add messages data to the messages table
                    TableModel messagesDataTable = storeApplication.getMessagesTable(messageData);
                    storeApplication.msgTable.setModel(messagesDataTable);

                    // Format the table so it looks organized
                    storeApplication.msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    storeApplication.msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                    storeApplication.msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    storeApplication.msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                    storeApplication.msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    storeApplication.msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                    // Add buttons recursively by calling "this" object to the JTable
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
                    // Open the store contact window
                    EventQueue.invokeLater(storeApplication::createStoreContactWindow);
                }

            } else {

                if (action.equals("Save Store Data")) {
                    // Get the row and value the user selected
                    int row = storeApplication.getjTable1().getSelectedRow();
                    newStoreName = storeApplication.getjTable1().getValueAt(row, 0).toString();
                    newStoreDesc = storeApplication.getjTable1().getValueAt(row, 1).toString();

                    // Send the server the user ID, the selected row, the new store name, and the store description
                    storeApplication.writer.println("Save Store Data");
                    System.out.println("Save Store Data");
                    storeApplication.writer.println(storeApplication.ID);
                    storeApplication.writer.println(row);
                    storeApplication.writer.println(newStoreName);
                    storeApplication.writer.println(newStoreDesc);
                    storeApplication.writer.flush();

                    try {
                        // Read data from the server
                        String line = storeApplication.reader.readLine();

                        if (line.equals("Success")) {
                            // If the store data was successfully saved, tell the user
                            JOptionPane.showMessageDialog(null, "Success!", "Message",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else if (line.equals("Name Exists")) {
                            // If the store name already exists, throw an error
                            JOptionPane.showMessageDialog(null, "Name already exists. Please try again!", "Error",
                                    JOptionPane.ERROR_MESSAGE);

                        } else if (line.equals("Name Blank")) {
                            // If the store name is blank, throw an error
                            JOptionPane.showMessageDialog(null, "Name is blank. Please try again!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (action.equals("Delete Store")) {
                    // Get selected row
                    int row = storeApplication.getjTable1().getSelectedRow();
                    System.out.println(row);

                    // Send the server the user ID and the selected row
                    storeApplication.writer.println("Delete Store");
                    storeApplication.writer.println(storeApplication.ID);
                    storeApplication.writer.println(row);
                    storeApplication.writer.flush();

                    // Tell the user was store deleted
                    JOptionPane.showMessageDialog(null, "Store deleted!", "Message",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Remove the store and its data from the table
                    TableModel tableModel1 = storeApplication.getTableModel();

                    storeApplication.jTable1.setModel(tableModel1);

                    if (!storeApplication.isCustomer) {
                        // Add more columns and change the formatting if the user is a customer
                        storeApplication.jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        storeApplication.jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        storeApplication.jTable1.getColumnModel().getColumn(1).setPreferredWidth(550);
                        storeApplication.jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
                        storeApplication.jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        // Add "Save Store Data" and "Delete Store" buttons to the table using "this" object
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Save Store Data"));

                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Delete Store"));

                    } else {
                        // Add fewer columns and change the formatting if the user is a seller
                        storeApplication.jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        storeApplication.jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        storeApplication.jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
                        storeApplication.jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
                        storeApplication.jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        // Add "Save Store Data" and "Delete Store" buttons to the table using "this" object
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
            // Create a search user window
            EventQueue.invokeLater(storeApplication::createSearchUserWindow);

        }
        clicked = false;
        return lbl;
    }

    @Override
    public boolean stopCellEditing() {
        // Stops the user from double-clicking the cell
        clicked = false;
        return super.stopCellEditing();
    }

}