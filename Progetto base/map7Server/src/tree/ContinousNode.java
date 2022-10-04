package tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.Data;
import server.UnknownValueException;

/**
 * Modella l'entità per un nodo corrispondente ad un attributo continuo.
 */
public class ContinousNode extends SplitNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Istanzia un oggetto invocando il costruttore della superclasse SplitNode con
	 * il parametro continuo ContinuousAttribute.
	 * 
	 * @param trainingSet       Training set corrente
	 * @param beginExampleIndex Indice iniziale del sotto-insieme di training
	 * @param endExampleIndex   Indice finale del sotto-insieme di training
	 * @param attribute         Attributo continuo sul quale si definisce lo split
	 * @throws UnknownValueException valore sconosciuto
	 */
	ContinousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)
			throws UnknownValueException {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * Implementazione da class abstract SplitNode. Genera le informazioni
	 * necessarie per ciascuno degli split candidati (in mapSplit[]).
	 * 
	 * @param trainingSet       Training set complessivo
	 * @param beginExampleIndex Indice di inizio del sotto-insieme di training
	 * @param endExampleIndex   Indice di fine del sotto-insieme di training
	 * @param attribute         Attributo indipendente sul quale si definisce lo
	 *                          split
	 * @throws UnknownValueException Generato quando un valore è sconosciuto  		                  
	 */
	@Override
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)
			throws UnknownValueException {
		// Update mapSplit defined in SplitNode -- contiene gli indici del
		// partizionamento
		Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		double bestInfoVariance = 0;
		List<SplitInfo> bestMapSplit = null;

		for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
			final Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
			if (value.doubleValue() != currentSplitValue.doubleValue()) {
				// System.out.print(currentSplitValue +" var ");
				double localVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
				double candidateSplitVariance = localVariance;
				localVariance = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
				candidateSplitVariance += localVariance;
				// System.out.println(candidateSplitVariance);
				if (bestMapSplit == null) {
					bestMapSplit = new ArrayList<SplitInfo>();
					bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
					bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					bestInfoVariance = candidateSplitVariance;
				} else {
					if (candidateSplitVariance < bestInfoVariance) {
						bestInfoVariance = candidateSplitVariance;
						bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
						bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					}
				}
				currentSplitValue = value;
			}
		}
		mapSplit = bestMapSplit;

		try {
			if((mapSplit.get(1).getBeginindex()==mapSplit.get(1).getEndIndex()))
			{
				mapSplit.remove(1);	
			}
		}catch(NullPointerException e) {
				System.err.println("nessun nodo di split creato");
		}
	}

	/**
	 * Implementazione da class abstract SplitNode. Effettua il controllo del valore
	 * di input rispetto agli split di mapSplit[] e restituisce l'identificativo
	 * dello split con cui il test � positivo.
	 * 
	 * @param value Valore continuo dell'attributo che si vuole testare
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 * @return Numero del ramo di split
	 */
	@Override
	int testCondition(Object value) throws UnknownValueException{
		if (((Double) value).doubleValue() <= ((Double) mapSplit.get(0).splitValue).doubleValue()) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Invoca il metodo toString() della super-classe specializzandolo per i
	 * continui.
	 */
	@Override
	public String toString() {
		return "CONTINUOUS " + super.toString();
	}
}
