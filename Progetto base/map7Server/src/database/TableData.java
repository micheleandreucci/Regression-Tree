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
	 * @param db L'accesso al database
	 */
	public TableData(final DbAccess db) {
		this.db = db;
	}

	/**
	 * Ricava lo schema della tabella con nome table.
	 * @param table Nome della tabella all'interno del database
	 * @return transSet Lista di transazioni memorizzati nella tabella
	 * @throws SQLException Eccezione in presenza di errori nella esecuzione della query
	 * @throws EmptySetException Eccezione lanciata se il resultset è vuoto
	 */

	public List<Example> getTransazioni(final String table)
			throws SQLException, EmptySetException {

		final LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		final TableSchema tSchema = new TableSchema(db, table);
		String query = "select ";

		for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
			final Column c = tSchema.getColumn(i);
			if (i > 0) {
				query += ",";
			}
			query += c.getColumnName();
		}
		query += " FROM " + table;

		statement = db.getConnection().createStatement();
		final ResultSet rs = statement.executeQuery(query);
		boolean empty = true;

		while (rs.next()) {
			empty = false;
			final Example currentTuple = new Example();
			for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
				if (tSchema.getColumn(i).isNumber()) {
					currentTuple.add(rs.getDouble(i + 1));
				} else {
					currentTuple.add(rs.getString(i + 1));
				}
			}
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if (empty) {
			throw new EmptySetException("Il resultset restituito � vuoto");
		}
		return transSet;
	}

	/**
	 * Formula ed esegue una interrogazione SQL per estrarre i valori distinti
	 * ordinati di column e popolare un insieme da restituire.
	 * @param table Nome della tabella nel database
	 * @param column Nome della colonna nella tabella
	 * @return Insieme di valori distinti ordinati in modalit� ascendente che
	 *         l'attributo identificato da nome column assume nella tabella
	 *         identificata dal nome table
	 * @throws SQLException Eccezione lanciata in presenza di errori nella query
	 */
	public Set<Object> getDistinctColumnValues(final String table, final Column column)
			throws SQLException {

		final Set<Object> valueSet = new TreeSet<Object>();
		Statement statement;
		String query = "select distinct ";
		query += column.getColumnName();
		query += " from " + table;
		query += " order by " + column.getColumnName();
		statement = db.getConnection().createStatement();
		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
				if (column.isNumber()) {
					valueSet.add(rs.getDouble(1));
				} else {
					valueSet.add(rs.getString(1));
				}
		}
		rs.close();
		statement.close();
		return valueSet;
	}
}


