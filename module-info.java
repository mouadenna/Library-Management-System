/**
 * 
 */
/**
 * 
 */

module Application {
	requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    exports com.esi;
    exports com.esi.application; // Exporter le package contenant Main
    exports com.esi.controller; // Exporter le package contenant Main
    opens com.esi.controller to javafx.fxml; // Permet l'acc�s via r�flexion pour JavaFX.
    opens com.esi.bean to javafx.base;
    
    requires java.desktop;
    exports com.esi.GUI;
    

}