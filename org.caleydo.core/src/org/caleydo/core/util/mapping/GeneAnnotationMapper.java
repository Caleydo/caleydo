package org.caleydo.core.util.mapping;

import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IGenomeIdManager;


public class GeneAnnotationMapper {

	protected IGeneralManager generalManager;
	
	protected IGenomeIdManager genomeIDManager;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	public GeneAnnotationMapper (final IGeneralManager generalManager) {
	
		genomeIDManager = generalManager.getGenomeIdManager();
	}
	
	public final String getGeneShortNameByNCBIGeneId(
			String sGeneId) {
		
		// Remove prefix ("hsa:")
		sGeneId = sGeneId.substring(4);
				
		int iGeneId = genomeIDManager.getIdIntFromStringByMapping(sGeneId, 
				EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneId == -1)
		{	
			return "N.A.";
		}
		
		return genomeIDManager.getIdStringFromIntByMapping(
				iGeneId, EGenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);
	}
	
	public final String getGeneShortNameByAccession(int iAccession)
	{
		int sNcbiID = genomeIDManager.getIdIntFromIntByMapping(iAccession, EGenomeMappingType.ACCESSION_2_NCBI_GENEID);						
		return genomeIDManager.getIdStringFromIntByMapping(sNcbiID, EGenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);
	
	}
	
	public String getAccessionCodeByNCBIGeneIdCode(String sNCBIGeneIdCode) {
		
		// Remove prefix ("hsa:")
		sNCBIGeneIdCode = sNCBIGeneIdCode.substring(4);
		
		int iGeneID = genomeIDManager.getIdIntFromStringByMapping(sNCBIGeneIdCode, 
				EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneID == -1)
		{	
			return "invalid";
		}
		
		int iAccessionID = genomeIDManager.getIdIntFromIntByMapping(iGeneID, 
				EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
	
		if (iAccessionID == -1)
		{	
			return "invalid";
		}
		
		return genomeIDManager.getIdStringFromIntByMapping(
				iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);

	}
}
