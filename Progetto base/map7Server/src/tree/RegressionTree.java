package tree;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import data.TrainingDataException;
import server.UnknownValueException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;

/**
 * Classe RegressionTree che modella l'entit� dell'intero albero di decisione
 * come insieme di sotto-alberi.
 */
public class RegressionTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Radice del sotto-albero corrente.
	 */
	private Node root;

	/**
	 * Array di sotto alberi originati nel nodo root: vi � un elemento nell'array
	 * per ogni figlio del nodo.
	 */
	private RegressionTree[] childTree;

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
	 * @throws UnknownValueException Generato quando un valore è mancante
	 * @throws TrainingDataException Generato quando il file specificato è mancante/errato
	 */
	public RegressionTree(final Data trainingSet) throws UnknownValueException, TrainingDataException {
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
	 * @throws TrainingDataException Generato quando i dati per l'addestramento sono mancanti
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
	 * Verifica se il sotto-insieme corrente pu� essere coperto da un nodo foglia
	 * controllando la cardinalit� di tale sotto-insieme.
	 * 
	 * @param trainingSet             Training set complessivo
	 * @param begin                   Indice iniziale del sotto-insieme di training
	 * @param end                     Indice finale del sotto-insieme di training
	 * @param numberOfExamplesPerLeaf Numero minimo che una foglia deve contenere
	 * @return False se la dimensione del sotto-insieme di training � maggiore del
	 *         numero minimo , True nel caso contrario
	 */
	boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		boolean leaf = true;
		if ((end - begin) > numberOfExamplesPerLeaf) {
			for (int i = begin + 1; i <= end && leaf == true; i++) { // x==true
				if (!trainingSet.getClassValue(begin).equals(trainingSet.getClassValue(i))) { // == false
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
	 * @throws UnknownValueException Generato quando un valore è sconosciuto
	 * @throws TrainingDataException Generato quando mancano i dati per l'addestramento
	 */
	private SplitNode determineBestSplitNode(Data trainingSet, final int begin, final int end)
			throws UnknownValueException, TrainingDataException {

		TreeSet<SplitNode> v = new TreeSet<SplitNode>();

		for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
			SplitNode temp = null;
			Attribute a = trainingSet.getExplanatoryAttribute(i);
			try {
				if (a instanceof DiscreteAttribute) {
					DiscreteAttribute attribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);
					temp = new DiscreteNode(trainingSet, begin, end, attribute);
				} else {
					if (a instanceof ContinuousAttribute) {
						ContinuousAttribute attribute = (ContinuousAttribute) trainingSet.getExplanatoryAttribute(i);
						temp = new ContinousNode(trainingSet, begin, end, attribute);
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
	 * Concatena in una String tutte le informazioni di root e childTree[] correnti
	 * invocando i relativi metodi toString(). Nel caso il root corrente � di split
	 * vengono concatenate anche le informazioni dei rami.
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
	 * se � di split discende ricorsivamente l'albero per ottenere le informazioni
	 * del nodo sottostante di ogni ramo-regola, se � di foglia(leaf) termina
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
	 * se il nodo corrente � di split il metodo viene invocato ricorsivamente con
	 * current e le informazioni del nodo corrente, se � di foglio visualizza tutte
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
	 * client.Se il nodo root corrente � foglia termina l'acquisizione e invia al
	 * client la predizione per l'attributo di classe, altrimenti invoca
	 * ricorsivamente sul figlio di root in childTree[] individuato dal valore
	 * inviato dal client.
	 * 
	 * @param in  Stream di ingresso
	 * @param out Stream di uscita
	 * @return Oggetto Double contenente il valore di classe predetto per l'esempio
	 *         acquisito.
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 * @throws UnknownValueException  Generato quando un valore è sconosciuto
	 * @throws IOException            Geneato quando si verifica un errore I/O
	 */
	public Double predictClass(ObjectInputStream in, ObjectOutputStream out)
			throws ClassNotFoundException, UnknownValueException, IOException {

		if (root instanceof LeafNode) {
			return ((LeafNode) root).getPredictedClassValue();

		} else {
			out.writeObject("QUERY");
			out.writeObject(((SplitNode) root).formulateQuery());

			int choice = 0;
			try {
				choice = ((Integer) in.readObject()).intValue();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (choice >= root.getNumberOfChildren() || choice == -1) {
				// out.writeObject("err");
				out.writeObject(" The answer should be an integer between 0 and "
						+ (((SplitNode) root).getNumberOfChildren() - 1) + " ! ");
				choice = ((Integer) in.readObject()).intValue();
				throw new UnknownValueException(" The answer should be an integer between 0 and "
						+ (((SplitNode) root).getNumberOfChildren() - 1) + " ! ");

			} else
				return childTree[choice].predictClass(in, out);
		}

	}

	/**
	 * Carica un albero di regressione salvato in un file.
	 * 
	 * @param nomeFile Nome del file in cui è salvato l'albero
	 * @return n Albero contenuto nel file
	 * @throws FileNotFoundException  Generato quando si apre il file indicato da un
	 *                                percorso specificato che non � riuscito.
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
	 *                               percorso specificato che non � riuscito
	 * @throws IOException           Geneato quando si verifica un errore I/O
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeFile));
		out.writeObject(this);
		out.close();
	}
}