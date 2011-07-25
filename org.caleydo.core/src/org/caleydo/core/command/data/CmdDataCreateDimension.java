package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.data.dimension.DimensionManager;

/**
 * Command creates a new dimension.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdDataCreateDimension
	extends ACmdCreational<ADimension> {
	
	private ManagedObjectType dimensionType;

	private int dimensionID = -1;
	
	/**
	 * Constructor.
	 */
	public CmdDataCreateDimension() {
		super(CommandType.CREATE_DIMENSION);
	}

	@Override
	public void doCommand() {
		DimensionManager dimensionManager = generalManager.getDimensionManager();
		
		if (dimensionID == -1)
			createdObject = dimensionManager.createDimension(dimensionType);
		else
			createdObject = dimensionManager.createDimension(dimensionType, dimensionID);
		
		//generalManager.getIDCreator().mapInternalToExternalID(createdObject.getID(), externalID);

		// generalManager.getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
		// "Created Storage with ID: " + createdObject.getID()));
	}

	@Override
	public void undoCommand() {
	}

	public void setAttributes(ManagedObjectType storageType, int storageID) {
		this.dimensionType = storageType;
		this.dimensionID = storageID;
	}
	
	public void setAttributes(ManagedObjectType storageType) {
		this.dimensionType = storageType;
	}
}
