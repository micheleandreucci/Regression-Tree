package map7Client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * L'interfaccia utente di apprendimento client.
 */
public class LearningUI
{
	public void setMainApp(MainTest mainApp) {
    }
	
    /**
     *Chiude l'applicazione
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    /**
     * Apre una finestra di dialogo
     */
    @FXML
    private void handleAbout() 
    {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("RegressionTree");
    	alert.setHeaderText("About");
    	alert.setContentText("Michele Andreucci Matricola:661791");

    	alert.showAndWait();
    }
}

