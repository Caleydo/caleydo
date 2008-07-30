package org.caleydo.core.command.data.filter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.EManagerObjectType;
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
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	public enum EDataFilterMathType
	{
		LIN_2_LOG, NORMALIZE
	}

	private ArrayList<Integer> iAlIDs;

	private EDataFilterMathType dataFilterMathType;

	private EManagerObjectType objectType;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param commandManager
	 * @param commandQueueSaxType
	 */
	public CmdDataFilterMath(IGeneralManager generalManager, ICommandManager commandManager,
			CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);

		iAlIDs = new ArrayList<Integer>();
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

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

		if (sAttribute3.equals(""))
			objectType = EManagerObjectType.STORAGE;
		else
			objectType = EManagerObjectType.valueOf(sAttribute3);
	}

	/**
	 * Overwrites the specified storage with the results of the operation
	 * 
	 * @param dataFilterMathType
	 *            The type of operation
	 * @param iAlStorageID
	 *            The source storage ids. This storage is overwritten with the
	 *            result.
	 */
	public void setAttributes(EDataFilterMathType dataFilterMathType,
			ArrayList<Integer> iAlStorageID, EManagerObjectType objectType)
	{

		this.dataFilterMathType = dataFilterMathType;
		this.iAlIDs = iAlStorageID;
		if (objectType != EManagerObjectType.STORAGE || objectType != EManagerObjectType.SET)
			throw new CaleydoRuntimeException(
					"Math operations are not supportet on this type of object",
					CaleydoRuntimeExceptionType.COMMAND);

		this.objectType = objectType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand()
	{

		if (objectType == EManagerObjectType.STORAGE)
		{
			IStorage tmpStorage = null;
			for (int currentID : iAlIDs)
			{
				tmpStorage = generalManager.getStorageManager().getStorage(currentID);

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
				tmpSet = generalManager.getSetManager().getSet(currentID);

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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{

		// TODO Auto-generated method stub

	}

}
