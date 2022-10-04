package tree;

import java.io.Serializable;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import server.UnknownValueException;

/**
 * Classe DiscreteNode che modella l'entit� nodo di split relativo ad un
 * attributo indipendente discreto.
 */
public class DiscreteNode extends SplitNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Istanzia un oggetto invocando il costruttore della super-classe SplitNode con
	 * il parametro discreto DiscreteAttribute.
	 * 
	 * @param trainingSet       Training set complessivo
	 * @param beginExampleIndex Indice iniziale del sotto-insieme di training
	 * @param endExampleIndex   Indice finale del sotto-insieme di training
	 * @param attribute         Attributo indipendente sul quale si definiscce lo
	 *                          split
	 * @throws UnknownValueException Generato quando un valore è mancante
	 */
	public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute)
			throws UnknownValueException {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);

	}

	/**
	 * Implementazione da class abstract SplitNode. Istanzia oggetti SplitInfo con
	 * ciascuno dei valori discreti del sotto-insieme di training e popola l'array
	 * mapSplit[].
	 * 
	 * @param trainingSet       Training set complessivo
	 * @param beginExampleIndex Indice di inizio del sotto-insieme di training
	 * @param endExampleIndex   Indice di fine del sotto-insieme di training
	 * @param attribute         Attributo indipendente sul quale si definisce lo
	 *                          split
	 */
	@Override
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {

		Object currentSplitValue = trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());

		int beginSplit = beginExampleIndex;
		int child = 0;
		for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
			if (!currentSplitValue.equals(trainingSet.getExplanatoryValue(i, attribute.getIndex()))) { // == FALSE
				mapSplit.add(new SplitInfo(currentSplitValue, beginSplit, i - 1, child));
				currentSplitValue = trainingSet.getExplanatoryValue(i, attribute.getIndex());
				beginSplit = i;
				child++;
			}
		}
		mapSplit.add(new SplitInfo(currentSplitValue, beginSplit, endExampleIndex, child));
	}

	/**
	 * Implementazione da class abstract SplitNode. Effettua il controllo del valore
	 * di input rispetto a tutti gli split di mapSplit[] e restituisce
	 * l'identificativo dello split con cui il test � positivo.
	 * 
	 * @param value Valore discreto dell'attributo che si vuole testare
	 * @return Numero del ramo di split
	 */
	@Override
	int testCondition(Object value) {

		int i;
		for (i = 0; i < mapSplit.size(); i++) {
			if (mapSplit.get(i).splitValue.equals(value)) {
				break;
			}
		}
		return mapSplit.get(i).numberChild;
	}

	/**
	 * Invoca il metodo toString() della superclasse specializzandolo per i
	 * discreti.
	 */
	@Override
	public String toString() {
		return "DISCRETE " + super.toString();
	}
}