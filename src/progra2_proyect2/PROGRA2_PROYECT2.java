/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package progra2_proyect2;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Laura Sabillon
 */
public class PROGRA2_PROYECT2 {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                if (SERVER.isPortInUse(12345)) {
                    System.out.println("Server already running on port 12345.");
                } else {
                    Thread serverThread = new Thread(() -> {
                        try {
                            SERVER server = new SERVER(12345);
                            System.out.println("Server started...");
                            server.start(); 
                        } catch (IOException e) {
                            System.err.println("Failed to start the server:");
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error: Could not start the server.", "Server Error", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                    });
                    serverThread.setDaemon(true);
                    serverThread.start();
                }

                Administrador man = new Administrador(); 
                Main ventana = new Main(man);
                ventana.setVisible(true);             
        });
    }
}