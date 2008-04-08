//package org.caleydo.core.command.view.opengl;
//
//import org.caleydo.core.command.CommandQueueSaxType;
//import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
//import org.caleydo.core.manager.ICommandManager;
//import org.caleydo.core.manager.IGeneralManager;
//import org.caleydo.core.parser.parameter.IParameterHandler;
//import org.caleydo.core.util.exception.CaleydoRuntimeException;
//import org.caleydo.core.util.system.StringConversionTool;
//
///**
// * @author Michael Kalkusch
// *
// */
//public class CmdGlObjectMinMaxScatterPlot3D 
//extends ACmdCreate_GlCanvasUser {
//
//	protected float[] fResolution;
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
//	public CmdGlObjectMinMaxScatterPlot3D(
//			final IGeneralManager refGeneralManager,
//			final ICommandManager refCommandManager,
//			final CommandQueueSaxType refCommandQueueSaxType)
//	{
//		super(refGeneralManager,
//				refCommandManager,
//				refCommandQueueSaxType);
//		
//		localManagerObjectType = CommandQueueSaxType.CREATE_GL_MINMAX_SCATTERPLOT3D;
//	}
//
//	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
//		
//		super.setParameterHandler(refParameterHandler);
//		
//		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
//				this.sDetail, 
//				-1 );
//		
//		fResolution = StringConversionTool.convertStringToFloatArray( 
//				sAttribute3,
//				14 );
//	}
//	
//	@Override
//	public void doCommandPart() throws CaleydoRuntimeException
//	{
//		GLCanvasMinMaxScatterPlot3D canvas = 
//			(GLCanvasMinMaxScatterPlot3D) openGLCanvasUser;
//				
//		canvas.setOriginRotation( cameraOrigin, cameraRotation );
//		canvas.setResolution( fResolution );
//		canvas.setTargetSetId( iTargetCollectionSetId );
//	}
//
//	@Override
//	public void undoCommandPart() throws CaleydoRuntimeException
//	{
//		
//	}
//}
