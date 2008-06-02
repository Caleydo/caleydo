package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;

/**
 * Create pathway jukebox view. 
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 * @deprecated Use CmdGlObjectJukexbox3D instead
 *
 */
public class CmdGlObjectPathway3DJukebox 
extends ACmdCreate_GlCanvasUser {

	protected ArrayList<Integer> iArSetIDs;
	
	protected float [] fResolution;
	
	private int iMappingRowCount = -1;
	
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectPathway3DJukebox(final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager, commandManager, commandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_JUKEBOX_PATHWAY_3D;
	}

	public void setParameterHandler( final IParameterHandler parameterHandler ) {
	
		super.setParameterHandler(parameterHandler);

		// Read SET IDs (Data and Selection) 
		String sPathwaySets = "";
		parameterHandler.setValueAndTypeAndDefault("sPathwaySets",
				parameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"-1");
		
		sPathwaySets = parameterHandler.getValueString("sPathwaySets");
		
		StringTokenizer setToken = new StringTokenizer(
				sPathwaySets,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (setToken.hasMoreTokens())
		{
			iArSetIDs.add(StringConversionTool.convertStringToInt(
					setToken.nextToken(), -1));
		}
		
		fResolution = 
			StringConversionTool.convertStringToFloatArrayVariableLength(
					sAttribute3);
		
		parameterHandler.setValueAndTypeAndDefault("mapping_row_count", 
				parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()), 
				ParameterHandlerType.INT, "1");
		
		iMappingRowCount = parameterHandler.getValueInt("mapping_row_count");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 */
	public final void doCommand() {
		
		super.doCommand();
		
		int[] iArTmp = new int[iArSetIDs.size()];
		for(int index = 0; index < iArSetIDs.size(); index++)
			iArTmp[index] = iArSetIDs.get(index);
		
		((GLCanvasJukeboxPathway3D)gLEventListener).addSetId(iArTmp);
		((GLCanvasJukeboxPathway3D)gLEventListener).setMappingRowCount(iMappingRowCount);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 */
	public final void undoCommand() {
		
		super.undoCommand();
	}
}
