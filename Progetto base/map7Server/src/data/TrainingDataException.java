package data;

/**
 * Eccezione per gestire il caso di acquisizione errata del training set.
 */
public class TrainingDataException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TrainingDataException() {
		super("Impossibile trovare il file specificato");
	}

	/**
	 * Invoca il costruttore della superclasse per inizializzare il messaggio di
	 * errore.
	 * 
	 * @param message Stringa contenente il messaggio di errore.
	 */
	public TrainingDataException(String message) {
		super(message);
	}

}
