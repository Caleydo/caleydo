package org.caleydo.core.command.system;

import org.caleydo.core.command.ECommandType;
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
	public CmdLoadGlyphDefinition(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		IGlyphManager gm = generalManager.getGlyphManager();

		gm.loadGlyphDefinitaion(sXMLPath);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		sXMLPath = this.sAttribute1;
	}

	/**
	 * Set attributes for the command
	 * @param sPathwayXMLPath
	 */
	public void setAttributes(final String sPathwayXMLPath)
	{
		this.sXMLPath = sPathwayXMLPath;
	}
}
