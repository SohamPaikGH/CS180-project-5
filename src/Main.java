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
    JLabel label_password, label_username, message, title;
    JButton btn;
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

        message = new JLabel("Message Here");
        message.setBounds(300,400,300,40);

        username = new JTextField();
        username.setBounds(300, 200, 300, 40);

        password = new JPasswordField(50);
        password.setBounds(300, 250, 300, 40);

        btn = new JButton("Sign in");
        btn.setBounds(300, 320, 100, 40);
        btn.addActionListener(StoreApplication.this);

        add(label_username);
        add(username);
        add(label_password);
        add(password);
        add(btn);
        add(message);

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
        JComponent panel1 = createAccountSettingsPane();
        tabbedPane.addTab("Account Settings", null, panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = createConversationsPane();
        tabbedPane.addTab("Conversations", null, panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent panel3 = createDashboardPane();
        tabbedPane.addTab("Dashboard", null, panel3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        frame.getContentPane().add(tabbedPane);
        frame.setVisible(true);
        frame.setResizable(true);

    }

    protected JComponent createAccountSettingsPane() {
        JPanel panel = new JPanel(false);
        JLabel title = new JLabel("Account Settings");
        panel.setLayout(new GridLayout(1, 1));
        panel.add(title);
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
        if (e.getSource() == btn) {
//            dispatchEvent(new WindowEvent(StoreApplication.this, WindowEvent.WINDOW_CLOSING));
            setVisible(false);
            EventQueue.invokeLater(this::initializeApp);
        }
    }
}
