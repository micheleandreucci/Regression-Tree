package tree;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import data.Attribute;
import data.Data;
import server.UnknownValueException;

/**
 * Classe astratta SplitNode che modella l'astrazione dell'entità nodo di
 * split(continuo o discreto).
 */
abstract class SplitNode extends Node implements Comparable<SplitNode>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Classe SplitInfo che aggrega tutte le informazioni riguardanti un nodo di
	 * split.
	 */
	class SplitInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Valore di tipo Object (di un attributo indipendente) che definisce uno split.
		 */
		Object splitValue;

		/**
		 * Indice di inizio.
		 */
		int beginIndex;

		/**
		 * Indice di fine.
		 */
		int endIndex;

		/**
		 * Numero di split (nodi figli) originanti dal nodo corrente.
		 */
		int numberChild;

		/**
		 * Operatore matematico che definisce il test nel nodo corrente ("=" per valori
		 * discreti).
		 */
		String comparator = "=";

		/**
		 * Costruttore che avvalora gli attributi di classe per split a valori discreti.
		 * 
		 * @param splitValue  Valore dello split
		 * @param beginIndex  L'indice di inizio
		 * @param endIndex    L'indice di fine
		 * @param numberChild Numero di figli
		 */
		SplitInfo(final Object splitValue, final int beginIndex, final int endIndex, final int numberChild) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
		}

		/**
		 * Costruttore che avvalora gli attributi di classe per generici split (da
		 * unsare per valori continui).
		 * 
		 * @param splitValue  Valore dello split
		 * @param beginIndex  L'indice di inizio
		 * @param endIndex    L'indice di fine
		 * @param numberChild Numero di figli
		 * @param comparator  comparatore per confronto
		 */
		SplitInfo(final Object splitValue, final int beginIndex, final int endIndex, final int numberChild,
				final String comparator) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
			this.comparator = comparator;
		}

		/**
		 * Restituisce l'indice di inzio.
		 * 
		 * @return beginIndex L'indice di inizio
		 */
		int getBeginindex() {
			return beginIndex;
		}

		/**
		 * Restituisce l'indice di fine.
		 * 
		 * @return endIndex L'indice di fine
		 */
		int getEndIndex() {
			return endIndex;
		}

		/**
		 * restituisce il valore dello split.
		 * 
		 * @return splitValue Valore dello split.
		 */
		Object getSplitValue() {
			return splitValue;
		}

		/**
		 * Concatena in un oggetto String i valori di beginExampleIndex,
		 * endExampleIndex, splitValue, comparator e restituisce la stringa finale.
		 */
		@Override
		public String toString() {
			return "child " + numberChild + " split value" + comparator + splitValue + "[Examples:" + beginIndex + "-"
					+ endIndex + "]";
		}

		/**
		 * Restituise il valore dell'operatore matematico che definisce il set.
		 * 
		 * @return comparator valore dell'operatore matematico che definisce il set
		 */
		String getComparator() {
			return comparator;
		}

	}

	/**
	 * Oggetto Attribute che modella l'attributo indipendente sul quale lo split �
	 * generato.
	 */
	private Attribute attribute;

	/**
	 * Serve per memorizzare gli split candidati in una struttura di dati di
	 * dimensione pari ai possibili valori di test.
	 */
	protected List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();

	/**
	 * Attributo che contiene il valore di varianza a seguito del partizionamento
	 * indotto dallo split corrente.
	 */
	protected double splitVariance;

	/**
	 * Metodo abstract per generare le informazioni necessarie per ciascuno degli
	 * split candidati.
	 * 
	 * @param trainingSet       Training set complessivo
	 * @param beginExampelIndex Indice iniziale del sotto-insieme di training
	 * @param endExampleIndex   Indice finale del sotto-insieme di training.
	 * @param attribute         Attributo sul quale si definisce lo split
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 */
	abstract void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute)
			throws UnknownValueException;

	/**
	 * Metodo abstract per modellare la condizione di test (ad ogni valore di test
	 * c'è un ramo dello split).
	 * 
	 * @param value Valore dell'attributo che si vuole testare rispetto a tutti gli
	 *              split
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 * @return Numero del ramo di split
	 */
	abstract int testCondition(Object value) throws UnknownValueException;

	/**
	 * Invoca il costruttore della super-classe , ordina i valori dell'attributo di
	 * input per gli esempi contenuti nel sotto-insieme di training compreso tra
	 * beginExampleIndex e endExampleIndex , e sfrutta questo ordinamento per
	 * determinare i possibili split e popolare la lista mapSplit. Computa la
	 * varianza per l'attributo usato nello split sulla base del partizionamento
	 * indotto dallo split.
	 * 
	 * @param trainingSet       Training set complessivo
	 * @param beginExampleIndex Indice iniziale del sotto-insieme di training
	 * @param endExampleIndex   Indice finale del sotto-insieme di training
	 * @param attribute         Attributo indipendente sul quale definire lo split
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 */
	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)
			throws UnknownValueException {
		super(trainingSet, beginExampleIndex, endExampleIndex);
		this.attribute = attribute;
		trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
		setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

		splitVariance = 0;
		for (int i = 0; i < mapSplit.size(); i++) {
			double localVariance = new LeafNode(trainingSet, mapSplit.get(i).getBeginindex(),
					mapSplit.get(i).getEndIndex()).getVariance();
			splitVariance += (localVariance);
		}
	}

	/**
	 * Restituisce l'oggetto per l'attributo usato per lo split.
	 * 
	 * @return attribute Attributo usato per lo split
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * Restituisce l'information gain per lo split corrente.
	 */
	@Override
	public double getVariance() {
		return splitVariance;
	}

	/**
	 * Restistuisce il numero dei rami generati dal nodo corrente.
	 * 
	 * @return dimensione mapSplit
	 */
	public int getNumberOfChildren() {
		return mapSplit.size();
	}

	/**
	 * Restituisce le informazioni per il ramo all'interno della lista mapSplit
	 * indicizzato da child.
	 * 
	 * @param child Indice per la lista mapSplit
	 * @return Informazioni riguardanti il ramo indicizzato da child
	 */
	public SplitInfo getSplitInfo(int child) {
		return mapSplit.get(child);
	}

	/**
	 * Concatena le informazioni di ciascun test(attributo , operatore e valore) in
	 * un oggetto String. La stringa restituita � necessaria per la predizione di
	 * nuovi esempi.
	 * 
	 * @return query Stringa contenente le informazioni di ciascun test
	 */
	public String formulateQuery() {
		String query = "";
		for (int i = 0; i < mapSplit.size(); i++) {
			query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
		}
		return query;
	}

	/**
	 * Confronta i valori di splitVariance dei due nodi e restituisce l'esito.
	 * 
	 * @param o Nodo di split da confrontare con nodo DiscreteNode.
	 * @return Esito di confronto (0: uguali, -1: gain maggiore, 1 gain minore)
	 */
	@Override
	public int compareTo(SplitNode o) {

		if (o.getVariance() == splitVariance) {
			return 0;
		} else if (o.getVariance() > splitVariance) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * Concatena le informazioni di ciascun test (attributo, esempi coperti,
	 * varianza di Split) in una stringa finale.
	 * 
	 * @return v Stringa di informazioni concatenate
	 */
	@Override
	public String toString() {
		String v = "SPLIT : attribute=" + attribute + " " + super.toString() + " Split Variance: " + getVariance()
				+ "\n";

		for (int i = 0; i < mapSplit.size(); i++) {
			v += "\t" + mapSplit.get(i) + "\n";
		}
		return v;
	}
}
