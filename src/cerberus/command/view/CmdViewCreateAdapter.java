package cerberus.command.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.base.AManagedCommand;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;

public class CmdViewCreateAdapter 
extends AManagedCommand
implements ICommand
{
	protected int iCommandId;

	protected int iViewId;

	protected int iParentContainerId;
	
	protected String sLabel;

	protected Vector<String> refVecAttributes;

	public CmdViewCreateAdapter(IGeneralManager refGeneralManager,
			final LinkedList<String> listAttributes)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(-1, refGeneralManager);

		refVecAttributes = new Vector<String>();

		setAttributes(listAttributes);
	}

	/**
	 * Method needs to be implemented in the subclass
	 */
	public void doCommand() throws CerberusRuntimeException
	{
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
			iCommandId = (StringConversionTool.convertStringToInt(iter
					.next(), -1));
			iViewId = (StringConversionTool.convertStringToInt(
					iter.next(), -1));

			sLabel = iter.next();

			// Skip process, memenoto and detail argument
			iter.next();
			iter.next();
			//iProgressPercentage = (StringConversionTool.convertStringToInt(iter
			//		.next(), 0));
			iter.next();
			
			iParentContainerId = (StringConversionTool
					.convertStringToInt(iter.next(), -1));

			// Add view size
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
