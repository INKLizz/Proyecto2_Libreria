/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;

/**
 *
 * @author Laura Sabillon
 */
public class Main extends JFrame {

    private Administrador manager;
    private Usuarios user;

    public Main(Administrador manager) {
        this.manager = manager;
        setTitle("BIBLIOTECA DE JUEGOS Y MUSICA");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("BIENVENIDO A SU BIBLIOTECA VIRTUAL!");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
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
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        panel.add(title, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 20, 0);

        JButton crear = createButton("CREAR CUENTA");
        JButton login = createButton("LOGIN");
        JButton salir = createButton("SALIR");

        panel.add(crear, gbc);
        gbc.gridy++;
        panel.add(login, gbc);
        gbc.gridy++;
        panel.add(salir, gbc);

        panel.setBackground(Color.BLACK);

        add(panel, BorderLayout.CENTER);

        setVisible(true);

        // CREAR CUENTA 
        crear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame crearC = new JFrame("CREAR CUENTA");
                crearC.setSize(400, 300);
                crearC.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                crearC.setLocationRelativeTo(null);

                JPanel crearPanel = new JPanel();
                crearPanel.setLayout(new GridBagLayout());
                crearPanel.setBackground(Color.BLACK);

                GridBagConstraints gbcCrear = new GridBagConstraints();
                gbcCrear.gridx = 0;
                gbcCrear.gridy = GridBagConstraints.RELATIVE;
                gbcCrear.insets = new Insets(10, 10, 10, 10);
                gbcCrear.anchor = GridBagConstraints.WEST;

                JLabel usuario = new JLabel("Usuario:");
                usuario.setForeground(Color.WHITE);
                crearPanel.add(usuario, gbcCrear);

                JTextField usuarioField = new JTextField(20);
                gbcCrear.gridx = 1;
                crearPanel.add(usuarioField, gbcCrear);

                JLabel contra = new JLabel("Contraseña:");
                contra.setForeground(Color.WHITE);
                gbcCrear.gridx = 0;
                crearPanel.add(contra, gbcCrear);

                JPasswordField password = new JPasswordField(20);
                gbcCrear.gridx = 1;
                crearPanel.add(password, gbcCrear);

                JLabel confirm = new JLabel("Confirmar Contraseña:");
                confirm.setForeground(Color.WHITE);
                gbcCrear.gridx = 0;
                crearPanel.add(confirm, gbcCrear);

                JPasswordField passwordC = new JPasswordField(20);
                gbcCrear.gridx = 1;
                crearPanel.add(passwordC, gbcCrear);

                gbcCrear.gridx = 0;
                gbcCrear.gridy = GridBagConstraints.RELATIVE;
                gbcCrear.gridwidth = 2;

                JCheckBox showPassword = new JCheckBox("Mostrar Contraseña");
                showPassword.setForeground(Color.WHITE);
                showPassword.setBackground(Color.BLACK);
                showPassword.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        char echoChar = showPassword.isSelected() ? (char) 0 : '*';
                        password.setEchoChar(echoChar);
                        passwordC.setEchoChar(echoChar);
                    }
                });
                crearPanel.add(showPassword, gbcCrear);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout());
                buttonPanel.setBackground(Color.BLACK);

                JButton b_confirm = new JButton("CONFIRMAR");
                b_confirm.setBackground(Color.BLACK);
                b_confirm.setForeground(Color.WHITE);

                JButton b_salir = new JButton("SALIR");
                b_salir.setBackground(Color.BLACK);
                b_salir.setForeground(Color.WHITE);
                b_salir.addActionListener(event -> crearC.dispose());

                buttonPanel.add(b_confirm);
                buttonPanel.add(b_salir);

                gbcCrear.gridx = 0;
                gbcCrear.gridy = GridBagConstraints.RELATIVE;
                gbcCrear.gridwidth = 2;
                crearPanel.add(buttonPanel, gbcCrear);

                crearC.add(crearPanel);
                crearC.setVisible(true);

                b_confirm.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String usuario = usuarioField.getText();
                        String contra = password.getText();
                        String contraC = passwordC.getText();

                        if (usuario.isEmpty() || contra.isEmpty() || contraC.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Llene todos los espacios.");
                        } else {
                            if (contra.equals(contraC)) {
                                if (manager.AgregarUsers(usuario, contra)) {
                                    manager.Login(usuario, contra);
                                    JOptionPane.showMessageDialog(null, "Se añadió el usuario exitosamente!");
                                    SubMenu menu = new SubMenu(manager);
                                    menu.setVisible(true);
                                    dispose();
                                    crearC.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Usuario ya existe");
                                    usuarioField.setText("");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Contraseñas no coinciden.");
                            }
                        }
                    }
                });
            }
        });

        // LOGIN
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame login = new JFrame("LOGIN");
                login.setSize(400, 300);
                login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                login.setLocationRelativeTo(null);

                JPanel crearPanel = new JPanel();
                crearPanel.setLayout(new GridBagLayout());
                crearPanel.setBackground(Color.BLACK);

                GridBagConstraints grids = new GridBagConstraints();
                grids.gridx = 0;
                grids.gridy = GridBagConstraints.RELATIVE;
                grids.insets = new Insets(10, 10, 10, 10);
                grids.anchor = GridBagConstraints.WEST;

                JLabel usuario = new JLabel("Usuario:");
                usuario.setForeground(Color.WHITE);
                crearPanel.add(usuario, grids);

                JTextField usuarioField = new JTextField(20);
                grids.gridx = 1;
                crearPanel.add(usuarioField, grids);

                JLabel contra = new JLabel("Contraseña:");
                contra.setForeground(Color.WHITE);
                grids.gridx = 0;
                crearPanel.add(contra, grids);

                JPasswordField password = new JPasswordField(20);
                grids.gridx = 1;
                crearPanel.add(password, grids);

                grids.gridx = 0;
                grids.gridy = GridBagConstraints.RELATIVE;
                grids.gridwidth = 2;

                JCheckBox showPassword = new JCheckBox("Mostrar Contraseña");
                showPassword.setForeground(Color.WHITE);
                showPassword.setBackground(Color.BLACK);
                showPassword.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        password.setEchoChar(showPassword.isSelected() ? (char) 0 : '*');
                    }
                });
                crearPanel.add(showPassword, grids);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout());
                buttonPanel.setBackground(Color.BLACK);

                JButton b_confirm = new JButton("CONFIRMAR");
                b_confirm.setBackground(Color.BLACK);
                b_confirm.setForeground(Color.WHITE);

                JButton b_salir = new JButton("SALIR");
                b_salir.setBackground(Color.BLACK);
                b_salir.setForeground(Color.WHITE);
                b_salir.addActionListener(event -> login.dispose());

                buttonPanel.add(b_confirm);
                buttonPanel.add(b_salir);

                grids.gridx = 0;
                grids.gridy = GridBagConstraints.RELATIVE;
                grids.gridwidth = 2;
                crearPanel.add(buttonPanel, grids);

                login.add(crearPanel);
                login.setVisible(true);

                b_confirm.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String usuario = usuarioField.getText();
                        String contra = password.getText();

                        if (usuario.isEmpty() || contra.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Llene todos los espacios.");
                        } else {
                            if (manager.Login(usuario, contra)) {
                                JOptionPane.showMessageDialog(null, "Bienvenido " + usuario + "!");
                                SubMenu menu = new SubMenu(manager);
                                menu.setVisible(true);
                                dispose();
                                login.dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecta");
                                usuarioField.setText("");
                                password.setText("");
                            }
                        }
                    }
                });
            }
        });

        // SALIR
        salir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Usuarios user = manager.InSession();
                if (user != null) {
                    user.setActivo(false);
                    try {
                        user.guardarEstadoActivo();
                        user.guardarUsuario();
                        user.cargarUsuario();
                    } catch (IOException se) {
                        System.out.println("No se pudo guardar los usuarios : " + se.getMessage());
                    }
                    System.exit(0);
                } else {
                    System.exit(0);

                }
            }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }

}
