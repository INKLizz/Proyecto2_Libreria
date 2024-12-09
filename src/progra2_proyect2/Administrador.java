/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Laura Sabillon
 */
import java.io.File;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTextArea;

public class Administrador {

    private Usuarios loggedInUser = null;

    private ArrayList<Usuarios> usuarios;

    public Administrador() {
        usuarios = new ArrayList<>();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        File userDirectory = new File("usuarios");
        if (userDirectory.exists() && userDirectory.isDirectory()) {
            for (File userFolder : userDirectory.listFiles()) {
                if (userFolder.isDirectory()) {
                    try {
                        String username = userFolder.getName();
                        Usuarios user = new Usuarios(username, "");
                        user.cargarUsuario();
                        usuarios.add(user);
                    } catch (IOException e) {
                        System.out.println("Error loading user " + userFolder.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void guardarUsuarios() throws IOException {
        for (Usuarios usuario : usuarios) {
            try {
                usuario.guardarUsuario();
            } catch (IOException e) {
                System.out.println("Error saving user " + usuario.getNombre() + ": " + e.getMessage());
            }
        }
    }

    public Usuarios Buscar(String usuario) {
        for (Usuarios use : usuarios) {
            if (use != null && use.getNombre().equals(usuario)) {
                return use;
            }
        }
        return null;
    }

    public boolean AgregarUsers(String usuario, String password) {
        if (Buscar(usuario) == null) {
            Usuarios newUser = new Usuarios(usuario, password);
            usuarios.add(newUser);
            try {
                guardarUsuarios();
            } catch (Exception e) {
                System.out.println("Error saving new user: " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean Login(String usuario, String password) {
        Usuarios user = Buscar(usuario);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                user.setActivo(true);
                loggedInUser = user;
                try {
                    guardarUsuarios();
                } catch (IOException e) {
                    System.out.println("Error saving user state: " + e.getMessage());
                }
                return true;
            }
        }
        return false;
    }

    public String getUsernameInSession() {
        if (loggedInUser != null) {
            return loggedInUser.getNombre();
        }
        return null;
    }

    public Usuarios InSession() {
        return loggedInUser;
    }

    public ArrayList<Usuarios> getUsuarios() {
        return usuarios;
    }

    public boolean borrarUsuario(String username) {
        Usuarios user = Buscar(username);
        if (user != null) {
            if (user.hacerBorrar()) {
                usuarios.remove(user);
                try {
                    guardarUsuarios();
                    System.out.println("User " + username + " deleted successfully.");
                    return true;
                } catch (IOException e) {
                    System.out.println("Error saving updated user list: " + e.getMessage());
                    return false;
                }
            } else {
                System.out.println("Failed to delete user files for " + username);
                return false;
            }
        }
        System.out.println("User not found: " + username);
        return false;
    }

    // ----  FUNCIONES DE CHAT GENERAL Y PERSONAL
    public void GuardarMChat(String message) throws IOException {
        String chatFile = ("chat/generalChat.priv");
        try (RandomAccessFile raf = new RandomAccessFile(chatFile, "rw")) {
            raf.seek(raf.length());
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            raf.writeUTF(message);
            raf.writeUTF(currentDateTime);

        } catch (IOException e) {
            System.out.println("Error saving chat message: " + e.getMessage());
        }
    }

    public void MostrarMChat(JTextArea chatHistoryArea) throws IOException {
        RandomAccessFile chatFile = new RandomAccessFile("chat/generalChat.priv", "rw");
        StringBuilder chatContent = new StringBuilder();
        if (chatFile.length() != 0) {
            while (chatFile.getFilePointer() < chatFile.length()) {
                String message = chatFile.readUTF();
                String timestamp = chatFile.readUTF();

                chatContent.append(message)
                        .append(" - ")
                        .append(timestamp)
                        .append("\n\n");

            }
            chatHistoryArea.setText(chatContent.toString());
        } else {
            chatHistoryArea.setText("No chat history found.");

        }
    }

    public void GuardarPrivateChat(String sender, Usuarios recipient, String message) {
        String senderChatFilePath = "usuarios/" + sender + "/chat/" + sender + "_" + recipient.getNombre() + ".priv";
        String recipientChatFilePath = "usuarios/" + recipient.getNombre() + "/chat/" + recipient.getNombre() + "_" + sender + ".priv";

        try {
            appendToChatFile(senderChatFilePath, sender, recipient, message);
            appendToChatFile(recipientChatFilePath, sender, recipient, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToChatFile(String filePath, String sender, Usuarios recipient, String message) throws IOException {
        RandomAccessFile chatFile = new RandomAccessFile(filePath, "rw");

        chatFile.seek(chatFile.length());
        String chatMessage = sender + ": " + message;
        chatFile.writeUTF(chatMessage);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        chatFile.writeUTF(timeStamp + "\n");

    }

    public void MostrarPrivateChat(Usuarios recipient, JTextArea privateChatHistoryArea) throws IOException {
        if (recipient == null) {
            System.out.println("Error: Recipient is null.");
            return;
        }

        String senderChatFilePath = "usuarios/" + getUsernameInSession() + "/chat/" + getUsernameInSession() + "_" + recipient.getNombre() + ".priv";

        try {
            privateChatHistoryArea.setText("");
            loadChatHistoryFromFile(senderChatFilePath, privateChatHistoryArea);

            System.out.println("Private chat loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe!");
        } catch (IOException e) {
            System.out.println("Error loading private chat history:");
            e.printStackTrace();
        }
    }

    private void loadChatHistoryFromFile(String chatFilePath, JTextArea privateChatHistoryArea) throws IOException {
        RandomAccessFile chatFile = new RandomAccessFile(chatFilePath, "r");

        while (chatFile.getFilePointer() < chatFile.length()) {
            try {
                String message = chatFile.readUTF();
                privateChatHistoryArea.append(message + "\n");
            } catch (EOFException e) {
                System.out.println("Unexpected end of file reached while reading chat history.");
                break;
            } catch (IOException e) {
                System.out.println("Error reading chat file: " + e.getMessage());
                break;
            }
        }
        chatFile.close();
    }

    public Musica cargarMusica(String filePath) {
        try {
            Musica musica = new Musica("", "", "", "", "", 0, "");

            musica.Read(filePath);

            return musica;

        } catch (IOException e) {
            System.err.println("Error loading music data: " + e.getMessage());
            return null;
        }
    }

    public void borrarMusica(String filePath) {
        Musica musica = cargarMusica(filePath);

        if (musica != null) {
            String musicaFilePath = filePath;
            File musicaFile = new File(musicaFilePath);
            if (musicaFile.exists()) {
                boolean deleted = musicaFile.delete();

                if (deleted) {
                    System.out.println("Musica file deleted successfully: " + musicaFilePath);
                } else {
                    System.err.println("Error deleting the Musica file: " + musicaFilePath);
                }
            } else {
                System.err.println("Musica file not found: " + musicaFilePath);
            }
        } else {
            System.err.println("Musica object is null, cannot delete file.");
        }
    }

    public static Juego CargarJuego(String filePath) {
        try (RandomAccessFile game = new RandomAccessFile(filePath, "r")) {
            String nombre = game.readUTF();
            String icon = game.readUTF();
            String genero = game.readUTF();
            String desarollador = game.readUTF();
            long fechaLanzamiento = game.readLong();
            String ruta = game.readUTF();

            return new Juego(nombre, icon, genero, desarollador, fechaLanzamiento, ruta);
        } catch (IOException e) {
            System.out.println("ERROR READING GAME FILE: " + e.getMessage());
            return null;
        }
    }

    public void borrarJuego(String filePath) {
        try {
            File fileToDelete = new File(filePath.replace("\\", File.separator).replace("/", File.separator));

            if (fileToDelete.exists() && fileToDelete.isFile()) {
                fileToDelete.setWritable(true);

                if (fileToDelete.delete()) {
                    System.out.println("Game deleted successfully");
                } else {
                    System.out.println("Failed to delete the game");
                }
            } else {
                System.out.println("File does not exist or is not a valid file: " + filePath);
            }
        } catch (SecurityException e) {
            System.err.println("Access Denied: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error handling the file: " + e.getMessage());
        }
    }
}
