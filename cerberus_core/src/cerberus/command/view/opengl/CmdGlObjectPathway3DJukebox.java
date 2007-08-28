/**
 * 
 */
package cerberus.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.view.opengl.ACmdGLObjectPathway3D;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdGlObjectPathway3DJukebox 
extends ACmdGLObjectPathway3D
implements ICommand {

	protected ArrayList<Integer> iArSetIDs;
	
	protected float [] fResolution;
	
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectPathway3DJukebox(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
				
		iArSetIDs = new ArrayList<Integer>();

		localManagerObjectType = CommandQueueSaxType.CREATE_GL_JUKEBOX_PATHWAY_3D;
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
		
		fResolution = 
			StringConversionTool.convertStringToFloatArrayVariableLength(
					sAttribute3);
		
		setParameterHandler_DetailsPathway3D();
	}

	@Override
	public void doCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasJukeboxPathway3D canvas = 
			(GLCanvasJukeboxPathway3D) openGLCanvasUser;		
		
		canvas.setOriginRotation(cameraOrigin, cameraRotation);
		
		int[] iArTmp = new int[iArSetIDs.size()];
		for(int index = 0; index < iArSetIDs.size(); index++)
			iArTmp[index] = iArSetIDs.get(index);
		
		canvas.addSetId(iArTmp);
		canvas.setTextureTransparency(fSetTransparencyValue);
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {

		GLCanvasJukeboxPathway3D canvas = 
			(GLCanvasJukeboxPathway3D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
