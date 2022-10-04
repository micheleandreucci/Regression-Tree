package tree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;
import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import data.TrainingDataException;
import server.UnknownValueException;

/**
 * per modellare l'entità l'intero albero di decisione come insieme di
 * sotto-alberi
 */
public class RegressionTree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Radice del sotto-albero corrente.
	 */
	Node root;
	/**
	 * Array di sotto alberi originati nel nodo root: vi è un elemento nell'array
	 * per ogni figlio del nodo.
	 */
	RegressionTree childTree[];

	/**
	 * Istanzia un sotto-albero dell'intero albero.
	 */
	public RegressionTree() {
	}

	/**
	 * Istanzia un sotto-albero dell'intero albero e avvia l'induzione dell'albero
	 * dagli esempi di training in input.
	 * 
	 * @param trainingSet Training set complessivo
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 * @throws TrainingDataException File specificato non trovato
	 */
	public RegressionTree(Data trainingSet) throws UnknownValueException, TrainingDataException {
		learnTree(trainingSet, 0, trainingSet.getNumberofExamples() - 1, trainingSet.getNumberofExamples() * 10 / 100);
	}

	/**
	 * Genera un sotto-albero con il sotto-insieme di input istanziando un nodo
	 * fogliare o un nodo di split. In tal caso determina il miglior nodo rispetto
	 * al sotto-insieme di input, ed a tale nodo esso associa un sotto-albero avente
	 * radice il nodo medesimo e avente un numero di rami pari al numero dei figli
	 * determinati dallo split. Ricorsivamente ogni oggetto RegressionTree in
	 * childTree[] sarà re-invocato il metodo learnTree() per l'apprendimento su un
	 * insieme del ridotto del sotto-insieme attuale. Nella condizione in cui il
	 * nodo di split non origina figli , il nodo diventa fogliare.
	 * 
	 * @param trainingSet             Training set complessivo
	 * @param begin                   Indice iniziale del sotto-insieme di training
	 * @param end                     Indice finale del sotto-insieme di training
	 * @param numberOfExamplesPerLeaf Numero massimo che una foglia deve contenere
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 * @throws TrainingDataException File specificato non trovato
	 */
	public void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf)
			throws UnknownValueException, TrainingDataException {
		if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
			// determina la classe che compare più frequentemente nella partizione corrente
			root = new LeafNode(trainingSet, begin, end);
		} else // split node
		{
			root = determineBestSplitNode(trainingSet, begin, end);
			if (root.getNumberOfChildren() > 1) {
				childTree = new RegressionTree[root.getNumberOfChildren()];
				for (int i = 0; i < root.getNumberOfChildren(); i++) {
					try {
						childTree[i] = new RegressionTree();
						childTree[i].learnTree(trainingSet, ((SplitNode) root).getSplitInfo(i).getBeginindex(),
								((SplitNode) root).getSplitInfo(i).getEndIndex(), numberOfExamplesPerLeaf);
						if (childTree[i] == null) {
							throw new NullPointerException();
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				}
			} else
				root = new LeafNode(trainingSet, begin, end);
		}
	}

	/**
	 * Verifica se il sotto-insieme corrente può essere coperto da un nodo foglia
	 * controllando la cardinalità di tale sotto-insieme.
	 * 
	 * @param trainingSet             Training set complessivo
	 * @param begin                   Indice iniziale del sotto-insieme di training
	 * @param end                     Indice finale del sotto-insieme di training
	 * @param numberOfExamplesPerLeaf Numero minimo che una foglia deve contenere
	 * @return False se la dimensione del sotto-insieme di training è maggiore del
	 *         numero minimo , True nel caso contrario
	 */
	private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		boolean leaf = true;
		if ((end - begin) > numberOfExamplesPerLeaf) {
			for (int i = begin + 1; i <= end && leaf == true; i++) {
				if (!trainingSet.getClassValue(begin).equals(trainingSet.getClassValue(i))) {
					leaf = false;
				}
			}
		}
		return leaf;
	}

	/**
	 * Istanzia un DiscreteAttribute su ciascun attributo indipendente e ne computa
	 * la varianza a seguito dello split. Il nodo con varianza minore tra quelli
	 * istanziati viene restituito.
	 * 
	 * @param trainingSet Training set complessivo
	 * @param begin       Indice iniziale del sotto-insieme di training
	 * @param end         Indice finale del sotto-insieme di training
	 * @return Nodo migliore di split per il sotto-insieme di training corrente
	 * @throws UnknownValueException Generato quado un valore è sconosciuto
	 * @throws TrainingDataException File specificato non trovato
	 */
	private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end)
			throws UnknownValueException, TrainingDataException {
		TreeSet<SplitNode> v = new TreeSet<SplitNode>(); // TreeSet di supporto, avvalorato successivamente con gli
															// attributi discreti
		// avvaloramento del TreeSet
		for (int i = 0; i < trainingSet.getNumberofExplanatoryAttributes(); i++) {
			SplitNode temp = null;
			Attribute a = trainingSet.getExplanatoryAttribute(i);
			try {
				if (a instanceof DiscreteAttribute) {
					DiscreteAttribute attribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);
					temp = new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) attribute);
				} else {
					if (a instanceof ContinuousAttribute) {
						ContinuousAttribute attribute = (ContinuousAttribute) trainingSet.getExplanatoryAttribute(i);
						temp = new ContinousNode(trainingSet, begin, end, (ContinuousAttribute) attribute);
					}
				}
				v.add(temp);
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.err.println("NullPointerException");
			}
		}
		trainingSet.sort(v.first().getAttribute(), begin, end);
		return v.first();
	}

	/**
	 * Comportamento: Concatena in una String tutte le informazioni di
	 * root-childTree[] correnti invocando i relativi metodo toString(): nel caso il
	 * root corrente è di split vengono concatenate anche le informazioni dei rami.
	 * Fa uso di instanceof per riconoscere se root è SplitNode o LeafNode.
	 */
	@Override
	public String toString() {
		String tree = root.toString() + "\n";
		if (root instanceof LeafNode) {
		} else { // split node
			for (int i = 0; i < childTree.length; i++)
				tree += childTree[i];
		}
		return tree;
	}

	/**
	 * Invoca il metodo toString() per la visualizzazione dell'albero di
	 * regressione.
	 */
	public void printTree() {
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}

	/**
	 * Scandisce ciascun ramo dell'albero completo dalla radice alla foglia
	 * concatenando le informazioni dei nodi di split al nodo foglia. In particolare
	 * per ogni sotto-albero in childTree[] concatena le informazioni del nodo root:
	 * se è di split discende ricorsivamente l'albero per ottenere le informazioni
	 * del nodo sottostante di ogni ramo-regola, se è foglia(leaf) termina
	 * l'attraversamento visualizzando la regola.
	 */
	public void printRules() {
		System.out.println("********** RULES *********");
		String rules = "";

		if (root instanceof LeafNode) {
			System.out.println("==> Class= " + ((LeafNode) root).getPredictedClassValue() + "\n");
		} else if (root instanceof DiscreteNode) {
			for (int i = 0; i < ((SplitNode) root).mapSplit.size(); i++) {
				rules += ((SplitNode) root).getAttribute().getName() + "="
						+ ((SplitNode) root).mapSplit.get(i).getSplitValue() + " ";
				childTree[i].printRules(rules); // entra nella funzione e poi riprende nel for
				rules = "";
			}
		} else if (root instanceof ContinousNode) {
			for (int i = 0; i < ((SplitNode) root).mapSplit.size(); i++) {
				rules += ((SplitNode) root).getAttribute().getName()
						+ ((SplitNode) root).mapSplit.get(i).getComparator()
						+ ((SplitNode) root).mapSplit.get(i).getSplitValue() + " ";
				childTree[i].printRules(rules); // entra nella funzione e poi riprende nel for
				rules = "";
			}
		}
		System.out.println("*************************\n");
	}

	/**
	 * Supporta il metodo public void printRules(). Concatena alle informazioni in
	 * current del precedente nodo quelle del nodo root del corrente sotto-albero:
	 * se il nodo corrente è di split il metodo viene invocato ricorsivamente con
	 * current e le informazioni del nodo corrente, se è una foglia visualizza tutte
	 * le informazioni concatenate.
	 * 
	 * @param current Informazione del nodo di split del sotto-albero al livello
	 *                superiore
	 */
	private void printRules(String current) {

		String curr;
		if (root instanceof LeafNode) {
			System.out.println(current + "==>Class= " + ((LeafNode) root).getPredictedClassValue() + "");
		} else if (root instanceof DiscreteNode) {
			current += "AND " + ((SplitNode) root).getAttribute().getName();
			for (int i = 0; i < ((SplitNode) root).mapSplit.size(); i++) {
				curr = "";
				curr = current + ((SplitNode) root).mapSplit.get(i).getSplitValue() + " ";
				childTree[i].printRules(curr);
			}
		} else if (root instanceof ContinousNode) {
			current += "AND " + ((SplitNode) root).getAttribute().getName();
			for (int i = 0; i < ((SplitNode) root).mapSplit.size(); i++) {
				curr = "";
				curr = current + ((SplitNode) root).mapSplit.get(i).getComparator()
						+ ((SplitNode) root).mapSplit.get(i).getSplitValue() + " ";
				childTree[i].printRules(curr);
			}
		}
	}

	/**
	 * Invia al client le informazioni di ciascuno split dell'albero e per il
	 * corrispondente attributo acquisisce il valore da predire inviato dal
	 * client.Se il nodo root corrente è foglia termina l'acquisizione e invia al
	 * client la predizione per l'attributo di classe, altrimenti invoca
	 * ricorsivamente sul figlio di root in childTree[] individuato dal valore
	 * inviato dal client.
	 * 
	 * @param in  Stream di ingresso
	 * @param out Stream di uscita
	 * @return Oggetto Double contenente il valore di classe predetto per l'esempio
	 *         acquisito.
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 * @throws IOException Geneato quando si verifica un errore I/O
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 */
	public Double predictClass(ObjectInputStream in, ObjectOutputStream out)
			throws ClassNotFoundException, UnknownValueException, IOException {
		Double predictedValue = 0.0;

		try {
			if (root instanceof LeafNode) {
				predictedValue = ((LeafNode) root).getPredictedClassValue();

			} else {
				out.writeObject("QUERY");
				out.writeObject(((SplitNode) root).formulateQuery());

				int choice = 0;
				try {
					choice = ((Integer) in.readObject()).intValue();
				} catch (IOException e) {
					e.printStackTrace();
				}

				while (choice >= root.getNumberOfChildren() || choice < 0) {
					out.writeObject("err");
					out.writeObject(" The answer should be an integer between 0 and "
							+ (((SplitNode) root).getNumberOfChildren() - 1) + " ! ");
					choice = ((Integer) in.readObject()).intValue();
				}
				predictedValue = childTree[choice].predictClass(in, out);
			}
			return predictedValue;

		} catch (IOException e) {
			e.printStackTrace();
			return predictedValue;
		}
	}

	/**
	 * Carica un albero di regressione salvato in un file.
	 * 
	 * @param nomeFile Nome del file in cui è salvato l'albero
	 * @return n Albero contenuto nel file
	 * @throws FileNotFoundException  Generato quando si apre il file indicato da un
	 *                                percorso specificato che non è riuscito.
	 * @throws IOException            Geneato quando si verifica un errore I/O
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 */
	public static RegressionTree carica(String nomeFile)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeFile));
		RegressionTree n = (RegressionTree) in.readObject();
		in.close();
		return n;
	}

	/**
	 * Serializza l'albero in un file.
	 * 
	 * @param nomeFile Nome del file in cui salvare l'albero
	 * @throws FileNotFoundException Generato quando si apre il file indicato da un
	 *                               percorso specificato che non è riuscito
	 * @throws IOException           Geneato quando si verifica un errore I/O
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeFile));
		out.writeObject(this);
		out.close();
	}
}
