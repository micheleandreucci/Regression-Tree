package data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * La classe DiscreteAttribute estende la classe Attribute e rappresenta un
 * attributo discreto.
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Insieme di oggetti String, uno per ciascun valore discreto che l'attributo
	 * può assumere.
	 */
	private Set<String> values = new TreeSet<>();

	/**
	 * Invoca il costruttore della super-classe per avvalorare gli attributi name ed
	 * index , e in seguito assegna al TreeSet values l'insieme di valori ricevuto
	 * in input.
	 * 
	 * @param name   Nome simbolico dell'attributo
	 * @param index  Identidicativo numerico dell'attributo.
	 * @param values Insieme dei valori discreti che l'attributo pu� assumere
	 */
	public DiscreteAttribute(String name, int index, Set<String> values) {
		super(name, index);
		this.values = values;
	}

	/**
	 * Restituisce la cardinalita' dell'insieme values.
	 * 
	 * @return Numero di valori discreti dell'attributo.
	 */
	public int getNumberOfDistinctValues() {
		return values.size();
	}
	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Restituisce l'iteratore per l'insieme di valori.
	 * 
	 * @return Un iteratore per l'insieme dei valori.
	 */
	@Override
	public Iterator<String> iterator() {
		return values.iterator();
	}

}
