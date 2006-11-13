/**
 * 
 */
package cerberus.command.view.opengl;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D;
import cerberus.view.gui.opengl.canvas.pathway.GLCanvasPathway2D;
import cerberus.view.gui.swt.pathway.jgraph.PathwayGraphViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * @author Marc Streit
 *
 */
public class CmdGlObjectPathway2D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
		
	/**
	 * ID of the pathway that will be drawn.
	 */
	protected int iTargetPathwayId;
	
	protected float[] fResolution;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public CmdGlObjectPathway2D(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		setPathwayAttributes(refParameterHandler);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_PATHWAY2D;
	}

	private final void setPathwayAttributes( 
			IParameterHandler refParameterHandler) {
		
		iTargetPathwayId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
	}

	@Override
	public void doCommandPart() throws CerberusRuntimeException {
		
		GLCanvasPathway2D canvas = 
			(GLCanvasPathway2D) openGLCanvasUser;		
		
		canvas.setOriginRotation(vec3fOrigin, vec4fRotation);
		canvas.setTargetPathwayId(iTargetPathwayId);
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException {
		
		GLCanvasHistogram2D canvas = 
			(GLCanvasHistogram2D) openGLCanvasUser;
		
		canvas.destroy();
		canvas = null;
	}
}
