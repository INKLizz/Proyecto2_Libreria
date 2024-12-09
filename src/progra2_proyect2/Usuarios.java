/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Cristina Sabillon
 */
import java.io.*;
import java.util.*;

public class Usuarios {

    //USER
    private String nombre;
    private String icon;
    private String password;
    private final Calendar fechaRegistro;
    private boolean activo;
    private List<String> chatHistory;
    private String description;

    //FOLDERS
    private RandomAccessFile users;
    private RandomAccessFile games;
    private RandomAccessFile music;
    private RandomAccessFile chat;

    private static final String BASE_DIR = "usuarios/";

    public Usuarios(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
        this.fechaRegistro = Calendar.getInstance();
        this.activo = true;
        this.chatHistory = new ArrayList<>();
        this.description = "";
        icon = "DefaultIMAGE/guest.png";

        try {
            initializeUserDirectories();
            initFiles();
        } catch (IOException e) {
            System.out.println("Error initializing user: " + e.getMessage());
        }
    }

    private void initializeUserDirectories() {
        File userDirectory = new File(BASE_DIR + nombre);
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }

        crearFolder("game");
        crearFolder("music");
        crearFolder("chat");
    }

    private void crearFolder(String folder) {
        File directory = new File(BASE_DIR + nombre + "/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void initFiles() throws IOException {
        users = new RandomAccessFile(getUserPath("userInfo.priv"), "rw");

        if (users.length() == 0) {
            guardarUsuario();
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private String getUserPath(String filename) {
        return BASE_DIR + nombre + "/" + filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void guardarUsuario() throws IOException {
        users.seek(0);
        users.writeUTF(nombre);
        users.writeUTF(password);
        users.writeLong(fechaRegistro.getTimeInMillis());
        users.writeBoolean(activo);
        users.writeUTF(description);
        users.writeUTF(icon);

        users.writeInt(chatHistory.size());
        for (String message : chatHistory) {
            users.writeUTF(message);
        }
    }

    public void cargarUsuario() throws IOException {
        users.seek(0);

        nombre = users.readUTF();
        password = users.readUTF();
        fechaRegistro.setTimeInMillis(users.readLong());
        activo = users.readBoolean();
        description = users.readUTF();
        icon = users.readUTF();

        int chatSize = users.readInt();
        chatHistory.clear();
        for (int indice = 0; indice < chatSize; indice++) {
            chatHistory.add(users.readUTF());
        }
    }

    public void guardarEstadoActivo() throws IOException {
        users.seek(0);
        users.writeBoolean(activo);
    }

    public boolean leerEstadoActivo() throws IOException {
        users.seek(0);
        return users.readBoolean();
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public Calendar getFechaRegistro() {
        return fechaRegistro;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
