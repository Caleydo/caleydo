/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D;
import cerberus.view.gui.opengl.canvas.pathway.GLCanvasPathway3D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdGlObjectPathway3D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
	
	protected int iPathwaySetId = 0;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectPathway3D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
				
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_PATHWAY2D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
	
		super.setParameterHandler(refParameterHandler);

		iPathwaySetId = StringConversionTool.convertStringToInt(sDetail, -1);
	}

	@Override
	public void doCommandPart() throws CerberusRuntimeException {
		
		GLCanvasPathway3D canvas = 
			(GLCanvasPathway3D) openGLCanvasUser;		
		
		canvas.setOriginRotation(vec3fOrigin, vec4fRotation);
		canvas.setPathwaySet(iPathwaySetId);
		//canvas.setTargetPathwayId(iTargetPathwayId);
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException {
		
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
		
		canvas.destroy();
		canvas = null;
	}
}
