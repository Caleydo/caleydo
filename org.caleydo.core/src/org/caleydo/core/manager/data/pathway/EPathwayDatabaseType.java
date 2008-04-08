package org.caleydo.core.manager.data.pathway;

/**
 * Class holds all supported pathway database types.
 * 
 * @author Marc Streit
 *
 */
public enum EPathwayDatabaseType {
	
	KEGG ("KEGG", "www.genome.jp/kegg"),
	BIOCARTA ("BioCarta","www.biocarta.com");
	
	private String sName;
	
	private String sURL;
	
	/**
	 * Constructor. 
	 * 
	 * @param sName
	 * @param sURL
	 */
	private EPathwayDatabaseType(String sName, String sURL) {
		
		this.sName = sName;
		this.sURL = sURL;
	}
	
	public String getName() {
		return sName;
	}
	
	public String getURL() {
		return sURL;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return sName;
	}
}
