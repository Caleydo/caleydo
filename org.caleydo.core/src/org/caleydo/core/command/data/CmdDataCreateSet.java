package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.eclipse.core.runtime.Status;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateSet
	extends ACmdCreational<ISet> {
	private ESetType setType;

	private ArrayList<Integer> iAlStorageIDs;

	/**
	 * Constructor.
	 */
	public CmdDataCreateSet(final ECommandType cmdType) {
		super(cmdType);

		iAlStorageIDs = new ArrayList<Integer>();

		setType = ESetType.UNSPECIFIED;
	}

	private void fillSets(ISet newSet) {
		if (iAlStorageIDs.isEmpty())
			throw new IllegalStateException("No data available for creating storage.");

		for (int iStorageID : iAlStorageIDs) {
			newSet.addStorage(iStorageID);
		}
	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() {
		ISetManager setManager = generalManager.getSetManager();

		createdObject = setManager.createSet(setType);
		createdObject.setLabel(sLabel);

		if (iExternalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(), iExternalID);
		}

		fillSets(createdObject);

		generalManager.getLogger().log(
			new Status(Status.INFO, GeneralManager.PLUGIN_ID, "New Set with internal ID "
				+ createdObject.getID() + " and external ID " + iExternalID + " created."));

		if (createdObject.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
			GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA).setSet(createdObject);
		}
		else if (createdObject.getSetType() == ESetType.UNSPECIFIED) {
			GeneralManager.get().getUseCase(EDataDomain.UNSPECIFIED).setSet(createdObject);
		}
		else if (createdObject.getSetType() == ESetType.CLINICAL_DATA) {
			GeneralManager.get().getUseCase(EDataDomain.CLINICAL_DATA).setSet(createdObject);
		}
		else
			throw new IllegalStateException("Cannot find use case for set type " + createdObject.getSetType()
				+ ".");

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		/**
		 * Read TAG_ATTRIBUTE2 "attrib2" for storage!
		 */

		/**
		 * Separate "text1@text2"
		 */
		StringTokenizer strToken_StorageBlock =
			new StringTokenizer(parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey()),
				IGeneralManager.sDelimiter_Paser_DataItemBlock);

		while (strToken_StorageBlock.hasMoreTokens()) {
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_StorageId =
				new StringTokenizer(strToken_StorageBlock.nextToken(),
					IGeneralManager.sDelimiter_Parser_DataItems);

			while (strToken_StorageId.hasMoreTokens()) {
				iAlStorageIDs.add(Integer.valueOf(strToken_StorageId.nextToken()).intValue());
			}
		}

		// Convert external IDs from XML file to internal IDs
		iAlStorageIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(iAlStorageIDs);

		/**
		 * read "attrib3" key ...
		 */
		String sAttrib3 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey());

		if (sAttrib3.length() > 0) {
			setType = ESetType.valueOf(sAttrib3);
		}
		else {
			setType = ESetType.UNSPECIFIED;
		}
	}

	public void setAttributes(ArrayList<Integer> iAlStorageIDs, ESetType setType) {
		this.setType = setType;
		this.iAlStorageIDs = iAlStorageIDs;
	}
}
