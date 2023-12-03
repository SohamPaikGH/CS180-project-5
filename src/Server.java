import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
    private Socket socket;

    public Server(Socket socket) {
        super();
        this.socket = socket;
        start();
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
            String command = reader.readLine();
            if (command.equals("Log In")) {
                String username = reader.readLine();
                String password = reader.readLine();
                String ID = Account.IDofUsername(username);
                if (ID == null || !Account.getPassword(ID).equals(password)) {
                    writer.println("Failure");
                } else {
                    writer.println("Success");
                    writer.println(ID);
                }
                writer.flush();
            } else if (command.equals("Sign up")) {
                String username = reader.readLine();
                String email = reader.readLine();
                String password = reader.readLine();
                String role = reader.readLine();
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                    writer.println("Blank");
                } else if (Account.IDofUsername(username) != null) {
                    writer.println("Username Taken");
                } else if (Account.IDofEmail(email) != null) {
                    writer.println("Email Taken");
                } else {
                    Account.createAccount(username, email, password, role);
                    writer.println("Success");
                    writer.println(Account.IDofUsername(username));
                }
                writer.flush();
            } else if (command.equals("Account Data")) {
                String ID = reader.readLine();
                writer.println(Account.getUsername(ID));
                writer.println(Account.getEmail(ID));
                writer.println(Account.getPassword(ID));
                writer.flush();
            } else if (command.equals("Save Account Data")) {
                String ID = reader.readLine();
                String username = reader.readLine();
                String email = reader.readLine();
                String password = reader.readLine();
                String IDofUsername = Account.IDofUsername(username);
                String IDofEmail = Account.IDofEmail(email);
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    writer.println("Blank");
                } else if (IDofUsername != null && !IDofUsername.equals(ID)) {
                    writer.println("Username Taken");
                } else if (IDofEmail != null && !IDofEmail.equals(ID)) {
                    writer.println("Email Taken");
                } else {
                    writer.println("Success");
                }
                writer.flush();
            } else if (command.equals("Delete Account")) {
                String ID = reader.readLine();
                Account.deleteAccount(ID);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(4242)) {
                while (true) {
                    System.out.println("Waiting for the client to connect...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected!");
                    new Server(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
