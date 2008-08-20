package org.caleydo.core.command.data.filter;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
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
public class CmdDataFilterMath
	extends ACmdExternalAttributes
{
	public enum EDataFilterMathType
	{
		LIN_2_LOG,
		NORMALIZE
	}

	private ArrayList<Integer> iAlIDs;

	private EDataFilterMathType dataFilterMathType;

	private EManagedObjectType objectType;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdDataFilterMath(CommandType cmdType)
	{
		super(cmdType);

		iAlIDs = new ArrayList<Integer>();
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		dataFilterMathType = EDataFilterMathType.valueOf(sAttribute1);

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
		iAlIDs = GeneralManager.get().getIDManager()
			.convertExternalToInternalIDs(iAlIDs);
		
		if (sAttribute3.equals(""))
			objectType = EManagedObjectType.STORAGE;
		else
			objectType = EManagedObjectType.valueOf(sAttribute3);
	}

	/**
	 * Overwrites the specified storage with the results of the operation
	 * 
	 * @param dataFilterMathType The type of operation
	 * @param iAlStorageID The source storage ids. This storage is overwritten
	 *            with the result.
	 */
	public void setAttributes(EDataFilterMathType dataFilterMathType,
			ArrayList<Integer> iAlStorageID, EManagedObjectType objectType)
	{

		this.dataFilterMathType = dataFilterMathType;
		this.iAlIDs = iAlStorageID;
		if (objectType != EManagedObjectType.STORAGE && objectType != EManagedObjectType.SET)
			throw new CaleydoRuntimeException(
					"Math operations are not supportet on this type of object",
					CaleydoRuntimeExceptionType.COMMAND);

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

				if (dataFilterMathType.equals(EDataFilterMathType.LIN_2_LOG))
				{
					((NumericalStorage) tmpStorage).log10();
				}
				else if (dataFilterMathType.equals(EDataFilterMathType.NORMALIZE))
				{
					tmpStorage.normalize();
				}
			}
		}
		else
		{
			ISet tmpSet = null;
			for (int currentID : iAlIDs)
			{
				tmpSet = generalManager.getSetManager().getItem(currentID);

				if (dataFilterMathType.equals(EDataFilterMathType.LIN_2_LOG))
				{
					tmpSet.log10();
				}
				else if (dataFilterMathType.equals(EDataFilterMathType.NORMALIZE))
				{
					tmpSet.normalize();
				}
			}
		}
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
