/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author Cristina Sabillon
 */
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GamePanel {

    private Administrador admin;
    private Juego juego;
    private static final String GAMES_DIRECTORY = "games/";
    private static final String DEFAULT_IMAGE = "DefaultIMAGE/neo.jpg";

    public JPanel createGamesPanel(Administrador admin) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        this.admin = admin;

        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBackground(Color.BLACK);
        JTextField searchField = new JTextField("");
        searchField.setFont(new Font("Arial", Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBackground(Color.BLACK);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JButton searchButton = new JButton("Buscar Juegos: ");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(Color.BLACK);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.WEST);

        JPanel gamesListPanel = new JPanel();
        gamesListPanel.setLayout(new BoxLayout(gamesListPanel, BoxLayout.Y_AXIS));
        gamesListPanel.setBackground(Color.BLACK);

        List<Juego> juegos = loadGamesFromDirectory(GAMES_DIRECTORY);

        JPanel currentRowPanel = null;

        for (int i = 0; i < juegos.size(); i++) {
            if (i % 3 == 0) {
                if (currentRowPanel != null) {
                    gamesListPanel.add(currentRowPanel);
                }
                currentRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                currentRowPanel.setBackground(Color.BLACK);
            }

            Juego juego = juegos.get(i);
            JPanel gamePanel = createGamePanel(juego);
            currentRowPanel.add(gamePanel);
        }

        if (currentRowPanel != null) {
            gamesListPanel.add(currentRowPanel);
        }

        JScrollPane gamesScrollPane = new JScrollPane(gamesListPanel);
        gamesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterGames();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterGames();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterGames();
            }

            private void filterGames() {
                String searchText = searchField.getText().toLowerCase();
                gamesListPanel.removeAll();

                JPanel currentRowPanel = null;
                for (Juego juego : juegos) {
                    if (juego.getNombre().toLowerCase().contains(searchText)) {
                        if (currentRowPanel == null || currentRowPanel.getComponentCount() == 3) {
                            if (currentRowPanel != null) {
                                gamesListPanel.add(currentRowPanel);
                            }
                            currentRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                            currentRowPanel.setBackground(Color.BLACK);
                        }

                        JPanel gamePanel = createGamePanel(juego);
                        currentRowPanel.add(gamePanel);
                    }
                }

                if (currentRowPanel != null) {
                    gamesListPanel.add(currentRowPanel);
                }

                gamesListPanel.revalidate();
                gamesListPanel.repaint();
            }
        });

        contentPanel.add(searchBarPanel, BorderLayout.NORTH);
        contentPanel.add(gamesScrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();

        return contentPanel;
    }

    private List<Juego> loadGamesFromDirectory(String directoryPath) {
        List<Juego> juegos = new ArrayList<>();
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Directory does not exist: " + directoryPath);
            return juegos;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".priv"));
        if (files == null) {
            return juegos;
        }

        for (File file : files) {
            Juego juego = Juego.LeerJuego(file.getAbsolutePath());
            if (juego != null) {
                juegos.add(juego);
            }
        }

        return juegos;
    }

    private JPanel createGamePanel(Juego juego) {
        JPanel gamePanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 220);
            }
        };
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(Color.DARK_GRAY);
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        ImageIcon icon = loadGameIcon(juego.getIcon(), 400, 200);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLabel = new JLabel(juego.getNombre(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        gamePanel.add(iconLabel, BorderLayout.CENTER);
        gamePanel.add(nameLabel, BorderLayout.SOUTH);

        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showGameDetailsDialog(juego);
            }
        });

        return gamePanel;
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private void showGameDetailsDialog(Juego juego) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Información del Juego");
        dialog.setSize(720, 450);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        detailsPanel.setBackground(Color.BLACK);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Nombre: " + juego.getNombre(), SwingConstants.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        JLabel genreLabel = new JLabel("Género: " + juego.getGenero(), SwingConstants.LEFT);
        genreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        genreLabel.setForeground(Color.LIGHT_GRAY);

        JLabel creatorLabel = new JLabel("Desarrollador: " + juego.getDesarollador(), SwingConstants.LEFT);
        creatorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        creatorLabel.setForeground(Color.LIGHT_GRAY);

        Date date = new Date(juego.getFechaLanzamiento());
        String formattedDate = formatDate(date);
        JLabel dateLabel = new JLabel("Fecha: " + formattedDate, SwingConstants.LEFT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.LIGHT_GRAY);

        detailsPanel.add(nameLabel);
        detailsPanel.add(genreLabel);
        detailsPanel.add(creatorLabel);
        detailsPanel.add(dateLabel);

        ImageIcon icon = loadGameIcon(juego.getIcon(), 400, 350);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setPreferredSize(new Dimension(0, 60));

        JButton download = new JButton();
        ImageIcon iconG = new ImageIcon("DefaultIMAGE/download.png");
        download.setIcon(iconG);
        download.setBackground(Color.WHITE);
        download.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        download.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "Desea guardar este juego en su librería?",
                    "GUARDAR JUEGO",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                String userDirectoryPath = "usuarios/" + admin.getUsernameInSession() + "/";
                String privateFilePath = userDirectoryPath + "game/";
                String gameFilePath = privateFilePath + juego.getNombre().replace(" ", "_") + ".priv";

                File userDirectory = new File(userDirectoryPath);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }

                File gameFile = new File(gameFilePath);
                if (gameFile.exists()) {
                    JOptionPane.showMessageDialog(null, "El juego ya está guardado en su librería!");
                    return; 
                }

                try {
                    juego.setRuta(privateFilePath);
                    juego.initGame(); 
                    JOptionPane.showMessageDialog(null, "Se guardó el juego en su librería!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al guardar el juego: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se canceló la acción!");
            }
        });

        bottomPanel.add(download, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private ImageIcon loadGameIcon(String iconPath, int width, int height) {
        File iconFile = new File(iconPath);
        if (!iconFile.exists()) {
            iconFile = new File(DEFAULT_IMAGE);
        }

        ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}
