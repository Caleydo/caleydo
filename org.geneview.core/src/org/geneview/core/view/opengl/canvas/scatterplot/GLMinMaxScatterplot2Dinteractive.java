/**
 * 
 */
package org.geneview.core.view.opengl.canvas.scatterplot;

import gleem.linalg.Vec2f;
import gleem.linalg.open.Vec4i;

import java.awt.Color;
import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.collection.storage.FlatThreadStorageSimple;
import org.geneview.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.event.EventPublisher;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.math.statistics.minmax.MinMaxDataInteger;
import org.geneview.core.util.mapping.color.ColorMapping;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.heatmap.AGLCanvasHeatmap2D;
import org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
import org.geneview.core.view.opengl.util.GLInfoAreaRenderer;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

import com.sun.opengl.util.BufferUtil;

/**
 * @author Michael Kalkusch
 * 
 * @see org.geneview.core.view.opengl.IGLCanvasUser
 */
public class GLMinMaxScatterplot2Dinteractive
extends AGLCanvasHeatmap2D
//		extends GLCanvasHeatmap2D
		implements IMediatorReceiver, IMediatorSender {

	public static final int OFFSET = AGLCanvasHeatmap2D.OFFSET;

	public static final int X = AGLCanvasHeatmap2D.X;

	public static final int MIN = AGLCanvasHeatmap2D.MIN;

	public static final int Y = AGLCanvasHeatmap2D.Y;

	public static final int Z = AGLCanvasHeatmap2D.Z;
	
	public static final int COLOR = 2;

	public static final int MAX = AGLCanvasHeatmap2D.MAX;

	
	private int iHeatmapDisplayListId = -1;
	
//	private float fSelectionPixel = 0.1f;
//
//	private float fSelectionLineWidth = 2.0f;

	private boolean bRenderingStyleFlat = true;
	
	private int iSelectedMode_identifyier = 0;
	
//	private float fIncX_forSelection;
	
	private ColorMapping colorMapper;
	
	/**
	 * If this limit is exceeded no lines between the data values will be shown 
	 */
	private final int iRenderIndexLength_UpperLimit = 1000;
	
	private boolean bRenderEnableDelimiterPerExperiment = true; 
	
	//private int[] iIndexPickedCoored = {-1,-1};
	
	/**
	 * used to store index, that shall not be rendered.
	 */
	private HashMap <Integer,Boolean> hashHighlightSelectionIndex; 
	
	/** 
	 * remove 	duplicates using a HashMap..
	 * 
	 * @see GLCanvasHeatmap2DColumn#setSelectedIndex_asArrayList(ArrayList, ArrayList)
	 */
	private HashMap <Integer,Integer> hashRemoveDuplicates;
	
	/**
	 * 
	 */
	private ArrayList < ArrayList <Integer>> alHighlightSelectionIndex_sortByDepth;

	//private HashMap<Integer, Integer> hashNCBI_GENE2index;

	private int[] selectedIndex;

	private int[] selectedIndexMode;

	private HashMap<Integer, Integer> hashExternalId_2_HeatmapIndex;
	
	private HashMap<Integer, Integer> hashExternalId_2_HeatmapIndex_reverse;

	private int iRenderIndexStart = 0;

	private int iRenderIndexStop = 80;
	
	private HashMap <Integer,Vec4i> hashChildrenWindow;

	private static final int iInitialSizeArrayList_Selection = 10;
	
	private ISet alTargetSet;

	private PathwayVertexGraphItem pickedGeneVertex;
		
	private GLInfoAreaRenderer infoAreaRenderer;
	
	private boolean bEnablePicking = true;
	
	private boolean bUseGLWireframe = false;
	
	private boolean bEnalbeMultipleSelection = false;
	
	private int iSetCacheId = 0;
	
	private MinMaxDataInteger refMinMaxDataInteger;
	
	private ArrayList <Vec2f> fIndexPickedCoored = new ArrayList <Vec2f> (1);
	
	private int iScatterplotDimensions = 2;
	
	private int [] iScatterplotMin;
	private int [] iScatterplotMax;
	
	private float [] fScatterplotScale;
	
	private float [] fScatterplotMinMaxRange;
	
	               
	/**
	 * Picking Mouse handler
	 */
	protected PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	protected int[] iSelectionStartAtIndexX;

	protected int[] iSelectionStartAtIndexY;

	protected int[] iSelectionLengthX;

	protected int[] iSelectionLengthY;
	
	/**
	 * Stretch lowest color in order to be not Back!
	 */
	private float fColorMappingPercentageStretch = 0.2f;
	
	
	/**
	 * @param setGeneralManager
	 */
	public GLMinMaxScatterplot2Dinteractive(final IGeneralManager setGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {
		
		super(setGeneralManager, 
				null,
				iViewId, 
				iParentContainerId, 
				sLabel);

		fAspectRatio = new float[2][3];
		viewingFrame = new float[3][2];

		fAspectRatio[X][MIN] = 0.0f;
		fAspectRatio[X][MAX] = 20.0f;
		fAspectRatio[Y][MIN] = 0.0f;
		fAspectRatio[Y][MAX] = 20.0f;

		fAspectRatio[Y][OFFSET] = 0.0f;
		fAspectRatio[Y][OFFSET] = -2.0f;

		viewingFrame[X][MIN] = -1.0f;
		viewingFrame[X][MAX] = 1.0f;
		viewingFrame[Y][MIN] = 1.0f;
		viewingFrame[Y][MAX] = -1.0f;

		viewingFrame[Z][MIN] = 0.0f;
		viewingFrame[Z][MAX] = 0.0f;
		
		fScatterplotMinMaxRange = new float [2];
		fScatterplotMinMaxRange[X] = viewingFrame[X][MAX] - viewingFrame[X][MIN];
		fScatterplotMinMaxRange[Y] = viewingFrame[Y][MAX] - viewingFrame[Y][MIN];
		

		refMinMaxDataInteger = new MinMaxDataInteger(1);
		
		
		//hashNCBI_GENE2index = new HashMap<Integer, Integer>();

		System.err.println("  GLCanvasHeatmap2DColumn()");		

		selectedIndex = new int[0];
		selectedIndexMode = new int[0];

		//hashExternalId_2_HeatmapIndex = new HashMap<Integer, Integer>(100);

		hashHighlightSelectionIndex = new HashMap <Integer,Boolean> ();
		
//		int[] selIndex =
//		{ 3, 5, 10, 15 };
//		int[] selIndexMode =
//		{ 0, 1, 2, 3 };
//		this.setSelectedIndex(selIndex, selIndexMode);
		
		alHighlightSelectionIndex_sortByDepth = new ArrayList < ArrayList <Integer>> (5);
		
		alHighlightSelectionIndex_sortByDepth.add( new ArrayList <Integer> (5));
		
		infoAreaRenderer = new GLInfoAreaRenderer(refGeneralManager,
				new GLPathwayManager(setGeneralManager));
		
		colorMapper = new ColorMapping(0, 60000);
		
		hashChildrenWindow = new HashMap <Integer,Vec4i> (4);		
	}
	
	private void initScatterplot( final int iDimensions,
			MinMaxDataInteger minmaxContainer ) {
	
		iScatterplotDimensions = iDimensions;
		
		iScatterplotMin = new int [iDimensions];
		iScatterplotMax = new int [iDimensions];
		
		fScatterplotScale = new float [iDimensions] ;
		
		for ( int i=0; i<iDimensions; i++) 
		{
			iScatterplotMin[i] = (int) minmaxContainer.getMin(i);
			iScatterplotMax[i] = (int) minmaxContainer.getMax(i);
			
			fScatterplotScale[i] = 1.0f / (iScatterplotMax[i] - iScatterplotMin[i]);
		}
	}
	
	/**
	 * @return the bEnablePicking
	 */
	public final boolean isEnablePicking() {
	
		return bEnablePicking;
	}
	
	/**
	 * @param enablePicking the bEnablePicking to set
	 */
	public final void setEnablePicking(boolean enablePicking) {
	
		bEnablePicking = enablePicking;
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + ".setEnablePicking( " +
				Boolean.toString(enablePicking) + " )",
				LoggerType.STATUS);
	}
	
	private int addhighlightSelectionIndex_addItem( ArrayList < ArrayList <Integer>> container,
			final Integer value, 
			final int mode, 
			final int containerSize) {
		if ( mode < containerSize ) 
		{
			/* allocate new */
			int iDifference = mode - containerSize;
			
			/* insert missing empty ArrayList <Integer> objects .. */
			for (int i=0; i<iDifference; i++) 
			{
				container.add( new ArrayList<Integer> () );
			}
			
			container.get(mode).add( new Integer(value));
			return mode;
		}
		
		container.get(mode).add( new Integer(value));
		return containerSize;
	}
	
	private void removehighlightSelectionIndex_Item( ArrayList < ArrayList <Integer>> container,
			final Integer value  ) {
		Iterator <ArrayList <Integer>> iter = container.iterator();
		
		while ( iter.hasNext()) 
		{
			if ( iter.next().remove( value ) ) 
			{
				/* found value, exit immediately */
				return;
			}
		} //while ( iter.hasNext()) {
		
		assert false : "value could not be removed!";
	}
	
	protected void assignHighlightSelectionIndex( final ArrayList <Integer> indexList, 
			final ArrayList <Integer> indexListMode ) {
		
		Iterator<Integer> iterIndex = indexList.iterator();
		Iterator<Integer> iterIndexMode = indexListMode.iterator();

		int iSizeContainer = alHighlightSelectionIndex_sortByDepth.size();
		
		while (iterIndex.hasNext()) 
		{
			Integer buffer = iterIndex.next();
			int bufferMode = iterIndexMode.next().intValue();
			
			if ( bufferMode > 0 ) 
			{
				iSizeContainer = addhighlightSelectionIndex_addItem(
					this.alHighlightSelectionIndex_sortByDepth,
					buffer,
					bufferMode,
					iSizeContainer);
			
				hashHighlightSelectionIndex.put(buffer, new Boolean(true));
			} 
			else 
			{
				removehighlightSelectionIndex_Item(alHighlightSelectionIndex_sortByDepth, buffer);
				
				hashHighlightSelectionIndex.remove(buffer);
			} //if ( bufferMode > 0 ) {..}else{..
			
		} //alHighlightSelectionIndex_sortByDepth
		
	}

	public void setSelectedIndex_asArrayList(
			final ArrayList<Integer> updateIndex,
			final ArrayList<Integer> updateIndexMode) {

		/* integrity check.. */
		if (updateIndex.size() != updateIndexMode.size())
		{
			assert false : "setSelectedIndex()  int [] updateIndex !=  int [] updateIndexMode !";
			return;
		}

		/**
		 * remove duplicates using a HashMap.. 
		 */
		if (hashRemoveDuplicates == null) 
		{
			hashRemoveDuplicates = new HashMap <Integer,Integer>();
		}
		else
		{
			hashRemoveDuplicates.clear();
		}
		
		Iterator<Integer> iterIndex = updateIndex.iterator();
		Iterator<Integer> iterIndexMode = updateIndexMode.iterator();
		while (iterIndex.hasNext()) 
		{
			hashRemoveDuplicates.put(iterIndex.next(), iterIndexMode.next());
		}
		
		/* use keys for selectedIndex[] .. */
		/* assign set to get Iterator and iNoDuplicatesSize = resultNoDuplicates.size() */
		Set <Integer> resultNoDuplicates = hashRemoveDuplicates.keySet();		
		Iterator <Integer> iterNoDuplicatesIndex = resultNoDuplicates.iterator();
		int iNoDuplicatesSize = resultNoDuplicates.size();
		
		/* use values for selectedIndexMode[] .. */
		Iterator <Integer> iterNoDuplicatesIndexMode = hashRemoveDuplicates.values().iterator();
		
		/**
		 * end: remove duplicates using a HashMap.. 
		 */
		
		/* does size of arrays fit? */
		if (selectedIndex.length != iNoDuplicatesSize)
		{
			selectedIndex = new int[iNoDuplicatesSize];
			selectedIndexMode = new int[iNoDuplicatesSize];
		}

		for ( int index=0; index< iNoDuplicatesSize; index++ ) 
		{
			selectedIndex[index] = iterNoDuplicatesIndex.next().intValue();			
			selectedIndexMode[index] = iterNoDuplicatesIndexMode.next().intValue();		
		}
	}

//	public void setSelectedIndex(final int[] updateIndex,
//			final int[] updateIndexMode) {
//
//		/* integrity check.. */
//		if (updateIndex.length != updateIndexMode.length)
//		{
//			assert false : "setSelectedIndex()  int [] updateIndex !=  int [] updateIndexMode !";
//			return;
//		}
//
//		boolean bUpdate = false;
//		for (int i = 0; i < updateIndex.length; i++)
//		{
//			if (hashExternalId_2_HeatmapIndex.containsKey(updateIndex[i]))
//			{
//
//				if (hashExternalId_2_HeatmapIndex.get(updateIndex[i]).intValue() != updateIndexMode[i])
//				{
//					hashExternalId_2_HeatmapIndex.put(updateIndex[i], updateIndexMode[i]);
//					bUpdate = true;
//				}
//				/* else .. data is identical to data in HashMap! */
//
//			} else
//			{
//				hashExternalId_2_HeatmapIndex.put(updateIndex[i], updateIndexMode[i]);
//				bUpdate = true;
//			}
//		}
//
//		if (bUpdate)
//		{
//			/* does size of arrays fit? */
//			if (selectedIndex.length != hashExternalId_2_HeatmapIndex.size())
//			{
//				selectedIndex = new int[hashExternalId_2_HeatmapIndex.size()];
//				selectedIndexMode = new int[hashExternalId_2_HeatmapIndex.size()];
//			}
//
//			Iterator<Integer> keys = hashExternalId_2_HeatmapIndex.keySet().iterator();
//			Iterator<Integer> values = hashExternalId_2_HeatmapIndex.values().iterator();
//
//			for (int index = 0; keys.hasNext(); index++)
//			{
//				selectedIndex[index] = keys.next().intValue();
//				selectedIndexMode[index] = values.next().intValue();
//			}
//		} //if ( bUpdate ) {
//
//	}
	

//	private void renderGL_Selection(GL gl) {
//
//		if (selectedIndex.length < 1)
//		{
//			/* nothing to render.. */
//			return;
//		}
//
//		/* public void render_displayListHeatmap(GL gl) { */
//
//		gl.glNormal3f(0.0f, 0.0f, 1.0f);
//
//		/**
//		 * force update ...
//		 */
//
//		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
//				/ (float) (iValuesInRow);
//
////		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[X][MIN])
////				/ (float) (iValuesInColum);
//
//		/* Y_min .. Y_max*/
//		for (int index = 0; index < selectedIndex.length; index++)
//		{
//			float fNowY = viewingFrame[Y][MIN];
//			float fNextY = viewingFrame[Y][MAX];
//
//			float fNowX = viewingFrame[X][MIN] + fIncX * selectedIndex[index];
//			float fNextX = fNowX + fIncX;
//
//			gl.glBegin(GL.GL_TRIANGLE_FAN);
//
//			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
//			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
//			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
//			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);
//
//			gl.glEnd();
//			gl.glPopName();
//
//		} //for (int yCoord_name=0; yCoord_name<iValuesInRow; yCoord_name++)
//
//	}

	
	/**
	 * @see org.geneview.core.view.opengl.IGLCanvasUser#initGLCanvas(javax.media.opengl.GLCanvas)
	 */
	@Override
	public void initGLCanvas(GL gl)
	{
		pickingTriggerMouseAdapter = 
			(PickingJoglMouseListener) 
			openGLCanvasDirector.getJoglCanvasForwarder().getJoglMouseListener();
				
		setInitGLDone();		
	}
	
	/**
	 * @param pickingTriggerMouseAdapter the pickingTriggerMouseAdapter to set
	 */
	public final void setPickingTriggerMouseAdapter(
			PickingJoglMouseListener pickingTriggerMouseAdapter) {
	
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
	}
	
	/** 
	 * init after IGenomeIdManager has load all its data from file.
	 */
	//protected void init(final IGenomeIdManager readFromIGenomeIdManager) {
	
//	protected void init_External2InternalHashMap() {

	
//	}

//	public void setKeysForHeatmap(int[] keys) {
//
//	}
//
//	public void selectKeys(int[] keys) {
//
//	}

	/* --------------------------- */
	/* -----  BEGEN: PICKING ----- */

	protected final boolean processHits(final GL gl, int iHitCount,
			int iArPickingBuffer[], final Point pickPoint,
			ArrayList<Vec2f> fIndexPickedCoored) {

		// System.out.println("Number of hits: " +iHitCount);


		float fDepthSort = Float.MAX_VALUE;
		int iResultPickCoordIndex = 0;

		int[] resultPickPointCoord =
		{ -1, -1 };

		int iPtr = 0;
		int i = 0;

		//int iPickedObjectId = 0;

		System.out.println("GLCanvasHeatmap2DColumn  PICK: ----- ");

		// Only pick object that is nearest
		for (i = 0; i < iHitCount; i++)
		{
			int iNumbersPerHit = iArPickingBuffer[iPtr];
			System.out.print(" #name for this hit=" + iArPickingBuffer[iPtr]);
			iPtr++;

//			// Check if object is nearer than previous objects
//			if (iArPickingBuffer[iPtr] < iMinimumZValue)
//			{
//				System.out.print(" nearer than previous hit! ");				
//			}

			/* ist doch ein float! */
			//iMinimumZValue = iArPickingBuffer[iPtr];
			float fZmin = (float) iArPickingBuffer[iPtr];

			System.out.print(" minZ=" + fZmin);
			iPtr++;
			System.out.print(" maxZ=" + (float) iArPickingBuffer[iPtr]);
			iPtr++;

			System.out.print(" Pick-> [");

			int iName = -1;

			for (int j = 0; j < iNumbersPerHit; j++)
			{
				iName = iArPickingBuffer[iPtr];
				if (fZmin < fDepthSort)
				{
					fDepthSort = fZmin - AGLCanvasHeatmap2D.fPickingBias;
					if (iResultPickCoordIndex < 2)
					{
						resultPickPointCoord[iResultPickCoordIndex] = iName;
						iResultPickCoordIndex++;
					}
				}

				System.out.print(" #" + i + " name=" + iName + ",");
				iPtr++;
			}
			System.out.println("]");
		}

		if (iHitCount < 1)
		{
			// Remove pathway pool fisheye

			return false;
		}

		System.out
				.println("GLCanvasHeatmap2DColumn  PICKED index=["
						+ resultPickPointCoord[0] + ","
						+ resultPickPointCoord[1] + "]");
		
		// Reset pick point
		//source of problems??		
		infoAreaRenderer.convertWindowCoordinatesToWorldCoordinates(gl,
				pickPoint.x, pickPoint.y);

//		// Check if real picking is performed
//		if (bIsMousePickingEvent)
//		{	
			int[] selectedIndexArray = addPickedPoint(fIndexPickedCoored,
					//(float) resultPickPointCoord[0],
					(float) resultPickPointCoord[0] + this.iRenderIndexStart,
					(float) resultPickPointCoord[1] );
			
//			if ( selectedIndexArray != null ) {
//	
//				/** notify other listeners .. */
//				notifyReceiver_PickedObject_singleSelection(selectedIndexArray, iSelectedMode_identifyier);
//	
////				Integer iNCBIGeneID = hashExternalId_2_HeatmapIndex_reverse.get(resultPickPointCoord[0]);
////				if ( iNCBIGeneID != null) 
////				{
////					PathwayVertexGraphItem pickedGeneVertex;
////					pickedGeneVertex = (PathwayVertexGraphItem) refGeneralManager.getSingelton(
////							).getPathwayItemManager().getItem(iNCBIGeneID.intValue());
////					
////					infoAreaRenderer.renderInfoArea(gl, pickedGeneVertex);
////				}
//			}
			
//			/* hack, check if external hashmap was already created */
//			//TODO: clean up this call, init from outside as soon as data is available!
//			if ( hashExternalId_2_HeatmapIndex==null ) {			
//				this.init_External2InternalHashMap();
//			}
			
			/** 
			 * Render details on enzyme in heatmap.. 
			 */
//			Integer iNCBIGeneID = hashExternalId_2_HeatmapIndex_reverse.get(resultPickPointCoord[0]);
//			if ( iNCBIGeneID != null) 
//			{
//				//PathwayVertexGraphItem pickedGeneVertex;
//				pickedGeneVertex = (PathwayVertexGraphItem) refGeneralManager.getSingelton(
//						).getPathwayItemManager().getItem(iNCBIGeneID.intValue());
//				
//				assert pickedGeneVertex != null : "should not get null-pointer!";
//				
//				infoAreaRenderer.renderInfoArea(gl, pickedGeneVertex);
//			}
			/** 
			 * END: Render details on enzyme in heatmap.. 
			 */
			
			//source of problems??		
			//infoAreaRenderer.resetPoint();
//		}
		
		return true;
	}

	protected int[] addPickedPoint( ArrayList <Vec2f> fIndexPickedCoord, 
			final float addIndexCoordX, float addIndexCoordY) {
		
		int iSize = fIndexPickedCoord.size();
		
		if ( (iSize % 2) == 1) {
			/* one remaining point of last picking */
			Vec2f lastPickedIndexCoord= fIndexPickedCoord.get(iSize-1);
			
			Vec2f lowerLeftPoint = new Vec2f();
			Vec2f upperRightPoint = new Vec2f();
			
			/* create a rectangle with lower.left point and upper,right point */
			if (lastPickedIndexCoord.x() < addIndexCoordX ) {
				lowerLeftPoint.setX( lastPickedIndexCoord.x() );
				upperRightPoint.setX( addIndexCoordX );
			} else {
				lowerLeftPoint.setX( addIndexCoordX );
				upperRightPoint.setX( lastPickedIndexCoord.x());
			}
			
			/* create a rectangle with lower.left point and upper,right point */
			if (lastPickedIndexCoord.y() < addIndexCoordY) {
				lowerLeftPoint.setY( lastPickedIndexCoord.y() );
				upperRightPoint.setY( addIndexCoordY );
			} else {
				lowerLeftPoint.setY( addIndexCoordY );
				upperRightPoint.setY( lastPickedIndexCoord.y());
			}
			
			if  ( bEnalbeMultipleSelection ) 
			{
				fIndexPickedCoord.set( iSize-1, lowerLeftPoint);
				fIndexPickedCoord.add( upperRightPoint );
			}
			else 
			{			
				fIndexPickedCoord.clear();
				fIndexPickedCoord.add( lowerLeftPoint);
				fIndexPickedCoord.add( upperRightPoint );
			}
			
			/** Calculate all indices between left and right point and create an array with all these indices.. */
			int iCurrentIndex = (int) lowerLeftPoint.x();
			int iLength = (int) upperRightPoint.x() - iCurrentIndex;
			
			int[] resultArray = new int[iLength+1];
			for ( int i=0; i< iLength+1; i++) {
				resultArray[i] = iCurrentIndex;
				iCurrentIndex++;
			}
			
			return resultArray;
		} else {
			fIndexPickedCoord.add( new Vec2f(addIndexCoordX,addIndexCoordY) );
			return null;
		}		
	}
	

	/* -----   END: PICKING  ----- */
	/* --------------------------- */
	
//	private void notifyReceiver_PickedObject_singleSelection( final int [] resultPickPointCoord, 
//			final int iModeValue) {
//		
//		if ( resultPickPointCoord.length < 1) {
//			return;
//		}
//		
//		int [] resultSelectedIndex = new int [resultPickPointCoord.length];
//		int [] resultSelectedIndexMode = new int [resultPickPointCoord.length];
//		
//		/* copy new indices to end of existing array.. */
//		for ( int iIndex=0; iIndex<resultPickPointCoord.length; iIndex++) {
//			resultSelectedIndex[iIndex] = resultPickPointCoord[iIndex];
//			resultSelectedIndexMode[iIndex] = iModeValue;
//		}
//		
//		/* swap .. */
//		selectedIndex = resultSelectedIndex;
//		selectedIndexMode = resultSelectedIndexMode;
//		
//		/* hack, check if external hashmap was already created */
//		//TODO: clean up this call, init from outside as soon as data is available!
//		if ( hashExternalId_2_HeatmapIndex==null ) {			
//			this.init_External2InternalHashMap();
//		}
//		
//		
//		/* notify external objects via eventMediator.. */
//		if ( hashExternalId_2_HeatmapIndex_reverse != null ) {
//			/* data for SetSelection.. */
//			SetSelection selectionSet = new SetSelection(-5, refGeneralManager);
//			
//			ArrayList <Integer> alSelectionId = new ArrayList <Integer> (resultPickPointCoord.length);
//			ArrayList <Integer> alSelectionId_PathwayId = new ArrayList <Integer> (resultPickPointCoord.length);
//			
//			for ( int i=0; i< resultPickPointCoord.length; i++ ) {
//				/* convert internal index to external id.. */
//				Integer lookupValue = hashExternalId_2_HeatmapIndex_reverse.get(resultPickPointCoord[i]);
//				if ( lookupValue ==  null ) {
//					return;
//				}
//			
//				PathwayVertexGraphItem vertexItemBuffer = (PathwayVertexGraphItem) refGeneralManager.getSingelton()
//					.getPathwayItemManager().getItem(lookupValue.intValue());
//										
//				Iterator <IGraphItem> iterList = 
//					vertexItemBuffer.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD).iterator();
//				
//				while (iterList.hasNext()) {
//					IGraphItem bufferItem = iterList.next();					
//					alSelectionId.add( bufferItem.getId() );
//					//alSelectionId.add( iterList.next().getId() );
//					
//					// get pathway id from graph
//					 List<IGraph> list = bufferItem.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT);
//					 PathwayGraph buffer = (PathwayGraph) list.get(0);
//					 alSelectionId_PathwayId.add( buffer.getKeggId() );
//				}							
//			}
//	
//			int[] iArSelectionId = new int[alSelectionId.size()];
//			int[] iArSelectionDepthData = new int[alSelectionId.size()];
//			int[] iArSelectionOptionalData = new int[alSelectionId.size()];
//			
//			for ( int i=0; i < alSelectionId.size(); i++) {
//				iArSelectionId[i] = alSelectionId.get(i).intValue();
//				iArSelectionDepthData[i] = iModeValue;	
//				iArSelectionOptionalData[i] = alSelectionId_PathwayId.get(i);
//			}
//			
//			IStorage [] storageArray = new IStorage [3];
//			for ( int i=0; i< 3 ; i++ ) {
//				storageArray[i] = new FlatThreadStorageSimple( selectionSet.getId(), refGeneralManager, null);		
//				selectionSet.setStorageByDim(storageArray, 0);
//			}
//			
//			selectionSet.setAllSelectionDataArrays(iArSelectionId, iArSelectionDepthData, iArSelectionOptionalData );
//			
//			// Calls update with the ID of the PathwayViewRep
//	 		((EventPublisher)refGeneralManager.getSingelton().
//				getEventPublisher()).updateReceiver(this, selectionSet);
//	 		refGeneralManager.getSingelton().logMsg(this.getClass().getSimpleName()+
//	 				".broadcast selection event! " + selectionSet.toString(),
//	 				LoggerType.STATUS);
//		}
//	}

	private void notifyReceiver_PickedObject_multipleSelection( final int [] resultPickPointCoord, final int iModeValue) {
		
		if ( resultPickPointCoord.length < 1) {
			return;
		}
		
		int [] resultSelectedIndex = new int [selectedIndex.length + resultPickPointCoord.length];
		int [] resultSelectedIndexMode = new int [selectedIndex.length + resultPickPointCoord.length];
		
		/* copy existing array.. */
		for ( int i=0; i< selectedIndex.length; i++ ) {
			resultSelectedIndex[i]= selectedIndex[i];
			resultSelectedIndexMode[i]=selectedIndexMode[i];
		}
		
		/* copy new indices to end of existing array.. */
		int iIndex = selectedIndex.length;
		for ( int i=0; i<resultPickPointCoord.length; i++) {
			resultSelectedIndex[iIndex] = resultPickPointCoord[i];
			resultSelectedIndexMode[iIndex] = iModeValue;
			iIndex++;
		}
		
		/* swap .. */
		selectedIndex = resultSelectedIndex;
		selectedIndexMode = resultSelectedIndexMode;
		
//		if ( hashExternalId_2_HeatmapIndex==null ) {
//			this.init_External2InternalHashMap();
//		}
		
		
		/* notify external objects via eventMediator.. */
		if ( hashExternalId_2_HeatmapIndex_reverse != null ) {
			/* data for SetSelection.. */
			SetSelection selectionSet = new SetSelection(-5, refGeneralManager);
			
			ArrayList <Integer> alSelectionId = new ArrayList <Integer> (resultPickPointCoord.length);
			ArrayList <Integer> alSelectionId_PathwayId = new ArrayList <Integer> (resultPickPointCoord.length);
			
			for ( int i=0; i< resultPickPointCoord.length; i++ ) {
				/* convert internal index to external id.. */
				Integer lookupValue = hashExternalId_2_HeatmapIndex_reverse.get(resultPickPointCoord[i]);
				if ( lookupValue ==  null ) {
					return;
				}
			
				PathwayVertexGraphItem vertexItemBuffer = (PathwayVertexGraphItem) refGeneralManager.getSingelton()
					.getPathwayItemManager().getItem(lookupValue.intValue());
										
				Iterator <IGraphItem> iterList = 
					vertexItemBuffer.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD).iterator();
				
				while (iterList.hasNext()) {
					IGraphItem bufferItem = iterList.next();					
					alSelectionId.add( bufferItem.getId() );
					//alSelectionId.add( iterList.next().getId() );
					
					// get pathway id from graph
					 List<IGraph> list = bufferItem.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT);
					 PathwayGraph buffer = (PathwayGraph) list.get(0);
					 alSelectionId_PathwayId.add( buffer.getKeggId() );
				}							
			}

			int[] iArSelectionId = new int[alSelectionId.size()];
			int[] iArSelectionDepthData = new int[alSelectionId.size()];
			int[] iArSelectionOptionalData = new int[alSelectionId.size()];
			
			for ( int i=0; i < alSelectionId.size(); i++) {
				iArSelectionId[i] = alSelectionId.get(i).intValue();
				iArSelectionDepthData[i] = iModeValue;	
				iArSelectionOptionalData[i] = alSelectionId_PathwayId.get(i);
			}
			
			IStorage [] storageArray = new IStorage [3];
			for ( int i=0; i< 3 ; i++ ) {
				storageArray[i] = new FlatThreadStorageSimple( selectionSet.getId(), refGeneralManager, null);		
				selectionSet.setStorageByDim(storageArray, 0);
			}
			
			selectionSet.setAllSelectionDataArrays(iArSelectionId, iArSelectionDepthData, iArSelectionOptionalData );
			
			// Calls update with the ID of the PathwayViewRep
	 		((EventPublisher)refGeneralManager.getSingelton().
				getEventPublisher()).updateReceiver(this, selectionSet);
	 		refGeneralManager.getSingelton().logMsg("broadcast selection event! " + selectionSet.toString(),
	 				LoggerType.STATUS);
		}
	}

	protected void handlePicking(final GL gl) {

		Point pickPoint = null;
		
		/* if no pickingTriggerMouseAdapter was assinged yet, skip it.. */
		if  (pickingTriggerMouseAdapter==null) {
			return;
		}
		
		
//		if ( pickingTriggerMouseAdapter.wasMouseDragged() ) {
//			this.bMouseOverEvent = false;
//		}
//		if ( pickingTriggerMouseAdapter.wasMouseMoved())
//		pickingTriggerMouseAdapter.wasMouseMoved()
		
		if (pickingTriggerMouseAdapter.wasMousePressed())
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
//			bIsMousePickingEvent = true;
		}
