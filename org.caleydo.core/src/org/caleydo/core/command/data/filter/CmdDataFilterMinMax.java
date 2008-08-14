package org.caleydo.core.command.data.filter;

import javax.naming.OperationNotSupportedException;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * This class calculates the min and the max value of a
 * storage or a set It is implemented as a command and as a filter.
 * 
 * TODO: Min max for set not implemented yet
 * @author Alexander Lex 
 */

public class CmdDataFilterMinMax
	extends ACmdExternalAttributes
{

	private ISet mySet = null;

	private INumericalStorage myStorage = null;

	private double dMinValue = Double.MAX_VALUE;

	private double dMaxValue = Double.MIN_VALUE;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdDataFilterMinMax(CommandType cmdType)
	{
		super(cmdType);
	}

	/**
	 * Calculates the minimum and the maximum of either a set or a storage
	 * depending on what has been set using the setAttributes methods
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		try
		{
			if (myStorage == null && mySet != null)
			{
				dMinValue = mySet.getMin();
				dMaxValue = mySet.getMax();
			}
			else if (myStorage != null && mySet == null)
			{
				dMinValue = myStorage.getMin();
				dMaxValue = myStorage.getMax();
			}
			else
			{
				throw new CaleydoRuntimeException(
						"You have to initialize the filter before using it",
						CaleydoRuntimeExceptionType.COMMAND);
			}

			commandManager.runDoCommand(this);
		}
		catch (OperationNotSupportedException e)
		{
			throw new CaleydoRuntimeException(e.getExplanation(),
					CaleydoRuntimeExceptionType.COMMAND);
		}
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	/**
	 * You have to set the attributes of the command before executing
	 * doCommand() This is done here if you want to calculate the min and max on
	 * a storage
	 * 
	 * @param myStorage The storage
	 */
	public void setAttributes(INumericalStorage myStorage)
	{

		this.myStorage = myStorage;
	}

	/**
	 * You have to set the attributes of the command before executing
	 * doCommand() This is done here if you want to calculate the min and max on
	 * a storage
	 * 
	 * @param mySet The set
	 */
	public void setAttributes(ISet mySet)
	{

		this.mySet = mySet;
	}

	public double getMin()
	{

		return dMinValue;
	}

	public double getMax()
	{

		return dMaxValue;
	}

}
