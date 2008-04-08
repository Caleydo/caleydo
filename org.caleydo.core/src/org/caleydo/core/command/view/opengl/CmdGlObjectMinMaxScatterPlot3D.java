//package org.geneview.core.command.view.opengl;
//
//import org.geneview.core.command.CommandQueueSaxType;
//import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
//import org.geneview.core.manager.ICommandManager;
//import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.parser.parameter.IParameterHandler;
//import org.geneview.core.util.exception.GeneViewRuntimeException;
//import org.geneview.core.util.system.StringConversionTool;
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
//	 * @see org.geneview.core.data.collection.ISet
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
//	public void doCommandPart() throws GeneViewRuntimeException
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
//	public void undoCommandPart() throws GeneViewRuntimeException
//	{
//		
//	}
//}
