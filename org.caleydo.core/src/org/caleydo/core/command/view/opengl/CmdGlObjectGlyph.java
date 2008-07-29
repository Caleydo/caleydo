package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.glyph.GLCanvasGlyph;
import org.caleydo.core.view.opengl.canvas.heatmap.GLCanvasHeatMap;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Create glyph view.
 * 
 * @author Stefan Sauer
 * @author Marc Streit
 *
 */
public class CmdGlObjectGlyph 
extends ACmdCreate_GlCanvasUser {

	protected ArrayList<Integer> iArSetIDs;

	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectGlyph(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager, commandManager, commandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_GLYPH;
	}

	public void setParameterHandler( final IParameterHandler parameterHandler ) {
	
		super.setParameterHandler(parameterHandler);
		
		// Read SET IDs (Data and Selection) 
		String sDataSets = sDetail;
		
		StringTokenizer setToken = new StringTokenizer(
				sDataSets,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (setToken.hasMoreTokens())
		{
			iArSetIDs.add(StringConversionTool.convertStringToInt(
					setToken.nextToken(), -1));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 */
	public final void doCommand() {
		
		super.doCommand();
		
		AGLCanvasUser glCanvas = ((AGLCanvasUser)gLEventListener);
		for (Integer iSetID : iArSetIDs)
		{
			glCanvas.addSet(iSetID);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 */
	public final void undoCommand() {
		
		super.undoCommand();
	}
}