//		else
//		{
//			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
//			bIsMousePickingEvent = false;
//		}

		// Check if an object was picked
		if (pickPoint != null)
		{
			pickObjects(gl, pickPoint);
		}

	}

	protected void pickObjects(final GL gl, Point pickPoint) {

		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);
		gl.glInitNames();

		//gl.glPushName(0);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();		
		gl.glLoadIdentity();
		
		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				1.0, 
				1.0, 
				viewport, 
				0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float h = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]) * 4.0f;

		// FIXME: values have to be taken from XML file!!
		gl.glOrtho(-4.0f, 4.0f, -h, h, 1.0f, 60.0f);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

//		renderPart4pickingX(gl);
//	
//		/* second layer of picking.. */
//		gl.glPushMatrix();
//		
//		gl.glTranslatef( 0,0, AGLCanvasHeatmap2D.fPickingBias );
//		renderPart4pickingY(gl);
//		
//		gl.glPopMatrix();
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		
	

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		
		//boolean bPickinedNewObject = 
		processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint, fIndexPickedCoored);
				
	}
	
	@Override
	public void renderPart(GL gl) {

//		int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;
//		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
//			/ (float) (alTargetSet.size());
//		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
//			/ (float) (iRenderIndexRangeX + 1);
		
		gl.glPushMatrix();
		
		if ( bEnablePicking ) {
			handlePicking(gl);
		}

		gl.glTranslatef(0, 0, 0.01f);

		if (alTargetSet== null )
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"createHistogram() can not create GLMinMaxScatterplot2d, because targetSet=null",
							LoggerType.STATUS);
			return;
		}

		if (iValuesInRow < 1)
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"createHistogram() can not create GLMinMaxScatterplot2Dinteractive, because histogramLevels are outside range [1..max]",
							LoggerType.FULL);
			return;
		}

		if ((alTargetSet.hasCacheChanged(iSetCacheId))
				|| (iHeatmapDisplayListId == -1))
		{

			//	    			System.out.print("H:");
			//	    			for ( int i=0;i<iHistogramIntervalls.length; i++) {
			//	    				System.out.print(";" +
			//	    						Integer.toString(iHistogramIntervalls[i]) );
			//	    			}
			//System.out.println("GLMinMaxScatterplot2Dinteractive - UPDATED!");

			render_createDisplayLists(gl);

			refGeneralManager.getSingelton().logMsg(
					"createHistogram() use ISet(" + alTargetSet.getId()
							+ ":" + alTargetSet.getLabel() + ")",
					LoggerType.FULL);

		}

		if (bUseGLWireframe)
		{
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		}

