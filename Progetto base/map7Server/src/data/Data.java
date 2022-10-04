package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import database.Column;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.Example;
import database.TableData;
import database.TableSchema;

/**
 * La classe Data modella l'insieme di esempi di training.
 */
public class Data {

	/**
	 * Una lista in cui ogni esempio modella una transizione di valori.
	 */
	private List<Example> data = new ArrayList<Example>();

	/**
	 * Una lista che contiene il tipo degli attributi che ogni tupla in data
	 * contiene.
	 */
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	private ContinuousAttribute classAttribute;

	/**
	 * Costruttore pubblico di classe che avvia una connessione con il database , da
	 * dove andrà a recuperare i dati con cui avvalorare il training set e i vari
	 * attributi di classe.
	 * 
	 * @param tableName Nome della tabella del database MapDb dalla quale avvalorare
	 *                  il training set
	 * @throws TrainingDataException Errore nell'apprendimento del training set
	 * @throws SQLException          Errore durante la connessione al database o
	 *                               durante la lettura dei valori
	 * @throws EmptySetException ResultSet vuoto
	 */

	public Data(String tableName) throws TrainingDataException, SQLException, EmptySetException {

		DbAccess db = new DbAccess();

		try {
			db.initConnection();
		} catch (DatabaseConnectionException e) {
			throw new TrainingDataException(e.getMessage());
		}

		TableSchema ts = new TableSchema(db, tableName);

		if (ts.getNumberOfAttributes() == 0) {
			throw new TrainingDataException("La tabella non esiste");
		}

		if (ts.getNumberOfAttributes() < 2)
			throw new TrainingDataException("La tabella ha meno di due colonne.");

		TableData td = new TableData(db);
		Iterator<Column> it = ts.iterator();
		int i = 0;
		while (it.hasNext()) {
			Column column = (Column) it.next();
			if (!column.isNumber() && it.hasNext()) {
				final Set<Object> distinctValues = (TreeSet<Object>) td.getDistinctColumnValues(tableName, column);
				final Set<String> discreteValues = new TreeSet<String>();
				for (Object o : distinctValues) {
					discreteValues.add((String) o);
				}
				explanatorySet.add(new DiscreteAttribute(column.getColumnName(), i, discreteValues));
				i++;
			} else if (it.hasNext() && column.isNumber()) {
				explanatorySet.add(new ContinuousAttribute(column.getColumnName(), i));
				i++;
			} else if (!it.hasNext() && column.isNumber()) {
				classAttribute = new ContinuousAttribute(column.getColumnName(), i);
			} else {
				throw new TrainingDataException(
						"L'attributo corrispondente " + "all'ultima colonna della tabella non e' numerico.");
			}
		}
		// la tabella ha 0 tuple
		try {
			data = td.getTransazioni(tableName);
		} catch (EmptySetException e) {
			throw new TrainingDataException(e.getMessage());
		}
		db.closeConnection();
	}

	/**
	 * Ordina il sottoinsieme di esempi compresi nell'intervallo [inf,sup]
	 * all'interno della lista di esempi data rispetto allo specifico attributo
	 * Attribute. Fa uso dell'algoritmo quicksort per l'ordinamento di un array di
	 * interi con relazione d'ordine minore o uguale. L'array, in questo caso, �
	 * dato dai valori assunti dall'attributo passato in input.
	 * 
	 * @param attribute         Attributo i cui valori devono essere ordinati
	 * @param beginExampleIndex Indice iniziale degli esempi esaminati
	 * @param endExampleIndex   Indice finale degli esempi esaminati
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}

	/**
	 * scambio esempio i con esempi j.
	 * 
	 * @param i esempio
	 * @param j esempio
	 */
	private void swap(int i, int j) {
		Collections.swap(data, i, j);
	}

	/**
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di
	 * separazione.
	 * 
	 * @param attribute Attributo discreto
	 * @param inf       Posizione inferiore
	 * @param sup       Posizione superiore
	 * @return j Punto di separazione
	 */
	private int partition(DiscreteAttribute attribute, int inf, int sup) {
		int i, j;

		i = inf;
		j = sup;
		int med = (inf + sup) / 2;
		String x = (String) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);

		while (true) {
			while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
				i++;
			}

