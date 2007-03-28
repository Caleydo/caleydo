/**
 * 
 */
package cerberus.command.view.opengl;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.math.MathUtil;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.pathway.GLCanvasLayeredPathway3D;
import cerberus.view.gui.opengl.canvas.pathway.GLCanvasPanelPathway3D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdGlObjectLayeredPathway3D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
	
	protected ArrayList<Integer> iArSetIDs;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectLayeredPathway3D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_LAYERED_PATHWAY_3D;
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
	public void doCommandPart() throws CerberusRuntimeException {
		
		GLCanvasLayeredPathway3D canvas = 
			(GLCanvasLayeredPathway3D) openGLCanvasUser;		
		
		Rotf cam_rotation = new Rotf( new Vec3f( vec4fRotation.y(),
				vec4fRotation.z(),
				vec4fRotation.w() ),
				MathUtil.grad2radiant(vec4fRotation.x()));
		  
		canvas.setOriginRotation(vec3fOrigin, cam_rotation);
		
		int[] iArTmp = new int[iArSetIDs.size()];
		for(int index = 0; index < iArSetIDs.size(); index++)
			iArTmp[index] = iArSetIDs.get(index);
		
		canvas.addSetId(iArTmp);
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException {

		GLCanvasLayeredPathway3D canvas = 
			(GLCanvasLayeredPathway3D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
