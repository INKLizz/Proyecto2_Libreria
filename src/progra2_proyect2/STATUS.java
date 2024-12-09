/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package progra2_proyect2;

/**
 *
 * @author Laura Sabillon
 */
public enum STATUS {
    OFFLINE("Offline"),
    ONLINE("Online");

    private final String status;

    // Constructor
    STATUS(String status) {
        this.status = status;
    }

    // Getter for the status
    public String getStatus() {
        return status;
    }
}
