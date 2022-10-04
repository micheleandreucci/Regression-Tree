package database;

/**
 * Modella la restituzione di un resultset vuoto.
 */
public class EmptySetException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptySetException() {
		super("ResultSet vuoto");
	}

	/**
	 * Richiama il costruttore della superclasse per inizializzare il messaggio di
	 * errore.
	 * 
	 * @param message Messaggio di errore
	 */
	public EmptySetException(String message) {
		super(message);
	}

}
