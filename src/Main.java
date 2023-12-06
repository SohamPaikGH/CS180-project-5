import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Store Application
 * <p>
 * This file initializes the store application client and the GUI. All calls to the
 * server from the client are made by the StoreApplication object.
 *
 * @author Soham Paik, CS 180 Black
 * @version TBD
 */
class StoreApplication extends JFrame implements ActionListener {
    JPasswordField password;
    JTextField username, usernameSetting, emailSetting, passwordSetting;
    JLabel label_password, label_username, title;
    JButton signInButton, blockRecipientButton, appearInvisibleToRecipientButton,
            signUpButton, registerButton, storeSendMessageButton;
    JButton saveButton = new JButton("Save");
    JButton clearButton = new JButton("Clear");
    JButton deleteAccountButton = new JButton("Delete Account");
    JButton contactStoreButton = new JButton("Contact Store");
    JButton sendMessageButton = new JButton("Send");
    JComboBox roleSetting;
    boolean loginCompleted = false;
    boolean connectedToServer = false;

    // if true, user is customer; if false, user is seller
    boolean isCustomer = false;
    // Close sign-up window
    boolean signUpDone;
    JFrame signUpFrame;
    // For networking purposes
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    String ID;


    public StoreApplication() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800,600);
        setTitle("Login");
        setLocationRelativeTo(null);
        setLayout(null);

        // Start login page

        label_username = new JLabel("Username");
        label_username.setBounds(200, 200, 100, 40);

        label_password = new JLabel("Password");
        label_password.setBounds(200, 250, 100, 40);

        username = new JTextField();
        username.setBounds(300, 200, 300, 40);

        password = new JPasswordField(50);
        password.setBounds(300, 250, 300, 40);

        signInButton = new JButton("Sign in");
        signInButton.setBounds(300, 320, 100, 40);
        signInButton.addActionListener(StoreApplication.this);

        signUpButton = new JButton("Sign up");
        signUpButton.setBounds(420, 320, 100, 40);
        signUpButton.addActionListener(StoreApplication.this);

        add(label_username);
        add(username);
        add(label_password);
        add(password);
        add(signInButton);
        add(signUpButton);

        setResizable(false);
        setVisible(true);
    }

    protected void initializeSignUpPage() {
        signUpFrame = new JFrame();
        signUpFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        signUpFrame.setSize(new Dimension(800, 600));
        signUpFrame.setTitle("Sign Up");
        signUpFrame.setLocationRelativeTo(null);
        signUpFrame.setLayout(null);

        label_username = new JLabel("Username");
        label_username.setBounds(200, 200, 100, 40);

        label_password = new JLabel("Password");
        label_password.setBounds(200, 250, 100, 40);

        JLabel label_email = new JLabel("Email");
        label_email.setBounds(200, 300, 100, 40);

        JLabel label_role = new JLabel("Role");
        label_role.setBounds(200, 350, 100, 40);

        username = new JTextField();
        username.setBounds(300, 200, 300, 40);

        password = new JPasswordField(50);
        password.setBounds(300, 250, 300, 40);

        emailSetting = new JTextField();
        emailSetting.setBounds(300, 300, 300, 40);

        String options[] = {"Customer", "Seller"};
        roleSetting = new JComboBox(options);
        roleSetting.setBounds(300, 350, 300, 40);

        registerButton = new JButton("Register");
        registerButton.setBounds(300, 400, 100, 40);
        registerButton.addActionListener(StoreApplication.this);

        signUpFrame.add(label_username);
        signUpFrame.add(username);
        signUpFrame.add(label_password);
        signUpFrame.add(password);
        signUpFrame.add(label_email);
        signUpFrame.add(emailSetting);
        signUpFrame.add(label_role);
        signUpFrame.add(roleSetting);
        signUpFrame.add(registerButton);

        signUpFrame.setVisible(true);
        signUpFrame.setResizable(false);
    }

    protected void initializeApp() {
        JFrame frame = new JFrame("Store Application");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(size);

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        JComponent panel1 = createDashboardPane();
        tabbedPane.addTab("Dashboard", null, panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = createConversationsPane();
        tabbedPane.addTab("Conversations", null, panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent panel3 = createAccountSettingsPane();
        tabbedPane.addTab("Account", null, panel3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        frame.getContentPane().add(tabbedPane);
        frame.setVisible(true);
        frame.setResizable(true);

    }

    protected JComponent createAccountSettingsPane() {
        JPanel panel = new JPanel(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        JLabel usernameLbl = new JLabel("Username");
        usernameLbl.setPreferredSize(new Dimension(100, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(usernameLbl, c);

        usernameSetting = new JTextField(20);
        usernameSetting.setPreferredSize(new Dimension(300, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(usernameSetting, c);

        JLabel emailLbl = new JLabel("Email");
        emailLbl.setPreferredSize(new Dimension(100, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        panel.add(emailLbl, c);

        emailSetting = new JTextField(20);
        emailSetting.setPreferredSize(new Dimension(300, 40));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 4;
        panel.add(emailSetting, c);

        JLabel passwordLbl = new JLabel("Password");
        passwordLbl.setPreferredSize(new Dimension(100, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        panel.add(passwordLbl, c);

        passwordSetting = new JTextField(20);
        passwordSetting.setPreferredSize(new Dimension(300, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        panel.add(passwordSetting, c);

        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.addActionListener(StoreApplication.this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        panel.add(saveButton, c);

        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(StoreApplication.this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 8;
        panel.add(clearButton, c);

        deleteAccountButton.setPreferredSize(new Dimension(100, 40));
        deleteAccountButton.addActionListener(StoreApplication.this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 9;
        panel.add(deleteAccountButton, c);

        return panel;
    }


    protected JComponent createConversationsPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Create message area

        JTextArea msgArea = new JTextArea("English", 20, 60);
        JScrollPane msgScrollPane = new JScrollPane(msgArea);
        msgArea.setEditable(false);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(msgScrollPane, c);

        // Create sub-panel for dropdown menu that lets user select recipient
        // Create dropdown menu to select user to message
        JPanel recipientPanel = new JPanel();
        recipientPanel.setLayout(new FlowLayout());

        String[] recipientNames = {
                "Sam Walton",
                "George W. Jenkins",
                "Dayton Hudson",
                "Jeff Bezos"
        };

        JLabel recipientLbl = new JLabel("Recipient:");
        recipientLbl.setPreferredSize(new Dimension(100, 40));

        JComboBox recipientSelection = new JComboBox(recipientNames);
        recipientSelection.setPreferredSize(new Dimension(300, 40));
        recipientSelection.addActionListener(StoreApplication.this);

        recipientPanel.add(recipientLbl);
        recipientPanel.add(recipientSelection);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(recipientPanel, c);

        // Create sub-panel for text field and button to send message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout());

        JTextField jTextField = new JTextField(50);
        jTextField.setPreferredSize(new Dimension(300, 40));

        sendMessageButton.setPreferredSize(new Dimension(100, 40));
        sendMessageButton.addActionListener(StoreApplication.this);

        messagePanel.add(jTextField);
        messagePanel.add(sendMessageButton);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(messagePanel, c);

        // Create sub-panel for blocking and invisibility features
        // Then add buttons to block and be invisible to users
        JPanel blockingInvisibilityPanel = new JPanel();
        blockingInvisibilityPanel.setLayout(new FlowLayout());

        blockRecipientButton = new JButton("Block");
        blockRecipientButton.setPreferredSize(new Dimension(100, 40));
        blockRecipientButton.addActionListener(StoreApplication.this);

        appearInvisibleToRecipientButton = new JButton("Toggle Invisibility");
        appearInvisibleToRecipientButton.setPreferredSize(new Dimension(150, 40));
        appearInvisibleToRecipientButton.addActionListener(StoreApplication.this);

        blockingInvisibilityPanel.add(blockRecipientButton);
        blockingInvisibilityPanel.add(appearInvisibleToRecipientButton);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        panel.add(blockingInvisibilityPanel, c);

        return panel;
    }

    protected JComponent createDashboardPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        if (isCustomer) {
            title = new JLabel("All Stores");
        } else {
            title = new JLabel("Your Stores");
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(title, c);

        JTable jTable1 = new JTable();

        TableModel tableModel = getTableModel();

        jTable1.setModel(tableModel);

        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), this));
        jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));

        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.getViewport().setOpaque(true);
        jScrollPane1.setViewportBorder(null);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(jScrollPane1, c);

        contactStoreButton.setPreferredSize(new Dimension(100, 40));
        contactStoreButton.addActionListener(StoreApplication.this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(contactStoreButton, c);

        return panel;
    }

    private static TableModel getTableModel() {
        Object[][] data = {
                {"Walmart", "Sam Walton", "Contact Store"},
                {"Publix", "George W. Jenkins", "Contact Store"},
                {"Target", "Dayton Hudson", "Contact Store"},
                {"Walgreens", "Walgreen Boosts Alliance, Inc.", "Contact Store"},
                {"Amazon", "Jeff Bezos", "Contact Store"},
                {"Aldi", "Die Familie Albrecht", "Contact Store"}
        };

        String[] columnNames = new String[] {"Stores", "Owner", "Actions"};
        TableModel tableModel = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        return tableModel;
    }

    public void createStoreContactWindow() {
        JFrame frame = new JFrame("Contact Store");
        frame.setSize(new Dimension(300, 200));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        JLabel label1 = new JLabel("Select Store");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label1, c);

        String[] stores = {"Walmart", "Publix", "Target", "Walgreens", "Amazon", "Aldi"};
        JComboBox<String> comboBox = new JComboBox<>(stores);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comboBox, c);

        JLabel label2 = new JLabel("Message");
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label2, c);

        JTextField textField = new JTextField(20);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textField, c);

        storeSendMessageButton = new JButton("Send");
        storeSendMessageButton.addActionListener(StoreApplication.this);
        storeSendMessageButton.setPreferredSize(new Dimension(100, 40));
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(storeSendMessageButton, c);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
        frame.setResizable(true);
    }



    public static void main(String[] args) {
        StoreApplication storeApplication = new StoreApplication();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {

            try {
                socket = new Socket("localhost", 4242);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());

                writer.println("Log In");
                writer.println(username.getText());
                writer.println(password.getPassword());
                writer.println();
                writer.flush();

                connectedToServer = true;

                String line = reader.readLine();
                if (line.equals("Success")) {
                    ID = reader.readLine();
                    setVisible(false);
                    EventQueue.invokeLater(this::initializeApp);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid ID. Please try again.", "Error!",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (ConnectException connectException) {
                JOptionPane.showMessageDialog(null, "Server not found!", "Error!",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
        if (e.getSource() == saveButton) {
            JOptionPane.showMessageDialog(null, "Saved", "Account Settings",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == clearButton) {
            usernameSetting.setText("");
            emailSetting.setText("");
            passwordSetting.setText("");
        }
        if (e.getSource() == contactStoreButton) {
            EventQueue.invokeLater(this::createStoreContactWindow);
        }
        if (e.getSource() == sendMessageButton) {
            writer.write("Message");
            writer.println();
            writer.flush();
            JOptionPane.showMessageDialog(null, "Message Sent!", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == storeSendMessageButton) {
            writer.write("Message");
            writer.println();
            writer.flush();

            JOptionPane.showMessageDialog(null, "Message Sent!", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == blockRecipientButton) {
            JOptionPane.showMessageDialog(null, "Recipient Blocked!", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == appearInvisibleToRecipientButton) {
            JOptionPane.showMessageDialog(null, "You are now invisible to this recipient", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == signUpButton) {
            setVisible(false);
            EventQueue.invokeLater(this::initializeSignUpPage);
        }
        if (e.getSource() == registerButton) {
            signUpFrame.setVisible(false);
            setVisible(true);
        }
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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

class ButtonEditor extends DefaultCellEditor {
    protected JButton btn;
    private String lbl;
    private boolean clicked;
    StoreApplication storeApplication;

    public ButtonEditor(JCheckBox checkBox, StoreApplication storeApplication) {
        super(checkBox);

        this.storeApplication = storeApplication;

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
            EventQueue.invokeLater(storeApplication::createStoreContactWindow);
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