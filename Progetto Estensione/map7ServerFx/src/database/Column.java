package database;

/**
 * Modella lo schema di una colonna all'interno di una tabella del database.
 */
public class Column {
	/**
	 * Nome della colonna.
	 */
	private String name;
	/**
	 * Il tipo identificatore della colonna.
	 */
	private String type;
	
	/**
	 * Costruttore package che avvalora gli attributi name e type.
	 * 
	 * @param name Nome della colonna
	 * @param type Tipo di valore contenuto nella colonna
	 */
	public Column(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Restituisce il nome della colonna.
	 * 
	 * @return name Nome della colonna
	 */
	public String getColumnName() {
		return name;
	}

	/**
	 * Verifica se la tabella contiene un valore numerico , e restituisce un valore
	 * di verita' come risposta.
	 * 
	 * @return True se la colonna contiene un valore numerico , False altrimenti
	 */
	public boolean isNumber() {
		return type.equals("number");
	}

	/**
	 * Restituisce una stringa contenente il nome della tabella e il tipo di valore
	 * contenuto in essa.
	 */
	@Override
	public String toString() {
		return name + ":" + type;
	}
}