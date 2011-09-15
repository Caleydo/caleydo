package org.caleydo.datadomain.pathway.manager;

/**
 * Class holds all supported pathway database types.
 * 
 * @author Marc Streit
 */
public enum PathwayDatabaseType {

	KEGG("KEGG", "www.genome.jp/kegg"), BIOCARTA("BioCarta", "www.biocarta.com");

	private String name;

	private String url;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param url
	 */
	private PathwayDatabaseType(String name, String url) {

		this.name = name;
		this.url = url;
	}

	public String getName() {

		return name;
	}

	public String getURL() {

		return url;
	}
}
