package org.caleydo.core.command.system;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command sets relevant file paths in PathwayMaanger.
 * 
 * @author Sauer Stefan
 */
public class CmdLoadGlyphDefinition
	extends ACmdExternalAttributes
{

	private String sXMLPath = "";

	/**
	 * Constructor.
	 */
	public CmdLoadGlyphDefinition(final CommandType cmdType)
	{
		super(cmdType);
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
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
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
