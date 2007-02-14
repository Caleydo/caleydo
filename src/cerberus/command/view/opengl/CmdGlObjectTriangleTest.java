/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.opengl.canvas.GLCanvasTestTriangle;
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
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public CmdGlObjectTriangleTest(final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager)
	{
		super(refGeneralManager, refCommandManager);
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_TRIANGLE_TEST;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		refParameterHandler.setValueAndType( "OpenGLTriangleTest_color",
				sDetail,
				IParameterHandler.ParameterHandlerType.STRING);
	}
	
	@Override
	public void doCommandPart() throws CerberusRuntimeException
	{

		GLCanvasTestTriangle canvas = 
			(GLCanvasTestTriangle) openGLCanvasUser;
				
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException
	{
		
	}

}
