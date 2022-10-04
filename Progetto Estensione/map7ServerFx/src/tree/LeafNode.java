package tree;

import java.io.Serializable;

import data.Data;

/**
 * modella l'entita nodo fogliare
 */
public class LeafNode extends Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Valore dell'attributo di classe espresso nella foglia corrente.
	 */
	private Double predictedClassValue;

	/**
	 * Istanzia un oggetto invocando il costruttore della superclasse e avvalora
	 * l'attributo predictedClassValue , effettuando la media dei valori
	 * dell'attributo di classe che ricadono nella partizione.
	 * 
	 * @param trainingSet       Training set complessivo
	 * @param beginExampleIndex Indice iniziale del sotto-insieme di training
	 * @param endExampleIndex   Indice finale del sotto-insieme di training
	 */
	LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		super(trainingSet, beginExampleIndex, endExampleIndex);

		Double somma = 0.0;
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			somma += trainingSet.getClassValue(i);
		}
		Double media = somma / (endExampleIndex - beginExampleIndex + 1);

		predictedClassValue = media;

	}

	/**
	 * Restituisce il numero di split generati dal nodo foglia.
	 * 
	 * @return 0
	 */
	@Override
	public int getNumberOfChildren() {
		return 0;
	}

	/**
	 * Restituisce il valore del membro predictedClassValue.
	 * 
	 * @return predictedClassValue Media dei valori dell'attributo di classe che
	 *         ricadono nella partizione
	 */
	public Double getPredictedClassValue() {
		return predictedClassValue;
	}

	/**
	 * Invoca il metodo della superclasse assegnando anche il valore di classe della
	 * foglia.
	 */
	@Override
	public String toString() {
		return "LEAF class=" + predictedClassValue + " " + super.toString();
	}

}
