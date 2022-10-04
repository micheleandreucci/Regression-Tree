package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce la connessione al database.
 */
public class DbAccess {
	/**
	 * Driver usato per ottenere la connessione con SQL.
	 */
	private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";// (Per utilizzare questo Driver scaricare e
																		// aggiungere al classpath il connettore mysql
																		// connector)
	/**
	 * Il protocollo usato.
	 */
	private final String DBMS = "jdbc:mysql";
	/**
	 * L'identificatore del server del database.
	 */
	private String SERVER = "localhost";
	/**
	 * Il nome del database.
	 */
	private String DATABASE = "MapDB";
	/**
	 * La porta su cui il DBMS MySQL accetta la connessione.
	 */
	private final String PORT = "3306";
	/**
	 * Il nome utente per fare il login.
	 */
	private String USER_ID = "MapUser";
	/**
	 * La password dell'utente 'MapUser' per fare il login.
	 */
	private String PASSWORD = "map";
	/**
	 * Gestisce una connessione con il database.
	 */
	private Connection conn;

	/**
	 * Impartisce al class loader l'ordine di caricare il driver MySQL , e
	 * inizializza la connessione riferita da conn.
	 * 
	 * @throws DatabaseConnectionException Eccezione sollevata in caso di fallimento
	 *                                     nella connessione al database
	 */
	@SuppressWarnings("deprecation")
	public void initConnection() throws DatabaseConnectionException {
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (ClassNotFoundException e) {
			System.out.println("[!] Driver not found: " + e.getMessage());
			throw new DatabaseConnectionException();
		} catch (InstantiationException e) {
			System.out.println("[!] Error during the instantiation : " + e.getMessage());
			throw new DatabaseConnectionException();
		} catch (IllegalAccessException e) {
			System.out.println("[!] Cannot access the driver : " + e.getMessage());
			throw new DatabaseConnectionException();
		}
		try {
			String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE + "?user=" + USER_ID
					+ "&password=" + PASSWORD + "&serverTimezone=UTC";
			System.out.println("Connection's String: " + connectionString);
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			System.out.println("[!] SQLException: " + e.getMessage());
			System.out.println("[!] SQLState: " + e.getSQLState());
			System.out.println("[!] VendorError: " + e.getErrorCode());
			throw new DatabaseConnectionException();
		}
	}

	/**
	 * Restituisce la connessione stabilita con il database SQL.
	 * 
	 * @return connection Attuale connessione a MySQL.
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * Chiude la connessione con il database SQL.
	 * 
	 * @throws SQLException Un'eccezione che fornisce informazioni su un errore di
	 *                      accesso al database
	 */
	public void closeConnection() throws SQLException {
		conn.close();
	}

}
