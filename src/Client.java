import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args)  {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket("localhost", 4242);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream())) {

            String line = scanner.nextLine();
            writer.println(line);
            writer.flush();
            System.out.println(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}