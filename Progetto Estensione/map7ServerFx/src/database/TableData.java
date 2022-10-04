package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Recupera i dati dal database.
 */
public class TableData {
	/**
	 * L'oggetto di accesso al database.
	 */
	private DbAccess db;

	/**
	 * Inizializza db.
	 * 
	 * @param db L'accesso al database
	 */
	public TableData(DbAccess db) {
		this.db = db;
	}

	/**
	 * Ricava lo schema della tabella con nome table.
	 * 
	 * @param table Nome della tabella all'interno del database
	 * @return transSet Lista di transazioni memorizzati nella tabella
	 * @throws SQLException      Eccezione in presenza di errori nella esecuzione
	 *                           della query
	 * @throws EmptySetException Eccezione lanciata se il resultset e' vuoto
	 */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema = new TableSchema(db, table);
		String query = "select ";
		for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
			Column c = tSchema.getColumn(i);
			if (i > 0)
				query += ",";
			query += c.getColumnName();
		}
		if (tSchema.getNumberOfAttributes() == 0)
			throw new SQLException();
		query += (" FROM " + table);
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;
		while (rs.next()) {
			empty = false;
			Example currentTuple = new Example();
			for (int i = 0; i < tSchema.getNumberOfAttributes(); i++)
				if (tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i + 1));
				else
					currentTuple.add(rs.getString(i + 1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if (empty)
			throw new EmptySetException();
		return transSet;
	}

	/**
	 * Formula ed esegue una interrogazione SQL per estrarre i valori distinti
	 * ordinati di column e popolare un insieme da restituire.
	 * 
	 * @param table  Nome della tabella nel database
	 * @param column Nome della colonna nella tabella
	 * @return Insieme di valori distinti ordinati in modalita' ascendente che
	 *         l'attributo identificato da nome column assume nella tabella
	 *         identificata dal nome table
	 * @throws SQLException      Eccezione lanciata in presenza di errori nella
	 *                           query
	 * @throws EmptySetException Eccezione lanciata se il resultset e' vuoto
	 */
	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException, EmptySetException {
		TreeSet<Object> distinctValues = null;
		TableSchema tSchema = new TableSchema(db, table);
		distinctValues = new TreeSet<Object>();
		String query = "SELECT DISTINCT ";
		Statement statement;
		if (tSchema.getNumberOfAttributes() == 0)
			throw new SQLException();
		query += (column.getColumnName() + " FROM " + table + " " + " ORDER BY " + column.getColumnName() + " ASC");
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		while (rs.next()) {
			if (column.isNumber())
				distinctValues.add(rs.getDouble(1));
			else
				distinctValues.add(rs.getString(1));
		}
		rs.close();
		statement.close();
		return distinctValues;
	}

	public enum QUERY_TYPE {
		/**
		 * aggiungi 'min' dopo 'select' nella query.
		 */
		MIN,
		/**
		 * aggiungi 'max' dopo 'select' nella query.
		 */
		MAX
	}
}