//		    else 
//		    {
//		    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
//		    }     

		//renderGLSingleQuad(gl,this.fIndexPickedCoored,0);

		gl.glDisable(GL.GL_LIGHTING);

		gl.glCallList(iHeatmapDisplayListId);

		if ( pickedGeneVertex!= null)
		{
			infoAreaRenderer.renderInfoArea(gl, pickedGeneVertex);
		}
		
//		render_Selection_ownArea(gl, fIncY);
//		gl.glTranslatef(0, 0, -AGLCanvasHeatmap2D.fPickingBias);
		
//		gl.glColor3f(1, 1, 0);
//		renderGLAllQuadRectangle(gl, this.fIndexPickedCoored);
//
//		gl.glColor3f(0.8f, 0.8f, 0);
//		renderGLAllQuadDots(gl, this.fIndexPickedCoored);

//		gl.glColor3f(0, 0, 0.8f);
//		renderGL_Selection(gl);
		  

		//if ( bEnablePicking ) {
//			gl.glTranslatef(0, 0, AGLCanvasHeatmap2D.fPickingBias);
//			render_picketPoints(gl, fIncY, fIncX);
		//}
		
//		if (pickedGeneVertex != null && infoAreaRenderer.isPositionValid())
//		{
//			infoAreaRenderer.renderInfoArea(gl, pickedGeneVertex);
//		}
		
		
		gl.glEnable(GL.GL_LIGHTING);

		//System.err.println(" Heatmap2D ! .render(GLCanvas canvas)");
		
		gl.glPopMatrix();
	}

	public void render_createDisplayLists(GL gl) {
		
		iHeatmapDisplayListId = gl.glGenLists(1);
		
		gl.glNewList(iHeatmapDisplayListId, GL.GL_COMPILE);	
		render_displayListHeatmap( gl );
		gl.glEndList();
		
		  refGeneralManager.getSingelton().logMsg(
				  "createHeatmap() create DsiplayList)",
				  LoggerType.FULL );
		  
	}
	

