/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Laura Sabillon
 */
public interface Manager_Interfaz {
       
    boolean AgregarUsers(String usuario, String password);
    boolean Login(String usuario, String password);
    String getUsernameInSession();
    Usuarios Buscar(String usuario);
}
