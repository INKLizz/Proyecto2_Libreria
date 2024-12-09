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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class MusicPanel {

    private JFrame nowPlayingDialog;
    private String currentAlbum;
    private int currentDuration;
    private String songAlbum;
    private int songDuration;
    private int playbackPosition = 0;
    private boolean isSeeking = false;
    private JPanel musicListPanel;
    private Player currentPlayer;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private File currentSongFile;
    ImageIcon pause = new ImageIcon("DefaultIMAGE/pause.png");
    ImageIcon download = new ImageIcon("DefaultIMAGE/download.png");
    Administrador admin = new Administrador();

    private static final String MUSIC_DIRECTORY = "music/";

    public JPanel createMusicPanel(Administrador admin) {
        JPanel musicPanel = new JPanel(new BorderLayout());
        musicPanel.setBackground(Color.BLACK);
        this.admin = admin;
        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBackground(Color.BLACK);

        JTextField searchField = new JTextField("");
        searchField.setFont(new Font("Arial", Font.PLAIN, 18));
        searchField.setBackground(Color.BLACK);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        searchField.setPreferredSize(new Dimension(250, 40));

        JButton searchButton = new JButton("Buscar Musica: ");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(Color.BLACK);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.WEST);

        musicListPanel = new JPanel();
        musicListPanel.setLayout(new BoxLayout(musicListPanel, BoxLayout.Y_AXIS));
        musicListPanel.setBackground(Color.BLACK);

        loadMusicFiles();

        JScrollPane musicListScrollPane = new JScrollPane(musicListPanel);
        musicListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        musicListScrollPane.setPreferredSize(new Dimension(400, 300));

        musicPanel.add(searchBarPanel, BorderLayout.NORTH);
        musicPanel.add(musicListScrollPane, BorderLayout.CENTER);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterMusic();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterMusic();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterMusic();
            }

            private void filterMusic() {
                String searchText = searchField.getText().toLowerCase();
                musicListPanel.removeAll();

                File[] musicFiles = new File(MUSIC_DIRECTORY).listFiles((dir, name)
                        -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".priv"));

                if (musicFiles != null) {
                    for (File file : musicFiles) {
                        try {
                            String title = "";
                            String artist = "Unknown Artist";
                            String albumPath = "";
                            String albumArtPath = "No Album Art";
                            File actualSongFile = null;

                            if (file.getName().endsWith(".mp3")) {
                                AudioFile audioFile = AudioFileIO.read(file);
                                Tag tag = audioFile.getTag();

                                title = (tag != null && tag.getFirst(FieldKey.TITLE).length() > 0)
                                        ? tag.getFirst(FieldKey.TITLE)
                                        : file.getName().replace(".mp3", "");

                                artist = (tag != null && tag.getFirst(FieldKey.ARTIST).length() > 0)
                                        ? tag.getFirst(FieldKey.ARTIST)
                                        : "Unknown Artist";

                                if (tag != null && tag.getFirstArtwork() != null) {
                                    byte[] albumArtBytes = tag.getFirstArtwork().getBinaryData();
                                    File albumArtFile = new File("album_art/" + file.getName() + ".jpg");
                                    albumArtFile.getParentFile().mkdirs();
                                    java.nio.file.Files.write(albumArtFile.toPath(), albumArtBytes);
                                    albumArtPath = albumArtFile.getAbsolutePath();
                                }
                                actualSongFile = file;
                            } else if (file.getName().endsWith(".priv")) {
                                Musica musica = admin.cargarMusica(file.getAbsolutePath());
                                if (musica != null) {
                                    title = musica.getTitulo();
                                    artist = musica.getArtista();
                                    albumPath = musica.getAlbum();
                                    albumArtPath = musica.getCoverPath();
                                    actualSongFile = new File(musica.getRuta());
                                }
                            }

                            if (title.toLowerCase().contains(searchText) || artist.toLowerCase().contains(searchText)) {
                                addSongToPanel(title, albumPath, artist, albumArtPath, actualSongFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                musicListPanel.revalidate();
                musicListPanel.repaint();
            }
        });
        return musicPanel;
    }

    private void processMp3File(File file) throws Exception {
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTag();

        String title = tag != null && tag.getFirst(FieldKey.TITLE).length() > 0
                ? tag.getFirst(FieldKey.TITLE)
                : file.getName().replace(".mp3", "");

        String artist = tag != null && tag.getFirst(FieldKey.ARTIST).length() > 0
                ? tag.getFirst(FieldKey.ARTIST)
                : "Unknown Artist";

        String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown Album";
        int duration = (int) audioFile.getAudioHeader().getTrackLength();

        String albumArtPath = "No Album Art";
        if (tag != null && tag.getFirstArtwork() != null) {
            byte[] albumArtBytes = tag.getFirstArtwork().getBinaryData();
            File albumArtFile = new File("album_art/" + file.getName() + ".jpg");
            albumArtFile.getParentFile().mkdirs();
            java.nio.file.Files.write(albumArtFile.toPath(), albumArtBytes);
            albumArtPath = albumArtFile.getAbsolutePath();
        }

        addSongToPanel(title, album, artist, albumArtPath, file);
    }

    private void loadMusicFiles() {
        File musicDirectory = new File(MUSIC_DIRECTORY);
        if (musicDirectory.exists() && musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles((dir, name)
                    -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".priv")
            );

            if (files != null && files.length > 0) {
                System.out.println("Found " + files.length + " music files.");
                for (File file : files) {
                    try {
                        if (file.getName().toLowerCase().endsWith(".mp3")) {
                            processMp3File(file);
                        } else if (file.getName().toLowerCase().endsWith(".priv")) {
                            processPrivFile(file);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing file: " + file.getName());
                        e.printStackTrace();
                    }
                }
                musicListPanel.revalidate();
                musicListPanel.repaint();
            } else {
                System.out.println("No music files found in the directory.");
            }
        } else {
            System.out.println("Invalid music directory: " + MUSIC_DIRECTORY);
        }
    }

    private void processPrivFile(File privFile) {
        try {

            Musica musica = admin.cargarMusica(privFile.getAbsolutePath());

            if (musica != null) {
                System.out.println("Loaded music: " + musica.getTitulo() + " by " + musica.getArtista());

                String title = musica.getTitulo();
                String artist = musica.getArtista();
                String albumArtPath = musica.getCoverPath();
                String album = musica.getAlbum();
                String actualSongPath = musica.getRuta();

                File actualFile = new File(actualSongPath);
                if (actualFile.exists()) {
                    addSongToPanel(title, album, artist, albumArtPath, actualFile);
                } else {
                    System.err.println("The referenced song file does not exist: " + actualSongPath);
                }
            } else {
                System.err.println("Failed to load music object from file: " + privFile.getName());
            }
        } catch (Exception e) {
            System.err.println("Error processing .priv file: " + privFile.getName());
            e.printStackTrace();
        }
    }

    private void addSongToPanel(String title, String album, String artist, String albumArtPath, File file) {

        JPanel songPanel = new JPanel(new BorderLayout());
        songPanel.setBackground(Color.DARK_GRAY);
        songPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel songTitleLabel = new JLabel(title);
        songTitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        songTitleLabel.setForeground(Color.WHITE);

        songPanel.setPreferredSize(new Dimension(1300, 60));
        songPanel.setMaximumSize(new Dimension(1300, 60));
        songPanel.setMinimumSize(new Dimension(1300, 60));

        songPanel.add(songTitleLabel, BorderLayout.CENTER);

        songPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (nowPlayingDialog != null) {
                    nowPlayingDialog.dispose();
                    stopMusic();
                }
                showNowPlayingPanel(title, album, artist, albumArtPath, file);
            }
        });

        musicListPanel.add(songPanel);
        musicListPanel.add(Box.createVerticalStrut(10));
    }

    private void showNowPlayingPanel(String songTitle, String album, String artistName, String albumArtPath, File file) {
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

        // Album Art
        JLabel albumArtLabel = new JLabel();
        albumArtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumArtLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        if (albumArtPath != null && !albumArtPath.isEmpty()) {
            albumArtLabel.setIcon(new ImageIcon(new ImageIcon(albumArtPath).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH)));
        } else {
            albumArtLabel.setText("No Artwork Available");
            albumArtLabel.setForeground(Color.WHITE);
        }

        // Song Info
        JPanel songInfoPanel = new JPanel();
        songInfoPanel.setLayout(new GridLayout(3, 1));
        songInfoPanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel(songTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel artistLabel = new JLabel(artistName, SwingConstants.CENTER);
        artistLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        artistLabel.setForeground(Color.GRAY);

        JLabel albumLabel = new JLabel(album, SwingConstants.CENTER);
        albumLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        albumLabel.setForeground(Color.LIGHT_GRAY);

        songInfoPanel.add(titleLabel);
        songInfoPanel.add(albumLabel);
        songInfoPanel.add(artistLabel);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlsPanel.setBackground(Color.WHITE);

        JButton playPauseButton = new JButton();
        ImageIcon playIcon = new ImageIcon("DefaultIMAGE/play.png");
        ImageIcon pauseIcon = new ImageIcon("DefaultIMAGE/pause.png");
        playPauseButton.setIcon(playIcon);
        playPauseButton.setBackground(Color.WHITE);

        JButton savingButton = new JButton();
        savingButton.setIcon(new ImageIcon("DefaultIMAGE/download.png"));
        savingButton.setBackground(Color.WHITE);

        controlsPanel.add(playPauseButton);
        controlsPanel.add(savingButton);

        playPauseButton.addActionListener(e -> {
            if (isPlaying && !isPaused) {
                pauseMusic();
                playPauseButton.setIcon(playIcon);
            } else if (isPaused) {
                resumeMusic();
                playPauseButton.setIcon(pauseIcon);
            } else {
                playMusic(file);
                playPauseButton.setIcon(pauseIcon);
            }
        });

        savingButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "Desea guardar esta canción en su librería?",
                    "GUARDAR CANCION",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option == JOptionPane.OK_OPTION) {
                String userDirectoryPath = "usuarios/" + admin.getUsernameInSession() + "/";
                String musicDirectoryPath = userDirectoryPath + "music/";
                String songFilePath = musicDirectoryPath + songTitle.replace(" ", "_") + ".priv";

                File userDirectory = new File(userDirectoryPath);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }

                File musicDirectory = new File(musicDirectoryPath);
                if (!musicDirectory.exists()) {
                    musicDirectory.mkdirs();
                }

                File songFile = new File(songFilePath);
                if (songFile.exists()) {
                    JOptionPane.showMessageDialog(null, "La canción ya está guardada en su librería!");
                    return;
                }

                try {
                    String albumArtPathToUse = (albumArtPath != null && !albumArtPath.isEmpty()) ? albumArtPath : "default/cover.png";
                    Musica newMusica = new Musica(
                            musicDirectoryPath,
                            songTitle,
                            artistName,
                            album,
                            albumArtPathToUse,
                            4,
                            file.getAbsolutePath()
                    );
                    newMusica.init();

                    JOptionPane.showMessageDialog(null, "Se guardó la canción en su librería!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al guardar la canción: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Se canceló la acción!");
            }
        });

        nowPlayingDialog.add(albumArtLabel, BorderLayout.NORTH);
        nowPlayingDialog.add(songInfoPanel, BorderLayout.CENTER);
        nowPlayingDialog.add(controlsPanel, BorderLayout.SOUTH);
        nowPlayingDialog.setLocation(1100, Toolkit.getDefaultToolkit().getScreenSize().height - nowPlayingDialog.getHeight() - 250);
        nowPlayingDialog.setVisible(true);
    }

    private void playMusic(File file) {
        try {
            currentSongFile = file;
            currentPlayer = new Player(new FileInputStream(file));
            isPlaying = true;
            isPaused = false;
            System.out.println("Stopping music: " + (currentSongFile != null ? currentSongFile.getName() : "None"));
            System.out.println("Starting music: " + file.getName());
            new Thread(() -> {
                try {
                    currentPlayer.play();
                    isPlaying = false;
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (JavaLayerException | FileNotFoundException e) {
            e.printStackTrace();
        }
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
}