//	// public int[] createHistogram(final int iHistogramLevels) {
//	public void renderHeatmap(final int iHistogramLevels) {
//
//		refGeneralManager.getSingelton().logMsg("HEATMAP: set  ",
//				LoggerType.FULL);
//
//	}
//
//	public int getHeatmapValuesInRow() {
//
//		return iValuesInRow;
//	}


	protected void colorMapping(final GL gl, final int iValue) {

//		float fValue = fColorMappingMiddleValue - (float) iValue;
//
//		if (fValue < 0.0f)
//		{
//			// range [fColorMappingLowValue..fColorMappingMiddleValue[
//
//			float fScale = (fColorMappingLowRange + fValue)
//					* fColorMappingLowRangeDivisor;
//			gl.glColor3f(0, 1.0f - fScale, 0);
//
//			return;
//		}
//		//else
//
//		//range [fColorMappingMiddleValue..fColorMappingHighValue]
//		float fScale = (fValue) * fColorMappingHighRangeDivisor;
		
		Color tmpNodeColor = colorMapper.colorMappingLookup(iValue);
		gl.glColor4f(tmpNodeColor.getRed() / 255.0f, 
				tmpNodeColor.getGreen() / 255.0f, 
				tmpNodeColor.getBlue() / 255.0f, 1.0f);

	}

	public final void setRednerIndexStartStop(final int iSetRenderIndexStart,
			final int iSetRenderIndexStop) {

		if (iSetRenderIndexStart < iSetRenderIndexStop)
		{
			this.iRenderIndexStart = iSetRenderIndexStart;
			this.iRenderIndexStop = iSetRenderIndexStop;
		}
		else
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"Ignore render start/stop=["
									+ iSetRenderIndexStart
									+ "/"
									+ iSetRenderIndexStop
									+ "] , because start index is smaller than stop index!",
							LoggerType.MINOR_ERROR_XML);
		}
		
		if ( (iSetRenderIndexStop - iSetRenderIndexStart) > iRenderIndexLength_UpperLimit ) {
			bRenderEnableDelimiterPerExperiment = false;
		}
			
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setTargetSetId(int)
	 */
	public final void setTargetSetId(int iTargetCollectionSetId) {

		this.alTargetSet = refGeneralManager.getSingelton().getSetManager()
			.getItemSet(iTargetCollectionSetId);

		if (alTargetSet == null)
		{
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasScatterPlot2D.setTargetSetId("
							+ iTargetCollectionSetId
							+ ") failed, because Set is not registered!",
					LoggerType.FULL);
			
			return;
		}
		
		refMinMaxDataInteger =	new MinMaxDataInteger(alTargetSet.getDimensions());
		refMinMaxDataInteger.useSet( alTargetSet );

		refGeneralManager.getSingelton().logMsg(
				"GLCanvasScatterPlot2D.setTargetSetId("
						+ iTargetCollectionSetId + ") done!", LoggerType.FULL);
		
		initScatterplot(alTargetSet.getDimensions(), refMinMaxDataInteger);

	}

	public void render_displayListHeatmap(GL gl) {
		
		/** 
		 * Render frame around scatter plot..
		 */
		/* Surrounding box */
		
		float fBias_Z =  viewingFrame[Z][MIN] + AGLCanvasHeatmap2D.fPickingBias;
		
		gl.glColor3f(1.0f, 1.0f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], fBias_Z);
		gl.glEnd();
		
		/* END: Surrounding box */
		
		if ( alTargetSet == null )
		{
			return;
		}
		
		if ( bRenderingStyleFlat ) {
			render_displaylistHeatmap_FlatStyle(gl, viewingFrame[Z][MIN]);
		} else {		
			render_displaylistHeatmap_SortedStyle(gl);
		}
	}

	private void render_displaylistHeatmap_SortedStyle(GL gl) {


	}


