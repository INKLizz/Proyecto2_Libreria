/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Laura Sabillon
 */
public class SERVER {

    private ServerSocket serverSocket;
    private ArrayList<ChatClientHandler> clients;

    public SERVER(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);
        clients = new ArrayList<>();
    }

    public void start() throws IOException {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ChatClientHandler clientHandler = new ChatClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    public static boolean isPortInUse(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private class ChatClientHandler implements Runnable {

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ChatClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    for (ChatClientHandler client : clients) {
                        client.out.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading message from client: " + e.getMessage());
            }
        }
    }
}
