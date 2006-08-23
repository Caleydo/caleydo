package cerberus.command.base;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;

public abstract class ACmdCreate 
extends AManagedCommand
implements ICommand
{
	protected int iCommandId;

	protected int iParentContainerId;
	
	protected String sLabel;

	protected Vector<String> refVecAttributes;

	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param listAttributes
	 */
	public ACmdCreate(IGeneralManager refGeneralManager,
			final LinkedList<String> listAttributes)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(-1, refGeneralManager);

		refVecAttributes = new Vector<String>();

		setAttributes(listAttributes);
	}

	/**
	 * Nothing to undo at this time.
	 */
	public final void undoCommand() throws CerberusRuntimeException
	{
	}

	/**
	 * Method needs to be implemented in the subclass!
	 */
	public final CommandType getCommandType() throws CerberusRuntimeException
	{
		return null;
	}

	protected final boolean setAttributes(final LinkedList<String> listAttrib)
	{
		assert listAttrib != null : "can not handle null object!";

		Iterator<String> iter = listAttrib.iterator();
		final int iSizeList = listAttrib.size();

		assert iSizeList > 1 : "can not handle empty argument list!";

		try
		{			
			iCommandId = (StringConversionTool.convertStringToInt(
					iter.next(), -1));
			iUniqueId = (StringConversionTool.convertStringToInt(
					iter.next(), -1));

			sLabel = iter.next();

			// Skip process and mementoId
			iter.next();
			iter.next();
			
			// Add detail string - index 0
			refVecAttributes.addElement(iter.next());

			// Add attrib1
			String sAttrib1;
			sAttrib1 = iter.next();
			
			refVecAttributes.addElement(sAttrib1);
			
			iParentContainerId = (StringConversionTool
					.convertStringToInt(sAttrib1, -1));			
			
			// Add attrib2 string - index 1
			refVecAttributes.addElement(iter.next());

			setId(iCommandId);

			return true;
		} catch (NumberFormatException nfe)
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateWindow::doCommand() error on attributes!");
			return false;
		}
	}
}
