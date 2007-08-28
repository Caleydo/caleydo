/**
 * 
 */
package cerberus.command.view.opengl;

import java.util.StringTokenizer;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_GlCanvasUser;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.opengl.canvas.texture.GLCanvasTexture2D;
import cerberus.xml.parser.parameter.IParameterHandler;

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
	 * Constructor.
	 * 
	 */
	public CmdGlObjectTexture2D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_TEXTURE2D;
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		int iSizeTokens= token.countTokens();
		
		fResolution = 
			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
	}




	@Override
	public void doCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasTexture2D canvas = 
			(GLCanvasTexture2D) openGLCanvasUser;
				
		canvas.setFileNameForTexture( sDetail );
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		
		if ( sDetail.length() < 1 ) {
			refGeneralManager.getSingelton().logMsg( 
					"CmdGLObjectTexture2D no textrue defined!",
					LoggerType.STATUS );
		}
		
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasTexture2D canvas = 
			(GLCanvasTexture2D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
