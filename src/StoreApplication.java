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
 *
 * This file initializes the store application client and the GUI. All calls to the
 * server from the client are made by the StoreApplication object.
 *
 * @author Soham Paik, Sean Kim, Yash Patel, CS 18000 Black, lab sec l17
 *
 * @version December 11, 2023
 */
class StoreApplication extends JFrame implements ActionListener {
    String host = "localhost"; // IP Address of Host
    JPasswordField password; // Password field used in the initial Sign In and Sign Up Pages
    JTextField usernameSignUpPage;
    JPasswordField passwordSignUpPage;
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
    JButton clearButton = new JButton("Clear"); // Clears username, email, and password fields in Account Settings
    JButton deleteAccountButton = new JButton("Delete Account"); // Deletes account when pushed in Account Settings
    JButton contactStoreButton = new JButton("Contact Store"); // Initializes the Contact Store window
    JButton sendMessageButton = new JButton("Send"); // Sends message to specified store/user when clicked
    JButton searchUserButton = new JButton("Search User"); // Initializes the Search User window in Conversations
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

        usernameSignUpPage = new JTextField();
        usernameSignUpPage.setBounds(300, 200, 300, 40);

        passwordSignUpPage = new JPasswordField(50);
        passwordSignUpPage.setBounds(300, 250, 300, 40);

        emailSetting = new JTextField();
        emailSetting.setBounds(300, 300, 300, 40);

        String options[] = {"Customer", "Seller"};
        roleSetting = new JComboBox(options);
        roleSetting.setBounds(300, 350, 300, 40);

        registerButton = new JButton("Register");
        registerButton.setBounds(300, 400, 100, 40);
        registerButton.addActionListener(StoreApplication.this);

        signUpFrame.add(label_username);
        signUpFrame.add(usernameSignUpPage);
        signUpFrame.add(label_password);
        signUpFrame.add(passwordSignUpPage);
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

    // Generates data for messages table
    public TableModel getMessagesTable(Object[][] data) {
        String[] columnNames = {"Sender", "Message", "Actions", "Actions"};

        TableModel tableModel1 = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        return tableModel1;
    }

    // Creates Dashboard pane
    protected JComponent createDashboardPane() {
        // Creates panel to contain all components of this tab
        // GridBagLayout is default layout of the panel because it is easier to use
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Changes Stores Dashboard pane based on user role (e.g. Customer, Seller)
        if (isCustomer) {
            title = new JLabel("All Stores");
        } else {
            title = new JLabel("Your Stores");
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(title, c);

        // Adds Store dashboard table to tab
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
            jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                    this, "Save Store Data"));

            jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                    this, "Delete Store"));

        } else {
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

            jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                    this, "Contact Store"));
        }
        jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));

        // Adds table to scroll pane, so the table is scrollable
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.getViewport().setOpaque(true);
        jScrollPane1.setViewportBorder(null);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(jScrollPane1, c);

        // If the user is a seller, add the button to create stores at the bottom of the tab
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

    // Generates table model for store dashboard pane
    public TableModel getTableModel() {
        String[] columnNames;
        Object[][] data = null;
        // Changes the number and titles of columns if the user is seller v. customer
        if (role.equals("Customer")) {
            // Create column names and fetch store data from server
            columnNames = new String[] {"Stores", "Descriptions", "Owner", "Actions"};
            writer.println("Customer Dashboard");
            writer.flush();
            try {
                // Save all store data to the variable "data"
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
            // Create column names and fetch personal store data from server
            columnNames = new String[] {"Stores", "Descriptions", "Actions", "Actions"};
            writer.println("Seller Dashboard");
            writer.println(ID);
            writer.flush();
            try {
                // Save all store data to the variable "data"
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

        // Make column 3 editable for customers, so that the button for contacting stores can be clicked
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

    // Creates the window for searching users to message
    public void createSearchUserWindow() {
        // Create frame to represent the window and set its properties
        searchUserFrame = new JFrame("Search User");
        searchUserFrame.setSize(new Dimension(300, 200));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates panel to contain all components of this tab
        // GridBagLayout is default layout of the panel because it is easier to use
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        // Initialize the userSearchField
        userSearchField = new JTextField();

        // Adds the user search textfield to the window
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(userSearchField, c);

        // Adds the user search button to the window
        searchButton = new JButton("Search Username");
        searchButton.addActionListener(StoreApplication.this);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchButton, c);

        // Adds the dropdown menu that will display the results to the window
        searchUserResults = new JComboBox();
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchUserResults, c);

        // Adds the button to add a search result to the recipient list to the window
        addRecipientButton.addActionListener(StoreApplication.this);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addRecipientButton, c);

        // Adds all components of panel to window
        searchUserFrame.getContentPane().add(BorderLayout.CENTER, panel);
        searchUserFrame.setVisible(true);
        searchUserFrame.setResizable(true);
    }

    // Creates the window for contacting stores
    public void createStoreContactWindow() {
        // Create frame to represent the window and set its properties
        storeContactFrame = new JFrame("Contact Store");
        storeContactFrame.setSize(new Dimension(300, 200));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates panel to contain all components of this tab
        // GridBagLayout is default layout of the panel because it is easier to use
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        // Adds all necessary labels, text fields, and buttons to the window
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

        // Adds all components of panel to window
        storeContactFrame.getContentPane().add(BorderLayout.CENTER, panel);
        storeContactFrame.setVisible(true);
        storeContactFrame.setResizable(true);
    }

    // Creates the window for creating stores
    private void initializeStoreCreationWindow() {
        // Create frame to represent the window and set its properties
        storeCreationWindow = new JFrame("Create Store");
        storeCreationWindow.setSize(new Dimension(300, 200));

        // Creates panel to contain all components of this tab
        // GridBagLayout is default layout of the panel because it is easier to use
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);

        GridBagConstraints c = new GridBagConstraints();

        // Adds all required labels, text fields, and buttons to the panel
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

        // Adds all components of panel to window
        storeCreationWindow.getContentPane().add(BorderLayout.CENTER, panel);
        storeCreationWindow.setVisible(true);
        storeCreationWindow.setResizable(true);
    }

    // Creates and runs a new StoreApplication object
    public static void main(String[] args) {
        StoreApplication storeApplication = new StoreApplication();
    }

    // Actions for all button action listeners used in the store application
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {
            // When user signs in, connection to server is established
            try {
                if (!connectedToServer || !socket.isConnected()) {
                    socket = new Socket(host, 4242);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new PrintWriter(socket.getOutputStream());
                    connectedToServer = true;
                }

                // Login info sent to server
                writer.println("Log In");
                writer.println(username.getText());
                writer.println(password.getPassword());
                writer.flush();

                // If login info is valid, server sends back confirmation and main app window is initialized
                // Otherwise, the server tells client the login info is invalid, which causes an error message to pop up
                // An error message will also pop up if the server is not up
                String line = reader.readLine();
                System.out.println(line);
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
                    JOptionPane.showMessageDialog(null,
                            "Please check your username and password " + "before trying again.", "Error!",
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
            // Saves any changes made to account data

            // Sends all user info for email, username, and password to server
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

            // If all changes are successfully saved, server sends confirmation and client creates pop up to show
            // user it worked
            // Otherwise, based on what the server sends back, the client creates pop up that shows what the user did
            // wrong
            if (line.equals("Success")) {
                JOptionPane.showMessageDialog(null, "Saved.",
                        "Account Data", JOptionPane.INFORMATION_MESSAGE);
            } else if (line.equals("Blank")) {
                JOptionPane.showMessageDialog(null,
                        "At least one of your fields is blank. Please try again.",
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
            // Clears all fields in Account Settings
            usernameSetting.setText("");
            emailSetting.setText("");
            passwordSetting.setText("");
        }

        if (e.getSource() == contactStoreButton) {
            // Opens store contact window
            EventQueue.invokeLater(this::createStoreContactWindow);
        }

        if (e.getSource() == searchUserButton) {
            // Opens search user window
            EventQueue.invokeLater(this::createSearchUserWindow );
        }

        if (e.getSource() == sendMessageButton) {
            // Sends message to user
            if (role.equals("Seller") && !ID.equals(conversationID)) {
                // Server is told that user wants to send message as store
                writer.println("Send Message As Store");
            } else {
                // Server is told that user wants to directly message
                writer.println("Send Message");
            }
            // Sends the conversation ID, recipient name, and message to the server
            writer.println(conversationID);
            writer.println(String.valueOf(recipientSelection.getSelectedItem()));
            writer.println(messageField.getText());
            writer.flush();

            try {
                // Gets response from server
                String response = reader.readLine();

                if ( response.equals("Success") ) {
                    // The message was sent

                    if (!recipientName.isEmpty()) {
                        // If the recipient still exists and hasn't deleted their account,
                        // Send the conversation ID to the server
                        writer.println("Conversation");
                        writer.println(conversationID);
                        writer.println(recipientName);
                        writer.flush();

                        String messageCountLine = reader.readLine();
                        int messageCount = Integer.parseInt(messageCountLine);

                        // Read all message data sent by the server
                        Object[][] messageData = new Object[messageCount][4];
                        for (int i = 0; i < messageCount; i++) {
                            // Add each message to the data for messages that will be added to Conversations table
                            messageData[i][0] = reader.readLine();
                            messageData[i][1] = reader.readLine();
                            // Leave the last two columns for the Edit and Delete buttons
                            if (!messageData[i][0].equals(recipientName)) {
                                messageData[i][2] = "Edit";
                                messageData[i][3] = "Delete";
                            }
                        }

                        // Add the data to the messages data table
                        TableModel messagesDataTable = getMessagesTable(messageData);
                        msgTable.setModel(messagesDataTable);

                        // Set up the formatting and size of the message table in Conversations
                        msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                        msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                        msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                        msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                        msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                        msgTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        msgTable.getColumnModel().getColumn(2).setCellEditor(
                                new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Edit Message"));

                        msgTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        msgTable.getColumnModel().getColumn(3).setCellEditor(
                                new ButtonEditor(new JCheckBox(), StoreApplication.this,
                                "Delete Message"));
                    }

                } else if ( response.equals("Blocked") ) {
                    // The message is blocked
                    JOptionPane.showMessageDialog(null, "Blocked!", "Conversations",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // The recipient deleted their account
                    JOptionPane.showMessageDialog(null,
                            "This recipient has deleted their account!", "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (e.getSource() == storeSendMessageButton) {
            // Get name of store user wants to contact
            String storeName = jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString();

            // Tell server the message, ID, and name of the store user wants to contact
            writer.println("Send Message");
            writer.println(ID);
            writer.println(storeName);
            writer.println(storeMessageField.getText());
            writer.flush();

            try {
                // Read data from server
                String line = reader.readLine();

                if (line.equals("Success")) {
                    // The message has been sent, so tell the user it successfully reached the recipient
                    JOptionPane.showMessageDialog(null, "Message Sent!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (line.equals("Blocked")) {
                    // The recipient blocked the user
                    JOptionPane.showMessageDialog(null, "Blocked!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // The store no longer exists
                    JOptionPane.showMessageDialog(null, "This store has been deleted!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }

        if (e.getSource() == blockRecipientButton) {
            // Toggle blocking
            // Send ID and name of recipient to block to the server
            writer.println("Toggle Block");
            writer.println(ID);
            writer.println(recipientSelection.getSelectedItem());
            writer.flush();

            try {
                // Read server data
                String response = reader.readLine();
                if (response.equals("Blocked")) {
                    // The recipient was not blocked before but just got blocked by this button
                    JOptionPane.showMessageDialog(null, "User blocked!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (response.equals("Unblocked")) {
                    // The recipient was blocked before but got unblocked by this button
                    JOptionPane.showMessageDialog(null, "User unblocked!", "Conversations",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }

        if (e.getSource() == appearInvisibleToRecipientButton) {
            // Toggle invisibility
            // Send ID and recipient name to the server
            writer.println("Toggle Invisible");
            writer.println(ID);
            writer.println(recipientSelection.getSelectedItem());
            writer.flush();

            try {
                String response = reader.readLine();
                if (response.equals("Invisible")) {
                    // The user is now invisible to the recipient
                    JOptionPane.showMessageDialog(null, "You are now invisible to this user!",
                            "Conversations", JOptionPane.INFORMATION_MESSAGE);
                } else if (response.equals("Visible")) {
                    // The user is now visible to the recipient
                    JOptionPane.showMessageDialog(null, "You are now visible to this user!",
                            "Conversations", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }

        if (e.getSource() == signUpButton) {
            // Close the log in window and open the sign up page
            setVisible(false);
            EventQueue.invokeLater(this::initializeSignUpPage);
        }

        if (e.getSource() == registerButton) {
            // Register a new account with the server
            try {
                // Connect to the server if there is no connection already
                if (!connectedToServer) {
                    socket = new Socket(host, 4242);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new PrintWriter(socket.getOutputStream());
                    connectedToServer = true;
                }

                // Send new username, email, and password to server
                writer.println("Sign up");
                writer.println(usernameSignUpPage.getText());
                writer.println(emailSetting.getText());
                writer.println(passwordSignUpPage.getPassword());
                writer.println(String.valueOf(roleSetting.getSelectedItem()));
                writer.flush();

                String line = reader.readLine();
                if (line.equals("Success")) {
                    // If the account was created, bring back the login page
                    JOptionPane.showMessageDialog(null, "Account created.",
                            "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                    signUpFrame.setVisible(false);
                    setVisible(true);
                } else if (line.equals("Blank")) {
                    // Print an error if one of the fields was blank
                    JOptionPane.showMessageDialog(null,
                            "At least one of your fields is blank. " + "Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (line.equals("Username Taken")) {
                    // If the username entered already exists in the system, print an error
                    JOptionPane.showMessageDialog(null,
                            "That username is taken! Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (line.equals("Email Taken")) {
                    // If the email entered already exists in the system, print an error
                    JOptionPane.showMessageDialog(null,
                            "That email is taken! Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (e.getSource() == deleteAccountButton) {
            // Delete the account by sending the ID
            writer.println("Delete Account");
            writer.println(ID);
            writer.flush();
            // Tell the user their account was deleted and close the window
            JOptionPane.showMessageDialog(null, "Account Deleted! Program will close.",
                    "Account Settings",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.dispose();
            // Also close the connection
            writer.write("Close Socket");
            System.exit(0);
        }

        if (e.getSource() == createStoreButton) {
            // Initialize the store creation window
            EventQueue.invokeLater(this::initializeStoreCreationWindow);
        }

        if (e.getSource() == confirmStoreCreateButton) {
            // Send the server the User ID, the store name, and the store description
            writer.println("Create Store");
            writer.println(ID);
            writer.println(storeName.getText());
            writer.println(storeDesc.getText());
            writer.flush();

            // Get data from server
            try {
                String line = reader.readLine();

                if (line.equals("Success")) {
                    // If the store was successfully created, tell the user the store was added
                    JOptionPane.showMessageDialog(null, "Store created!",
                            "Success!", JOptionPane.INFORMATION_MESSAGE);
                    storeCreationWindow.setVisible(false);

                    // Update the Stores dashboard table with the new store
                    TableModel tableModel1 = getTableModel();
                    jTable1.setModel(tableModel1);

                    if (!isCustomer) {
                        // Adjust the size of the columns
                        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        jTable1.getColumnModel().getColumn(1).setPreferredWidth(550);
                        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
                        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        // Add the "Save Store Data" and "Delete Store" buttons
                        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Save Store Data"));

                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Delete Store"));

                    } else {
                        // Adjust the size of the columns
                        jTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
                        jTable1.getColumnModel().getColumn(1).setPreferredWidth(450);
                        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
                        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);

                        // Add the "Save Store Data" and "Delete Store" buttons
                        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                        jTable1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(),
                                StoreApplication.this,
                                "Contact Store"));
                    }
                    jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 500));

                } else if (line.equals("Name Blank")) {
                    // Tell the user they left the name blank and give them an error
                    JOptionPane.showMessageDialog(null,
                            "The store name is blank. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (line.equals("Name Exists")) {
                    // Tell the user the name already exists and give them an error
                    JOptionPane.showMessageDialog(null,
                            "That store name is taken! Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        if (e.getSource() == searchButton) {
            // Send the server the user ID and the search term
            String searchText = userSearchField.getText();
            writer.println("Search Users");
            writer.println(ID);
            writer.println(searchText);
            writer.flush();

            // Clear previous search results
            searchUserResults.removeAllItems();
            try {
                // Add all search results received from server to the search user results drop down menu
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
            // Select the recipient based on the value of the drop down menu
            recipientName = String.valueOf(recipientSelection.getSelectedItem());
            if (!recipientName.isEmpty()) {
                // Send the server the conversation ID and the name of the recipient
                writer.println("Conversation");
                writer.println(conversationID);
                writer.println(recipientName);
                writer.flush();

                try {
                    // Read data from the server
                    String messageCountLine = reader.readLine();
                    int messageCount = Integer.parseInt(messageCountLine);

                    // Add the message data to the messages table
                    Object[][] messageData = new Object[messageCount][4];
                    for (int i = 0; i < messageCount; i++) {
                        messageData[i][0] = reader.readLine();
                        messageData[i][1] = reader.readLine();

                        // Leave the last two columns blank and add the "Edit" and "Delete" buttons
                        if (!messageData[i][0].equals(recipientName)) {
                            messageData[i][2] = "Edit";
                            messageData[i][3] = "Delete";
                        }
                    }

                    // Add the data to the messages data table
                    TableModel messagesDataTable = getMessagesTable(messageData);
                    msgTable.setModel(messagesDataTable);

                    // Format the messages table, so it looks nice
                    msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                    msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                    msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                    // Leave the last two columns blank, so that the "Edit Message" and "Delete Message" buttons are
                    // blank
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
            // The recipient is added to the recipient drop down menu
            recipientName = (String) searchUserResults.getSelectedItem();
            boolean itemExists = false;
            // If the recipient name exists in the drop down menu, don't add it again
            for (int i = 0; i < recipientSelection.getItemCount(); i++) {
                if (recipientSelection.getItemAt(i).equals(recipientName)) {
                    itemExists = true;
                }
            }
            // Otherwise, add it again
            if (!itemExists) {
                recipientSelection.addItem(recipientName);
            }

            // Get recipient selection name
            recipientSelection.setSelectedItem(recipientName);

            // Send the server the conversation ID and recipient name
            writer.println("Conversation");
            writer.println(conversationID);
            writer.println(recipientName);
            writer.flush();

            try {
                // Read data from server
                String messageCountLine = reader.readLine();
                int messageCount = Integer.parseInt(messageCountLine);

                // Add messages data from server to the messageData variable
                Object[][] messageData = new Object[messageCount][4];
                for (int i = 0; i < messageCount; i++) {
                    messageData[i][0] = reader.readLine();
                    messageData[i][1] = reader.readLine();
                    // Leave the last two columns blank for the "Edit" and "Delete" buttons
                    if (!messageData[i][0].equals(recipientName)) {
                        messageData[i][2] = "Edit";
                        messageData[i][3] = "Delete";
                    }
                }

                // Add data to the messages table
                TableModel messagesDataTable = getMessagesTable(messageData);
                msgTable.setModel(messagesDataTable);

                // Format the messages table, so it looks organized
                msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                // Leave the last two columns blank and add the "Edit Message" and "Delete Message" buttons
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
            // Change seller view to selected store
            String choice = sellerViewSelect.getSelectedItem().toString();
            writer.println("Get ID");
            writer.println(choice);
            writer.flush();
            try {
                conversationID = reader.readLine();
            } catch (IOException ee) {
                ee.printStackTrace();
            }

            // Get conversations to the recipient seller currently has selected
            writer.println("Get Conversations");
            writer.println(conversationID);
            writer.flush();

            // Clear the recipient selection
            recipientSelection.removeAllItems();
            try {
                // Add conversation data to the message tavle data
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

                // Add conversation data to the message tavle data
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

                    // Add message data to the messages table
                    TableModel messagesDataTable = getMessagesTable(messageData);
                    msgTable.setModel(messagesDataTable);

                    // Format the messages table so it looks nice
                    msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    msgTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));
                    msgTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                    msgTable.getColumnModel().getColumn(1).setPreferredWidth(600);
                    msgTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                    msgTable.getColumnModel().getColumn(3).setPreferredWidth(100);

                    // Leave the last two columns blank and add the "Edit Message" and "Delete Message" buttons
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