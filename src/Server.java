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
            String line = reader.readLine();
            writer.println(line.toLowerCase());
            writer.flush();
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
