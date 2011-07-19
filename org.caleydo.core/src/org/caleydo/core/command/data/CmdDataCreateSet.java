package org.caleydo.core.command.data;

import java.util.ArrayList;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.util.logging.Logger;
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
	public CmdDataCreateSet() {
		super(CommandType.CREATE_SET_DATA);

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
	@Override
	public void doCommand() {

		createdObject = new Set(dataDomain);

		if (externalID != -1) {
			generalManager.getIDCreator().mapInternalToExternalID(createdObject.getID(), externalID);
		}

		fillSets(createdObject);

		Logger.log(new Status(IStatus.INFO, this.toString(), "New Set with internal ID "
			+ createdObject.getID() + " and external ID " + externalID + " created."));

		dataDomain.setSet(createdObject);

	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(ArrayList<Integer> iAlStorageIDs, ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.storageIDs = iAlStorageIDs;
	}
}