//	private void render_Selection_ownArea(GL gl, 
//			final float fIncY ) {

	
//	}
	
	private void render_displaylistHeatmap_FlatStyle(GL gl, final float fZ) {

		/**
		 * Get data from Set...
		 */
		IStorage [] storageArray = new IStorage [iScatterplotDimensions];
		IVirtualArray [] VA_array = new IVirtualArray [iScatterplotDimensions];;
		int [] dataArrayInt_X = null;
		int [] dataArrayInt_Y = null;
		//int [] dataArrayInt_Z = null;
		int [] dataArrayInt_Color = null;
		
		for (int i=0; i< iScatterplotDimensions; i++ )
		{
			storageArray[i] = alTargetSet.getStorageByDimAndIndex(i,0);
			VA_array[i] = alTargetSet.getVirtualArrayByDimAndIndex(i, 0);
			
			if ( i==0 )
			{
				dataArrayInt_X =  storageArray[i].getArrayInt();
			}
			else if ( i==1 ) 
			{
				dataArrayInt_Y =  storageArray[i].getArrayInt();
			}
			else if (i==2)
			{
				dataArrayInt_Color =  storageArray[i].getArrayInt();
			}
		}
		
		IVirtualArrayIterator iter_X = VA_array[X].iterator();
		IVirtualArrayIterator iter_Y = VA_array[Y].iterator();
		
		//IVirtualArrayIterator iter_Color = VA_array[COLOR].iterator();
		float[] value = new float [iScatterplotDimensions];
		
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL.GL_POINTS);
		
		while (iter_X.hasNext())
		{
			value[X] = viewingFrame[X][MIN] + (
					(dataArrayInt_X[iter_X.next()] - iScatterplotMax[X]) * fScatterplotScale[X]);
			value[Y] = viewingFrame[Y][MIN] + (
					(dataArrayInt_Y[iter_Y.next()] - iScatterplotMax[Y]) * fScatterplotScale[Y]);  

//			value[X] = viewingFrame[X][MIN] + (
//					(dataArrayInt_X[iter_X.next()] - iScatterplotMax[X]) * fScatterplotScale[X]) * fScatterplotMinMaxRange[X];
//			value[Y] = viewingFrame[Y][MIN] + (
//					(dataArrayInt_Y[iter_Y.next()] - iScatterplotMax[Y]) * fScatterplotScale[Y]) * fScatterplotMinMaxRange[Y];  
			
			//colorMapping(gl, dataArrayInt_Color[index]);

			gl.glVertex3f(value[X], value[Y], fZ);
			
		} //while ( iter.hasNext() )  

		gl.glEnd();
		
	}

	/* --------------------------- */
	/* -----  BEGEN: PICKING ----- */


