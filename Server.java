import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private List<ClientHandler> clients;
    private static final int PORT = 6789;
    private String path = "a.txt";
    private String text = "";

    public Server() {
        clients = new LinkedList<>();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getAllMessageFromFile(ClientHandler clientHandler){
        String text = "";
        try (FileReader fileReader = new FileReader(path)) {
            char [] a = new char[200];
            while (fileReader.ready()){

                fileReader.read(a);
                for(char c : a)
                    text +=c;
                System.out.println("что то получилось");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        clientHandler.sendMessage(text);
    }

    private void fileWriter(String message) {
        synchronized (message) {

            try (FileWriter printWriter = new FileWriter(path, true)) {
                printWriter.write(message);
                printWriter.write(System.lineSeparator());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToAll(String message) {
        fileWriter(message);
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public void removeClientHandler(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

}
