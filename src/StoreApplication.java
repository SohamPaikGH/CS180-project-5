import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
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
 * @version December 10, 2023
 */
class StoreApplication extends JFrame implements ActionListener {
    String host = "localhost"; // IP Address of Host
    JPasswordField password; // Password field used in the initial Sign In and Sign Up Pages
    JTextField username, usernameSetting, emailSetting, passwordSetting; // Text fields that take input for username
    // and email in the sign up page and Account Settings tab of the main application window
    JLabel label_password, label_username, title; // Labels for text fields that take input for username and email

    // signInButton allows user to sign in, signUpButton initializes the Sign Up window
    // registerButton creates user's new account in Sign Up window
    // blockRecipientButton and appearInvisibletoRecipientButtons toggle blocking and invisibility for users
    // storeSendMessageButton and searchButton send messages to selected stores in dashboard and search for users
    JButton signInButton, blockRecipientButton, appearInvisibleToRecipientButton,
            signUpButton, registerButton, storeSendMessageButton, searchButton;
    JButton saveButton = new JButton("Save"); // Saves new username, email, and password in Account Settings tab
    JButton clearButton = new JButton("Clear"); // Clears username, email, and password text fields in Account Settings
    JButton deleteAccountButton = new JButton("Delete Account"); // Deletes account when pushed in Account Settings
    JButton contactStoreButton = new JButton("Contact Store"); // Initializes the Contact Store window
    JButton sendMessageButton = new JButton("Send"); // Sends message to specified store/user when clicked
    JButton searchUserButton = new JButton("Search User"); // Initializes the Search User window in Conversations tab
    JButton createStoreButton = new JButton("Create Store"); // Creates store owned by user when triggered
    JTextField storeName; // Text field for taking input for store name
    JTextField storeDesc; // Text field for taking input for store description
    JButton confirmStoreCreateButton; // Creates store when triggered in the Store Creation window
    JButton addRecipientButton = new JButton("Select Recipient"); // Selects recipient user in Conversations
    JComboBox roleSetting; // Used in the Sign Up window to get input for user's role (Customer or Seller)
    JFrame storeCreationWindow, searchUserFrame; // Windows for the Store Creation Window and the Search User Window
    boolean connectedToServer = false; // verifies if user is connected to server
    JTable msgTable; // displays all messages from selected recipient in a table in the Conversations tab
    JTextField storeMessageField;  // Text field where user enters message they want to send to a store

    boolean isCustomer = false; // if true, user is customer; if false, user is seller
    JFrame signUpFrame, storeContactFrame; //  Sign Up and Store Contact Windows
    Socket socket; // Used to create and manage connection between client and server
    BufferedReader reader; // Reads data sent by server
    PrintWriter writer; // Writes data to server
    String ID; // Stores user's account ID
    String role; // Stores user's role (e.g. Customer, Seller)
    String recipientName; // Stores the name of the customer/seller/store the user wants to message
    JTextField userSearchField; // Text field for storing user's search query in Search Users window
    JComboBox recipientSelection; // Stores recipient user has currently messages
    JComboBox<String> searchUserResults; // Displays all search results returned by customer query
    JTextField messageField; // Text field to enter the message user wants to send to recipient in Conversations

    // Adds recipient user chooses in Search Results to recipientSelection
    JButton selectRecipientButton = new JButton("Select Recipient");
    JTable jTable1; // Represents store dashboard table
    TableModel tableModel; // Stores table model of the Store dashboard table
    JComboBox sellerViewSelect; // Allows seller to message customers as a store
    String conversationID; // Stores the ID of the conversation user has with another user
    JButton selectViewButton; // Selects the view specified in sellerViewSelect
    JFrame mainFrame; // Represents main application window

    // Gets store dashboard table
    public JTable getjTable1() {
        return jTable1;
    }

