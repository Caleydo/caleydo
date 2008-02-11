package org.geneview.core.util.mapping;

import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IGenomeIdManager;


public class GeneAnnotationMapper {

	protected IGeneralManager refGeneralManager;
	
	protected IGenomeIdManager refGenomeIdManager;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	public GeneAnnotationMapper (final IGeneralManager refGeneralManager) {
	
		refGenomeIdManager = refGeneralManager.getSingelton().getGenomeIdManager();
	}
	
	public final String getGeneShortNameByNCBIGeneId(
			String sGeneId) {
		
		// Remove prefix ("hsa:")
		sGeneId = sGeneId.substring(4);
				
		int iGeneId = refGenomeIdManager.getIdIntFromStringByMapping(sGeneId, 
				EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneId == -1)
		{	
			return "N.A.";
		}
		
		return refGenomeIdManager.getIdStringFromIntByMapping(
				iGeneId, EGenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);
	}
	
	public String getAccessionCodeByNCBIGeneIdCode(String sNCBIGeneIdCode) {
		
		// Remove prefix ("hsa:")
		sNCBIGeneIdCode = sNCBIGeneIdCode.substring(4);
		
		int iGeneID = refGenomeIdManager.getIdIntFromStringByMapping(sNCBIGeneIdCode, 
				EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneID == -1)
		{	
			return "invalid";
		}
		
		int iAccessionID = refGenomeIdManager.getIdIntFromIntByMapping(iGeneID, 
				EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
		if (iAccessionID == -1)
		{	
			return "invalid";
		}
		
		return refGenomeIdManager.getIdStringFromIntByMapping(
				iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);

	}
}
