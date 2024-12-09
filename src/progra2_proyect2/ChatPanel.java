/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.*;

/**
 *
 * @author Cristina Sabillon
 */
public class ChatPanel {

    private JPanel userButtonPanel;
    private JPanel contentPanel;
    public JTextArea chatHistoryArea;
    private boolean isMainChat = true;
    private final Administrador manager;
    private ChatClient chatClient;

    private Usuarios recipient = null;

    ChatPanel(Administrador admin) {
        manager = admin;
        this.chatClient = new ChatClient(this, admin);

    }

    public void startChatClient(String host, int port) {
        chatClient.connect(host, port);
    }

    public JPanel createChatPanel() {
        contentPanel = new JPanel(new BorderLayout());
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.BLACK);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        chatHistoryArea = new JTextArea();
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setBackground(Color.BLACK);
        chatHistoryArea.setForeground(Color.WHITE);
        chatHistoryArea.setFont(new Font("Arial", Font.PLAIN, 20));
        chatHistoryArea.setLineWrap(true);
        chatHistoryArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(chatHistoryArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setBackground(Color.BLACK);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        try {
            manager.MostrarMChat(chatHistoryArea);
        } catch (IOException e) {
            System.out.println("No se puede mostrar el general chat");
        }

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField messageInputField = new JTextField();
        messageInputField.setFont(new Font("Arial", Font.PLAIN, 18));
        messageInputField.setBackground(Color.DARK_GRAY);
        messageInputField.setForeground(Color.WHITE);
        messageInputField.setCaretColor(Color.WHITE);
        messageInputField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        messageInputField.setPreferredSize(new Dimension(0, 40));

        // SEND BUTTON
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(Color.BLACK);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        ActionListener sendMessageListener = e -> {
            String message = messageInputField.getText().trim();
            if (!message.isEmpty()) {
                if (isMainChat) {
                    try {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String timestamp = now.format(formatter);
                        manager.GuardarMChat(manager.getUsernameInSession() + ": " + message + "\n");
                        chatClient.sendMessage(manager.getUsernameInSession() + ": " + message + "\n- " + timestamp + "\n");
                    } catch (IOException ex) {
                        System.out.println("Trouble charging ");
                    }
                } else {
                    if (recipient != null) {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        String timestamp = now.format(formatter);
                        System.out.println(manager.getUsernameInSession());
                        manager.GuardarPrivateChat(manager.getUsernameInSession(), recipient, message);
                        chatClient.sendMessageP(manager.getUsernameInSession() + ": " + message + "\n- " + timestamp + "\n", recipient);

                    } else {
                        System.out.println("Error: No recipient selected for private chat.");
                    }
                }
                messageInputField.setText("");
            }
        };

        sendButton.addActionListener(sendMessageListener);
        messageInputField.addActionListener(sendMessageListener);

        inputPanel.add(messageInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        // --- SIDEBAR ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.BLACK);
        sidebarPanel.setPreferredSize(new Dimension(300, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        addMainChatButtonToSidebar(sidebarPanel);

        userButtonPanel = new JPanel();
        userButtonPanel.setLayout(new BoxLayout(userButtonPanel, BoxLayout.Y_AXIS));
        userButtonPanel.setBackground(Color.BLACK);
        userButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane userScrollPane = new JScrollPane(userButtonPanel);
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        userScrollPane.setBackground(Color.BLACK);
        userScrollPane.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(userScrollPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, chatPanel);
        splitPane.setDividerLocation(240);
        splitPane.setDividerSize(2);
        splitPane.setBackground(Color.BLACK);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(splitPane, BorderLayout.CENTER);

        updateUserList();

        new Timer(5000, e -> updateUserList()).start();

        contentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateUserList();
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();

        return contentPanel;
    }

    void updateUserList() {
        ArrayList<Usuarios> usuarios = manager.getUsuarios();
        userButtonPanel.removeAll();

        for (Usuarios user : usuarios) {
            try {
                user.cargarUsuario();

                if (!user.getNombre().equals(manager.getUsernameInSession())) {
                    String status = user.getActivo() ? "Active" : "Offline";
                    String buttonLabel = user.getNombre() + " - " + status;

                    JButton userButton = new JButton(buttonLabel);
                    userButton.setFont(new Font("Arial", Font.PLAIN, 18));
                    userButton.setBackground(Color.DARK_GRAY);
                    userButton.setForeground(Color.WHITE);
                    userButton.setFocusPainted(false);
                    userButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                    userButton.setPreferredSize(new Dimension(180, 40));
                    userButton.setMaximumSize(new Dimension(180, 40));
                    userButton.setMinimumSize(new Dimension(180, 40));

                    userButton.addActionListener(e -> {
                        showUserProfilePopup(user);
                    });

                    userButtonPanel.add(userButton);
                    userButtonPanel.add(Box.createVerticalStrut(10));
                }
            } catch (IOException e) {
                System.out.println("Error loading user data for " + user.getNombre() + ": " + e.getMessage());
            }
        }

        userButtonPanel.revalidate();
        userButtonPanel.repaint();
        System.out.println("updated list!");
    }

    private void showUserProfilePopup(Usuarios user) {
        JDialog profileDialog = new JDialog((Frame) null, "Profile - " + user.getNombre(), true);
        profileDialog.setLayout(new GridLayout(6, 1, 5, 5));
        profileDialog.setLayout(new BorderLayout());
        profileDialog.getContentPane().setBackground(Color.BLACK);
        profileDialog.setSize(350, 500);
        profileDialog.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Profile Picture
        ImageIcon icon = new ImageIcon(user.getIcon());
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(126, 190, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        JLabel profilePicLabel = new JLabel();
        profilePicLabel.setIcon(scaledIcon);
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePicLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Username
        JLabel nameLabel = new JLabel(user.getNombre(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Joining Date
        Calendar date = user.getFechaRegistro();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaString = dateFormat.format(date.getTime());

        JLabel dateLabel = new JLabel("Joined: " + fechaString, SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.LIGHT_GRAY);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(profilePicLabel);
        headerPanel.add(nameLabel);
        headerPanel.add(dateLabel);

        // --- DESCRIPTION PANEL ---
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
        descriptionPanel.setBackground(Color.BLACK);
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel descriptionLabel = new JLabel("<html><p style='text-align: center;'><b>Descripción:</b> "
                + user.getDescription() + "</p></html>",
                SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionLabel.setForeground(Color.LIGHT_GRAY);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descriptionPanel.add(descriptionLabel);

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton chatButton = new JButton("Chat with " + user.getNombre());
        chatButton.setFont(new Font("Arial", Font.BOLD, 14));
        chatButton.setBackground(Color.BLACK);
        chatButton.setForeground(Color.WHITE);
        chatButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        chatButton.setFocusPainted(false);

        chatButton.addActionListener(e -> {
            if (user != null) {
                profileDialog.dispose();
                switchChat(user);
            } else {
                System.out.println("Error: Recipient user is null.");
            }
        });
        buttonPanel.add(chatButton, BorderLayout.CENTER);

        profileDialog.add(headerPanel, BorderLayout.NORTH);
        profileDialog.add(descriptionPanel, BorderLayout.CENTER);
        profileDialog.add(buttonPanel, BorderLayout.SOUTH);

        profileDialog.setVisible(true);
    }

    private void addMainChatButtonToSidebar(JPanel sidebarPanel) {
        JButton mainChatButton = new JButton("Main Chat");
        mainChatButton.setFont(new Font("Arial", Font.BOLD, 18));
        mainChatButton.setBackground(Color.BLACK);
        mainChatButton.setForeground(Color.WHITE);
        mainChatButton.setFocusPainted(false);
        mainChatButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        int buttonWidth = 180;
        int buttonHeight = 40;

        mainChatButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        mainChatButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        mainChatButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));

        mainChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMainChat = true;
                switchChat(null);
            }
        });

        sidebarPanel.add(mainChatButton);
    }

    private void switchChat(Usuarios user) {
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setBackground(Color.BLACK);
        chatHistoryArea.setForeground(Color.WHITE);
        chatHistoryArea.setFont(new Font("Arial", Font.PLAIN, 20));
        chatHistoryArea.setLineWrap(true);
        chatHistoryArea.setWrapStyleWord(true);
        try {
            if (user != null) {
                isMainChat = false;
                recipient = user;
                manager.MostrarPrivateChat(user, chatHistoryArea);
            } else {
                isMainChat = true;
                recipient = null;
                chatClient.resetRecipient();
                manager.MostrarMChat(chatHistoryArea);
            }
        } catch (IOException e) {
            System.out.println("Chat doesn't switch");
        }
        updateChatWindow(chatHistoryArea);
    }

    private void updateChatWindow(JTextArea chatHistoryArea) {
        JSplitPane splitPane = (JSplitPane) contentPanel.getComponent(0);

        JPanel chatPanel = (JPanel) splitPane.getRightComponent();

        JScrollPane chatScrollPane = new JScrollPane(chatHistoryArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setBackground(Color.BLACK);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel inputPanel = (JPanel) chatPanel.getComponent(1);

        chatPanel.removeAll();

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public void updateChatHistory() {
        SwingUtilities.invokeLater(() -> {
            chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());
        });
    }
}
