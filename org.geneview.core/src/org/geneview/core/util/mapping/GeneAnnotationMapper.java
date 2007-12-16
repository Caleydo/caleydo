package org.geneview.core.util.mapping;

import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IGenomeIdManager;


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
				EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
				
		if (iGeneId == -1)
		{	
			return "N.A.";
		}
		
		return refGenomeIdManager.getIdStringFromIntByMapping(
				iGeneId, EGenomeMappingType.NCBI_GENEID_2_GENE_SHORT_NAME);
	}
}
