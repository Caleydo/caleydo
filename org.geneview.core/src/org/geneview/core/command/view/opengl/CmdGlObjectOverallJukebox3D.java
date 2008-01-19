package org.geneview.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.opengl.canvas.jukebox.GLCanvasOverallJukebox3D;
import org.geneview.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdGlObjectOverallJukebox3D 
extends ACmdGLObjectPathway3D {

	protected ArrayList<Integer> iArSetIDs;
		
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectOverallJukebox3D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_OVERALL_JUKEBOX_3D;
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
	}

	@Override
	public void doCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasOverallJukebox3D canvas = 
			(GLCanvasOverallJukebox3D) openGLCanvasUser;		
		
		canvas.setOriginRotation(cameraOrigin, cameraRotation);
		
		int[] iArTmp = new int[iArSetIDs.size()];
		for(int index = 0; index < iArSetIDs.size(); index++)
			iArTmp[index] = iArSetIDs.get(index);
		
		canvas.addSetId(iArTmp);
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {

		GLCanvasOverallJukebox3D canvas = 
			(GLCanvasOverallJukebox3D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
