package cerberus.util.mapping;

import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IGenomeIdManager;


public class GeneAnnotationMapper {

	protected IGeneralManager refGeneralManager;
	
	protected IGenomeIdManager refGenomeIdManager;
	
	public GeneAnnotationMapper (final IGeneralManager refGeneralManager) {
	
		refGenomeIdManager = refGeneralManager.getSingelton().getGenomeIdManager();
	}
	
	public final String getGeneShortNameByNCBIGeneId(
			String sGeneId) {
		
		// Remove prefix ("hsa:")
		sGeneId = sGeneId.substring(4);
				
		int iGeneId = refGenomeIdManager.getIdIntFromStringByMapping(sGeneId, 
				GenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneId == -1)
		{	
			return "N.A.";
		}
		
		return refGenomeIdManager.getIdStringFromIntByMapping(
				iGeneId, GenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);
	}
}
