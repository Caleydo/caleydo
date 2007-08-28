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
//import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.view.opengl.canvas.heatmap.GLCanvasHeatmap2D;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHeatmap2D 
extends ACmdCreate_GlCanvasUser
implements ICommand {
	
	/**
	 * Contains values for selection.
	 * Note: should be replaced by VirtualArray
	 */
	protected int[] selectionArrayX;
	protected int[] selectionArrayY;
	
	protected float [] fResolution;
	
	/**
	 * If of Set to be read data from
	 * 
	 * @see cerberus.data.collection.ISet
	 */
	protected int iTargetCollectionSetId;
	
	protected int iHistogramLevel = 50;
	
	protected String color;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectHeatmap2D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		localManagerObjectType = CommandQueueSaxType.CREATE_GL_HEATMAP2D;
	}


	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		StringTokenizer token = new StringTokenizer(
				sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		int iSizeTokens= token.countTokens();
		
		fResolution = 
			StringConversionTool.convertStringToFloatArray(sAttribute3,iSizeTokens);
		
		iHistogramLevel = (int) fResolution[iSizeTokens-1];
		
		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
				sDetail, 
				-1 );
		
		if ( sAttribute4.length() > 0) 
		{
			if ( sAttribute4.contains( IGeneralManager.sDelimiter_Parser_DataType )) {
				
				StringTokenizer splitter = 
					new StringTokenizer(sAttribute4, IGeneralManager.sDelimiter_Parser_DataType);
								
				String bufferOne = splitter.nextToken();
				
				if ( bufferOne.length() > 0 ) {
					selectionArrayX = StringConversionTool.convertStringToIntArrayVariableLength(
							refGeneralManager.getSingelton().getLoggerManager(), 
							bufferOne, 
							IGeneralManager.sDelimiter_Parser_DataItems);
				}  //if ( bufferOne.length() > 0 ) {
				else
				{
					selectionArrayX = null;
				}  //if ( bufferOne.length() > 0 ) {..} else {..}
				
				if  (splitter.hasMoreTokens()) {
					selectionArrayY = StringConversionTool.convertStringToIntArrayVariableLength(
							refGeneralManager.getSingelton().getLoggerManager(), 
							splitter.nextToken(), 
							IGeneralManager.sDelimiter_Parser_DataItems);
				} //if  (splitter.hasMoreTokens()) {
				else 
				{
					selectionArrayY = null;
				} //if  (splitter.hasMoreTokens()) {..} else {..}
				
			}  //if ( sAttribute4.contains( IGeneralManager.sDelimiter_Parser_DataType )) {
			else 
			{
				
				selectionArrayX = StringConversionTool.convertStringToIntArrayVariableLength(
						refGeneralManager.getSingelton().getLoggerManager(), 
						this.sAttribute4, 
						IGeneralManager.sDelimiter_Parser_DataItems);
				
				selectionArrayY = null;
				
			}  //if ( sAttribute4.contains( IGeneralManager.sDelimiter_Parser_DataType )) {
			
		} //if ( sAttribute4.length() > 0) 
	}
	

	private void convertSelectionArrays( final int [] inSelectionArray, 
			int inSelectionArray_HalfSize,
			int[] outSelectionIndex,
			int[] outSelectionLength) {
		
		/* is it possible to create an two arrays from input array? */
		if ( inSelectionArray_HalfSize > 0) {
			
			int j = 0;
			for( int i=0; i < inSelectionArray_HalfSize; i++ ) 
			{
				outSelectionIndex[i]  = inSelectionArray[j++];
				outSelectionLength[i] = inSelectionArray[j++];
			}  //for( int i=0; i < inSelectionArray_HalfSize; i++ ) 

		} //if ( halfLengthSelectionArray > 0) {		
	} 
	
	
	@Override
	public void doCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasHeatmap2D canvas = 
			(GLCanvasHeatmap2D) openGLCanvasUser;
				
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		
		if ( iTargetCollectionSetId > -1 ) {
			canvas.setTargetSetId( iTargetCollectionSetId );
		}
		else 
		{
			refGeneralManager.getSingelton().logMsg( "CmdGLObjectHistogram2D no set defined!",
					LoggerType.ERROR_ONLY );
		}
		
		/**
		 * Handle selection via XML file..
		 */
		if  ((selectionArrayX != null )||(selectionArrayY != null))
		{
			int [] bufferSelectionIndexArrayX = null;
			int [] bufferSelectionLengthArrayX = null;		
			int [] bufferSelectionIndexArrayY = null;
			int [] bufferSelectionLengthArrayY = null;	
			
			if  (selectionArrayX != null ) {
				
				int iHalfSize = (int) selectionArrayX.length / 2;
				bufferSelectionIndexArrayX  = new int [iHalfSize];
				bufferSelectionLengthArrayX = new int [iHalfSize];
				
				convertSelectionArrays(selectionArrayX,
						iHalfSize,
						bufferSelectionIndexArrayX,
						bufferSelectionLengthArrayX );
				
			} //if  (selectionArrayX != null ) {
			
			if  (selectionArrayY != null ) {
				
				int iHalfSize = (int) selectionArrayY.length / 2;
				bufferSelectionIndexArrayY  = new int [iHalfSize];
				bufferSelectionLengthArrayY = new int [iHalfSize];
				
				convertSelectionArrays(selectionArrayY,
						iHalfSize,
						bufferSelectionIndexArrayY,
						bufferSelectionLengthArrayY );
			
			} //if  (selectionArrayY != null ) {
				
			canvas.setSelectionItems(bufferSelectionIndexArrayX,
					bufferSelectionLengthArrayX,
					bufferSelectionIndexArrayY,
					bufferSelectionLengthArrayY );
			
		}  //if ((selectionArrayX != null )||(selectionArrayY != null))
		
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {
		
		GLCanvasHeatmap2D canvas = 
			(GLCanvasHeatmap2D) openGLCanvasUser;
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
