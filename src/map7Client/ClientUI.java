package map7Client;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ClientUI {

	/**
	 * Un riferimento al client.
	 */
	protected MainTest client;

	/**
	 * Costruisce una interfaccia utente client.
	 * @param client Un riferimento al clien
	 */
	ClientUI(final MainTest client) {
		this.client = client;	
	}

	/**
	 * Mostra un popup di errore.
	 * @param headerText Il testo dell'intestazione
	 * @param contentText Il testo del contenuto
	 */
	static void showError(final String headerText, final String contentText) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.setResizable(true);
		alert.showAndWait();
	}

	/**
	 * Mostra un popup di informazioni.
	 * @param text Il testo da mostrare
	 */
	static void showInformation(final String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(text);
		alert.setResizable(true);
		alert.showAndWait();
	}
}
