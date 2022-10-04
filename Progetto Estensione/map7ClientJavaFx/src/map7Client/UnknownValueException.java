package map7Client;

/**
 * Eccezione per gestire il caso di acqusizione di valore mancante o fuori range
 * di un attributo di un nuovo esempio da classificare.
 */
public class UnknownValueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Invoca il costruttore della super-classe per settare il messaggio di
	 * errore.
	 * @param message Stringa contenente il messaggio di errore
	 */
	public UnknownValueException(final String message) {
		super(message);
	}
}
