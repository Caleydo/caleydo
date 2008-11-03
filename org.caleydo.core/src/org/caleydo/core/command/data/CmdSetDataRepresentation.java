package org.caleydo.core.command.data;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Command triggers filtering of storage data Example: LIN -> LOG etc.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdSetDataRepresentation
	extends ACmdExternalAttributes
{

	private ArrayList<Integer> iAlIDs;

	private EExternalDataRepresentation externalDataRep;

	private EManagedObjectType objectType;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdSetDataRepresentation(ECommandType cmdType)
	{
		super(cmdType);

		iAlIDs = new ArrayList<Integer>();
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		externalDataRep = EExternalDataRepresentation.valueOf(sAttribute1);

		/**
		 * Fill storage IDs
		 */
		StringTokenizer strToken_DataTypes = new StringTokenizer(sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (strToken_DataTypes.hasMoreTokens())
		{
			iAlIDs.add(new Integer(strToken_DataTypes.nextToken()));
		}

		// Convert external IDs from XML file to internal IDs
		iAlIDs = GeneralManager.get().getIDManager().convertExternalToInternalIDs(iAlIDs);

		if (sAttribute3.equals(""))
			objectType = EManagedObjectType.STORAGE;
		else
		{
			objectType = EManagedObjectType.valueOf(sAttribute3);
			if (objectType != EManagedObjectType.SET)
				throw new IllegalArgumentException(
						"Setting of external data rep is only allowed on storages or sets");
		}

	}

	/**
	 * Overwrites the specified storage with the results of the operation
	 * 
	 * @param dataFilterMathType The type of operation
	 * @param iAlStorageID The source storage ids. This storage is overwritten
	 *            with the result.
	 */
	public void setAttributes(EExternalDataRepresentation externalDataRep,
			ArrayList<Integer> iAlStorageID, EManagedObjectType objectType)
	{

		this.externalDataRep = externalDataRep;
		this.iAlIDs = iAlStorageID;
		if (objectType != EManagedObjectType.STORAGE && objectType != EManagedObjectType.SET)
			throw new IllegalArgumentException(
					"Setting of external data rep is only allowed on storages or sets");

		this.objectType = objectType;
	}

	@Override
	public void doCommand()
	{

		if (objectType == EManagedObjectType.STORAGE)
		{
			IStorage tmpStorage = null;
			for (int currentID : iAlIDs)
			{
				tmpStorage = generalManager.getStorageManager().getItem(currentID);

				tmpStorage.setExternalDataRepresentation(externalDataRep);
			}
		}
		else
		{
			ISet tmpSet = null;
			for (int currentID : iAlIDs)
			{
				tmpSet = generalManager.getSetManager().getItem(currentID);

				tmpSet.setExternalDataRepresentation(externalDataRep);
			}
		}
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}
}