			while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				j--;
			}

			if (i < j) {
				swap(i, j);
			} else {
				break;
			}
		}
		swap(inf, j);
		return j;

	}

	/**
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di
	 * separazione.
	 * 
	 * @param attribute Attributo continuo
	 * @param inf       Posizione inferiore
	 * @param sup       Posizione superiore
	 * @return j Punto di separazione
	 */
	private int partition(ContinuousAttribute attribute, final int inf, final int sup) {
		int i;
		int j;

		i = inf;
		j = sup;
		int med = (inf + sup) / 2;
		Double x = (Double) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);

		while (true) {
			while (i <= sup && ((Double) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
				i++;
			}
			while (((Double) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				j--;
			}
			if (i < j) {
				swap(i, j);
			} else {
				break;
			}
		}
		swap(inf, j);
		return j;

	}

	/**
	 * Algoritmo quicksort per l'ordinamento di un array di interi A usando come
	 * relazione d'ordine totale il minore o uguale.
	 * 
	 * @param attribute Attributo i cui valori devono essere ordinati
	 * @param inf       Posizione inferiore
	 * @param sup       Posizione superiore
	 * 
	 */
	private void quicksort(Attribute attribute, int inf, int sup) {

		if (sup >= inf) {
			int pos;
			if (attribute instanceof DiscreteAttribute) {
				pos = partition((DiscreteAttribute) attribute, inf, sup);
			} else {
				pos = partition((ContinuousAttribute) attribute, inf, sup);
			}

			if ((pos - inf) < (sup - pos + 1)) {
				quicksort(attribute, inf, pos - 1);
				quicksort(attribute, pos + 1, sup);
			} else {
				quicksort(attribute, pos + 1, sup);
				quicksort(attribute, inf, pos - 1);
			}
		}
	}

	/**
	 * Restistuisce la cardinalità dell'insieme di esempi.
	 * 
	 * @return Cardinalità dell'insieme di esempi
	 */
	public int getNumberofExamples() {
		return data.size();
	}

	/**
	 * Restituisce la dimensione della lista di attributi explanatorySet.
	 * 
	 * @return Cardinalit� dell'insieme degli attributi indipendenti
	 */
	public int getNumberOfExplanatoryAttributes() {
		return explanatorySet.size();
	}

	/**
	 * Restituisce il valore dell'attributo di classe per l'esempio exampleIndex.
	 * 
	 * @param exampleIndex Iindice di posizione all'interno della lista
	 * @return valore dell'attributo di classe per l'esempio exampleIndex
	 */
	public Double getClassValue(int exampleIndex) {
		return (Double) data.get(exampleIndex).get(classAttribute.getIndex());
	}

	/**
	 * Restituisce il valore dell'attributo indicizzato da attributeIndex per
	 * l'esempio exampleIndex all'interno della lista.
	 * 
	 * @param exampleIndex   Indice di posizione all'interno della lista
	 * @param attributeIndex Iindice di posizione dell'attributo contenuto
	 *                       all'interno della lista explanatorySet
	 * @return oggetto associato all'attributo indipendente per l'esempio
	 *         exampleIndex
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
		return data.get(exampleIndex).get(explanatorySet.get(attributeIndex).getIndex());
	}

	/**
	 * Restituisce l'attributo indicizzato da index all'interno della lista
	 * explanatorySet.
	 * 
	 * @param index Indice nella lista explanatorySet per uno specifico attributo
	 *              indipendente
	 * @return oggetto di tipo Attribute indicizzato da index
	 */
	public Attribute getExplanatoryAttribute(final int index) {
		return explanatorySet.get(index);
	}

	/**
	 * Restituisce l'oggetto corrispondente all'attributo di classe.
	 * 
	 * @return oggetto ContinuousAttribute associato al membro classAttribute
	 */
	public ContinuousAttribute getClassAttribute() {
		return classAttribute;
	}

	/**
	 * Legge i valori di tutti gli attributi per ogni singolo esempio contenuto
	 * nella lista di esempi data , e li concatena in un ogetto String che viene
	 * restituito come risultato finale in forma di sequenza di testi.
	 * 
	 * @return una stringa che modella lo stato dell'oggetto come matrice con righe
	 *         e colonne enumerate dal nome dell'attributo
	 */
	@Override
	public String toString() {
		String value = "";
		for (int i = 0; i < getNumberofExamples(); i++) {
			value += "[" + i + "]\t";
			for (int j = 0; j < explanatorySet.size(); j++) {
				value += data.get(i).get(j);
			}
			value += data.get(i).get(explanatorySet.size());
		}
		return value;
	}
}
