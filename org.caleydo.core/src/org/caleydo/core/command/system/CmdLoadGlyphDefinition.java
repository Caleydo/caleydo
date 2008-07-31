package org.caleydo.core.command.system;

import java.util.logging.Level;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command sets relevant file paths in PathwayMaanger.
 * 
 * @author Sauer Stefan
 */
public class CmdLoadGlyphDefinition
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	private String sXMLPath = "";

	/**
	 * Constructor.
	 */
	public CmdLoadGlyphDefinition(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IGlyphManager gm = generalManager.getGlyphManager();

		gm.loadGlyphDefinitaion(sXMLPath);

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

		assert parameterHandler != null : "can not handle null object!";

		super.setParameterHandler(parameterHandler);

		sXMLPath = this.sAttribute1;
	}

	/*
	 * (non-Javadoc)
	 */
	public void setAttributes(final String sPathwayXMLPath)
	{

		this.sXMLPath = sPathwayXMLPath;
	}
}
