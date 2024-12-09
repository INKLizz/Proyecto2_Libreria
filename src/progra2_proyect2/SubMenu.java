/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Cristina Sabillon
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.filechooser.FileNameExtensionFilter;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SubMenu extends JFrame {

    //LLAMAR CLASES
    private final Administrador manager;
    private final MusicPanel music;
    private final GamePanel game;
    private final ChatPanel chat;
    private AÑADIR add;
    private ChatClient client;
    private ADMIN admin;
    private boolean isAdmin = false;

    //CREACION DE PANELES
    private JFrame frame;
    private JFrame nowPlayingDialog;
    private JPanel musicListPanel;
    private JPanel songListPanel;
    private int durationInSeconds;
    private JPanel contentPanel;
    private boolean isadmin;

    //VARIABLES PARA PLAY - RESUME - STOP MUSIC
    private int playbackPosition = 0;
    private boolean isSeeking = false;
    private Player currentPlayer;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private String currentSongFile;

    //IMAGENES  
    ImageIcon pause = new ImageIcon("DefaultIMAGE/pause.png");
    ImageIcon download = new ImageIcon("DefaultIMAGE/download.png");
    private static final String DEFAULT_IMAGE = "DefaultIMAGE/neo.jpg";

    SubMenu(Administrador manager) {
        add = new AÑADIR();
        this.manager = manager;
        music = new MusicPanel();
        game = new GamePanel();
        chat = new ChatPanel(manager);
        admin = new ADMIN(manager.getUsernameInSession(), "", isAdmin);
        setTitle("BIBLIOTECA DE JUEGOS Y MUSICA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Usuarios user = manager.InSession();
                user.setActivo(false);
                try {
                    user.guardarEstadoActivo();
                    user.guardarUsuario();
                } catch (IOException ex) {
                    System.out.println("No se pudo desactivar!");
                }
                System.out.println(user + "se desactivo");
                System.exit(0);
            }
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);

        // PANEL PARA LOS BOTONES
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new GridBagLayout());
        sideMenu.setBackground(Color.BLACK);
        sideMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        sideMenu.setPreferredSize(new Dimension(250, getHeight()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton musica = createButton("MUSICA");
        JButton juegos = createButton("JUEGOS");
        JButton chat = createButton("CHAT");
        JButton salir = createButton("SALIR");

        JButton miPerfil = createButton("MI PERFIL");

        String user = manager.getUsernameInSession();

        if (admin.checkIfAdmin(user)) {
            isAdmin = true;
            System.out.println("ADMIN IS : " + isAdmin);
        }

        if (isAdmin) {
            JButton adminButton = createButton("AÑADIR CANCIONES O JUEGOS");
            adminButton.addActionListener(e -> switchAdd(contentPanel));

            gbc.gridy++;
            sideMenu.add(adminButton, gbc);
        }

        gbc.gridy++;
        sideMenu.add(miPerfil, gbc);
        gbc.gridy++;
        sideMenu.add(musica, gbc);
        gbc.gridy++;
        sideMenu.add(juegos, gbc);
        gbc.gridy++;
        sideMenu.add(chat, gbc);
        gbc.gridy++;
        sideMenu.add(salir, gbc);

        miPerfil.addActionListener(e -> switchToProfile(contentPanel));
        musica.addActionListener(e -> switchToMusic(contentPanel));
        juegos.addActionListener(e -> switchToGames(contentPanel));
        chat.addActionListener(e -> switchToChat(contentPanel));
        salir.addActionListener(e -> exitToMain());

        add(sideMenu, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        switchToGames(contentPanel);

        setVisible(true);
    }

    //CAMBIO DE PANELES CON LOS BOTONES
    private void switchContent(JPanel contentPanel, JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void switchToProfile(JPanel contentPanel) {
        switchContent(contentPanel, createProfilePanel());
    }

    private void switchToMusic(JPanel contentPanel) {
        switchContent(contentPanel, music.createMusicPanel(this.manager));
    }

    private void switchToGames(JPanel contentPanel) {
        switchContent(contentPanel, game.createGamesPanel(this.manager));
    }

    private void switchToChat(JPanel contentPanel) {
        chat.startChatClient("127.0.0.1", 12345);

        switchContent(contentPanel, chat.createChatPanel());
    }

    private void switchAdd(JPanel contentPanel) {
        switchContent(contentPanel, add.getMainPanel());
    }

    //  --- PROFILE ---
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.BLACK);

        JPanel userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setBackground(Color.BLACK);
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        userInfoPanel.setPreferredSize(new Dimension(400, 230));
        Usuarios use = manager.InSession();

        // Avatar Image
        ImageIcon icon = new ImageIcon(use.getIcon());
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(126, 199, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(Color.BLACK);
        avatarLabel.setIcon(scaledIcon);
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        JPanel userDetailsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        userDetailsPanel.setBackground(Color.BLACK);
        userDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String user = manager.getUsernameInSession();
        JLabel usernameLabel = new JLabel("Usuario: " + user, SwingConstants.LEFT);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel descriptionLabel = new JLabel("", SwingConstants.LEFT);

        try {
            use.cargarUsuario();
            String desc = use.getDescription();

            if (desc == null || desc.isEmpty()) {
                descriptionLabel.setText("No hay descripción");
            } else {
                descriptionLabel.setText("<html>Descripción: " + desc + "</html>");
            }
        } catch (IOException e) {
            descriptionLabel.setText("Error al cargar la descripción");
        }

        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        Usuarios enSession = manager.InSession();
        Calendar date = enSession.getFechaRegistro();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaString = dateFormat.format(date.getTime());

        JLabel joinDateLabel = new JLabel("Se unio el : " + fechaString, SwingConstants.LEFT);
        joinDateLabel.setForeground(Color.WHITE);
        joinDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton changeTextButton = new JButton("Cambiar Descripción");
        changeTextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        changeTextButton.setBackground(Color.LIGHT_GRAY);
        changeTextButton.setFocusPainted(false);

        JButton pf = new JButton("Cambiar Imagen de Perfil");
        pf.setFont(new Font("Arial", Font.PLAIN, 14));
        pf.setBackground(Color.LIGHT_GRAY);
        pf.setFocusPainted(false);

        pf.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "Desea Cambiar la Imagen de Perfil?",
                    "CAMBIAR FOTO DE PERFIL",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (PNG, JPG)", "png", "jpg");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    use.setIcon(path);

                    ImageIcon iconic = new ImageIcon(path);
                    Image iamg = iconic.getImage();
                    Image resizedImg = iamg.getScaledInstance(126, 190, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImg);

                    avatarLabel.setIcon(resizedIcon);
                    avatarLabel.revalidate();
                    avatarLabel.repaint();

                    try {
                        use.guardarUsuario();
                        System.out.println("Imagen guardada exitosamente.");
                    } catch (IOException ex) {
                        System.out.println("Error al guardar la imagen: " + ex.getMessage());
                    }
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                } else {
                    System.out.println("No file selected.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Cambio de Imagen de Perfil cancelado.");
            }
        });

        changeTextButton.addActionListener(e -> {
            Usuarios currentUser = manager.InSession();
            JTextField text = new JTextField(15);

            int opcion = JOptionPane.showConfirmDialog(
                    null, text, "CAMBIO DE DESCRIPCIÓN", JOptionPane.OK_CANCEL_OPTION
            );
            if (opcion == JOptionPane.OK_OPTION) {
                String newDescription = text.getText();

                currentUser.setDescription(newDescription);
                descriptionLabel.setText("<html>Descripción: " + newDescription + "</html>");

                try {
                    currentUser.guardarUsuario();
                    System.out.println("Descripción guardada exitosamente.");
                } catch (IOException ex) {
                    System.out.println("Error al guardar la descripción: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Cambio de descripción cancelado.");
            }
        });

        userDetailsPanel.add(usernameLabel);
        userDetailsPanel.add(joinDateLabel);
        userDetailsPanel.add(descriptionLabel);
        userDetailsPanel.add(changeTextButton);
        userDetailsPanel.add(pf);

        userInfoPanel.add(avatarLabel, BorderLayout.WEST);
        userInfoPanel.add(userDetailsPanel, BorderLayout.CENTER);

        JPanel tuLibreriaPanel = TuLibreria();
        tuLibreriaPanel.setBackground(Color.BLACK);

        profilePanel.add(userInfoPanel, BorderLayout.NORTH);
        profilePanel.add(tuLibreriaPanel, BorderLayout.CENTER);
        avatarLabel.getSize();
        return profilePanel;

    }

    public JPanel TuLibreria() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 10);

        JPanel steamPanel = createSteamPanel();
        steamPanel.setPreferredSize(new Dimension(400, 600));

        gbc.gridx = 0;
        mainPanel.add(steamPanel, gbc);

        JPanel spotifyPanel = createSpotifyPanel();
        spotifyPanel.setPreferredSize(new Dimension(400, 600));

        gbc.gridx = 1;
        mainPanel.add(spotifyPanel, gbc);

        return mainPanel;
    }

    public JPanel createSteamPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);

        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        searchBarPanel.setBackground(Color.BLACK);

        JPanel gamesPanel = new JPanel();
        gamesPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Tus Juegos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBackground(Color.BLACK);
        titleLabel.setOpaque(true);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        gamesPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel gamesListPanel = new JPanel();
        gamesListPanel.setLayout(new BoxLayout(gamesListPanel, BoxLayout.Y_AXIS));
        gamesListPanel.setBackground(Color.BLACK);

        String gamesDirectoryPath = "usuarios/" + manager.getUsernameInSession() + "/game/";
        ArrayList<Juego> juegos = loadGamesFromDirectory(gamesDirectoryPath);

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

        gamesPanel.add(gamesScrollPane, BorderLayout.CENTER);

        contentPanel.add(gamesPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();

        return contentPanel;
    }

    private ArrayList<Juego> loadGamesFromDirectory(String directoryPath) {
        ArrayList<Juego> juegos = new ArrayList<>();
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
            Juego juego = manager.CargarJuego(file.getAbsolutePath());

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
                if (frame != null){
                    frame.dispose();
                }
                showGameDetailsFrame(juego);
            }
        });

        return gamePanel;
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

    private JPanel createSpotifyPanel() {
        JPanel spotifyPanel = new JPanel(new BorderLayout());
        spotifyPanel.setBackground(Color.BLACK);
        spotifyPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Tu Música", 0, 0, new Font("Arial", Font.BOLD, 16), Color.WHITE));

        songListPanel = new JPanel();
        songListPanel.setLayout(new BoxLayout(songListPanel, BoxLayout.Y_AXIS));
        songListPanel.setBackground(Color.BLACK);

        cargarCanciones(songListPanel);

        JScrollPane scrollPane = new JScrollPane(songListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.BLACK);

        spotifyPanel.add(scrollPane, BorderLayout.CENTER);
        return spotifyPanel;
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private void showGameDetailsFrame(Juego juego) {
        frame = new JFrame("Información del Juego");
        frame.setSize(720, 450);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  

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

        JButton delete = new JButton();
        ImageIcon iconG = new ImageIcon("DefaultIMAGE/delete.png");
        delete.setIcon(iconG);
        delete.setBackground(Color.WHITE);
        delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        String userDirectoryPath = "usuarios/" + manager.getUsernameInSession() + "/game/";

        delete.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "Desea borrar este juego de su librería?",
                    "BORRAR JUEGO",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                String gameTitle = juego.getNombre();
                String username = manager.getUsernameInSession();

                String formattedTitle = gameTitle.replace(" ", "_");

                String basePath = "usuarios/" + username + "/game/";
                String filePath = basePath + formattedTitle + ".priv";

                if (filePath != null && !filePath.isEmpty()) {
                    System.out.println("File path to delete: " + filePath);

                    manager.borrarJuego(filePath);

                    frame.dispose();  

                    contentPanel.removeAll();
                    contentPanel.add(createSteamPanel());
                    contentPanel.revalidate();
                    contentPanel.repaint();

                    JOptionPane.showMessageDialog(null, "Se borró el juego!");
                } else {
                    JOptionPane.showMessageDialog(null, "Ruta no válida!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se canceló la acción!");
            }
        });

        bottomPanel.add(delete, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true); 
    }

    private void showNowPlayingPanel(Musica musica, File file) {
        nowPlayingDialog = new JFrame("Now Playing");
        nowPlayingDialog.setLayout(new BorderLayout());

        nowPlayingDialog.setSize(400, 500);
        nowPlayingDialog.setResizable(true);
        nowPlayingDialog.getContentPane().setBackground(Color.BLACK);
        nowPlayingDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        nowPlayingDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopMusic();
            }
        });
        nowPlayingDialog.setAlwaysOnTop(true);

        JLabel albumArtLabel = new JLabel();
        albumArtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumArtLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        if (musica.getCoverPath() != null && !musica.getCoverPath().isEmpty()) {
            albumArtLabel.setIcon(new ImageIcon(new ImageIcon(musica.getCoverPath()).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH)));
        } else {
            albumArtLabel.setText("No Artwork Available");
            albumArtLabel.setForeground(Color.WHITE);
        }

        JPanel songInfoPanel = new JPanel();
        songInfoPanel.setLayout(new GridLayout(4, 1));
        songInfoPanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel(musica.getTitulo(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel artistLabel = new JLabel(musica.getArtista(), SwingConstants.CENTER);
        artistLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        artistLabel.setForeground(Color.GRAY);

        JLabel albumLabel = new JLabel(musica.getAlbum(), SwingConstants.CENTER);
        albumLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        albumLabel.setForeground(Color.LIGHT_GRAY);

        JLabel durar = new JLabel("Duracion (segundos) :" + musica.getDuracion(), SwingConstants.CENTER);
        albumLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        albumLabel.setForeground(Color.LIGHT_GRAY);

        songInfoPanel.add(titleLabel);
        songInfoPanel.add(albumLabel);
        songInfoPanel.add(artistLabel);
        songInfoPanel.add(durar);

        // BUTTONS
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlsPanel.setBackground(Color.WHITE);

        JButton playPauseButton = new JButton();
        ImageIcon icon = new ImageIcon("DefaultIMAGE/play.png");
        playPauseButton.setIcon(icon);
        playPauseButton.setBackground(Color.WHITE);
        playPauseButton.setForeground(Color.WHITE);

        JButton delete = new JButton();
        delete.setIcon(new ImageIcon("DefaultIMAGE/delete.png"));
        delete.setBackground(Color.WHITE);
        delete.setForeground(Color.WHITE);

        controlsPanel.add(playPauseButton);
        controlsPanel.add(delete);
        delete.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "Desea borrar esta canción en su librería?",
                    "BORRAR CANCION",
                    JOptionPane.OK_CANCEL_OPTION
            );
            if (option == JOptionPane.OK_OPTION) {
                String filePath = file.getAbsolutePath();

                manager.borrarMusica(filePath);

                songListPanel.removeAll();
                cargarCanciones(songListPanel);
                songListPanel.revalidate();
                songListPanel.repaint();
                nowPlayingDialog.dispose();
                JOptionPane.showMessageDialog(null, "Se borró la canción!");
            } else {
                JOptionPane.showMessageDialog(null, "Se canceló la acción!");
            }
        });

        playPauseButton.addActionListener(e -> {
            if (isPlaying && !isPaused) {
                pauseMusic();
                playPauseButton.setIcon(icon);
            } else if (isPaused) {
                resumeMusic();
                playPauseButton.setIcon(new ImageIcon("DefaultIMAGE/pause.png"));
            } else {
                playMusic(musica);
                playPauseButton.setIcon(new ImageIcon("DefaultIMAGE/pause.png"));
            }
        });

        nowPlayingDialog.add(albumArtLabel, BorderLayout.NORTH);
        nowPlayingDialog.add(songInfoPanel, BorderLayout.CENTER);
        nowPlayingDialog.add(controlsPanel, BorderLayout.SOUTH);

        nowPlayingDialog.setLocation(1100, Toolkit.getDefaultToolkit().getScreenSize().height - nowPlayingDialog.getHeight() - 250);
        nowPlayingDialog.setVisible(true);
    }

    private void cargarCanciones(JPanel songListPanel) {
        songListPanel.removeAll();

        File musicDirectory = new File("usuarios/" + manager.getUsernameInSession() + "/music/");
        System.out.println("Music directory path: " + musicDirectory.getAbsolutePath());

        if (musicDirectory.exists() && musicDirectory.isDirectory()) {
            File[] musicFiles = musicDirectory.listFiles((dir, name) -> name.endsWith(".priv"));

            System.out.println("Files in directory: ");
            if (musicFiles != null) {
                for (File file : musicFiles) {
                    System.out.println(file.getName());
                }
            } else {
                System.out.println("No music files found with the .priv extension.");
            }

            if (musicFiles != null) {
                for (File musicFile : musicFiles) {
                    Musica musica = manager.cargarMusica(musicFile.getAbsolutePath());
                    if (musica != null) {

                        System.out.println("Loaded music: " + musica.getTitulo() + " by " + musica.getArtista());

                        JButton songButton = new JButton(musica.getTitulo() + " - " + musica.getArtista());
                        songButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
                        songButton.setBackground(Color.BLACK);
                        songButton.setForeground(Color.WHITE);
                        songButton.setFont(new Font("Arial", Font.PLAIN, 16));
                        songButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                        songButton.setFocusPainted(false);
                        songButton.setPreferredSize(new Dimension(600, 40));
                        songButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                        songButton.addActionListener(e -> {
                            if(nowPlayingDialog != null){
                                nowPlayingDialog.dispose();
                                stopMusic();
                            }
                            showNowPlayingPanel(musica, musicFile);
                        });

                        SwingUtilities.invokeLater(() -> {
                            songListPanel.add(songButton);
                            songListPanel.add(Box.createVerticalStrut(10)); 
                            songListPanel.revalidate();
                            songListPanel.repaint();
                        });

                    } else {
                        System.out.println("Failed to load music from file: " + musicFile.getName());
                    }
                }
            }
        } else {
            System.err.println("Music directory does not exist or is invalid.");
        }

        songListPanel.revalidate();
        songListPanel.repaint();
    }

    private void resumeMusic() {
        if (currentSongFile != null && isPaused) {
            try {
                FileInputStream fileInputStream = new FileInputStream(currentSongFile);
                currentPlayer = new Player(fileInputStream);

                isSeeking = true;
                new Thread(() -> {
                    try {
                        fileInputStream.skip(playbackPosition);
                        currentPlayer.play();
                        isSeeking = false;
                        isPlaying = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                isPlaying = true;
                isPaused = false;
                System.out.println("Resumed from position: " + playbackPosition + " ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No song to resume.");
        }
    }

    private void pauseMusic() {
        if (currentPlayer != null && isPlaying) {
            try {
                playbackPosition += currentPlayer.getPosition();
                stopMusic();
                isPaused = true;
                System.out.println("Paused at position: " + playbackPosition + " ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.close();
            isPlaying = false;
            isPaused = false;
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(210, 60));
        return button;
    }

    private void exitToMain() {
        Usuarios user = manager.InSession();
        if (user != null) {
            user.setActivo(false);
            try {
                user.guardarUsuario();
                user.cargarUsuario();
            } catch (IOException e) {
                System.out.println("se desactivo el usuario!!!!!");
            }
        }
        Main main = new Main(manager);
        main.setVisible(true);
        dispose();
    }

    public void playMusic(Musica musica) {
        try {
            String filePath = musica.getRuta();
            System.out.println("Attempting to play music from: " + filePath);

            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                System.err.println("Error: The file does not exist at the specified path.");
                return;
            }

            currentSongFile = filePath;
            currentPlayer = new Player(new FileInputStream(filePath));

            isPlaying = true;
            isPaused = false;

            System.out.println("Starting music: " + filePath);

            new Thread(() -> {
                try {
                    currentPlayer.play();
                    isPlaying = false;
                } catch (JavaLayerException e) {
                    System.err.println("Error while playing music:");
                    e.printStackTrace();
                }
            }).start();
        } catch (JavaLayerException | FileNotFoundException e) {
            System.err.println("Error while trying to play the file:");
            e.printStackTrace();
        }
    }
}
