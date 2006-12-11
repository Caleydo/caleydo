/**
 * 
 */
package cerberus.command.view.opengl;

import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.opengl.canvas.texture.GLCanvasTexture2D;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectTexture2D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
	
	protected float [] fResolution;
	
	protected String color;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public CmdGlObjectTexture2D(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler) {
		
		super(refGeneralManager, refParameterHandler);

		// fResolution = new float [3];
		
		setAttributesHeatmapWidthHeight( refParameterHandler );
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_TEXTURE2D;
	}

	private final void setAttributesHeatmapWidthHeight( 
			IParameterHandler refParameterHandler ) {
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				CommandFactory.sDelimiter_Parser_DataItems);
		
		int iSizeTokens= token.countTokens();
		
		fResolution = 
			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
		
	}



	@Override
	public void doCommandPart() throws CerberusRuntimeException {
		
		GLCanvasTexture2D canvas = 
			(GLCanvasTexture2D) openGLCanvasUser;
				
		canvas.setFileNameForTexture( sDetail );
		canvas.setOriginRotation( vec3fOrigin, vec4fRotation );
		canvas.setResolution( fResolution );
		
		if ( sDetail.length() < 1 ) {
			refGeneralManager.getSingelton().getLoggerManager().logMsg( "CmdGLObjectTexture2D no textrue defined!");
		}
		
	}

	@Override
	public void undoCommandPart() throws CerberusRuntimeException {
		
		GLCanvasTexture2D canvas = 
			(GLCanvasTexture2D) openGLCanvasUser;
		
		canvas.destroy();
		canvas = null;
	}
}
