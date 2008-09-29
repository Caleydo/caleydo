package org.caleydo.core.util.mapping;

import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * 
 * 
 * @TODO Revise this class
 * @author Marc Streit
 */
public class GeneAnnotationMapper
{
	protected IIDMappingManager genomeIDManager;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public GeneAnnotationMapper()
	{
		genomeIDManager = GeneralManager.get().getGenomeIdManager();
	}

	public final String getGeneShortNameByNCBIGeneId(String sGeneId)
	{

		// // Remove prefix ("hsa:")
		// sGeneId = sGeneId.substring(4);
		//				
		// int iGeneId = genomeIDManager.getIdIntFromStringByMapping(sGeneId,
		// EMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
		//				
		// if (iGeneId == -1)
		// {
		// return "N.A.";
		// }
		//		
		// return genomeIDManager.getIdStringFromIntByMapping(
		// iGeneId, EMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);

		return "";
	}

	public final String getGeneShortNameByAccession(int iAccession)
	{

		// int sNcbiID = genomeIDManager.getIdIntFromIntByMapping(iAccession,
		// EMappingType.ACCESSION_2_NCBI_GENEID);
		// return genomeIDManager.getIdStringFromIntByMapping(sNcbiID,
		// EMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);
		//	
		return "";
	}

	public String getAccessionCodeByNCBIGeneIdCode(String sNCBIGeneIdCode)
	{

		// // Remove prefix ("hsa:")
		// sNCBIGeneIdCode = sNCBIGeneIdCode.substring(4);
		//		
		// int iGeneID =
		// genomeIDManager.getIdIntFromStringByMapping(sNCBIGeneIdCode,
		// EMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
		//				
		// if (iGeneID == -1)
		// {
		// return "invalid";
		// }
		//		
		// int iAccessionID = genomeIDManager.getIdIntFromIntByMapping(iGeneID,
		// EMappingType.NCBI_GENEID_2_ACCESSION);
		//	
		// if (iAccessionID == -1)
		// {
		// return "invalid";
		// }
		//		
		// return genomeIDManager.getIdStringFromIntByMapping(
		// iAccessionID, EMappingType.ACCESSION_2_ACCESSION_CODE);

		return null;

	}
}
