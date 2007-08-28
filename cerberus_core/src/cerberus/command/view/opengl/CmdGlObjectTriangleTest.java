/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.opengl.canvas.GLCanvasTestTriangle;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectTriangleTest 
extends ACmdCreate_GlCanvasUser
		implements ICommand
{
	
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