//	protected final void renderPart4pickingX(GL gl) {

//	}
//
//	protected final void renderPart4pickingY(GL gl) {

//	}

	/* (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setSelectionItems(int[], int[], int[], int[])
	 */
	public void setSelectionItems(int[] selectionStartAtIndexX,
			int[] selectionLengthX, int[] selectionStartAtIndexY,
			int[] selectionLengthY) {

		if  (selectionStartAtIndexX != null ) 
    	{
    		/* consistency */ 
        	assert selectionLengthX != null : "selectionStartAtIndex is null-pointer";    	
        	assert selectionStartAtIndexX.length == selectionLengthX.length : "both arrays must have equal length";
        	
	    	iSelectionStartAtIndexX = selectionStartAtIndexX;    	
	    	iSelectionLengthX = selectionLengthX;
    	}
    	
    	if  (selectionStartAtIndexY != null ) 
    	{
    		/* consistency */ 
        	assert selectionLengthY != null : "selectionStartAtIndex is null-pointer";    	
        	assert selectionStartAtIndexX.length == selectionLengthX.length : "both arrays must have equal length";
        	
	    	iSelectionStartAtIndexY = selectionStartAtIndexY;    	
	    	iSelectionLengthY = selectionLengthY;
    	}

	}

	public void updateReceiver(Object eventTrigger) {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName()+
				": updateReceiver( (" + 
				eventTrigger.getClass().getSimpleName() + ") " +
				eventTrigger.toString() + ")",
				LoggerType.STATUS );
	}

	
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName()+
				": updateReceiver( (" + 
				eventTrigger.getClass().getSimpleName() + ") " +
				eventTrigger.toString() + ")",
				LoggerType.STATUS );

		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName()+
				": updateReceiver() udateSet: " +  updatedSet.toString(),
				LoggerType.STATUS );

