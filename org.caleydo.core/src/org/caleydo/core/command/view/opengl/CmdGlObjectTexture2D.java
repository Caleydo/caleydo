//package org.caleydo.core.command.view.opengl;
//
//import java.util.StringTokenizer;
//
//import org.caleydo.core.command.CommandQueueSaxType;
//import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
//import org.caleydo.core.manager.ICommandManager;
//import org.caleydo.core.manager.IGeneralManager;
//import org.caleydo.core.manager.ILoggerManager.LoggerType;
//import org.caleydo.core.parser.parameter.IParameterHandler;
//import org.caleydo.core.util.exception.CaleydoRuntimeException;
//import org.caleydo.core.util.system.StringConversionTool;
//import org.caleydo.core.view.opengl.canvas.texture.GLCanvasTexture2D;
//
///**
// * @author Michael Kalkusch
// *
// */
//public class CmdGlObjectTexture2D 
//extends ACmdCreate_GlCanvasUser {
//	
//	protected float [] fResolution;
//	
//	protected String color;
//	
//	/**
//	 * Constructor.
//	 * 
//	 */
//	public CmdGlObjectTexture2D(
//			final IGeneralManager refGeneralManager,
//			final ICommandManager refCommandManager,
//			final CommandQueueSaxType refCommandQueueSaxType) {
//		
//		super(refGeneralManager, 
//				refCommandManager,
//				refCommandQueueSaxType);
//		
//		localManagerObjectType = CommandQueueSaxType.CREATE_GL_TEXTURE2D;
//	}
//
//	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
//		super.setParameterHandler(refParameterHandler);
//		
//		StringTokenizer token = new StringTokenizer(
//				sAttribute3,
//				IGeneralManager.sDelimiter_Parser_DataItems);
//		
//		int iSizeTokens= token.countTokens();
//		
//		fResolution = 
//			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
//	}
//
//
//
//
//	@Override
//	public void doCommandPart() throws CaleydoRuntimeException {
//		
//		GLCanvasTexture2D canvas = 
//			(GLCanvasTexture2D) openGLCanvasUser;
//				
//		canvas.setFileNameForTexture( sDetail );
//		canvas.setOriginRotation( cameraOrigin, cameraRotation );
//		canvas.setResolution( fResolution );
//		
//		if ( sDetail.length() < 1 ) {
//			generalManager.getSingelton().logMsg( 
//					"CmdGLObjectTexture2D no textrue defined!",
//					LoggerType.STATUS );
//		}
//		
//	}
//
//	@Override
//	public void undoCommandPart() throws CaleydoRuntimeException {
//		
//		GLCanvasTexture2D canvas = 
//			(GLCanvasTexture2D) openGLCanvasUser;
//		
//		canvas.destroyGLCanvas();
//		canvas = null;
//	}
//}
