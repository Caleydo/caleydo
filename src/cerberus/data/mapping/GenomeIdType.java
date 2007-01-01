/**
 * 
 */
package cerberus.data.mapping;


/**
 * @author Michael Kalkusch
 *
 */
public enum GenomeIdType
{

	ACCESSION_NUMBER    ("acc","accession"),
	ENZYME_ID    		("ec","EC_number"),
	ENZYME_CODE  		("ec","EC_number as String"),
	METHOBOLIT   		("ko","methobliot"),
	MICROARRAY   		("IMAGp","Microarray LUT"),
	NCBI_GENEID  		("ncbi-geneid","ncbi-geneid"),
	NCBI_GI      		("ncbi-gi","ncbi-gi"),
	PATHWAY      		("path","pathway-id"),
	KEGG_ID      		("kegg","kegg-id");
	
	private String sName;
	
	private String sDesciption;
	
	private GenomeIdType( String name, String desciption ) {
		sName = name;
		sDesciption = desciption;
	}
	
	/**
	 * @return Returns the desciption.
	 */
	public String getDesciption() {
	
		return sDesciption;
	}

	
	/**
	 * @param desciption The desciption to set.
	 */
	public void setDesciption(String desciption) {
	
		sDesciption = desciption;
	}

	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
	
		return sName;
	}

	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
	
		sName = name;
	}
}
