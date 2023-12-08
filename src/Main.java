import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            signUpButton, registerButton, storeSendMessageButton, searchButton;
    JButton saveButton = new JButton("Save");
    JButton clearButton = new JButton("Clear");
    JButton deleteAccountButton = new JButton("Delete Account");
    JButton contactStoreButton = new JButton("Contact Store");
    JButton sendMessageButton = new JButton("Send");
    JButton searchUserButton = new JButton("Search User");
    JButton createStoreButton = new JButton("Create Store");
    JTextField storeName;
    JTextField storeDesc;
    JButton confirmStoreCreateButton;
    JButton addRecipientButton = new JButton("Select Recipient");
    JComboBox roleSetting;
    JFrame storeCreationWindow, frame2;
    boolean loginCompleted = false;
    boolean connectedToServer = false;
    JTextArea msgArea;

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
    String role;
    String[] recipientNames;
    JTextField userSearchField;
    JComboBox recipientSelection;
    JComboBox<String> searchUserResults;
    JTextField messageField;
    JButton selectRecipientButton = new JButton("Select Recipient");
    JTable jTable1;
    TableModel tableModel;

    public JTable getjTable1() {
        return jTable1;
    }

    public StoreApplication() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800,600);
        setTitle("Login");
        setLocationRelativeTo(null);
        setLayout(null);

        // Establish connection
        try {
            socket = new Socket("localhost", 4242);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
            connectedToServer = true;
        } catch (IOException e) {
            System.out.println("Connection not found!");
        }

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

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                int tabIndex = tabbedPane.getSelectedIndex();

                if (tabIndex == 0) {
                    TableModel tableModel1 = getTableModel();
                    jTable1.setModel(tableModel1);

                    if (!isCustomer) {
                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Save Store Data"));

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Delete Store"));

                    } else {
                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Contact Store"));
                    }
                    jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));

                }

                if (tabIndex == 1) {


                }

                if (tabIndex == 2) {

                    writer.println("Account Data");
                    writer.println(ID);
                    writer.flush();

                    String accountUsername;
                    String accountEmail;
                    String accountPassword;
                    try {
                        accountUsername = reader.readLine();
                        accountEmail = reader.readLine();
                        accountPassword = reader.readLine();

                        System.out.println(accountUsername);
                        System.out.println(accountEmail);
                        System.out.println(accountPassword);

                        usernameSetting.setText(accountUsername);
                        emailSetting.setText(accountEmail);
                        passwordSetting.setText(accountPassword);

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                }

            }
        });

    }

    protected JComponent createAccountSettingsPane() {
        JPanel panel = new JPanel(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        writer.println("Account Data");
        writer.println(ID);
        writer.flush();

        String accountUsername;
        String accountEmail;
        String accountPassword;
        try {
            accountUsername = reader.readLine();
            accountEmail = reader.readLine();
            accountPassword = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JLabel usernameLbl = new JLabel("Username");
        usernameLbl.setPreferredSize(new Dimension(100, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(usernameLbl, c);

        usernameSetting = new JTextField(20);
        usernameSetting.setText(accountUsername);
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
        emailSetting.setText(accountEmail);
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
        passwordSetting.setText(accountPassword);
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

        msgArea = new JTextArea(null, 20, 60);
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

        writer.println("List Users");
        writer.println(ID);
        writer.flush();

        try {
            int userCount = Integer.parseInt(reader.readLine());
            recipientNames = new String[userCount];
            for (int i = 0; i < userCount; i++) {
                recipientNames[i] = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel recipientLbl = new JLabel("Recipient:");
        recipientLbl.setPreferredSize(new Dimension(100, 40));

        recipientSelection = new JComboBox();
        recipientSelection.setPreferredSize(new Dimension(300, 40));
        recipientSelection.addActionListener(StoreApplication.this);

        recipientPanel.add(recipientLbl);
        recipientPanel.add(recipientSelection);

        selectRecipientButton.setPreferredSize(new Dimension(200, 40));
        selectRecipientButton.addActionListener(StoreApplication.this);
        recipientPanel.add(selectRecipientButton);

        searchUserButton.setPreferredSize(new Dimension(200, 40));
        searchUserButton.addActionListener(StoreApplication.this);
        recipientPanel.add(searchUserButton);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(recipientPanel, c);

        // Create sub-panel for text field and button to send message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout());

        messageField = new JTextField(50);
        messageField.setPreferredSize(new Dimension(300, 40));

        sendMessageButton.setPreferredSize(new Dimension(100, 40));
        sendMessageButton.addActionListener(StoreApplication.this);

        messagePanel.add(messageField);
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

        jTable1 = new JTable();

        tableModel = getTableModel();

        jTable1.setModel(tableModel);

        if (!isCustomer) {
            jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), this,
                    "Save Store Data"));

            jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), this,
                    "Delete Store"));

        } else {
            jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), this,
                    "Contact Store"));
        }
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

        if (!isCustomer) {
            createStoreButton.setPreferredSize(new Dimension(100, 40));
            createStoreButton.addActionListener(StoreApplication.this);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 3;

            panel.add(createStoreButton, c);
        }

        return panel;
    }

    public TableModel getTableModel() {
        String[] columnNames;
        Object[][] data = null;
        if (role.equals("Customer")) {
            columnNames = new String[] {"Stores", "Owner", "Actions"};
            writer.println("Customer Dashboard");
            writer.flush();
            try {
                int storeCount = Integer.parseInt(reader.readLine());
                data = new Object[storeCount][];
                for (int i = 0; i < storeCount; i++) {
                    data[i] = new Object[3];
                    data[i][0] = reader.readLine();
                    data[i][1] = reader.readLine();
                    data[i][2] = "Contact Store";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            columnNames = new String[] {"Stores", "Descriptions", "Actions", "Actions"};
            writer.println("Seller Dashboard");
            writer.println(ID);
            writer.flush();
            try {
                int storeCount = Integer.parseInt(reader.readLine());
                System.out.println(storeCount);
                data = new Object[storeCount][];
                for (int i = 0; i < storeCount; i++) {
                    data[i] = new Object[4];
                    data[i][0] = reader.readLine();
                    data[i][1] = reader.readLine();
                    data[i][2] = "Save Data";
                    data[i][3] = "Delete Store";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TableModel tableModel = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                if (role.equals("Customer")) {
                    return column == 2;
                } else {
                    return true;
                }
            }
        };
        return tableModel;
    }
    public void createSearchUserWindow() {
        frame2 = new JFrame("Search User");
        frame2.setSize(new Dimension(300, 200));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        userSearchField = new JTextField();

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(userSearchField, c);

        searchButton = new JButton("Search Username");
        searchButton.addActionListener(StoreApplication.this);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchButton, c);

        searchUserResults = new JComboBox();
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchUserResults, c);

        addRecipientButton.addActionListener(StoreApplication.this);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addRecipientButton, c);

        frame2.getContentPane().add(BorderLayout.CENTER, panel);
        frame2.setVisible(true);
        frame2.setResizable(true);
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

    private void initializeStoreCreationWindow() {
        storeCreationWindow = new JFrame("Create Store");
        storeCreationWindow.setSize(new Dimension(300, 200));

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        JLabel label1 = new JLabel("Store Name");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label1, c);

        storeName = new JTextField();
        storeName.setPreferredSize(new Dimension(300, 40));
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(storeName, c);

        JLabel label2 = new JLabel("Store Description");
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label2, c);

        storeDesc = new JTextField();
        storeDesc.setPreferredSize(new Dimension(300, 40));
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(storeDesc, c);

        confirmStoreCreateButton = new JButton("Create Store");
        confirmStoreCreateButton.addActionListener(StoreApplication.this);
        confirmStoreCreateButton.setPreferredSize(new Dimension(100, 40));
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(confirmStoreCreateButton, c);

        storeCreationWindow.getContentPane().add(BorderLayout.CENTER, panel);
        storeCreationWindow.setVisible(true);
        storeCreationWindow.setResizable(true);
    }

    public static void main(String[] args) {
        StoreApplication storeApplication = new StoreApplication();
        Account.resetAccountsData();
        Account.createAccount("Customer1", "customer1@gmail.com", "Customer1", "Customer"); // ID = 0
        Account.createAccount("Seller1", "seller1@gmail.com", "Seller1", "Seller");  // ID = 2
        Account.createAccount("Seller2", "seller2@gmail.com", "Seller2", "Seller");  // ID = 4
        Account.createAccount("Seller3", "seller3@gmail.com", "Seller3", "Seller");  // ID = 6
        Store.createStore("2", "Store1", "Seller1's first store");      // ID = 1
        Store.createStore("2", "Store2", "Seller1's second store");     // ID = 3
        Store.createStore("2", "Store3", "Seller1's third store");      // ID = 5
        Store.createStore("4", "Store4", "Seller2's store");    // ID = 7
        Store.createStore("6", "Store5", "Seller3's store");    // ID = 9
        Message.createMessage("0", "2", "Hello!");
        Message.createMessage("2", "0", "Hi.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {

            try {
                if (!connectedToServer || !socket.isConnected()) {
                    socket = new Socket("localhost", 4242);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new PrintWriter(socket.getOutputStream());
                    connectedToServer = true;
                }

                writer.println("Log In");
                writer.println(username.getText());
                writer.println(password.getPassword());
                writer.flush();


                String line = reader.readLine();
                if (line.equals("Success")) {
                    ID = reader.readLine();
                    role = reader.readLine();

                    if ( role.equals("Customer") ) {
                        isCustomer = true;
                    } else {
                        isCustomer = false;
                    }

                    setVisible(false);
                    EventQueue.invokeLater(this::initializeApp);
                } else {
                    JOptionPane.showMessageDialog(null, "Please check your username and password " +
                                    "before trying again.", "Error!",
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
            writer.println("Save Account Data");
            writer.println(ID);
            writer.println(usernameSetting.getText());
            writer.println(emailSetting.getText());
            writer.println(passwordSetting.getText());
            writer.flush();
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            if (line.equals("Success")) {
                JOptionPane.showMessageDialog(null, "Saved.",
                        "Account Data", JOptionPane.INFORMATION_MESSAGE);
            } else if (line.equals("Blank")) {
                JOptionPane.showMessageDialog(null, "At least one of your fields is blank. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (line.equals("Username Taken")) {
                JOptionPane.showMessageDialog(null, "That username is taken! Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else if (line.equals("Email Taken")) {
                JOptionPane.showMessageDialog(null, "That email is taken! Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == clearButton) {
            usernameSetting.setText("");
            emailSetting.setText("");
            passwordSetting.setText("");
        }

        if (e.getSource() == contactStoreButton) {
            EventQueue.invokeLater(this::createStoreContactWindow);
        }

        if (e.getSource() == searchUserButton) {
            EventQueue.invokeLater(this::createSearchUserWindow );
        }

        if (e.getSource() == sendMessageButton) {
            writer.println("Send User Message");
            writer.println(ID);
            writer.println(String.valueOf(recipientSelection.getSelectedItem()));
            writer.println(messageField.getText());
            writer.flush();

            try {
                String response = reader.readLine();

                if ( response.equals("Success") ) {

                    String recipientName = String.valueOf(recipientSelection.getSelectedItem());
                    writer.println("Conversation");
                    writer.println(ID);
                    writer.println(recipientName);
                    writer.flush();

                    String messageCountLine = reader.readLine();
                    int messageCount = Integer.parseInt(messageCountLine);

                    msgArea.setText(null);
                    for (int i = 0; i < messageCount; i++) {
                        msgArea.append(reader.readLine() + ": " + reader.readLine() + "\n");
                    }

                } else if ( response.equals("Blocked") ) {
                    JOptionPane.showMessageDialog(null, "Blocked!", "Conversations",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "This recipient has deleted their account!", "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (e.getSource() == storeSendMessageButton) {
            writer.write("Message");
            writer.println();
            writer.flush();

            JOptionPane.showMessageDialog(null, "Message Sent!", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == blockRecipientButton) {
            writer.println("Toggle Block");
            writer.println(ID);
            writer.println(recipientSelection.getSelectedItem());
            writer.flush();

            JOptionPane.showMessageDialog(null, "Recipient Blocked!", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == appearInvisibleToRecipientButton) {
            writer.println("Toggle Invisible");
            writer.println(ID);
            writer.println(recipientSelection.getSelectedItem());
            writer.flush();

            JOptionPane.showMessageDialog(null, "You are now invisible to this recipient", "Conversations",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == signUpButton) {
            setVisible(false);
            EventQueue.invokeLater(this::initializeSignUpPage);
        }

        if (e.getSource() == registerButton) {

            try {
                if (!connectedToServer) {
                    socket = new Socket("localhost", 4242);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new PrintWriter(socket.getOutputStream());
                    connectedToServer = true;
                }

                writer.println("Sign up");
                writer.println(username.getText());
                writer.println(emailSetting.getText());
                writer.println(password.getPassword());
                writer.println(String.valueOf(roleSetting.getSelectedItem()));
                writer.flush();

                String line = reader.readLine();
                if (line.equals("Success")) {
                    JOptionPane.showMessageDialog(null, "Account created.",
                            "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                    signUpFrame.setVisible(false);
                    setVisible(true);
                } else if (line.equals("Blank")) {
                    JOptionPane.showMessageDialog(null, "At least one of your fields is blank. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (line.equals("Username Taken")) {
                    JOptionPane.showMessageDialog(null, "That username is taken! Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (line.equals("Email Taken")) {
                    JOptionPane.showMessageDialog(null, "That email is taken! Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (e.getSource() == deleteAccountButton) {
            writer.println("Delete Account");
            writer.println(ID);
            writer.flush();
            JOptionPane.showMessageDialog(null, "Account Deleted!", "Account Settings",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == createStoreButton) {
            EventQueue.invokeLater(this::initializeStoreCreationWindow);
        }

        if (e.getSource() == confirmStoreCreateButton) {
            writer.println("Create Store");
            writer.println(ID);
            writer.println(storeName.getText());
            writer.println(storeDesc.getText());
            writer.flush();

            try {
                String line = reader.readLine();

                if (line.equals("Success")) {
                    JOptionPane.showMessageDialog(null, "Store created!",
                            "Success!", JOptionPane.INFORMATION_MESSAGE);
                    storeCreationWindow.setVisible(false);

                    TableModel tableModel1 = getTableModel();
                    jTable1.setModel(tableModel1);

                    if (!isCustomer) {
                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Save Store Data"));

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Delete Store"));

                    } else {
                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Contact Store"));
                    }
                    jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));

                } else if (line.equals("Name Blank")) {
                    JOptionPane.showMessageDialog(null, "The store name is blank. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (line.equals("Name Exists")) {
                    JOptionPane.showMessageDialog(null, "That store name is taken! Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        if (e.getSource() == searchButton) {
            String searchText = userSearchField.getText();
            writer.println("Search Users");
            writer.println(ID);
            writer.println(searchText);
            writer.flush();
            searchUserResults.removeAllItems();
            try {
                int resultCount = Integer.parseInt(reader.readLine());
                System.out.println(resultCount);
                if (resultCount == 0) {
                    searchUserResults.addItem("No results");
                } else {
                    for (int i = 0; i < resultCount; i++) {
                        searchUserResults.addItem(reader.readLine());
                    }
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
        if ( e.getSource() == selectRecipientButton ) {
            String recipientName = String.valueOf(recipientSelection.getSelectedItem());
            writer.println("Conversation");
            writer.println(ID);
            writer.println(recipientName);
            writer.flush();

            try {
                String messageCountLine = reader.readLine();
                int messageCount = Integer.parseInt(messageCountLine);

                msgArea.setText(null);
                for (int i = 0; i < messageCount; i++) {
                    msgArea.append(reader.readLine() + ": " + reader.readLine() + "\n");
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }
        if (e.getSource() == addRecipientButton) {
            Object recipientName = searchUserResults.getSelectedItem();
            recipientSelection.addItem(recipientName);

            writer.println("Conversation");
            writer.println(ID);
            writer.println(recipientName.toString());
            writer.flush();

            try {
                String messageCountLine = reader.readLine();
                int messageCount = Integer.parseInt(messageCountLine);

                msgArea.setText(null);
                for (int i = 0; i < messageCount; i++) {
                    msgArea.append(reader.readLine() + ": " + reader.readLine() + "\n");
                }

                frame2.setVisible(false);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

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
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Save Store Data"));

                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                storeApplication,
                                "Delete Store"));

                    } else {
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        storeApplication.jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
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