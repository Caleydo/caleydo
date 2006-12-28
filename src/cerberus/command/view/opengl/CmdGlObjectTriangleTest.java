/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.opengl.canvas.GLCanvasTestTriangle;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

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
	public CmdGlObjectTriangleTest(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		refParameterHandler.setValueAndType( "OpenGLTriangleTest_color",
				sDetail,
				IParameterHandler.ParameterHandlerType.STRING);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_TRIANGLE_TEST;
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
