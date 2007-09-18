/**
 * 
 */
package org.geneview.core.command.view.opengl;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdGlObjectParCoords3D 
extends ACmdGLObjectPathway3D {

	//protected ArrayList<Integer> iArSetIDs;
		
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectParCoords3D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
				
		//iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_PARALLEL_COORDINATES_3D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
	
		super.setParameterHandler(refParameterHandler);
	}

	@Override
	public void doCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasParCoords3D canvas = 
			(GLCanvasParCoords3D) openGLCanvasUser;		
		
		canvas.setOriginRotation(cameraOrigin, cameraRotation);
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {

		GLCanvasParCoords3D canvas = 
			(GLCanvasParCoords3D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