//		if ( this.hashExternalId_2_HeatmapIndex == null ) {
//			init_External2InternalHashMap();
//		}
		
		IStorage[] storage = updatedSet.getStorageByDim(0);
		//TODO: Fix this after CHI
		//IVirtualArray[] virtualArray = updatedSet.getVirtualArrayByDim(0);

		ArrayList<Integer> resultBuffer = new ArrayList<Integer>(10);
		ArrayList<Integer> resultBufferMode = new ArrayList<Integer>(10);

//		for (int i = 0; i < storage.length; i++)
//		{
			//IVirtualArray vaBuffer = virtualArray[i];

			int[] intBuffer = storage[0].getArrayInt();
			int[] intBufferMode = storage[1].getArrayInt();			
			//int[] intBufferPathway = storage[2].getArrayInt();					

			for (int j = 0; j < intBuffer.length; j++)
			{
				/* lookup id..*/
				Iterator <IGraphItem> iterGraphItemRep = 
					((IGraphItem) refGeneralManager.getSingelton().getPathwayItemManager()
					.getItem(intBuffer[j])).getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).iterator();
					
				while (iterGraphItemRep.hasNext()) {
					/* convert external id to internal.. */
					Integer indexInt = hashExternalId_2_HeatmapIndex.get(
							iterGraphItemRep.next().getId());
					
					if (indexInt != null)
					{
						System.err.println("GLCanvasHeatmap2DColumn receiveUpdate [" + intBuffer[j] + "=>" + indexInt
								+ "], ");

						resultBuffer.add(indexInt);
						
						if ( intBufferMode.length != 0 ) {												
							resultBufferMode.add(intBufferMode[j]);
						} else {
							resultBufferMode.add( iSelectedMode_identifyier );
						}
						
					} else
					{
						System.err.println("[" + intBuffer[j] + "=> ?? ], ");
					}
				}
				          				
			
			}
//		}

		if (!resultBuffer.isEmpty())
		{
			System.err.println("GLCanvasHeatmap2DColumn receiveUpdate []    COPY to Heatmap-Selection.. ");
					
			/* copy selection into local data structure .. */
			setSelectedIndex_asArrayList(resultBuffer, resultBufferMode);
		}

	}

}
