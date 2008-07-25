package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
import org.caleydo.core.data.view.camera.ViewFrustumBase;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;

/**
 * Create single OpenGL pathway view.
 *
 * @author Marc Streit
 *
 */
public class CmdGlObjectPathway3D 
extends ACmdCreate_GlCanvasUser {

	private ArrayList<Integer> iArSetIDs;
	
	private int iPathwayID = -1;
	
	
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
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
		
		parameterHandler.setValueAndTypeAndDefault("mapping_row_count", 
				parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()), 
				ParameterHandlerType.INT, "1");		
		
		iPathwayID = StringConversionTool.convertStringToInt(sAttribute4, -1);
	}
	
	public void setAttributes(final int iUniqueID,
			final int iPathwayID,
			final ArrayList<Integer> iArSetIDs,
			final ViewFrustumBase.ProjectionMode projectionMode,
			final float fLeft,
			final float fRight,
			final float fTop,
			final float fBottom,
			final float fNear,
			final float fFar)
	{
		super.setAttributes(projectionMode, fLeft, fRight, fTop, fBottom, fNear, fFar);
		
		this.iArSetIDs = iArSetIDs;
		this.iPathwayID = iPathwayID;
		this.iUniqueId = iUniqueID;
		iParentContainerId = -1;
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
		
		((GLCanvasPathway3D)gLEventListener).addSetId(iArTmp);
		((GLCanvasPathway3D)gLEventListener).setPathwayID(iPathwayID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
	 */
	public final void undoCommand() {
		
		super.undoCommand();
	}
}
