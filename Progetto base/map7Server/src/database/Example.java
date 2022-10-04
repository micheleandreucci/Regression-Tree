package database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Modella una transazione letta dalla base di dati.
 */
public class Example implements Comparable<Example>, Iterable<Object> {
	/**
	 * Una lista di oggetti che modella una transizione da un database.
	 */
	private List<Object> example = new ArrayList<Object>();

	/**
	 * Aggiungi un oggetto 'o' all'esempio.
	 * 
	 * @param o Oggetto aggiunto
	 */
	public void add(Object o) {
		example.add(o);
	}

	/**
	 * Restituisce l'oggetto in posizione i sull'esempio.
	 * 
	 * @param i Indice dell'oggetto da restituire
	 * @return restituisce l'oggetto nella posizione i dall'esempio
	 */
	public Object get(int i) {
		return example.get(i);
	}

	/**
	 * Implementa il metodo compareTo() id Comparable.
	 * 
	 * @param ex e' l'esempio da confrontare con l'esempio corrente
	 * @return 0 se l'esempio corrente e 'ex' sono uguali, altrimenti il metodo
	 *         compareTo() di oggetti nella stessa posizione degli esempio
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(Example ex) {

		int i = 0;
		for (Object o : ex.example) {
			if (!o.equals(this.example.get(i)))
				return ((Comparable<Object>) o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}

	/**
	 * @return la stringa creata dalla concatenazione degli esempi di oggetti
	 *         toString()
	 */
	@Override
	public String toString() {
		String str = "";
		for (Object o : example)
			str += o.toString() + " ";
		return str;
	}

	@Override
	public Iterator<Object> iterator() {
		return null;
	}

}