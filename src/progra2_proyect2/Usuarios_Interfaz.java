/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package progra2_proyect2;

import java.io.IOException;

/**
 *
 * @author Laura Sabillon
 */
public interface Usuarios_Interfaz {
    void guardarEstadoActivo() throws IOException;
    void guardarUsuario() throws IOException;
    void cargarUsuario () throws IOException;
    String getIcon ();
    String getNombre();
}
