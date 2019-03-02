import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Scanner scanner;
    private PrintWriter printWriter;
    private Server server;
    private static int client_count = 0;

    public ClientHandler(Socket clientSocket, Server server) {
        client_count++;
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            scanner = new Scanner(clientSocket.getInputStream());
            printWriter = new PrintWriter(clientSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        server.getAllMessageFromFile(this);
        server.sendMessageToAll("Новый участник вошел в чат!");
        server.sendMessageToAll("Клиент в чате = " + client_count);
        while (true) {
            if (scanner.hasNext()) {
                String message = scanner.nextLine();
                if (message.equals("##session##end##")) {
                    break;
                }
                System.out.println(message);
                server.sendMessageToAll(message);
            }
        }
        closeSession();
    }

    public void sendMessage(String message) {
        printWriter.println(message);
        printWriter.flush();
    }

    private void closeSession() {
        //close all stream
        //save all info in database
        client_count--;
        server.removeClientHandler(this);
    }
    public String getAllMessageFromFile(){
        String text = "";
        try (FileReader printWriter = new FileReader("a.txt")) {
            text = String.valueOf(printWriter.read());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(text);
        return text;
    }
}
