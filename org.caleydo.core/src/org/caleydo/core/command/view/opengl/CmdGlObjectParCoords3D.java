package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Create parallel coordinates view.
 * 
 * @author Marc Streit
 *
 */
public class CmdGlObjectParCoords3D 
extends ACmdCreate_GlCanvasUser {

	protected ArrayList<Integer> iArSetIDs;

	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectParCoords3D(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager, commandManager, commandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_PARALLEL_COORDINATES_3D;
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 */
	public final void doCommand() {
		
		super.doCommand();
		
//		((GLCanvasParCoords3D)gLEventListener).removeAllSetIdByType(ESetType.GENE_EXPRESSION_DATA);
//		((GLCanvasParCoords3D)gLEventListener).removeAllSetIdByType(ESetType.CLINICAL_DATA);
		
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
