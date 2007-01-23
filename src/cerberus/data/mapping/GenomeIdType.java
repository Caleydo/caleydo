/**
 * 
 */
package cerberus.data.mapping;


/**
 * Note: *_CODE indicates, that is it a String that is mapped to an integer internally.
 * 
 * @author Michael Kalkusch
 *
 */
public enum GenomeIdType
{

	ACCESSION    ("acc","accession"),
	ACCESSION_CODE    ("acc","accession_code"),
	ENZYME       ("ec","EC_number"),
	ENZYME_CODE  ("ec","EC_number as String"),
	METABOLIT   ("ko","methobliot"),
	MICROARRAY   ("IMAGp","Microarray LUT"),
	NCBI_GENEID  ("ncbi-geneid","ncbi-geneid"),
	NCBI_GI      ("ncbi-gi","ncbi-gi"),
	PATHWAY      ("path","pathway-id"),
	KEGG_ID      ("kegg","kegg-id"),
	NONE         ("none","none");
	
	private final String sName;
	
	private final String sDesciption;
	
	private GenomeIdType( final String name, final String desciption ) {
		sName = name;
		sDesciption = desciption;
	}
	
	/**
	 * @return Returns the desciption.
	 */
	public String getDesciption() {
	
		return sDesciption;
	}

	
//	/**
//	 * @param desciption The desciption to set.
//	 */
//	public void setDesciption(String desciption) {
//	
//		sDesciption = desciption;
//	}

	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
	
		return sName;
	}

	
//	/**
//	 * @param name The name to set.
//	 */
//	public void setName(String name) {
//	
//		sName = name;
//	}
}
