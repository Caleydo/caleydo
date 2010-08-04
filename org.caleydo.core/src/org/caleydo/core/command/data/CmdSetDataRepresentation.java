package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.set.SetManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command triggers filtering of storage data Example: LIN -> LOG etc.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdSetDataRepresentation
	extends ACmdExternalAttributes {

	private ArrayList<Integer> iAlIDs;

	private EExternalDataRepresentation externalDataRep;
	private boolean bIsSetHomogeneous;

	private EManagedObjectType objectType;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdSetDataRepresentation(ECommandType cmdType) {
		super(cmdType);

		iAlIDs = new ArrayList<Integer>();
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		externalDataRep = EExternalDataRepresentation.valueOf(attrib1);

		/**
		 * Fill storage IDs
		 */
		StringTokenizer strToken_DataTypes =
			new StringTokenizer(attrib2, IGeneralManager.sDelimiter_Parser_DataItems);

		while (strToken_DataTypes.hasMoreTokens()) {
			iAlIDs.add(Integer.valueOf(strToken_DataTypes.nextToken()).intValue());
		}

		// Convert external IDs from XML file to internal IDs
		iAlIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(iAlIDs);

		if (attrib3.equals("")) {
			objectType = EManagedObjectType.STORAGE;
		}
		else {
			objectType = EManagedObjectType.valueOf(attrib3);
			if (objectType != EManagedObjectType.SET)
				throw new IllegalArgumentException(
					"Setting of external data rep is only allowed on storages or sets");
		}

		// default is homogeneous
		if (attrib4.equals("")) {
			bIsSetHomogeneous = true;
		}
		else {
			if (attrib4.equals("homogeneous")) {
				bIsSetHomogeneous = true;
			}
			else if (attrib4.equals("inhomogeneous")) {
				bIsSetHomogeneous = false;
			}
			else
				throw new IllegalArgumentException(
					"Illegal string for attrib 4: 'homogeneous' and 'inhomogeneous' are valid.");
		}

	}

	/**
	 * Overwrites the specified storage with the results of the operation
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link EExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all storages in the set contain equal data. If false, each
	 *            storage is treated separately, has it's own min and max etc. Sets that contain nominal data
	 *            MUST be inhomogeneous.
	 * @param iAlStorageID
	 *            The source storage ids. This storage is overwritten with the result.
	 * @param objectType
	 *            Signal whether you want to apply this on a set or a storage.
	 */
	public void setAttributes(EExternalDataRepresentation externalDataRep, boolean bIsSetHomogeneous,
		ArrayList<Integer> iAlStorageID, EManagedObjectType objectType) {

		this.externalDataRep = externalDataRep;
		this.bIsSetHomogeneous = bIsSetHomogeneous;
		this.iAlIDs = iAlStorageID;
		if (objectType != EManagedObjectType.STORAGE && objectType != EManagedObjectType.SET)
			throw new IllegalArgumentException(
				"Setting of external data rep is only allowed on storages or sets");

		this.objectType = objectType;
	}

	@Override
	public void doCommand() {

		if (objectType == EManagedObjectType.STORAGE) {
			IStorage tmpStorage = null;
			for (int currentID : iAlIDs) {
				tmpStorage = generalManager.getStorageManager().getItem(currentID);

				tmpStorage.setExternalDataRepresentation(externalDataRep);
			}
		}
		else {
			Set tmpSet = null;
			for (int currentID : iAlIDs) {
				tmpSet = (Set) SetManager.getInstance().getItem(currentID);

				SetUtils.setExternalDataRepresentation(tmpSet, externalDataRep, bIsSetHomogeneous);
			}
		}
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}
}
