package org.geneview.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.opengl.canvas.pathway.GLCanvasPathway3D;

/**
 * Create single OpenGL pathway view.
 *
 * @author Marc Streit
 *
 */
public class CmdGlObjectPathway3D 
extends ACmdCreate_GlCanvasUser {

	protected ArrayList<Integer> iArSetIDs;
	
	protected float [] fResolution;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectPathway3D(final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager, commandManager, commandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_PATHWAY_3D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
	
		super.setParameterHandler(refParameterHandler);

		// Read SET IDs (Data and Selection) 
		String sPathwaySets = "";
		refParameterHandler.setValueAndTypeAndDefault("sPathwaySets",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"-1");
		
		sPathwaySets = refParameterHandler.getValueString("sPathwaySets");
		
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
		
		refParameterHandler.setValueAndTypeAndDefault("mapping_row_count", 
				refParameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()), 
				ParameterHandlerType.INT, "1");		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 */
	public final void doCommand() {
		
		super.doCommand();
		
		int[] iArTmp = new int[iArSetIDs.size()];
		for(int index = 0; index < iArSetIDs.size(); index++)
			iArTmp[index] = iArSetIDs.get(index);
		
		((GLCanvasPathway3D)gLEventListener).addSetId(iArTmp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 */
	public final void undoCommand() {
		
		super.undoCommand();
	}
}
