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
//import org.caleydo.core.view.opengl.canvas.isosurface.GLCanvasIsoSurface3D;
//
///**
// * @author Michael Kalkusch
// *
// */
//public class CmdGlObjectIsosurface3D 
//extends ACmdCreate_GlCanvasUser {
//	
//	protected float [] fResolution;
//	
//	/**
//	 * If of Set to be read data from
//	 * 
//	 * @see org.caleydo.core.data.collection.ISet
//	 */
//	protected int iTargetCollectionSetId;
//	
//	protected int iHistogramLevel = 50;
//	
//	protected String color;
//	
//	/**
//	 * Constructor.
//	 * 
//	 */
//	public CmdGlObjectIsosurface3D(
//			final IGeneralManager refGeneralManager, 
//			final ICommandManager refCommandManager,
//			final CommandQueueSaxType refCommandQueueSaxType) {
//		
//		super(refGeneralManager, 
//				refCommandManager,
//				refCommandQueueSaxType);
//		
//		localManagerObjectType = CommandQueueSaxType.CREATE_GL_ISOSURFACE3D;
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
//		
//		iHistogramLevel = (int) fResolution[iSizeTokens-1];
//		
//		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
//				sDetail, 
//				-1 );
//	}
//
//
//
//	@Override
//	public void doCommandPart() throws CaleydoRuntimeException {
//		
//		GLCanvasIsoSurface3D canvas = 
//			(GLCanvasIsoSurface3D) openGLCanvasUser;
//				
//		canvas.setOriginRotation( cameraOrigin, cameraRotation );
//		canvas.setResolution( fResolution );
//		
//		if ( iTargetCollectionSetId > -1 ) {
//			canvas.setTargetSetId( iTargetCollectionSetId );
//		}
//		else 
//		{
//			generalManager.getSingelton().logMsg( 
//					"CmdGLObjectHistogram2D no set defined!",
//					LoggerType.STATUS );
//		}
//		canvas.setIsoValue( iHistogramLevel );
//	}
//
//	@Override
//	public void undoCommandPart() throws CaleydoRuntimeException {
//		
//		GLCanvasIsoSurface3D canvas = 
//			(GLCanvasIsoSurface3D) openGLCanvasUser;
//		
//		canvas.destroyGLCanvas();
//		canvas = null;
//	}
//}