    public StoreApplication() {
        // Closes socket whenever user exits program
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                writer.write("Close Socket");
                writer.flush();
                System.exit(0);
            }
        });

        // Initializes login page
        setSize(800,600);
        setTitle("Login");
        setLocationRelativeTo(null);
        setLayout(null);

        // Establishes connection to server
        try {
            socket = new Socket(host, 4242);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
            connectedToServer = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Adds all required fields, buttons , and labels to login page
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

        // Makes login window visible
        setResizable(false);
        setVisible(true);
    }

    // Creates Sign Up page after user has hit the "Sign Up" button
    protected void initializeSignUpPage() {
        // Creates the window and its properties (e.g. title, dimensions, layout)
        signUpFrame = new JFrame();
        signUpFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        signUpFrame.setSize(new Dimension(800, 600));
        signUpFrame.setTitle("Sign Up");
        signUpFrame.setLocationRelativeTo(null);
        signUpFrame.setLayout(null);

        // Adds all required labels, fields, and buttons to window
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

        // Makes Sign Up frame visible
        signUpFrame.setVisible(true);
        signUpFrame.setResizable(false);
    }

    protected void initializeApp() {
        // Creates main application window and sets its dimensions to size of user window
        mainFrame = new JFrame("Store Application");
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(size);

        // Sets look and feel of the window to the default OS look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates a tabbed pane with three tabs and three panels
        // Each panel represents one of the tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Creates Stores dashboard tab
        JComponent panel1 = createDashboardPane();
        tabbedPane.addTab("Stores", null, panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        // Creates Conversations tab
        JComponent panel2 = createConversationsPane();
        tabbedPane.addTab("Conversations", null, panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        // Creates Account Settings tab
        JComponent panel3 = createAccountSettingsPane();
        tabbedPane.addTab("Account", null, panel3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        // Adds all tab to main window
        mainFrame.getContentPane().add(tabbedPane);
        mainFrame.setVisible(true);
        mainFrame.setResizable(true);

        // Adds change listener to tabbed pane, so that window is automatically refreshed
        // whenever user selects a different tab
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                int tabIndex = tabbedPane.getSelectedIndex();

                if (tabIndex == 0) {
                    TableModel tableModel1 = getTableModel();
                    jTable1.setModel(tableModel1);

                    if (!isCustomer) {
                        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        jTable1.getColumnModel().getColumn(1).setPreferredWidth(550);
                        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
                        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Save Store Data"));

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Delete Store"));

                    } else {
                        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
                        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
                        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Contact Store"));
                    }
                    jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));

                }

                if (tabIndex == 1) {
                    if (role.equals("Seller")) {
                        sellerViewSelect.removeAllItems();
                        writer.println("Seller View Options");
                        writer.println(ID);
                        writer.flush();

                        try {
                            sellerViewSelect.addItem(reader.readLine());
                            int storeCount = Integer.parseInt(reader.readLine());
                            for (int i = 0; i < storeCount; i++) {
                                sellerViewSelect.addItem(reader.readLine());
                            }
                        } catch (IOException ee) {
                            ee.printStackTrace();
                        }

                    }

                    writer.println("Get Conversations");
                    writer.println(conversationID);
                    writer.flush();

                    recipientSelection.removeAllItems();
                    try {
                        int namesCount = Integer.parseInt(reader.readLine());
                        for (int i = 0; i < namesCount; i++) {
                            recipientSelection.addItem(reader.readLine());
                        }
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }

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

    // Creates Account Settings tab
    protected JComponent createAccountSettingsPane() {
        // Creates panel to contain all components of this tab
        // GridBagLayout is default layout of the panel because it is easier to use
        JPanel panel = new JPanel(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Client fetches the username, email, and password of the user from the server
        // Then it fills the username, email, and password text fields with the suitable data it gets from server
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

        // Adds all required labels, buttons, and text fields using GridBagLayout
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

    // Creates Conversations tab
    protected JComponent createConversationsPane() {
        // Creates panel to contain all components of this tab
        // GridBagLayout is default layout of the panel because it is easier to use
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Checks user's role and changes the number of columns and the column names of the
        // Stores dashboard table accordingly
        if (role.equals("Seller")) {
            sellerViewSelect = new JComboBox();
            writer.println("Seller View Options");
            writer.println(ID);
            writer.flush();

            // Gets all stores owned by seller and add them to sellerViewSelect
            try {
                sellerViewSelect.addItem(reader.readLine());
                int storeCount = Integer.parseInt(reader.readLine());
                for (int i = 0; i < storeCount; i++) {
                    sellerViewSelect.addItem(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            panel.add(sellerViewSelect, c);

            selectViewButton = new JButton("Select View");
            selectViewButton.addActionListener(StoreApplication.this);
            c.gridx = 1;
            panel.add(selectViewButton, c);
        }

        // Create message area

        msgTable = new JTable();
        TableModel msgDataTable = getMessagesTable(null);
        msgTable.setModel(msgDataTable);
        JScrollPane msgScrollPane = new JScrollPane(msgTable);

        // Sets dimensions of each column in the table
        msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
        msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Adds buttons for editing and deleting messages to the last 2 columns of messages table
        msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        msgTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                StoreApplication.this,
                "Edit Message"));

        msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        msgTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                StoreApplication.this,
                "Delete Message"));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(msgScrollPane, c);

        // Creates sub-panel for dropdown menu that lets user select recipient
        // Creates dropdown menu to select user to message
        // Add all necessary buttons and labels
        JPanel recipientPanel = new JPanel();
        recipientPanel.setLayout(new FlowLayout());

        JLabel recipientLbl = new JLabel("Recipient:");
        recipientLbl.setPreferredSize(new Dimension(100, 40));

        recipientSelection = new JComboBox();
        recipientSelection.setPreferredSize(new Dimension(300, 40));
        recipientSelection.addActionListener(StoreApplication.this);

        writer.println("Get Conversations");
        writer.println(conversationID);
        writer.flush();

        // Remove all items if one of the recipients has deleted their account
        recipientSelection.removeAllItems();
        try {
            int namesCount = Integer.parseInt(reader.readLine());
            for (int i = 0; i < namesCount; i++) {
                recipientSelection.addItem(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        c.gridy = 2;
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
        c.gridy = 3;
        panel.add(messagePanel, c);

        // Create sub-panel for blocking and invisibility features
        // Then add buttons to block and be invisible to users
        JPanel blockingInvisibilityPanel = new JPanel();
        blockingInvisibilityPanel.setLayout(new FlowLayout());

        blockRecipientButton = new JButton("Toggle Block");
        blockRecipientButton.setPreferredSize(new Dimension(100, 40));
        blockRecipientButton.addActionListener(StoreApplication.this);

        appearInvisibleToRecipientButton = new JButton("Toggle Invisibility");
        appearInvisibleToRecipientButton.setPreferredSize(new Dimension(150, 40));
        appearInvisibleToRecipientButton.addActionListener(StoreApplication.this);

        blockingInvisibilityPanel.add(blockRecipientButton);
        blockingInvisibilityPanel.add(appearInvisibleToRecipientButton);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        panel.add(blockingInvisibilityPanel, c);

        return panel;
    }

    public TableModel getMessagesTable(Object[][] data) {
        String[] columnNames = {"Sender", "Message", "Actions", "Actions"};

        TableModel tableModel1 = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        return tableModel1;
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
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(550);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

            jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), this,
                    "Save Store Data"));

            jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), this,
                    "Delete Store"));

        } else {
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

            jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), this,
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
            columnNames = new String[] {"Stores", "Descriptions", "Owner", "Actions"};
            writer.println("Customer Dashboard");
            writer.flush();
            try {
                int storeCount = Integer.parseInt(reader.readLine());
                data = new Object[storeCount][];
                for (int i = 0; i < storeCount; i++) {
                    data[i] = new Object[4];
                    data[i][0] = reader.readLine();
                    data[i][1] = reader.readLine();
                    data[i][2] = reader.readLine();
                    data[i][3] = "Contact Store";
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
                    return column == 3;
                } else {
                    return true;
                }
            }
        };
        return tableModel;
    }
    public void createSearchUserWindow() {
        searchUserFrame = new JFrame("Search User");
        searchUserFrame.setSize(new Dimension(300, 200));

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

        searchUserFrame.getContentPane().add(BorderLayout.CENTER, panel);
        searchUserFrame.setVisible(true);
        searchUserFrame.setResizable(true);
    }
    public void createStoreContactWindow() {
        storeContactFrame = new JFrame("Contact Store");
        storeContactFrame.setSize(new Dimension(300, 200));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        JLabel label1 = new JLabel("Message");
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label1, c);

        storeMessageField = new JTextField(20);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(storeMessageField, c);

        storeSendMessageButton = new JButton("Send");
        storeSendMessageButton.addActionListener(StoreApplication.this);
        storeSendMessageButton.setPreferredSize(new Dimension(100, 40));
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(storeSendMessageButton, c);

        storeContactFrame.getContentPane().add(BorderLayout.CENTER, panel);
        storeContactFrame.setVisible(true);
        storeContactFrame.setResizable(true);
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {

            try {
                if (!connectedToServer || !socket.isConnected()) {
                    socket = new Socket(host, 4242);
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
                    conversationID = ID;
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
            if (role.equals("Seller") && !ID.equals(conversationID)) {
                writer.println("Send Message As Store");
            } else {
                writer.println("Send Message");
            }
            writer.println(conversationID);
            writer.println(String.valueOf(recipientSelection.getSelectedItem()));
            writer.println(messageField.getText());
            writer.flush();

            try {
                String response = reader.readLine();

                if ( response.equals("Success") ) {

                    if (!recipientName.isEmpty()) {
                        writer.println("Conversation");
                        writer.println(conversationID);
                        writer.println(recipientName);
                        writer.flush();

                        String messageCountLine = reader.readLine();
                        int messageCount = Integer.parseInt(messageCountLine);

                        Object[][] messageData = new Object[messageCount][4];
                        for (int i = 0; i < messageCount; i++) {
                            messageData[i][0] = reader.readLine();
                            messageData[i][1] = reader.readLine();
                            if (!messageData[i][0].equals(recipientName)) {
                                messageData[i][2] = "Edit";
                                messageData[i][3] = "Delete";
                            }
                        }

                        TableModel messagesDataTable = getMessagesTable(messageData);
                        msgTable.setModel(messagesDataTable);

                        msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                        msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                        msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                        msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                        msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                        msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        msgTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Edit Message"));

                        msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        msgTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Delete Message"));
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
            String storeName = jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString();

            writer.println("Send Message");
            writer.println(ID);
            writer.println(storeName);
            writer.println(storeMessageField.getText());
            writer.flush();

            try {
                String line = reader.readLine();

                if (line.equals("Success")) {
                    JOptionPane.showMessageDialog(null, "Message Sent!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (line.equals("Blocked")) {
                    JOptionPane.showMessageDialog(null, "Blocked!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "This store has been deleted!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }

        if (e.getSource() == blockRecipientButton) {
            writer.println("Toggle Block");
            writer.println(ID);
            writer.println(recipientSelection.getSelectedItem());
            writer.flush();

            try {
                String response = reader.readLine();
                if (response.equals("Blocked")) {
                    JOptionPane.showMessageDialog(null, "User blocked!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (response.equals("Unblocked")) {
                    JOptionPane.showMessageDialog(null, "User unblocked!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }

        if (e.getSource() == appearInvisibleToRecipientButton) {
            writer.println("Toggle Invisible");
            writer.println(ID);
            writer.println(recipientSelection.getSelectedItem());
            writer.flush();

            try {
                String response = reader.readLine();
                if (response.equals("Invisible")) {
                    JOptionPane.showMessageDialog(null, "You are now invisible to this user!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (response.equals("Visible")) {
                    JOptionPane.showMessageDialog(null, "You are now visible to this user!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }

        if (e.getSource() == signUpButton) {
            setVisible(false);
            EventQueue.invokeLater(this::initializeSignUpPage);
        }

        if (e.getSource() == registerButton) {

            try {
                if (!connectedToServer) {
                    socket = new Socket(host, 4242);
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
            JOptionPane.showMessageDialog(null, "Account Deleted! Program will close.", "Account Settings",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.dispose();
            writer.write("Close Socket");
            System.exit(0);
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
                        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        jTable1.getColumnModel().getColumn(1).setPreferredWidth(550);
                        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
                        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Save Store Data"));

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Delete Store"));

                    } else {
                        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
                        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
                        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
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
                if (resultCount != 0) {
                    for (int i = 0; i < resultCount; i++) {
                        searchUserResults.addItem(reader.readLine());
                    }
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
        if ( e.getSource() == selectRecipientButton ) {
            recipientName = String.valueOf(recipientSelection.getSelectedItem());
            if (!recipientName.isEmpty()) {
                writer.println("Conversation");
                writer.println(conversationID);
                writer.println(recipientName);
                writer.flush();

                try {
                    String messageCountLine = reader.readLine();
                    int messageCount = Integer.parseInt(messageCountLine);

                    Object[][] messageData = new Object[messageCount][4];
                    for (int i = 0; i < messageCount; i++) {
                        messageData[i][0] = reader.readLine();
                        messageData[i][1] = reader.readLine();
                        if (!messageData[i][0].equals(recipientName)) {
                            messageData[i][2] = "Edit";
                            messageData[i][3] = "Delete";
                        }
                    }

                    TableModel messagesDataTable = getMessagesTable(messageData);
                    msgTable.setModel(messagesDataTable);

                    msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                    msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                    msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                    msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                    msgTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                            StoreApplication.this,
                            "Edit Message"));

                    msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                    msgTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                            StoreApplication.this,
                            "Delete Message"));

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

        }
        if (e.getSource() == addRecipientButton) {
            recipientName = (String) searchUserResults.getSelectedItem();
            boolean itemExists = false;
            for (int i = 0; i < recipientSelection.getItemCount(); i++) {
                if (recipientSelection.getItemAt(i).equals(recipientName)) {
                    itemExists = true;
                }
            }
            if (!itemExists) {
                recipientSelection.addItem(recipientName);
            }

            recipientSelection.setSelectedItem(recipientName);

            writer.println("Conversation");
            writer.println(conversationID);
            writer.println(recipientName);
            writer.flush();

            try {
                String messageCountLine = reader.readLine();
                int messageCount = Integer.parseInt(messageCountLine);

                Object[][] messageData = new Object[messageCount][4];
                for (int i = 0; i < messageCount; i++) {
                    messageData[i][0] = reader.readLine();
                    messageData[i][1] = reader.readLine();
                    if (!messageData[i][0].equals(recipientName)) {
                        messageData[i][2] = "Edit";
                        messageData[i][3] = "Delete";
                    }
                }

                TableModel messagesDataTable = getMessagesTable(messageData);
                msgTable.setModel(messagesDataTable);

                msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                msgTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                        StoreApplication.this,
                        "Edit Message"));

                msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                msgTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                        StoreApplication.this,
                        "Delete Message"));

                searchUserFrame.setVisible(false);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }

        if (e.getSource() == selectViewButton) {
            String choice = sellerViewSelect.getSelectedItem().toString();
            writer.println("Get ID");
            writer.println(choice);
            writer.flush();
            try {
                conversationID = reader.readLine();
            } catch (IOException ee) {
                ee.printStackTrace();
            }

            writer.println("Get Conversations");
            writer.println(conversationID);
            writer.flush();

            recipientSelection.removeAllItems();
            try {
                int namesCount = Integer.parseInt(reader.readLine());
                for (int i = 0; i < namesCount; i++) {
                    recipientSelection.addItem(reader.readLine());
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }

            String recipientName = String.valueOf(recipientSelection.getSelectedItem());
            if (!recipientName.isEmpty()) {
                writer.println("Conversation");
                writer.println(conversationID);
                writer.println(recipientName);
                writer.flush();

                try {
                    String messageCountLine = reader.readLine();
                    int messageCount = Integer.parseInt(messageCountLine);

                    Object[][] messageData = new Object[messageCount][4];
                    for (int i = 0; i < messageCount; i++) {
                        messageData[i][0] = reader.readLine();
                        messageData[i][1] = reader.readLine();
                        if (!messageData[i][0].equals(recipientName)) {
                            messageData[i][2] = "Edit";
                            messageData[i][3] = "Delete";
                        }
                    }

                    TableModel messagesDataTable = getMessagesTable(messageData);
                    msgTable.setModel(messagesDataTable);

                    msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                    msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                    msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                    msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                    msgTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                            StoreApplication.this,
                            "Edit Message"));

                    msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                    msgTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                            StoreApplication.this,
                            "Delete Message"));

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}