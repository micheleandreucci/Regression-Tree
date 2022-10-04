package tree;

import java.io.Serializable;

import data.Data;

/**
 * Classe astratta Node che modella l'astrazione dell'entit� nodo dell'albero di decisione.
 */
abstract class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Contatore dei nodi generati nell'albero.
	 */
	private static int idNodeCount = 0;

	/**
	 * Identificativo numerico del nodo.
	 */
	private int idNode;

	/**
	 * Indice del training set del primo esempio coperto dal nodo corrente.
	 */
	private int beginExampleIndex;

	/**
	 * Indice del training set dell'ultimo esempio coperto dal nodo corrente.
	 */
	private int endExampleIndex;

	/**
	 * Valore dello SSE calcolato, rispetto all'attributo di classe, nel sotto-insieme di training del nodo.
	 */
	private double variance;

	/**
	 * Avvolara gli attributi primitivi di classe, inclusa la varianza che
	 * viene calcolata rispetto all'attributo di classe nel sotto-insieme di
	 * training coperto dal nodo.
	 * @param trainingSet Oggetto di classe Data contenente il training set completo.
	 * @param beginExampleIndex Indice iniziale del sotto-insieme di training coperto dal nodo
	 *        corrente.
	 * @param endExampleIndex Indice finale del sotto-insieme di training coperto dal nodo
	 *        corrente.
	 */
	Node(final Data trainingSet, int beginExampleIndex, int endExampleIndex) {

		idNode = idNodeCount++;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;

		double somma = 0;

		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			somma += trainingSet.getClassValue(i);
		}

		final double media = somma / (endExampleIndex - beginExampleIndex + 1);

		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			variance += Math.pow(trainingSet.getClassValue(i) - media, 2);
		}

	}

	/**
	 * Restituisce il valode del membro idNode.
	 * @return idNode Identificativo numerico del nodo
	 */
	int getNode() {
		return idNode;
	}

	/**
	 * Restistuisce il valore del membro beginExampleIndex.
	 * @return beginExampleIndex Indice del primo esempio nel sotto-insieme rispetto al training
	 *         set complessivo
	 */
	int getBeginExampleIndex() {
		return beginExampleIndex;
	}

	/**
	 * Restituisve il valore del membro endExampleIndex.
	 * @return endExampleIndex Indice dell'ultimo esempio nel sotto-insieme rispetto al training
	 *         set complessivo
	 */
	int getEndExampleIndex() {
		return endExampleIndex;
	}

	/**
	 * Restituise il valore del membro variance.
	 * @return variance Valore dello SSE dell'attributo da predire rispetto al nodo corrente
	 */
	double getVariance() {
		return variance;
	}

	/**
	 * E' un metodo astratto la cui implementazione riguarda i nodi di tipo test
	 * (split node) dai quali si possono generare figli , uno per ogni split
	 * prodotto. Restituisce il numero di tali nodi figli.
	 * @return Valore del numero di nodi sottostanti
	 */
	abstract int getNumberOfChildren();


	/**
	 * Concatena in un oggetto String i valori di beginExampleIndex,
	 * endExampleIndex e variance e restituisce la stringa finale.
	 */
	@Override
	public String toString() {
		return "Nodo: [Examples:" + getBeginExampleIndex() + "-" + getEndExampleIndex() + "] variance: "
				+ this.variance;
	}

}
