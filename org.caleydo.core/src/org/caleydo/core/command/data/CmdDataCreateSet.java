package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateSet
	extends ACmdCreational<Set> {

	private ASetBasedDataDomain dataDomain;
	private ArrayList<Integer> storageIDs;

	/**
	 * Constructor.
	 */
	public CmdDataCreateSet(final ECommandType cmdType) {
		super(cmdType);

		storageIDs = new ArrayList<Integer>();
	}

	private void fillSets(Set newSet) {
		if (storageIDs.isEmpty())
			throw new IllegalStateException("No data available for creating storage.");

		SetUtils.setStorages(newSet, storageIDs);
	}

	/**
	 * Load data from file using a token pattern.
	 */
	public void doCommand() {

		createdObject = new Set(dataDomain);
		createdObject.setLabel(label);

		if (externalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(), externalID);
		}

		fillSets(createdObject);

		generalManager.getLogger().log(
			new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "New Set with internal ID "
				+ createdObject.getID() + " and external ID " + externalID + " created."));

		dataDomain.setSet(createdObject);

	}

	@Override
	public void undoCommand() {
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		StringTokenizer strToken_StorageBlock =
			new StringTokenizer(parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey()),
				GeneralManager.sDelimiter_Paser_DataItemBlock);

		while (strToken_StorageBlock.hasMoreTokens()) {
			StringTokenizer strToken_StorageId =
				new StringTokenizer(strToken_StorageBlock.nextToken(),
					GeneralManager.sDelimiter_Parser_DataItems);

			while (strToken_StorageId.hasMoreTokens()) {
				storageIDs.add(Integer.valueOf(strToken_StorageId.nextToken()).intValue());
			}
		}

		// Convert external IDs from XML file to internal IDs
		storageIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(storageIDs);

		String sAttrib3 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey());
		dataDomain = (ASetBasedDataDomain) DataDomainManager.getInstance().getDataDomain(sAttrib3);
		if (dataDomain == null) {
			DataDomainManager.getInstance().createDataDomain(sAttrib3);
			GeneralManager
				.get()
				.getLogger()
				.log(
					new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Lazy creation of data domain "
						+ sAttrib3));
		}
	}

	public void setAttributes(ArrayList<Integer> iAlStorageIDs, ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.storageIDs = iAlStorageIDs;
	}
}
