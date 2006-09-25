/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetParentGLObject;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author java
 *
 */
public class CmdGlObjectTriangleTest 
extends ACmdCreate_IdTargetParentGLObject
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
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub

	}

}
