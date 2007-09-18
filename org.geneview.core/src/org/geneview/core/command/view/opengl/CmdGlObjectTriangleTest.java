/**
 * 
 */
package org.geneview.core.command.view.opengl;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.opengl.canvas.GLCanvasTestTriangle;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectTriangleTest 
extends ACmdCreate_GlCanvasUser {
	
	protected String color;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectTriangleTest(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_TRIANGLE_TEST;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		refParameterHandler.setValueAndType( "OpenGLTriangleTest_color",
				sDetail,
				IParameterHandler.ParameterHandlerType.STRING);
	}
	
	public void doCommandPart() throws GeneViewRuntimeException
	{

		GLCanvasTestTriangle canvas = 
			(GLCanvasTestTriangle) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException
	{
		
	}

}
