package org.caleydo.core.command.data;

import java.util.ArrayList;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Command, creates a new dimension.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateTable
	extends ACmdCreational<DataTable> {

	private ATableBasedDataDomain dataDomain;
	private ArrayList<Integer> dimensionIDs;

	/**
	 * Constructor.
	 */
	public CmdDataCreateTable() {
		super(CommandType.CREATE_DATA_TABLE);

		dimensionIDs = new ArrayList<Integer>();
	}

	private void fillDataTables(DataTable newTable) {
		if (dimensionIDs.isEmpty())
			throw new IllegalStateException("No data available for creating dimension.");

		DataTableUtils.setTables(newTable, dimensionIDs);
	}

	/**
	 * Load data from file using a token pattern.
	 */
	@Override
	public void doCommand() {

		createdObject = new DataTable(dataDomain);

		if (externalID != -1) {
			generalManager.getIDCreator().mapInternalToExternalID(createdObject.getID(), externalID);
		}

		fillDataTables(createdObject);

		Logger.log(new Status(IStatus.INFO, this.toString(), "New Set with internal ID "
			+ createdObject.getID() + " and external ID " + externalID + " created."));

		dataDomain.setTable(createdObject);
	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(ArrayList<Integer> iAlDimensionIDs, ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.dimensionIDs = iAlDimensionIDs;
	}
}
