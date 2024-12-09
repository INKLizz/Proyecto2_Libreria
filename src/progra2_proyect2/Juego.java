/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Laura Sabillon
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class Juego {

    private String nombre;
    private String icon;
    private String genero;
    private String desarollador;
    private long fechaLanzamiento;
    private String ruta;

    public Juego(String nombre, String icon, String genero, String desarollador,  long fecha, String ruta) {
        this.nombre = nombre;
        this.genero = genero;
        this.desarollador = desarollador;
        this.icon = icon;
        fechaLanzamiento = fecha;
        this.ruta = ruta;
        try {
            initGame();
        } catch (IOException e) {
            System.out.println("ERROR IN CREATING FILE: " + e.getMessage());
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getGenero() {
        return genero;
    }

    public String getDesarollador() {
        return desarollador;
    }

    public long getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public String getRuta() {
        return ruta;
    }

    public String getIcon() {
        return icon;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public void initGame() throws IOException {
        String sanitizedFileName = nombre.replace(" ", "_");
        String path = ruta + sanitizedFileName + ".priv";
        try (RandomAccessFile game = new RandomAccessFile(path, "rw")) {
            game.seek(0);
            game.writeUTF(nombre);
            game.writeUTF(icon);
            game.writeUTF(genero);
            game.writeUTF(desarollador);
            game.writeLong(fechaLanzamiento);
            game.writeUTF(ruta);
        }
    }

    public static Juego LeerJuego(String filePath) {
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

    @Override
    public String toString() {
        return "Juego: " + nombre + ", Género: " + genero + ", Desarrollador: " + desarollador
                + ", Fecha de Lanzamiento: " + new Date(fechaLanzamiento) + ", Icono: " + icon + ", Ruta: " + ruta;
    }
}
