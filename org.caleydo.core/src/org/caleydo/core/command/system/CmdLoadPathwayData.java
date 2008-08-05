package org.caleydo.core.command.system;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command triggers loading of pathways.
 * 
 * @author Marc Streit
 */
public class CmdLoadPathwayData
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	/**
	 * Constructor.
	 */
	public CmdLoadPathwayData(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager, commandManager, commandQueueSaxType);

		setCommandQueueSaxType(CommandQueueSaxType.LOAD_DATA_FILE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		generalManager.getPathwayManager().triggerParsingPathwayDatabases();
		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttr#
	 * setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);
	}
}
