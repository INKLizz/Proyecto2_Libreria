/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Laura Sabillon
 */
public class AÑADIR {

    private String ruta;
    private String ikon;
    private JPanel mainPanel;

    public AÑADIR() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setSize(600, 600);
        mainPanel.setBackground(Color.BLACK);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.BLACK);
        tabbedPane.setForeground(Color.WHITE);

        JPanel gamePanel = createGameTab();
        tabbedPane.addTab("Añadir Juego", gamePanel);

        JPanel musicPanel = createMusicTab();
        tabbedPane.addTab("Añadir Musica", musicPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel createGameTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Crear Juego");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        JTextField nombreField = new JTextField();
        JTextField generoField = new JTextField();
        JTextField desarrolladorField = new JTextField();

        addRow(panel, gbc, "Nombre:", nombreField);
        addRow(panel, gbc, "Género:", generoField);
        addRow(panel, gbc, "Desarrollador:", desarrolladorField);

        JButton iconButton = new JButton("Escoger Icon");
        iconButton.setForeground(Color.BLACK);
        iconButton.setBackground(Color.WHITE);
        iconButton.setBorder(new LineBorder(Color.BLACK, 2));

        final String[] selectedIcon = new String[1];
        iconButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
            int returnValue = fileChooser.showOpenDialog(panel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedIcon[0] = selectedFile.getAbsolutePath();
                iconButton.setText(selectedFile.getName());
            }
        });

        addRow(panel, gbc, "Icono del Juego:", iconButton);

        JPanel datePanel = new JPanel(new GridBagLayout());
        datePanel.setBackground(Color.BLACK);

        GridBagConstraints dateGbc = new GridBagConstraints();
        dateGbc.insets = new Insets(5, 5, 5, 5);
        dateGbc.fill = GridBagConstraints.HORIZONTAL;

        SpinnerNumberModel yearModel = new SpinnerNumberModel(2024, 1900, 2100, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);
        JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(yearSpinner, "####");
        yearSpinner.setEditor(yearEditor);

        SpinnerNumberModel monthModel = new SpinnerNumberModel(1, 1, 12, 1);
        JSpinner monthSpinner = new JSpinner(monthModel);

        SpinnerNumberModel dayModel = new SpinnerNumberModel(1, 1, 31, 1);
        JSpinner daySpinner = new JSpinner(dayModel);

        dateGbc.gridx = 0;
        dateGbc.gridy = 0;
        JLabel yearLabel = new JLabel("Año:");
        yearLabel.setForeground(Color.WHITE);
        datePanel.add(yearLabel, dateGbc);

        dateGbc.gridx++;
        datePanel.add(yearSpinner, dateGbc);

        dateGbc.gridx++;
        JLabel monthLabel = new JLabel("Mes:");
        monthLabel.setForeground(Color.WHITE);
        datePanel.add(monthLabel, dateGbc);

        dateGbc.gridx++;
        datePanel.add(monthSpinner, dateGbc);

        dateGbc.gridx++;
        JLabel dayLabel = new JLabel("Día:");
        dayLabel.setForeground(Color.WHITE);
        datePanel.add(dayLabel, dateGbc);

        dateGbc.gridx++;
        datePanel.add(daySpinner, dateGbc);

        addRow(panel, gbc, "Fecha de Lanzamiento:", datePanel);

        JButton createButton = new JButton("CREAR JUEGO");
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(Color.BLACK);
        createButton.setBorder(new LineBorder(Color.WHITE, 2));
        createButton.setPreferredSize(new Dimension(200, 50));

        gbc.gridwidth = 2;
        addRow(panel, gbc, "", createButton);

        createButton.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            String icon = selectedIcon[0] != null ? selectedIcon[0].trim() : "";
            String genero = generoField.getText().trim();
            String desarrollador = desarrolladorField.getText().trim();

            int year = (int) yearSpinner.getValue();
            int month = (int) monthSpinner.getValue();
            int day = (int) daySpinner.getValue();

            if (nombre.isEmpty() || icon.isEmpty() || genero.isEmpty() || desarrollador.isEmpty()) {
                JOptionPane.showMessageDialog(
                        panel,
                        "Por favor, complete todos los campos antes de crear el juego.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                Calendar calendar = new GregorianCalendar(year, month - 1, day);
                long fecha = calendar.getTimeInMillis();

                String ruta = "games/";
                Juego nuevoJuego = new Juego(nombre, icon, genero, desarrollador, fecha, ruta);

                JOptionPane.showMessageDialog(panel, "¡El juego se creó exitosamente!");

                nombreField.setText("");
                generoField.setText("");
                desarrolladorField.setText("");
                selectedIcon[0] = null;
                iconButton.setText("Select Icon");
                yearSpinner.setValue(2024);
                monthSpinner.setValue(1);
                daySpinner.setValue(1);

                System.out.println("Juego creado: " + nuevoJuego.getNombre());
                System.out.println("Icono: " + nuevoJuego.getIcon());
                System.out.println("Fecha de lanzamiento: " + calendar.getTime());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        panel,
                        "Ocurrió un error al crear el juego: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        return panel;
    }

    private JPanel createMusicTab() {
        JPanel panele = new JPanel(new GridBagLayout());
        panele.setBackground(Color.BLACK);
        GridBagConstraints gb = new GridBagConstraints();
        gb.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("AÑADIR CANCIONES");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gb.gridx = 0;
        gb.gridy = 0;
        gb.gridwidth = 2;
        gb.anchor = GridBagConstraints.CENTER;
        panele.add(titleLabel, gb);

        gb.gridwidth = 1;
        gb.anchor = GridBagConstraints.WEST;

        Dimension textFieldSize = new Dimension(200, 25);

        JLabel label1 = new JLabel("Song Title:");
        label1.setForeground(Color.WHITE);
        gb.gridx = 0;
        gb.gridy = 1;
        panele.add(label1, gb);

        JTextField cancion = new JTextField();
        cancion.setPreferredSize(textFieldSize);
        gb.gridx = 1;
        panele.add(cancion, gb);

        JLabel label2 = new JLabel("Artista:");
        label2.setForeground(Color.WHITE);
        gb.gridx = 0;
        gb.gridy = 2;
        panele.add(label2, gb);

        JTextField artist = new JTextField();
        artist.setPreferredSize(textFieldSize);
        gb.gridx = 1;
        panele.add(artist, gb);

        JLabel label3 = new JLabel("Album:");
        label3.setForeground(Color.WHITE);
        gb.gridx = 0;
        gb.gridy = 3;
        panele.add(label3, gb);

        JTextField album = new JTextField();
        album.setPreferredSize(textFieldSize);
        gb.gridx = 1;
        panele.add(album, gb);

        JLabel label4 = new JLabel("Cover Path:");
        label4.setForeground(Color.WHITE);
        gb.gridx = 0;
        gb.gridy = 4;
        panele.add(label4, gb);

        JButton icono = new JButton("Escoger Icono");
        icono.setPreferredSize(textFieldSize);
        gb.gridx = 1;
        panele.add(icono, gb);

        JLabel label5 = new JLabel("Duracion (segundos):");
        label5.setForeground(Color.WHITE);
        gb.gridx = 0;
        gb.gridy = 5;
        panele.add(label5, gb);

        JTextField textField5 = new JTextField();
        textField5.setPreferredSize(textFieldSize);
        gb.gridx = 1;
        panele.add(textField5, gb);

        JLabel label6 = new JLabel("Escoger Archivo:");
        label6.setForeground(Color.WHITE);
        gb.gridx = 0;
        gb.gridy = 6;
        panele.add(label6, gb);

        JButton mp3 = new JButton("Escoger Mp3");
        mp3.setPreferredSize(textFieldSize);
        gb.gridx = 1;
        panele.add(mp3, gb);

        JButton addButton = new JButton("AÑADIR CANCION");
        addButton.setPreferredSize(new Dimension(300, 30));
        gb.gridx = 0;
        gb.gridy = 7;
        gb.gridwidth = 2;
        gb.anchor = GridBagConstraints.CENTER;
        panele.add(addButton, gb);

        icono.addActionListener(ex -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (PNG, JPG)", "png", "jpg");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                ikon = filePath;
                JOptionPane.showMessageDialog(null, "Selected Image: " + selectedFile.getName());
            }
        });

        mp3.addActionListener(ex -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files (*.mp3)", "mp3");
            fileChooser.setFileFilter(filter);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                ruta = filePath;
                JOptionPane.showMessageDialog(null, "Selected Mp3: " + selectedFile.getName());
            }
        });
        addButton.addActionListener(actionEvent -> {
            String titulo = cancion.getText();
            String artista = artist.getText();
            String albumName = album.getText();
            String coverPath = ikon;
            String duracion = "";
            int time = 0;

            try {
                duracion = textField5.getText().trim();
                time = Integer.parseInt(duracion);
                System.out.println("Duration is: " + duracion);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Duracion tiene que ser un numero!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }

            String path = ruta;
            if (ikon == null || ruta == null) {
                JOptionPane.showMessageDialog(null, "Tiene que escoger un icono y un mp3!");
                return;
            }
            if (titulo.isEmpty() || artista.isEmpty() || albumName.isEmpty() || duracion.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tiene que llenar todo los espacios!!");
            } else {
                String pathi = "music/";

                Musica music = new Musica(pathi, titulo, artista, albumName, coverPath, time, ruta);
                JOptionPane.showMessageDialog(null, "Se salvo la Cancion!");

                cancion.setText("");
                artist.setText("");
                album.setText("");
                textField5.setText("");
                ikon = null;
                ruta = null;
            }
        });

        return panele;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(5, 0, 5, 0));
        return label;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(label, gbc);

        component.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBackground(Color.DARK_GRAY);
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        textField.setToolTipText(placeholder);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return button;
    }
}
