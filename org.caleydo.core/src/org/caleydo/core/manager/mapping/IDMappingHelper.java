package org.caleydo.core.manager.mapping;

import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;

public class IDMappingHelper {
	private static IDMappingHelper idMappingHelper;

	private IIDMappingManager idMappingManager;

	/**
	 * Constructor
	 */
	private IDMappingHelper() {
		idMappingManager = GeneralManager.get().getIDMappingManager();
	}

	public static IDMappingHelper get() {
		if (idMappingHelper == null) {
			idMappingHelper = new IDMappingHelper();
		}

		return idMappingHelper;
	}

	@Deprecated
	public int getDavidIDFromStorageIndex(int index) {
		Integer iRefSeqID = idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, index);

		if (iRefSeqID == null)
			return -1;

		Integer iDavidId = idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, iRefSeqID);

		if (iDavidId == null)
			return -1;

		return iDavidId;
	}

	@Deprecated
	public int getRefSeqFromStorageIndex(int index) {
		Integer iRefSeqID = idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, index);

		if (iRefSeqID == null)
			return -1;

		return iRefSeqID;
	}

	@Deprecated
	public String getShortNameFromDavid(int index) {
		// Convert expression storage ID to RefSeq
		Integer iDavidID = getDavidIDFromStorageIndex(index);

		if (iDavidID == null)
			return "Unknown Gene";

		String sGeneSymbol = idMappingManager.getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);
		if (sGeneSymbol == "")
			return "Unkonwn Gene";
		else
			return sGeneSymbol;
	}

	@Deprecated
	public String getRefSeqStringFromStorageIndex(int iIndex) {
		int iRefSeqID = getRefSeqFromStorageIndex(iIndex);
		return idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA, iRefSeqID);
	}
}
