/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Laura Sabillon
 */
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

public class ChatClient {

    private ChatPanel chatPanel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Administrador Admin;
    private Usuarios recipient;

    public ChatClient(ChatPanel chatPanel, Administrador admin) {
        this.chatPanel = chatPanel;
        Admin = admin;
    }

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server on port " + port);
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            System.out.println("Connection disrupted: " + e.getMessage());;
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (recipient != null) {
                            Admin.MostrarPrivateChat(recipient, chatPanel.chatHistoryArea);
                        } else {
                            Admin.MostrarMChat(chatPanel.chatHistoryArea);
                        }
                        chatPanel.updateChatHistory();
                    } catch (IOException ex) {
                        System.out.println("Error updating chat: " + ex.getMessage());
                    }
                });
            }
        } catch (SocketException e) {
            System.out.println("Connection reset. Please check the server or network connection.");
        } catch (IOException e) {
            System.out.println("An error occurred while listening for messages: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            if (recipient == null) {
                out.println(message);
            } else {
                out.println(message);
                updateReceiverChatHistory();
            }
        }
    }

    public void sendMessageP(String message, Usuarios recipient) {
        if (out != null) {
            this.recipient = recipient;
            System.out.println("Sending private message to " + recipient.getNombre());
            out.println(message);
            updateReceiverChatHistory();
        }
    }

    private void updateReceiverChatHistory() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (recipient != null) {
                    Admin.MostrarPrivateChat(recipient, chatPanel.chatHistoryArea);
                } else {
                    Admin.MostrarMChat(chatPanel.chatHistoryArea);
                }
                chatPanel.updateChatHistory();
            } catch (IOException ex) {
                System.out.println("Error updating chat history.");
            }
        });
    }

    public void resetRecipient() {
        this.recipient = null;
        SwingUtilities.invokeLater(() -> {
            try {
                Admin.MostrarMChat(chatPanel.chatHistoryArea);
            } catch (IOException e) {
                System.out.println("Couldn't load main chat after resetting recipient.");
            }
            chatPanel.updateChatHistory();
        });
    }
}
