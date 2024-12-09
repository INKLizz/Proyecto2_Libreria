/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

import java.io.File;
/**
 *
 * @author Laura Sabillon
 */
   
public class ADMIN extends Usuarios {
    private boolean isAdmin;

    public ADMIN(String nombre, String pass, boolean isAdmin) {
        super(nombre, pass);
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean checkIfAdmin(String file) {
        File userFolder = new File("usuarios/" + super.getNombre());
        System.out.println("usuarios/" + super.getNombre());
        return userFolder.exists() && userFolder.isDirectory() && userFolder.getName().equals("ADMIN");
    }
}
