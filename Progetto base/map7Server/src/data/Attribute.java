package data;

import java.io.Serializable;

/**
 * La classe Attribute modella un generico attributo discreto o continuo.
 */

public abstract class Attribute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Nome simbolico dell'attributo.
	 */
	private String name;
	/**
	 * Identificativo numerico dell'attributo.
	 */
	private int index;

	/**
	 * E' il costruttore di classe. Inizializza i vaori dei membri name e index.
	 * 
	 * @param name  Nome simbolico dell'attributo
	 * @param index Identificativo numerico dell'attributo
	 */
	public Attribute(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/**
	 * Restituisce il valore nel membro name.
	 * 
	 * @return name Nome simbolico dell'attributo di tipo String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Restituisce il valore nel membro index.
	 * 
	 * @return index Identificativo numerico dell'attributo
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return Stringa contenente il nome dell'attributo
	 */
	@Override
	public String toString() {
		return name;
	}

}
