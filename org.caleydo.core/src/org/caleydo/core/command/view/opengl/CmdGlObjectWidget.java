//package org.caleydo.core.command.view.opengl;
//
//import org.caleydo.core.command.CommandQueueSaxType;
//import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
//import org.caleydo.core.manager.ICommandManager;
//import org.caleydo.core.manager.IGeneralManager;
//import org.caleydo.core.parser.parameter.IParameterHandler;
//import org.caleydo.core.util.exception.CaleydoRuntimeException;
//import org.caleydo.core.util.system.StringConversionTool;
//import org.caleydo.core.view.opengl.canvas.widgets.GLCanvasWidget;
//
///**
// * @author Michael Kalkusch
// *
// */
//public class CmdGlObjectWidget 
//extends ACmdCreate_GlCanvasUser {
//
//	protected float[] fColorSpots = { 1.0f, 0, 0};
//	
//	protected float[] fResolution;
//	
//	protected String sGridDetails = "";
//	
//	/**
//	 * If of Set to be read data from
//	 * 
//	 * @see org.caleydo.core.data.collection.ISet
//	 */
//	protected int iTargetCollectionSetId;
//	
//	
//	/**
//	 * Constructor.
//	 * 
//	 */
//	public CmdGlObjectWidget(
//			final IGeneralManager generalManager,
//			final ICommandManager commandManager,
//			final CommandQueueSaxType commandQueueSaxType)
//	{
//		super(generalManager, 
//				commandManager,
//				commandQueueSaxType);
//		
//		localManagerObjectType = CommandQueueSaxType.CREATE_GL_WIDGET;
//	}
//
//	public void setParameterHandler( final IParameterHandler parameterHandler ) {
//		
//		super.setParameterHandler(parameterHandler);
//		
//		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
//				this.sDetail, 
//				-1 );
//		
//		fResolution = StringConversionTool.convertStringToFloatArray( 
//				sAttribute3,
//				14 );
//		
//		String sColor = parameterHandler.getValueString( 
//				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
//		
//		sGridDetails = parameterHandler.getValueString( 
//				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
//		
//		if ( sColor != null ) 
//		{
//			fColorSpots = StringConversionTool.convertStringToFloatArrayVariableLength(
//					sColor );
//		}
//	}
//
//	@Override
//	public void doCommandPart() throws CaleydoRuntimeException
//	{
//		GLCanvasWidget canvas = 
//			(GLCanvasWidget) openGLCanvasUser;
//				
//		canvas.setOriginRotation( cameraOrigin, cameraRotation );
//		canvas.setResolution( fResolution );
//		canvas.setTargetSetId( iTargetCollectionSetId );
//		canvas.setcolorDataPoints(fColorSpots);
//		
////		if ( sGridDetails.startsWith("no_grid") ) {
////			canvas.setShowGrid(false);
////		}
//		
//	}
//
//	@Override
//	public void undoCommandPart() throws CaleydoRuntimeException
//	{
//		
//	}
//
//}
