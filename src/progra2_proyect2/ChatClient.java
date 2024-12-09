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

    // Connect to the server
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server on port " + port);
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            System.out.println("Connection disrupted: " + e.getMessage());
        }
    }

    // Listen for incoming messages from the server
    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Clear the current chat history
                        chatPanel.chatHistoryArea.setText("");  // Clears any old messages

                        // Check if there's a recipient (private chat)
                        if (recipient != null) {
                            Admin.MostrarPrivateChat(recipient, chatPanel.chatHistoryArea);  // Load the recipient's private chat
                        } else {
                            Admin.MostrarMChat(chatPanel.chatHistoryArea);  // Load the main chat
                        }

                        // Update the chat panel with the latest history
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

    // Send a general or private message
    public void sendMessage(String message) {
        if (out != null) {
            // Send the message either to a recipient or to the group
            if (recipient == null) {
                out.println(message);  // General message (group chat)
            } else {
                out.println(message);  // Private message
                updateReceiverChatHistory();  // Update the recipient's chat history
            }
        }
    }

    // Send a private message
    public void sendMessageP(String message, Usuarios recipient) {
        if (out != null) {
            this.recipient = recipient;  // Set the recipient for private chat
            System.out.println("Sending private message to " + recipient.getNombre());
            out.println(message);  // Send the private message
            updateReceiverChatHistory();  // Update the recipient's chat history
        }
    }

    // Update the chat history (whether private or main)
    private void updateReceiverChatHistory() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear the current chat history area before loading new messages
                chatPanel.chatHistoryArea.setText("");  // Clears any old messages

                // Load the current chat (private or main)
                if (recipient != null) {
                    Admin.MostrarPrivateChat(recipient, chatPanel.chatHistoryArea);  // Show the recipient's private chat
                } else {
                    Admin.MostrarMChat(chatPanel.chatHistoryArea);  // Show the main chat
                }

                // Update the chat panel to reflect the new chat history
                chatPanel.updateChatHistory();  // Refresh chat UI

            } catch (IOException ex) {
                System.out.println("Error updating chat history.");
            }
        });
    }

    // Reset the recipient (i.e., switch to the main chat)
    public void resetRecipient() {
        this.recipient = null;  // Clear the recipient to go back to the main chat
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear the chat history area before loading the main chat
                chatPanel.chatHistoryArea.setText("");  // Clears any old messages

                // Show the main chat (no recipient)
                Admin.MostrarMChat(chatPanel.chatHistoryArea);
            } catch (IOException e) {
                System.out.println("Couldn't load main chat after resetting recipient.");
            }

            // Update the chat panel to show the latest chat
            chatPanel.updateChatHistory();
        });
    }
}
