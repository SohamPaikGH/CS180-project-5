import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Program Name
 * <p>
 * Brief description of program
 *
 * @author Soham Paik, CS 180 Black
 * @version Date of Completion
 */
public class Login extends JFrame implements ActionListener {

    JPanel pnl = new JPanel();
    JPanel subPnl1 = new JPanel();
    JPanel subPnl2 = new JPanel();
    JPanel subPnl3 = new JPanel();
    JPanel subPnl4 = new JPanel();
    JLabel usernameLbl = new JLabel("Username: ");
    JTextField usernameEntry = new JTextField(10);
    JLabel passwordLbl = new JLabel("Password: ");
    JTextField passwordEntry = new JTextField(10);

    public static void main(String[] args) {
        Login login = new Login();
    }

    public Login() {
        super("Login");
//        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(true);

        subPnl1.add(usernameLbl);
        subPnl1.setLayout(new FlowLayout());

        subPnl2.add(usernameEntry);
        subPnl2.setLayout(new FlowLayout());

        subPnl3.add(passwordLbl);
        subPnl3.setLayout(new FlowLayout());

        subPnl4.add(passwordEntry);
        subPnl4.setLayout(new FlowLayout());

        pnl.add(subPnl1);
        pnl.add(subPnl2);
        pnl.add(subPnl3);
        pnl.add(subPnl4);

        add(pnl);

        getContentPane().add(BorderLayout.CENTER, pnl);
        pack();
        setLocationByPlatform(true);
        setResizable(true);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
