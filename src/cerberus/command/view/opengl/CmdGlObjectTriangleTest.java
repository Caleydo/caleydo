/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetParentGLObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;
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
		IViewGLCanvasManager glCanvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		TestTriangleViewRep view =
			(TestTriangleViewRep) glCanvasManager.createView( ManagerObjectType.VIEW_SWT_JOGL_TEST_TRIANGLE,
				iUniqueTargetId,
				iParentContainerId,
				sLabel );
		
		glCanvasManager.registerGLEventListener( 
				view.getGLEventListener(), 
				iUniqueTargetId );
		glCanvasManager.addGLEventListener2GLCanvasById( 
				iUniqueTargetId,
				iParentContainerId );
		

	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub

	}

}
