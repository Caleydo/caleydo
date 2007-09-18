/**
 * 
 */
package org.geneview.core.command.view.opengl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn;
import org.geneview.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D;

/**
 * Creates IGLCanvasHeatmap2D or GLCanvasHeatmap2DColumn objects.
 * 
 * XML format:
 * 	<Cmd mementoId="0" process="RUN_CMD_NOW" type="CREATE_GL_HEATMAP2D"		cmdId="68993"	
 *				uniqueId="79401"
 *				///parent="[int][id of parent SWT container]" 	  [id of parent SWT container]..created with type="CREATE_VIEW_SWT_GLCANVAS" uniqueId="[id of parent SWT container]" 
 *				parent="12401" 
 *				///gl_forwarder="[int][id of CanvasForwareder]"   [id of CanvasForwareder]..created with type="CREATE_VIEW_SWT_GLCANVAS" gl_forwarder="[id of CanvasForwareder]"
 *				gl_forwarder="99078" 
 *				label="GL Triangle	C"
 *				///gl_origin OpenGL canvas, origin shift	
 *				gl_origin="0 0 0" 
 *				///gl_rotation="x y z rotation" OpenGL canvas, origin rotation	
 *				gl_rotation="1 0 0 15"		
 *				attrib3="0 10 0 40   -0.5f -0.5f    -4.0f 4.0f   -0.0f 1.0f    1.5f 1.0f   400" *				
 *				///attrib4="[int]StartIndex  [int]StopIndex  [int][>0..enable picking; <=0..disable picking | default=enalbe_picking]"			
 *				attrib4="0 50 1"	
 *				detail="35101" />		
 * 
 * @see org.geneview.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D
 * @see org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdGlObjectHeatmap2D 
extends ACmdCreate_GlCanvasUser {
	
	private boolean bUseDefaultHeatmap;
	
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
	 * @see org.geneview.core.data.collection.ISet
	 */
	//protected int iTargetCollectionSetId;
	
	protected int iHistogramLevel = 50;
	
	protected String color;
	
	protected ArrayList <Integer> iArSetIDs;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdGlObjectHeatmap2D(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final boolean useDefaultHeatmap) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		this.bUseDefaultHeatmap = useDefaultHeatmap;
		
		if  (bUseDefaultHeatmap) {
			localManagerObjectType = CommandQueueSaxType.CREATE_GL_HEATMAP2D;
		}
		else {
			localManagerObjectType = CommandQueueSaxType.CREATE_GL_HEATMAP2DCOLUMN;
		}
		
		iArSetIDs = new ArrayList <Integer> ();
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
		
//		iTargetCollectionSetId = StringConversionTool.convertStringToInt( 
//				sDetail, 
//				-1 );
		
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
	

