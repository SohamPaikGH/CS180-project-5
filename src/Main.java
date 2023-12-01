import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Program Name
 * <p>
 * Brief description of program
 *
 * @author Soham Paik, CS 180 Black
 * @version Date of Completion
 */
class StoreApplication extends JFrame implements ActionListener {
    JPasswordField password;
    JTextField username;
    JLabel label_password, label_username, title;
    JButton signInButton;
    JButton saveButton = new JButton("Save");
    ArrayList<JComponent> components;
    boolean loginCompleted = false;

    public StoreApplication() {
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
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

        add(label_username);
        add(username);
        add(label_password);
        add(password);
        add(signInButton);

        // Start main application
//        JComponent tabbedPane = initializeApp();


//        add(tabbedPane);
        setResizable(false);
        setVisible(true);
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

        JTextField username = new JTextField(20);
        username.setPreferredSize(new Dimension(300, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(username, c);

        JLabel emailLbl = new JLabel("Email");
        emailLbl.setPreferredSize(new Dimension(100, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        panel.add(emailLbl, c);

        JTextField email = new JTextField(20);
        email.setPreferredSize(new Dimension(300, 40));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 4;
        panel.add(email, c);

        JLabel passwordLbl = new JLabel("Password");
        passwordLbl.setPreferredSize(new Dimension(100, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        panel.add(passwordLbl, c);

        JTextField password = new JTextField(20);
        password.setPreferredSize(new Dimension(300, 40));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        panel.add(password, c);

        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.addActionListener(StoreApplication.this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        panel.add(saveButton, c);

//        JLabel title = new JLabel("Account Settings");
//
//        JTable jTable1 = new JTable();
//        JScrollPane jScrollPane1 = new JScrollPane();
//
//        jTable1.setModel(new javax.swing.table.DefaultTableModel(
//                new Object [][] {
//                        {"Username", null},
//                        {"Email", null},
//                        {"Password", null},
//                        {"Role", null}
//                },
//                new String [] {
//                        "Fields", "Information"
//                }
//        ));
//        jTable1.setPreferredScrollableViewportSize(new Dimension(1000, 200));
//        jScrollPane1.setViewportView(jTable1);
//        jScrollPane1.getViewport().setOpaque(true);
//        jScrollPane1.setViewportBorder(null);
//
//        String[] options = {"Username", "Email", "Password", "Role", "ID"};
//        JComboBox editList = new JComboBox(options);
//
//        subPanel1.add(title);
//        subPanel2.add(jScrollPane1);
//        subPanel3.add(editList);



        return panel;
    }


    protected JComponent createConversationsPane() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("Conversations");
        panel.setLayout(new GridLayout(1,1));
        panel.add(title);
        return panel;
    }

    protected JComponent createDashboardPane() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("Dashboard");
        title.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1,1));
        panel.add(title);
        return panel;
    }

    public static void main(String[] args) {
        StoreApplication storeApplication = new StoreApplication();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {
//            dispatchEvent(new WindowEvent(StoreApplication.this, WindowEvent.WINDOW_CLOSING));
            setVisible(false);
            EventQueue.invokeLater(this::initializeApp);
        }
        if (e.getSource() == saveButton) {
            JOptionPane.showMessageDialog(null, "Saved", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}