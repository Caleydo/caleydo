/**
 * 
 */
package cerberus.data.mapping;

import cerberus.data.collection.StorageType;

/**
 * Note: *_CODE indicates, that is it a String that is mapped to an integer internally.
 * 
 * @author Michael Kalkusch
 *
 */
public enum GenomeIdType
{

	ACCESSION    (StorageType.INT,"acc","accession"),
	ACCESSION_CODE    (StorageType.STRING,"acc","accession_code"),
	ENZYME       (StorageType.INT,"ec","EC_number"),
	ENZYME_CODE  (StorageType.STRING,"ec","EC_number as String"),
	METABOLIT   (StorageType.INT,"ko","methobliot"),
	METABOLIT_CODE   (StorageType.STRING,"ko","methobliot"),
	MICROARRAY   (StorageType.INT,"IMAGp","Microarray LUT"),
	MICROARRAY_CODE   (StorageType.STRING,"IMAGp","Microarray LUT"),
	NCBI_GENEID  (StorageType.INT,"ncbi-geneid","ncbi-geneid"),
	NCBI_GI      (StorageType.INT,"ncbi-gi","ncbi-gi"),
	PATHWAY      (StorageType.INT,"path","pathway-id"),
	KEGG      (StorageType.INT,"kegg","kegg-id"),
	NONE         (StorageType.NONE,"none","none");
	
	
	private final StorageType storageType;
	
	private final String sName;
	
	private final String sDesciption;
	
	private GenomeIdType( final StorageType setStorageType, 
			final String name, 
			final String desciption ) {
		sName = name;
		sDesciption = desciption;
		storageType = setStorageType;
	}
	
	/**
	 * @return Returns the desciption.
	 */
	public String getDesciption() {
	
		return sDesciption;
	}

	/**
	 * Define type of storage required for this type.
	 * 
	 * @return type of storage needed
	 */
	public StorageType getStorageType() {
	
		return storageType;
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
