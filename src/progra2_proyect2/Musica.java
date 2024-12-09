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
import java.io.IOException;
import java.io.RandomAccessFile;

public class Musica {

    private Administrador admin;
    private String titulo;
    private String artista;
    private int duracion;
    private String coverPath;
    private String album;
    private String ruta;
    private String saving;

    public Musica(String save, String titulo, String artista, String album, String coverPath, int duracion, String ruta) {
        admin = new Administrador();
        this.album = album;
        this.coverPath = coverPath;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
        this.ruta = ruta;
        saving = save;
        try {
            init();
        } catch (IOException e) {
            System.err.println("Error during initialization: " + e.getMessage());
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArtista() {
        return artista;
    }

    public int getDuracion() {
        return duracion;
    }

    public String getRuta() {
        return ruta;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getAlbum() {
        return album;
    }

    @Override
    public String toString() {
        return "Canción: " + titulo + " por: " + artista + " del álbum: " + album
                + " (Cover: " + coverPath + ")";
    }

    public void init() throws IOException {
        String user = admin.getUsernameInSession();
        String sanitizedTitle = titulo.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_\\-]", "");
        String filePath = saving + sanitizedTitle + ".priv";
        File file = new File(filePath);

        if (!file.exists()) {
            try (RandomAccessFile musica = new RandomAccessFile(filePath, "rw")) {
                musica.writeUTF(titulo);
                musica.writeUTF(artista);
                musica.writeUTF(album);
                musica.writeInt(duracion);
                musica.writeUTF(coverPath);
                musica.writeUTF(ruta);
            } catch (IOException e) {
                System.out.println("No se pudo guardar el archivo de musica!");
            }
        } else {
            System.out.println("El archivo ya existe, no se crea nuevamente.");
        }
    }

   public void Read(String filePath) throws IOException {
        try (RandomAccessFile musicaFile = new RandomAccessFile(filePath, "r")) {

            titulo = musicaFile.readUTF();
            artista = musicaFile.readUTF();
            album = musicaFile.readUTF();
            duracion = musicaFile.readInt();
            coverPath = musicaFile.readUTF();
            ruta = musicaFile.readUTF();
        } catch (IOException e) {
            System.err.println("Error reading the .priv file: " + e.getMessage());
            throw e; 
        }
    }
}
