package database;

/**
 * Modella il fallimento nella connesione al database.
 */
public class DatabaseConnectionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Invoca il costruttore della superclasse per avvalorare il campo String
	 * contenente il messaggio di errore.
	 * 
	 * @param message Messaggio di errore
	 */
	public DatabaseConnectionException(String message) {
		super(message);
	}

	public DatabaseConnectionException() {
		super("Fallimento nella connessione al Database");
	}

}
