///**
// * 
// */
//package org.caleydo.core.command.view.opengl;
//
//
//import java.util.StringTokenizer;
//
//import org.caleydo.core.command.CommandQueueSaxType;
//import org.caleydo.core.command.base.ACmdCreate_GlCanvasUser;
//import org.caleydo.core.manager.ICommandManager;
//import org.caleydo.core.manager.IGeneralManager;
//import org.caleydo.core.parser.parameter.IParameterHandler;
////import org.caleydo.core.manager.ILoggerManager.LoggerType;
////import org.caleydo.core.manager.command.factory.CommandFactory;
//import org.caleydo.core.util.exception.CaleydoRuntimeException;
//import org.caleydo.core.util.system.StringConversionTool;
////import org.caleydo.core.view.opengl.canvas.scatterplot.GLCanvasScatterPlot2D;
//import org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
//import org.caleydo.core.view.opengl.canvas.scatterplot.GLMinMaxScatterplot2Dinteractive;
//
///**
// * @author Michael Kalkusch
// *
// */
//public class CmdGlObjectScatterPlot2D 
//extends ACmdCreate_GlCanvasUser {
//	
//	protected int[] iResolution;
//	
//	/**
//	 * If of Set to be read data from
//	 * 
//	 * @see org.caleydo.core.data.collection.ISet
//	 */
//	protected int iTargetCollectionSetId;
//	
//	protected String color;
//	
//	protected float [] fResolution;
//	
//	/**
//	 * Constructur.
//	 * 
//	 */
//	public CmdGlObjectScatterPlot2D(
//			final IGeneralManager generalManager,
//			final ICommandManager commandManager,
//			final CommandQueueSaxType commandQueueSaxType)
//	{
//		super(generalManager, 
//				commandManager,
//				commandQueueSaxType);
//		
//		iResolution = new int[3];
//		
//		localManagerObjectType = CommandQueueSaxType.CREATE_GL_SCATTERPLOT2D;
//	}
//
//	public void setParameterHandler( final IParameterHandler parameterHandler ) {
//		
//		super.setParameterHandler(parameterHandler);
//		StringTokenizer token = new StringTokenizer(
//				sAttribute3,
//				IGeneralManager.sDelimiter_Parser_DataItems);
//		int iSizeTokens= token.countTokens();
//		
//		fResolution = 
//			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
//		
//		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
//				this.sDetail, 
//				-1 );
//		
//		iResolution = StringConversionTool.convertStringToIntArray(
//				generalManager.getSingelton().getLoggerManager(), 
//				sAttribute3,
//				3 );
//		
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
//	 */
//	public final void doCommand()
//	{
//		super.doCommand();
//		
//		int[] iArTmp = new int[iArSetIDs.size()];
//		for(int index = 0; index < iArSetIDs.size(); index++)
//			iArTmp[index] = iArSetIDs.get(index);
//		
//		((GLCanvasScatterplot2D)gLEventListener).addSetId(iArTmp);
//	}
//
//	/*
//	 * (non-Javadoc)
////	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#undoCommand()
//	 */
//	public final void undoCommand()
//	{
//		super.undoCommand();
//	}
//
//	@Override
//	public void doCommandPart() throws CaleydoRuntimeException
//	{
//		GLMinMaxScatterplot2Dinteractive canvas = 
//			(GLMinMaxScatterplot2Dinteractive) openGLCanvasUser;
//				
//		canvas.setOriginRotation( cameraOrigin, cameraRotation );
//		canvas.setResolution( fResolution );
//		canvas.setTargetSetId( iTargetCollectionSetId );
//	}
//
//	@Override
//	public void undoCommandPart() throws CaleydoRuntimeException
//	{
//		GLMinMaxScatterplot2Dinteractive canvas =
//			(GLMinMaxScatterplot2Dinteractive) openGLCanvasUser;
//		
//		canvas.destroyGLCanvas();
//		canvas = null;
//	}
//}