//	private void convertSelectionArrays( final int [] inSelectionArray, 
//			int inSelectionArray_HalfSize,
//			int[] outSelectionIndex,
//			int[] outSelectionLength) {
//		
//		/* is it possible to create an two arrays from input array? */
//		if ( inSelectionArray_HalfSize > 0) {
//			
//			int j = 0;
//			for( int i=0; i < inSelectionArray_HalfSize; i++ ) 
//			{
//				outSelectionIndex[i]  = inSelectionArray[j++];
//				outSelectionLength[i] = inSelectionArray[j++];
//			}  //for( int i=0; i < inSelectionArray_HalfSize; i++ ) 
//
//		} //if ( halfLengthSelectionArray > 0) {		
//	} 
	
	
	@Override
	public void doCommandPart() throws GeneViewRuntimeException {

		IGLCanvasHeatmap2D canvas;
		
		if  (this.bUseDefaultHeatmap) {
			canvas = 
				(IGLCanvasHeatmap2D) openGLCanvasUser;
		} else {
			canvas = 
				(GLCanvasHeatmap2DColumn) openGLCanvasUser;
		}		
		
		canvas.setOriginRotation( cameraOrigin, cameraRotation );
		canvas.setResolution( fResolution );
		
		Iterator<Integer> iter = iArSetIDs.iterator();
		while ( iter.hasNext() ) {
			canvas.setTargetSetId(  iter.next());
		}
//		if ( iTargetCollectionSetId > -1 ) {
//			canvas.setTargetSetId( iTargetCollectionSetId );
//			canvas.setTargetSetId( iTargetCollectionSetId );
//			canvas.setTargetSetId( iTargetCollectionSetId );
//		}
//		else 
//		{
//			refGeneralManager.getSingelton().logMsg( "CmdGLObjectHistogram2D no set defined!",
//					LoggerType.ERROR_ONLY );
//		}
		
		/**
		 * Handle selection via XML file..
		 */
		if  (selectionArrayX != null )
		{
			GLCanvasHeatmap2DColumn canvs2 = (GLCanvasHeatmap2DColumn) canvas;
			canvs2.setRednerIndexStartStop( selectionArrayX[0], selectionArrayX[1] );
			
			if ( selectionArrayX.length > 2) {
				if  (selectionArrayX[2] > 0) {
					canvs2.setEnablePicking( true );
				} else {
					canvs2.setEnablePicking( false );
				}
			}
			
			/**
			 * register external window.
			 * link child windows to this parent window..
			 * 
			 * TODO: use Event-Mediator and a SetSelection instead of this work around! 
			 */
			if ( selectionArrayX.length > 3 ) 
			{
				int [] idArray = new int[selectionArrayX.length-3];
				
				int j=0;
				for (int i=3; i < selectionArrayX.length; i++ )
				{
					idArray[j] = selectionArrayX[i];
					j++;
				}
				
				/**
				 * TODO: use Event-Mediator and a SetSelection instead of this work around! 
				 */
				canvs2.link_ChildWindows_to_ParentWindow(idArray);
			}
			/**
			 * end: register external window.
			 */
			
			
		}  //if  (selectionArrayX != null )
		
		
//		if  ((selectionArrayX != null )||(selectionArrayY != null))
//		{
//			int [] bufferSelectionIndexArrayX = null;
//			int [] bufferSelectionLengthArrayX = null;		
//			int [] bufferSelectionIndexArrayY = null;
//			int [] bufferSelectionLengthArrayY = null;	
//			
//			if  (selectionArrayX != null ) {
//				
//				int iHalfSize = (int) selectionArrayX.length / 2;
//				bufferSelectionIndexArrayX  = new int [iHalfSize];
//				bufferSelectionLengthArrayX = new int [iHalfSize];
//				
//				convertSelectionArrays(selectionArrayX,
//						iHalfSize,
//						bufferSelectionIndexArrayX,
//						bufferSelectionLengthArrayX );
//				
//			} //if  (selectionArrayX != null ) {
//			
//			if  (selectionArrayY != null ) {
//				
//				int iHalfSize = (int) selectionArrayY.length / 2;
//				bufferSelectionIndexArrayY  = new int [iHalfSize];
//				bufferSelectionLengthArrayY = new int [iHalfSize];
//				
//				convertSelectionArrays(selectionArrayY,
//						iHalfSize,
//						bufferSelectionIndexArrayY,
//						bufferSelectionLengthArrayY );
//			
//			} //if  (selectionArrayY != null ) {
//				
////			canvas.setSelectionItems(bufferSelectionIndexArrayX,
////					bufferSelectionLengthArrayX,
////					bufferSelectionIndexArrayY,
////					bufferSelectionLengthArrayY );
//		}  //if ((selectionArrayX != null )||(selectionArrayY != null))
		
		
	}

	@Override
	public void undoCommandPart() throws GeneViewRuntimeException {
		
		IGLCanvasHeatmap2D canvas;
		
		if  (this.bUseDefaultHeatmap) {
			canvas = 
				(IGLCanvasHeatmap2D) openGLCanvasUser;
		} else {
			canvas = 
				(GLCanvasHeatmap2DColumn) openGLCanvasUser;
		}		
		
		canvas.destroyGLCanvas();
		canvas = null;
	}
}
