/**
 * 
 */
package org.geneview.core.view.opengl.canvas.heatmap;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec3fGL;
import gleem.linalg.open.Vec4i;

import java.awt.Color;
import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

import com.sun.opengl.util.BufferUtil;


import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.set.selection.SetSelection;
import org.geneview.core.data.collection.storage.FlatThreadStorageSimple;
import org.geneview.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
//import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
//import org.geneview.core.data.mapping.GenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
//import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.manager.event.EventPublisher;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.math.statistics.minmax.MinMaxDataInteger;
//import org.geneview.core.math.statistics.minmax.MinMaxDataInteger;
//import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener; //import org.geneview.core.view.opengl.canvas.heatmap.AGLCanvasHeatmap2D;
import org.geneview.core.util.mapping.ColorMapping;
import org.geneview.core.util.mapping.ColorMapping3f_3SamplePoints;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.IGLCanvasUser;
//import org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2D;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
import org.geneview.core.view.opengl.util.GLInfoAreaRenderer;
import org.geneview.core.view.swt.jogl.SwtJoglGLCanvasViewRep;

/**
 * @author Michael Kalkusch
 * 
 * @see org.geneview.core.view.opengl.IGLCanvasUser
 */
public class GLCanvasHeatmap2DColumn
extends AGLCanvasHeatmap2D
//		extends GLCanvasHeatmap2D
		implements IMediatorReceiver, IMediatorSender, IGLCanvasHeatmap2D {

	public static final int OFFSET = AGLCanvasHeatmap2D.OFFSET;

	public static final int X = AGLCanvasHeatmap2D.X;

	public static final int MIN = AGLCanvasHeatmap2D.MIN;

	public static final int Y = AGLCanvasHeatmap2D.Y;

	public static final int Z = AGLCanvasHeatmap2D.Z;

	public static final int MAX = AGLCanvasHeatmap2D.MAX;
	

	/* ----  COLOR  ------ */
	private Vec3fGL color_PickedPoints = new Vec3fGL(1,1,0);
	private Vec3fGL color_HeatmapFrame = new Vec3fGL(0,0,0);
	private Vec3fGL color_Selection_FramefillColor = new Vec3fGL(0,1,1);
	private Vec3fGL color_Selection_Frame = new Vec3fGL(0.1f,0.1f,0.1f);
	private Vec3fGL color_Selection_Frame_Tip = new Vec3fGL(0,0,1);
	private Vec3fGL color_Selection_DepthIndicator = new Vec3fGL(0,0,0);
	private Vec3fGL color_Selection_ConnectionLine_inFocus = new Vec3fGL(0.2f,1, 110/255);
	private Vec3fGL color_Selection_ConnectionLine_outOffFocus = new Vec3fGL(0.1f, 1, 1);
	private Vec3fGL color_Selection_DepthIndicator_fillColor = new Vec3fGL(0, 0, 0);
	
	
	
	

	/* ----  END: COLOR ------ */
	
	private int iHeatmapDisplayListId = -1;
	
	private float fSelectionPixel = 0.1f;

	private float fSelectionLineWidth = 2.0f;

	private boolean bRenderingStyleFlat = false;
	
	private int iSelectedMode_identifyier = 0;
	
	private float fIncX_forSelection;
	
	private ColorMapping colorMapper;
	
	protected ColorMapping3f_3SamplePoints colorMapperVec3f;
	
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
	
	private ArrayList <GLCanvasHeatmap2DColumn> alExternalWindow_Link2Parent;
	
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
	
	private ArrayList<ISet> alTargetSet;

	private PathwayVertexGraphItem pickedGeneVertex;
		
	private GLInfoAreaRenderer infoAreaRenderer;
	
	private boolean bEnablePicking = true;
	
	private boolean bUseGLWireframe = false;
	
	private boolean bEnalbeMultipleSelection = false;
	
	private int iSetCacheId = 0;
	
	private MinMaxDataInteger refMinMaxDataInteger;
	
	private ArrayList <Vec2f> fIndexPickedCoored = new ArrayList <Vec2f> (1);
	
	/**
	 * Picking Mouse handler
	 */
	protected PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	protected int[] iSelectionStartAtIndexX;

	protected int[] iSelectionStartAtIndexY;

	protected int[] iSelectionLengthX;

	protected int[] iSelectionLengthY;

	private int iRenderSelection_encircleEachItem = 200;
	
	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHeatmap2DColumn(final IGeneralManager setGeneralManager,
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

		refMinMaxDataInteger = new MinMaxDataInteger(1);
		
		
		//hashNCBI_GENE2index = new HashMap<Integer, Integer>();

		System.err.println("  GLCanvasHeatmap2DColumn()");		

		selectedIndex = new int[0];
		selectedIndexMode = new int[0];

		//hashExternalId_2_HeatmapIndex = new HashMap<Integer, Integer>(100);
		alTargetSet = new ArrayList<ISet>(10);

		hashHighlightSelectionIndex = new HashMap <Integer,Boolean> ();
		
//		int[] selIndex =
//		{ 3, 5, 10, 15 };
//		int[] selIndexMode =
//		{ 0, 1, 2, 3 };
//		this.setSelectedIndex(selIndex, selIndexMode);
		
		alHighlightSelectionIndex_sortByDepth = new ArrayList < ArrayList <Integer>> (5);
		
		alHighlightSelectionIndex_sortByDepth.add( new ArrayList <Integer> (
				GLCanvasHeatmap2DColumn.iInitialSizeArrayList_Selection));
		
		infoAreaRenderer = new GLInfoAreaRenderer(refGeneralManager,
				new GLPathwayManager(setGeneralManager));
		
		colorMapper = new ColorMapping(0, 60000);
		
		colorMapperVec3f = new ColorMapping3f_3SamplePoints();
		
		hashChildrenWindow = new HashMap <Integer,Vec4i> (4);				
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
				container.add( new ArrayList<Integer> (
						GLCanvasHeatmap2DColumn.iInitialSizeArrayList_Selection));
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
	
	protected void init_External2InternalHashMap() {

		HashMap <Integer,Integer> hashNCBI_GENEID_2_internalGraphVertexId = 
			refGeneralManager.getSingelton().getPathwayItemManager().getHashNCBIGeneIdToPathwayVertexGraphItemId();
	

//		IGenomeIdManager refIGenomeIdManager = refGeneralManager.getSingelton()
//		.getGenomeIdManager();
//		
//		hashNCBI_GENE2index = refIGenomeIdManager
//				.getAllValuesByGenomeIdTypeHashMap(GenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE);
//
//		if ( hashNCBI_GENE2index == null ) {
//			refGeneralManager.getSingelton().logMsg("Can not load NCBI_GENEID mapping!", LoggerType.MINOR_ERROR_XML);
//			
//			return;
//		}
//
//
//		/* create HashMap now.. */
//		hashExternalId_2_HeatmapIndex_reverse = new HashMap<Integer, Integer>();
//		Iterator<Integer> iter = hashNCBI_GENE2index.keySet().iterator();
//	
//		for (int index=0; iter.hasNext(); index++) {
//			hashExternalId_2_HeatmapIndex_reverse.put(new Integer(index), iter.next());
//		}
		
		hashExternalId_2_HeatmapIndex = new HashMap<Integer, Integer>(hashNCBI_GENEID_2_internalGraphVertexId.size());
		hashExternalId_2_HeatmapIndex_reverse = new HashMap<Integer, Integer>(hashNCBI_GENEID_2_internalGraphVertexId.size());
		
		Iterator<Integer> iter_InternalGraphVertexId = 
			hashNCBI_GENEID_2_internalGraphVertexId.values().iterator();
		
		for ( int index = 0; iter_InternalGraphVertexId.hasNext(); index++ ) {
			Integer buffer = iter_InternalGraphVertexId.next();
			
			hashExternalId_2_HeatmapIndex.put(buffer, new Integer(index));
			hashExternalId_2_HeatmapIndex_reverse.put(new Integer(index),buffer);
		}
	}

	public void setKeysForHeatmap(int[] keys) {

	}

	public void selectKeys(int[] keys) {

	}

	/* --------------------------- */
	/* -----  BEGEN: PICKING ----- */

	protected final boolean processHits(final GL gl,
			final int iHitCount,
			int iArPickingBuffer[], 
			final Point pickPoint,
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
			
			if ( selectedIndexArray != null ) {
	
				/** notify other listeners .. */
				notifyReceiver_PickedObject_singleSelection(selectedIndexArray, iSelectedMode_identifyier);
	
//				Integer iNCBIGeneID = hashExternalId_2_HeatmapIndex_reverse.get(resultPickPointCoord[0]);
//				if ( iNCBIGeneID != null) 
//				{
//					PathwayVertexGraphItem pickedGeneVertex;
//					pickedGeneVertex = (PathwayVertexGraphItem) refGeneralManager.getSingelton(
//							).getPathwayItemManager().getItem(iNCBIGeneID.intValue());
//					
//					infoAreaRenderer.renderInfoArea(gl, pickedGeneVertex);
//				}
			}
			
			/* hack, check if external hashmap was already created */
			//TODO: clean up this call, init from outside as soon as data is available!
			if ( hashExternalId_2_HeatmapIndex==null ) {			
				this.init_External2InternalHashMap();
			}
			
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
	
	private void notifyReceiver_PickedObject_singleSelection( final int [] resultPickPointCoord, 
			final int iModeValue) {
		
		if ( resultPickPointCoord.length < 1) {
			return;
		}
		
		int [] resultSelectedIndex = new int [resultPickPointCoord.length];
		int [] resultSelectedIndexMode = new int [resultPickPointCoord.length];
		
		/* copy new indices to end of existing array.. */
		for ( int iIndex=0; iIndex<resultPickPointCoord.length; iIndex++) {
			resultSelectedIndex[iIndex] = resultPickPointCoord[iIndex];
			resultSelectedIndexMode[iIndex] = iModeValue;
		}
		
		/* swap .. */
		selectedIndex = resultSelectedIndex;
		selectedIndexMode = resultSelectedIndexMode;
		
		/* hack, check if external hashmap was already created */
		//TODO: clean up this call, init from outside as soon as data is available!
		if ( hashExternalId_2_HeatmapIndex==null ) {			
			this.init_External2InternalHashMap();
		}
		
		
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
	 		refGeneralManager.getSingelton().logMsg(this.getClass().getSimpleName()+
	 				".broadcast selection event! " + selectionSet.toString(),
	 				LoggerType.STATUS);
		}
	}

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
		
		if ( hashExternalId_2_HeatmapIndex==null ) {
			this.init_External2InternalHashMap();
		}
		
		
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
		
		if (pickingTriggerMouseAdapter.wasMouseReleased())
		{
			pickPoint = new Point( pickingTriggerMouseAdapter.getPickedPoint() );
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
			
			//pickPoint = null;
		}

	}

	protected void pickObjects(final GL gl, final Point pickPoint) {

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

		renderPart4pickingX(gl);
	
		/* second layer of picking.. */
		gl.glPushMatrix();
		
		gl.glTranslatef( 0,0, AGLCanvasHeatmap2D.fPickingBias );
		renderPart4pickingY(gl);
		
		gl.glPopMatrix();
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		
	

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		
		//boolean bPickinedNewObject = 
		processHits(gl, iHitCount, iArPickingBuffer, pickPoint, fIndexPickedCoored);
				
	}
	
	@Override
	public void renderPart(GL gl) {

		int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;
		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
			/ (float) (alTargetSet.size());
		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
			/ (float) (iRenderIndexRangeX + 1);
		
		gl.glPushMatrix();
		
		if ( bEnablePicking ) {
			handlePicking(gl);
		}

		gl.glTranslatef(0, 0, 0.01f);

		if (alTargetSet.isEmpty())
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"createHistogram() can not create Heatmap, because targetSet=null",
							LoggerType.STATUS);
			return;
		}

		if (iValuesInRow < 1)
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"createHistogram() can not create Heatmap, because histogramLevels are outside range [1..max]",
							LoggerType.FULL);
			return;
		}

		ISet primaryTargetSet = alTargetSet.get(0);
		if ((primaryTargetSet.hasCacheChanged(iSetCacheId))
				|| (iHeatmapDisplayListId == -1))
		{

			iSetCacheId = primaryTargetSet.getCacheId();

			//	    			System.out.print("H:");
			//	    			for ( int i=0;i<iHistogramIntervalls.length; i++) {
			//	    				System.out.print(";" +
			//	    						Integer.toString(iHistogramIntervalls[i]) );
			//	    			}
			System.out.println("GLCanvasHeatmap2DColumn - UPDATED!");

			render_createDisplayLists(gl);

			refGeneralManager.getSingelton().logMsg(
					"createHistogram() use ISet(" + primaryTargetSet.getId()
							+ ":" + primaryTargetSet.getLabel() + ")",
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
		
		render_Selection_ownArea(gl, fIncY);
		gl.glTranslatef(0, 0, -AGLCanvasHeatmap2D.fPickingBias);
		
//		gl.glColor3f(1, 1, 0);
//		renderGLAllQuadRectangle(gl, this.fIndexPickedCoored);
//
//		gl.glColor3f(0.8f, 0.8f, 0);
//		renderGLAllQuadDots(gl, this.fIndexPickedCoored);

//		gl.glColor3f(0, 0, 0.8f);
//		renderGL_Selection(gl);
		  

		render_ExternalChildrenWindows(gl, fIncY, fIncX);
				
		//if ( bEnablePicking ) {
			gl.glTranslatef(0, 0, AGLCanvasHeatmap2D.fPickingBias);
			render_picketPoints(gl, fIncY, fIncX);
		//}
		
		if (pickedGeneVertex != null && infoAreaRenderer.isPositionValid())
		{
			infoAreaRenderer.renderInfoArea(gl, pickedGeneVertex);
		}
		
		
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
	
	/**
	 * Render picked points in own area.
	 * 
	 * @param gl
	 */
	private void render_picketPoints(GL gl,
			final float fIncY,
			final float fIncX) {

		if ((selectedIndex.length > 0) || (!fIndexPickedCoored.isEmpty()))
		{
//			int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;

//			float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
//					/ (float) (alTargetSet.size());
			float fIncY_halfSize = fIncY * 0.5f;
			
			float fNowY = viewingFrame[Y][MIN];
			float fNextY = fNowY + fIncY;

//			float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
//					/ (float) (iRenderIndexRangeX + 1);
			//float fIncX_halfSize = fIncX * 0.5f;
			
			float fNowX = viewingFrame[X][MIN];
			float fNextX = fNowX + fIncX;
			float fZ = viewingFrame[Z][MIN] + 3*fPickingBias;

			if (!fIndexPickedCoored.isEmpty())
			{
				/* -------------------------- */
				/* show picked points .. */
				Iterator<Vec2f> iterPickedPoints = fIndexPickedCoored
						.iterator();

				color_Selection_FramefillColor.glColor3f(gl);				
				for (int i = 0; iterPickedPoints.hasNext(); i++)
				{
					/**
					 * Render a triangle on each selected item in the heatmap.
					 */
					Vec2f pickedIndex = iterPickedPoints.next();

					/* TOP */
					fNowX = viewingFrame[X][MIN] + fIncX * pickedIndex.x();
					fNextX = viewingFrame[X][MIN] + fIncX
							* (pickedIndex.x() + 1);
					fNowY = viewingFrame[Y][MIN] + fIncY * pickedIndex.y();
					fNextY = viewingFrame[Y][MIN] + fIncY
							* (pickedIndex.y() + 1);

					gl.glBegin(GL.GL_TRIANGLE_FAN);
					
					gl.glVertex3f(fNowX, fNowY, fZ);
					gl.glVertex3f(fNextX, fNowY, fZ);
					gl.glVertex3f(fNextX, fNowY + fIncY_halfSize, fZ);
					//gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

					gl.glEnd();
				}
				/* end: show picked points .. */
				/* -------------------------- */
			}

			if (selectedIndex.length > 0)
			{
				/* ------------------- */
				/* highlight selection */

				/* Y_min .. Y_max */
				for (int index_X_H = 0; index_X_H < selectedIndex.length; index_X_H++)
				{

					gl.glLineWidth(fSelectionLineWidth);

					if ((selectedIndex[index_X_H] >= iRenderIndexStart)
							&& (selectedIndex[index_X_H] < iRenderIndexStop))
					{
						/** 
						 * Render one BOX on top, one Box below and connect them with 2 lines 
						 * of each selected heatmap segment [X , Y_min..Y_max] 
						 * */
						
						fNowX = viewingFrame[X][MIN] + fIncX
								* selectedIndex[index_X_H];
						fNextX = fNowX + fIncX;

						/* LINES */
						fNowY = viewingFrame[Y][MIN];
						fNextY = viewingFrame[Y][MAX];
						
						/* BOTTOM */
						render_Selection_Detail(gl,
								fNowX,
								fNextX,
								viewingFrame[Y][MIN],
								fIncY,
								viewingFrame[Z][MIN],
								selectedIndexMode[index_X_H],
								-1.0f );							
						
						/* TOP */
						fNowY = viewingFrame[Y][MAX];
						fNextY = viewingFrame[Y][MAX] + fSelectionPixel * fIncY;
						
						color_Selection_FramefillColor.glColor3f(gl);
						gl.glBegin(GL.GL_TRIANGLE_FAN);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

						gl.glEnd();
						
						color_Selection_Frame.glColor3f(gl);
						gl.glBegin(GL.GL_LINES);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);

						gl.glEnd();

					} // if (( selectedIndex[index_X_H] >= iRenderIndexStart
					// )&&( selectedIndex[index_X_H] < iRenderIndexStop)) {

				} // for (int index_X_H=0; index_X_H<selectedIndex.length;
				// index_X_H++)
				/* end highlight selection */

			}
		}
	}

	// public int[] createHistogram(final int iHistogramLevels) {
	public void renderHeatmap(final int iHistogramLevels) {

		refGeneralManager.getSingelton().logMsg("HEATMAP: set  ",
				LoggerType.FULL);

	}

	public int getHeatmapValuesInRow() {

		return iValuesInRow;
	}

	/**
	 * 
	 * @param fColorMappingShiftFromMean 1.0f indicates no shift
	 */
	private void initColorMapping() {

		if (refMinMaxDataInteger.isValid())
		{
			/**
			 * Dynamic color mapping
			 */
			colorMapperVec3f.addSamplingPoint_Vecf( new Vec3f(1,0,0), (float) refMinMaxDataInteger.getMin(0));
			colorMapperVec3f.addSamplingPoint_Vecf( new Vec3f(1,1,0), (float) refMinMaxDataInteger.getMean(0));
			colorMapperVec3f.addSamplingPoint_Vecf( new Vec3f(0,1,0), (float) refMinMaxDataInteger.getMax(0));

			float fValuesInColum = (float) refMinMaxDataInteger.getItems(0)
					/ (float) iValuesInRow;

			iValuesInColum = (int) (fValuesInColum);
			
			return;
		} else
		{
			System.err.println("Error while init color mapping for Heatmap!");
		}
		
		float fColorMappingLowValue = 0.0f;
		float fColorMappingHighValue = 60000.0f;
		
		colorMapperVec3f.addSamplingPoint_Vecf( new Vec3f(1,0,0), fColorMappingLowValue);
		colorMapperVec3f.addSamplingPoint_Vecf( new Vec3f(1,1,0), (fColorMappingHighValue - fColorMappingLowValue) / 2);
		colorMapperVec3f.addSamplingPoint_Vecf( new Vec3f(0,1,0), fColorMappingHighValue);

	}

	protected void colorMapping(final GL gl, final int iValue) {
		
//		colorMapperVec3f.colorMapping_glColor3f(gl, iValue);
		
		/* Use color mapper with LUT.. */
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
		} else
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
	public final void setTargetSetId(final int iTargetCollectionSetId) {

		boolean bUpdateColorMapping = alTargetSet.isEmpty();

		ISet addTargetSet = refGeneralManager.getSingelton().getSetManager()
				.getItemSet(iTargetCollectionSetId);

		if (addTargetSet == null)
		{
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasScatterPlot2D.setTargetSetId("
							+ iTargetCollectionSetId
							+ ") failed, because Set is not registered!",
					LoggerType.FULL);
		}

		alTargetSet.add(addTargetSet);

		refGeneralManager.getSingelton().logMsg(
				"GLCanvasScatterPlot2D.setTargetSetId("
						+ iTargetCollectionSetId + ") done!", LoggerType.FULL);

		if (bUpdateColorMapping)
		{
			refMinMaxDataInteger.useSet(addTargetSet);
			initColorMapping();
		}

	}

	public void render_displayListHeatmap(GL gl) {
		
		if ( alTargetSet.isEmpty())
		{
			return;
		}
		
		
		if ( bRenderingStyleFlat ) {
			render_displaylistHeatmap_FlatStyle(gl);
		} else {		
			render_displaylistHeatmap_SortedStyle(gl);
		}
		
		/* Surrounding box */
		float fBias_Z = viewingFrame[Z][MIN] + fPickingBias;
		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
		/ (float) (alTargetSet.size());

		color_HeatmapFrame.glColor3f(gl);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], fBias_Z);
		gl.glEnd();

		/** 
		 * Render each experiment in a row .. 
		 */
		if ( bRenderEnableDelimiterPerExperiment ) {
			if (alTargetSet.size() > 1)
			{
				/**
				 * Render lines between each row of experiments 
				 */
				float fZ_2 = fBias_Z + fPickingBias;
				
				gl.glBegin(GL.GL_LINES);	
				
				/* start with index=1 and goto alTargetSet.size() */
				for (int i = 1; i < alTargetSet.size(); i++)
				{
					gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN]
							+ fIncY * i, fZ_2);
					gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN]
							+ fIncY * i, fZ_2);
				}
	
				gl.glEnd();
			}
		}
		
	}

	private void render_displaylistHeatmap_SortedStyle(GL gl) {
		/**
		 * Get data from Set...
		 */
		
		Iterator<ISet> iterTargetSet = alTargetSet.iterator();

		int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;

		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
				/ (float) (alTargetSet.size());
		float fNowY = viewingFrame[Y][MIN];
		float fNextY = fNowY + fIncY;

		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
				/ (float) (iRenderIndexRangeX + 1);

		this.fIncX_forSelection = fIncX;
			
		float fNowX = viewingFrame[X][MIN];
		float fNextX = fNowX + fIncX;

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		while (iterTargetSet.hasNext())
		{
			ISet currentTargetSet = iterTargetSet.next();

			IStorage refStorage = currentTargetSet.getStorageByDimAndIndex(
					0, 0);

			int[] dataArrayInt = refStorage.getArrayInt();

			IVirtualArray refVArray = currentTargetSet
					.getVirtualArrayByDimAndIndex(0, 0);

			IVirtualArrayIterator iter = refVArray.iterator();

			//int[] i_dataValues = refStorage.getArrayInt();

			if (dataArrayInt != null)
			{

				/**
				 * force update ...
				 */

				for (int iIndex_X = 0; iter.hasNext(); iIndex_X++)
				{
					if (iIndex_X >= iRenderIndexStart)
					{

						if (iIndex_X >= iRenderIndexStop)
						{
							iter.setToEnd();
							break;
						}

						gl.glBegin(GL.GL_TRIANGLE_FAN);
						// gl.glBegin( GL.GL_LINE_LOOP );

						colorMapping(gl, dataArrayInt[iter.next()]);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

						gl.glEnd();

						fNowX = fNextX;
						fNextX += fIncX;

					} else
					{
						iter.next();
					}

				} //for (int iIndex_X = 0;iter.hasNext(); iIndex_X++ )

			} // if (i_dataValues != null)

			/* reset X .. */
			fNowX = viewingFrame[X][MIN];
			fNextX = fNowX + fIncX;

			/* increment Y .. */
			fNowY = fNextY;
			fNextY += fIncY;

		} //while ( iter.hasNext() )  
		

	}

	private void render_Selection_Detail(GL gl,
			final float fX,
			final float fX_next,
			final float fY,
			final float fIncY,
			final float fZ,
			final int selectedIndexMode_Value,
			final float fSign ) {
		
		//if  (selectedIndexModeArray[selectedIndexMode_Index] > 0 ) {
					
			color_Selection_Frame_Tip.glColor3f(gl);
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			
			float fY_a = fY + selectedIndexMode_Value * fIncY * fSelectionPixel * fSign;
			                         
			gl.glVertex3f(fX, fY, fZ);
			gl.glVertex3f(fX_next, fY, fZ);
			gl.glVertex3f(fX_next, fY_a, fZ);
			gl.glVertex3f(fX, fY_a, fZ);
			
			gl.glEnd();
			
			/** 
			 * Render a line between the selected area and the top of the heatmap 
			 */
			
			/**
			 * Render a line to indicate depth of selection; from neighborhood 
			 */
			color_Selection_DepthIndicator.glColor3f(gl);
			float fZb = fZ + fPickingBias;
			for ( int j=1; j < selectedIndexMode_Value; j++ ) {
				fY_a = fY + j * fIncY * fSelectionPixel * fSign;
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fX, fY_a, fZb);
				gl.glVertex3f(fX_next, fY_a, fZb);
				gl.glEnd();
			} //for ( int j=1; j < selectedIndexMode[i]; j++ ) {
			
		//} //if  (selectedIndexMode[i] > 0 ) {	
	}
	
	private void render_Selection_ownArea(GL gl, 
			final float fIncY ) {
		/* ----------------------------------- */
		/* ---  Render selected once more  --- */		

//		Iterator < ArrayList <Integer>> iter_sortByDepth = 
//			alHighlightSelectionIndex_sortByDepth.iterator();
//		
//		int iSumItems = 0;
//		while ( iter_sortByDepth.hasNext() ) {
//			iSumItems += iter_sortByDepth.next().size();
//		}
//		
//		if ( iSumItems < 1) {
//			/* no selection to render.. */
//			return;
//		}
		
		if ( selectedIndex.length < 1 ) {
			return;
		}
		
		boolean bShowSelectionItemBoundaries = true;
		
		if ( iRenderIndexStop - iRenderIndexStart > iRenderSelection_encircleEachItem  ) 
		{
			bShowSelectionItemBoundaries = false;
		}
		
		/* reset rendering parameters.. */
		float fOffsetY= 2.0f; //(viewingFrame[X][MAX] - viewingFrame[X][MIN]);
		
			
		float fNowY = viewingFrame[Y][MIN] + fOffsetY;
		float fNextY = fNowY + fIncY;
		
		/* use global variable.. */
		float fIncX = fIncX_forSelection;
		float fIncX_halfSize = fIncX_forSelection * 0.5f;
		
		float fXOffset = ((viewingFrame[X][MAX] - viewingFrame[X][MIN])
			 - (selectedIndex.length + 1) * fIncX) * 0.5f;
		
		float fNowX = viewingFrame[X][MIN] + fXOffset;
		float fNextX = fNowX + fIncX;
		
		
		/* copy references to raw data int[] .. */
		ArrayList <int[]> bufferValueArrays = new ArrayList <int[]> ();
		ArrayList <Integer> bufferValueArrays_Offset = new ArrayList <Integer> ();
		
		Iterator <ISet> iterTargetSet = alTargetSet.iterator();
		
		boolean bBuffer_ShowSelectionItemBoundaries = false;
		boolean bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea = true;
		boolean bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea_FinishLine = false;
		float fBuffer_ShowSelectionItemBoundaries = 0.0f;
		float iBuffer_ShowSelectionItemBoundaries_lastIndex = -1;
		
		while (iterTargetSet.hasNext()) {
			/* read ISet and add "selection-data" to internal data structure */
			ISet currentTargetSet = iterTargetSet.next();
			IVirtualArray refVArray = currentTargetSet.getVirtualArrayByDimAndIndex(0, 0);
			IVirtualArrayIterator iter = refVArray.iterator();
			IStorage refStorage = currentTargetSet.getStorageByDimAndIndex(0, 0);
			int[] dataArrayInt = refStorage.getArrayInt();

			if (dataArrayInt != null)
			{
				bufferValueArrays.add(dataArrayInt);
				bufferValueArrays_Offset.add(iter.next());	
			}								
		} //while (iterTargetSet.hasNext()) {
		
		/* end: copy references to raw data int[] .. */
		
		/* if groups are defined enable the rendering of the groups.. */
		boolean bEnableGroupHighlighting = false;
		if  (selectedIndexMode.length > 0 ) {
			bEnableGroupHighlighting = true;
		}
		
		/**
		 * sort selected index
		 * sort selected index in each view; preserve mapping <selected_index,selected_index_mode> using a HashMap
		 */
		HashMap <Integer,Integer> hashPreservMapping_selectedIndex_2_selectedIndexMode = 
			new HashMap <Integer,Integer> ();
		
		for ( int i=0; i < selectedIndex.length; i++) {
			hashPreservMapping_selectedIndex_2_selectedIndexMode.put(selectedIndex[i], selectedIndexMode[i]);
		}		
		
		/* Bubble sort O( n*log(n) )*/
		Arrays.sort(selectedIndex);
		
		/** 
		 * end: sort selectedIndex
		 * array (int[]) selectedIndex[] is sorted now
		 */
		
		for ( int i=0; i < selectedIndex.length; i++) {
			
				int iCurrentIndex = selectedIndex[i];
				
				Iterator<int[]> iterRawDataArrays = bufferValueArrays.iterator();
				Iterator<Integer> iterRawDataArrays_Offset = bufferValueArrays_Offset.iterator();
				
				/**
				 * Render "selection" on top of heatmap
				 */
				while (iterRawDataArrays.hasNext()) {
					int [] currentArrayBuffer = iterRawDataArrays.next();
					int iCurrentIndex_InArray = 
						iterRawDataArrays_Offset.next().intValue() 
						+ iCurrentIndex; 
					
					gl.glBegin(GL.GL_TRIANGLE_FAN);
					
					try {
						colorMapping(gl, currentArrayBuffer[iCurrentIndex_InArray] );
					} catch (ArrayIndexOutOfBoundsException aie) {
						System.err.println("ERROR! ");
					}
					
					gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);
	
					gl.glEnd();		
					
					fNowY = fNextY;
					fNextY += fIncY;
				}
								
				/**
				 * end: Render "selection" on top of heatmap
				 */
				
				if  (bEnableGroupHighlighting) {
					
					int selectedIndexMode_currentValue = 
						hashPreservMapping_selectedIndex_2_selectedIndexMode.get(iCurrentIndex).intValue();
					
					if  (selectedIndexMode_currentValue > 0) {
					//if  (selectedIndexMode[i] > 0 ) {
						
						render_Selection_Detail(gl,
								fNowX,
								fNextX,
								fNowY,
								fIncY,
								viewingFrame[Z][MIN],
								selectedIndexMode_currentValue,
								1.0f );					
						
					} //if  (selectedIndexMode[i] > 0 ) {
				}
						
				/** render connection line to target in heatmap */
				int iRenderTargetLine_Index = 
					iCurrentIndex - this.iRenderIndexStart;
					
				float fLineX = viewingFrame[X][MIN] + 
					fIncX * iRenderTargetLine_Index +
					fIncX_halfSize;
				
				float fLineXleft = viewingFrame[X][MIN] + 
					fIncX * iRenderTargetLine_Index;
				float fLineXright = viewingFrame[X][MIN] + 
					fIncX * (iRenderTargetLine_Index+1);
				
				/**
				 * Render LINES connecting Selected area and heatmap
				 */
				
				if  ( (iCurrentIndex > this.iRenderIndexStart) && 
						(iCurrentIndex < this.iRenderIndexStop )) 
				{
					/** render separation line in selection */
					color_Selection_Frame.glColor3f(gl);
					
					if  (bShowSelectionItemBoundaries)
					{
						
						if ( bBuffer_ShowSelectionItemBoundaries ) 
						{
							if ( iBuffer_ShowSelectionItemBoundaries_lastIndex + 1 == iRenderTargetLine_Index ) 
							{
								/** put right line in buffer... 
								 * overwrite buffer also */
								fBuffer_ShowSelectionItemBoundaries = fLineXright;
								iBuffer_ShowSelectionItemBoundaries_lastIndex = iRenderTargetLine_Index;	
								bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea = false;
								bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea_FinishLine = true;
							}
							else
							{
								/**
								 * Render line from buffer..
								 */
								/* render separation line in original heatmap  */
								color_Selection_Frame.glColor3f(gl);
								gl.glBegin(GL.GL_LINES);								
								gl.glVertex3f(fBuffer_ShowSelectionItemBoundaries, 
										viewingFrame[Y][MIN], 
										viewingFrame[Z][MIN]);
								gl.glVertex3f(fBuffer_ShowSelectionItemBoundaries, 
										viewingFrame[Y][MAX], 
										viewingFrame[Z][MIN]);
								gl.glEnd();
								
								/** first line to draw */
								
								//gl.glColor3f(1, 0, 0);
								gl.glBegin(GL.GL_LINES);								
								gl.glVertex3f(fLineXleft, 
										viewingFrame[Y][MIN], 
										viewingFrame[Z][MIN]);
								gl.glVertex3f(fLineXleft, 
										viewingFrame[Y][MAX], 
										viewingFrame[Z][MIN]);
								gl.glEnd();
								
								iBuffer_ShowSelectionItemBoundaries_lastIndex = iRenderTargetLine_Index;
								fBuffer_ShowSelectionItemBoundaries = fLineXright;
								bBuffer_ShowSelectionItemBoundaries = true;
								bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea = true;
							}
						}		
						else
						{
							/** first line to draw */
							/* render separation line in original heatmap  */
							color_Selection_Frame.glColor3f(gl);
							gl.glBegin(GL.GL_LINES);								
							gl.glVertex3f(fLineXleft, 
									viewingFrame[Y][MIN], 
									viewingFrame[Z][MIN]);
							gl.glVertex3f(fLineXleft, 
									viewingFrame[Y][MAX], 
									viewingFrame[Z][MIN]);
							gl.glEnd();
							
							iBuffer_ShowSelectionItemBoundaries_lastIndex = iRenderTargetLine_Index;
							fBuffer_ShowSelectionItemBoundaries = fLineXright;
							bBuffer_ShowSelectionItemBoundaries = true;
							bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea = true;
						}
						
						if ( bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea )
						{
							/* render separation line in own render area for selection */
							color_Selection_Frame.glColor3f(gl);
							gl.glBegin(GL.GL_LINES);
							
							gl.glVertex3f(fNowX, viewingFrame[Y][MIN] + fOffsetY, viewingFrame[Z][MIN]);
							gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
							
							gl.glEnd();
						}
						
						/** end: render separation line in selection */																		
					}
					
					color_Selection_ConnectionLine_inFocus.glColor3f(gl);
				}
				else
				{										
					if ( bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea_FinishLine )
					{
						/* render separation line in own render area for selection */
						color_Selection_Frame.glColor3f(gl);
						gl.glBegin(GL.GL_LINES);
						
						gl.glVertex3f(fNowX, viewingFrame[Y][MIN] + fOffsetY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						
						gl.glEnd();
						bBuffer_ShowSelectionItemBoundaries_RenderSelectionArea_FinishLine = false;
					}
					
					color_Selection_ConnectionLine_outOffFocus.glColor3f(gl);
				}
				
				gl.glBegin(GL.GL_LINES);
				
				gl.glVertex3f(fNowX + fIncX_halfSize, 
						viewingFrame[Y][MIN] + fOffsetY - fSelectionPixel * fIncY, 
						viewingFrame[Z][MIN]);
				gl.glVertex3f(fLineX, 
						viewingFrame[Y][MAX] + fSelectionPixel * fIncY, 
						viewingFrame[Z][MIN]);
				
				gl.glEnd();
				
				/** 
				 * end: render connection line to target in heatmap 
				 */
				
				/** 
				 * Render triangle below selection area 
				 */				
				gl.glBegin(GL.GL_TRIANGLES);				
					gl.glVertex3f(fNowX, 
							viewingFrame[Y][MIN] + fOffsetY, 
							viewingFrame[Z][MIN]);
					gl.glVertex3f(fNowX + fIncX_halfSize, 
							viewingFrame[Y][MIN] + fOffsetY - fSelectionPixel * fIncY, 
							viewingFrame[Z][MIN]);
					gl.glVertex3f(fNowX + fIncX, 
							viewingFrame[Y][MIN] + fOffsetY, 
							viewingFrame[Z][MIN]);				
				gl.glEnd();		
				/** 
				 * END: Render triangle below selection area 
				 */
				
				
				/**
				 * Render BlueBox on top of heatmap, were selection is done
				 */				
				/** 
				 * TOP
				 */
				gl.glBegin(GL.GL_TRIANGLES);				
					gl.glVertex3f(fLineXleft, 
							viewingFrame[Y][MAX], 
							viewingFrame[Z][MIN]);
					gl.glVertex3f(fLineX, 
							viewingFrame[Y][MAX] + fSelectionPixel * fIncY, 
							viewingFrame[Z][MIN]);
					gl.glVertex3f(fLineXright, 
							viewingFrame[Y][MAX], 
							viewingFrame[Z][MIN]);				
				gl.glEnd();		
				
				/** 
				 * BOTTOM
				 */
				
				color_Selection_DepthIndicator_fillColor.glColor3f(gl);
				gl.glBegin(GL.GL_TRIANGLES);
				//gl.glBegin(GL.GL_LINE_LOOP);
				
					gl.glVertex3f(fLineXleft, 
							viewingFrame[Y][MIN], 
							viewingFrame[Z][MIN]);
					gl.glVertex3f(fLineX, 
							viewingFrame[Y][MIN] - fSelectionPixel * fIncY, 
							viewingFrame[Z][MIN]);
					gl.glVertex3f(fLineXright, 
							viewingFrame[Y][MIN], 
							viewingFrame[Z][MIN]);
				
				gl.glEnd();					
				/** 
				 * end: Render BlueBox on top of heatmap, were selection is done 
				 */
				
				
				fNowX = fNextX;
				fNextX += fIncX;
				
				/* reset Y.. */
				fNowY = viewingFrame[Y][MIN] + fOffsetY;
				fNextY = fNowY + fIncY;
								
				/* search for data values now.. */
				
		
		} //for ( int i=0; i < selectedIndex.length; i++) {
		
		if ( bBuffer_ShowSelectionItemBoundaries )
		{
			/* render remaining separation line in original heatmap  */
			color_Selection_Frame.glColor3f(gl);
			gl.glBegin(GL.GL_LINES);								
			gl.glVertex3f(fBuffer_ShowSelectionItemBoundaries, 
					viewingFrame[Y][MIN], 
					viewingFrame[Z][MIN]);
			gl.glVertex3f(fBuffer_ShowSelectionItemBoundaries, 
					viewingFrame[Y][MAX], 
					viewingFrame[Z][MIN]);
			gl.glEnd();
		}
		
		
		/**
		 * Surrounding box for "selection" on top of heatmap
		 */
		color_Selection_Frame.glColor3f(gl);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(viewingFrame[X][MIN] + fXOffset, viewingFrame[Y][MIN]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glVertex3f(fNowX, viewingFrame[Y][MIN]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glVertex3f(fNowX, viewingFrame[Y][MAX]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glVertex3f(viewingFrame[X][MIN] + fXOffset, viewingFrame[Y][MAX]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glEnd();
				
		/* ---  Render selected once more  --- */
		/* ----------------------------------- */
	
	}
	
	private void render_displaylistHeatmap_FlatStyle(GL gl) {

		/**
		 * Get data from Set...
		 */
		
		Iterator<ISet> iterTargetSet = alTargetSet.iterator();

		int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;

		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
				/ (float) (alTargetSet.size());
		float fNowY = viewingFrame[Y][MIN];
		float fNextY = fNowY + fIncY;

		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
				/ (float) (iRenderIndexRangeX + 1);

		this.fIncX_forSelection = fIncX;
		
		float fNowX = viewingFrame[X][MIN];
		float fNextX = fNowX + fIncX;

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		while (iterTargetSet.hasNext())
		{
			ISet currentTargetSet = iterTargetSet.next();

			IStorage refStorage = currentTargetSet.getStorageByDimAndIndex(
					0, 0);

			int[] dataArrayInt = refStorage.getArrayInt();

			IVirtualArray refVArray = currentTargetSet
					.getVirtualArrayByDimAndIndex(0, 0);

			IVirtualArrayIterator iter = refVArray.iterator();

			int[] i_dataValues = refStorage.getArrayInt();

			if (i_dataValues != null)
			{

//					if (currentTargetSet.hasCacheChanged(iSetCacheId))
//					{
//	
//						iSetCacheId = currentTargetSet.getCacheId();
//	
//						//	    			System.out.print("H:");
//						//	    			for ( int i=0;i<iHistogramIntervalls.length; i++) {
//						//	    				System.out.print(";" +
//						//	    						Integer.toString(iHistogramIntervalls[i]) );
//						//	    			}
//						System.err.println(" UPDATED inside DispalyList!");
//					}
				//System.out.print("-");

				/**
				 * force update ...
				 */

				for (int iIndex_X = 0; iter.hasNext(); iIndex_X++)
				{
					if (iIndex_X >= iRenderIndexStart)
					{

						if (iIndex_X >= iRenderIndexStop)
						{
							iter.setToEnd();
							break;
						}

						gl.glBegin(GL.GL_TRIANGLE_FAN);
						// gl.glBegin( GL.GL_LINE_LOOP );

						colorMapping(gl, dataArrayInt[iter.next()]);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

						gl.glEnd();

						fNowX = fNextX;
						fNextX += fIncX;

					} else
					{
						iter.next();
					}

				} //for (int iIndex_X = 0;iter.hasNext(); iIndex_X++ )

			} // if (i_dataValues != null)

			/* reset X .. */
			fNowX = viewingFrame[X][MIN];
			fNextX = fNowX + fIncX;

			/* increment Y .. */
			fNowY = fNextY;
			fNextY += fIncY;

		} //while ( iter.hasNext() )  

	}

	/* --------------------------- */
	/* -----  BEGEN: PICKING ----- */

//	private void setPickingBegin(GL gl,int id) {
//		gl.glPushMatrix();
//		gl.glPushName( id );
//	}
//	
//	private void setPickingEnd(GL gl) {
//		gl.glPopName();
//		gl.glPopMatrix();
//	}
//
//	private void setPickingrowAndColum(GL gl,int id) {
//		gl.glLoadName(id);
//	}
	protected final void renderPart4pickingX(GL gl) {

		/* public void render_displayListHeatmap(GL gl) { */

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		/**
		 * force update ...
		 */

		int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;

		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
				/ (float) (iRenderIndexRangeX);

		float fNowY = viewingFrame[Y][MIN];
		float fNextY = viewingFrame[Y][MAX];

		float fNowX = viewingFrame[X][MIN];
		float fNextX = fNowX + fIncX;

		/* Y_min .. Y_max*/
		for (int yCoord_name = 0; yCoord_name < iRenderIndexRangeX; yCoord_name++)
		{
			//this.setPickingBegin(gl, yCoord_name);
			gl.glLoadName(yCoord_name);
			gl.glPushName(yCoord_name);

			gl.glBegin(GL.GL_TRIANGLE_FAN);

			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

			gl.glEnd();
			gl.glPopName();

			fNowX = fNextX;
			fNextX += fIncX;

		} //for (int yCoord_name=0; yCoord_name<iValuesInRow; yCoord_name++)

	}

	protected final void renderPart4pickingY(GL gl) {

		/* public void render_displayListHeatmap(GL gl) { */

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		int ycoord_name = 0;

		/**
		 * force update ...
		 */

		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
				/ (float) (this.alTargetSet.size());

		float fNowX = viewingFrame[X][MIN];
		float fNextX = viewingFrame[X][MAX];

		float fNowY = viewingFrame[Y][MIN];
		float fNextY = fNowY + fIncY;

		/* Y_min .. Y_max */
		for (int i = 0; i < this.alTargetSet.size(); i++)
		{
			gl.glLoadName(ycoord_name);
			gl.glPushName(ycoord_name);

			gl.glBegin(GL.GL_TRIANGLE_FAN);

			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

			gl.glEnd();
			gl.glPopName();

			fNowY = fNextY;
			fNextY += fIncY;
			ycoord_name++;

		} // while (iter.hasNext())

	}

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

		if ( this.hashExternalId_2_HeatmapIndex == null ) {
			init_External2InternalHashMap();
		}
		
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

	/**
	 * Set or update a window from a child object.
	 * 
	 * @param idChild id of GLcanvasUser
	 * @param pos new position of window; if null window is removed
	 */
	public void setChildWindow( final int idChild, final Vec4i pos) {
		
		if ( alExternalWindow_Link2Parent== null ) 
		{
			/**
			 * this is a parent window! 
			 */			
			Vec4i currentPos = hashChildrenWindow.get(idChild);
			
			if ( currentPos == null ) 
			{
				/* add new window.. */
				hashChildrenWindow.put(idChild, new Vec4i(pos));
			}
			else
			{
				if ( pos == null) 
				{
					/* remove window.. */
					hashChildrenWindow.remove(idChild);
				}
				else
				{
					/* update existing window.. */
					currentPos.set( pos );
				}
			}
			
			return;
		}
		/** 
		 * This is a child window! 
		 */
		assert false : "try to call method for child that is intended for parent objects only!";
		
		/** 
		 * This is a child window! 
		 */
		
		if ( ! alExternalWindow_Link2Parent.isEmpty() ) 
		{
			Iterator <GLCanvasHeatmap2DColumn> iter =
				alExternalWindow_Link2Parent.iterator();
			
			while ( iter.hasNext() ) 
			{
				GLCanvasHeatmap2DColumn parent = iter.next();
				parent.setChildWindow(this.getId(), 
						new Vec4i(iRenderIndexStart, 
								iRenderIndexStop,
								0,
								3));
				
				//parent.setChildWindow(this.getId(), pos );
			}
		}
	}
	
	private void render_ExternalChildrenWindows(GL gl,
			final float fXinc, 
			final float fYinc) {
		
		float fTopScale = 0.2f;
		
		if  (alExternalWindow_Link2Parent != null) 
		{
			/**
			 * This is a child window!
			 *   ==> no rendering!
			 */
			return;
		}
		
		/**
		 * This is a parent window!
		 */
		
		float fZ = this.viewingFrame[Z][MIN] + 2*fPickingBias;
		
		if ( hashChildrenWindow.isEmpty()) 
		{
			/**
			 * nothing to render yet!
			 */
			return;
		}
		
		Iterator<Vec4i> iter = hashChildrenWindow.values().iterator();
		
		color_Selection_Frame.glColor3f(gl);
		
		gl.glLineWidth(4);
		while ( iter.hasNext() ) 
		{
			Vec4i windowPos = iter.next();
			
			float fX1 = this.viewingFrame[X][MIN] + fYinc * windowPos.x();
			float fX2 = this.viewingFrame[X][MIN] + fYinc * windowPos.y();
			
			float fY1 = this.viewingFrame[Y][MIN] + fXinc * windowPos.z();
			float fY2 = this.viewingFrame[Y][MIN] + fXinc * windowPos.w();			
			
			gl.glBegin(GL.GL_LINE_LOOP);
			
			gl.glVertex3f(fX1, fY1, fZ);
			gl.glVertex3f(fX1, fY2, fZ);
			gl.glVertex3f(fX2, fY2, fZ);
			gl.glVertex3f(fX2, fY1, fZ);
			
			gl.glEnd();
		}
		
		gl.glLineWidth(1);
	}
	
	/**
	 * This method is called at the child. It links the child to the parent.
	 * 
	 * @param id
	 */
	public void addLinkTo_ParentWindow( int id ) {
		
		if ( alExternalWindow_Link2Parent==null ) 
		{
			alExternalWindow_Link2Parent = new ArrayList <GLCanvasHeatmap2DColumn> ();
		}
		
		GLCanvasHeatmap2DColumn parentHeatmap =
			(GLCanvasHeatmap2DColumn) refGeneralManager.getSingelton().getViewGLCanvasManager().getItem(id);
		
		if ( ! alExternalWindow_Link2Parent.contains(parentHeatmap) ) 
		{
			alExternalWindow_Link2Parent.add( parentHeatmap );
		}	
	}
	
	public void addLinkTo_ParentWindow( GLCanvasHeatmap2DColumn parent ) {
		
		if ( alExternalWindow_Link2Parent==null ) 
		{
			alExternalWindow_Link2Parent = new ArrayList <GLCanvasHeatmap2DColumn> ();
		}
		
		if ( ! alExternalWindow_Link2Parent.contains(parent) ) 
		{
			alExternalWindow_Link2Parent.add( parent );
		}	
		
		parent.setChildWindow(this.getId(), 
				new Vec4i(iRenderIndexStart,
						iRenderIndexStop,
						0,
						3));
	}
	
	public void link_ChildWindows_to_ParentWindow( int[] idArray ) {	
		
		if ( alExternalWindow_Link2Parent!=null ) 
		{
			assert false : "Must not call addLinkTo_ChildWindows() on child!";
		}
		/**
		 * Parent code..
		 */
		
		/* read two (int) values at once.. */
		int i=0;
		while ( i<(idArray.length) ) 
		{
			try
			{
				/* get parent container.. */
				SwtJoglGLCanvasViewRep childHeatmapContainer = 
					(SwtJoglGLCanvasViewRep) refGeneralManager
						.getSingelton().getViewGLCanvasManager().getItem(
								idArray[i]);
				
				/* get all IGLCanvasUser objects.. */
				Iterator<IGLCanvasUser> iterGLCanvasUser = childHeatmapContainer.getAllGLCanvasUsers().iterator();
				while ( iterGLCanvasUser.hasNext() ) 
				{
					IGLCanvasUser canvasUSer = iterGLCanvasUser.next();
					if ( canvasUSer.getId() ==  idArray[i+1] ) {
						/* found referred object.. */
						try
						{
							GLCanvasHeatmap2DColumn childHeatmap = (GLCanvasHeatmap2DColumn) canvasUSer;
							childHeatmap.addLinkTo_ParentWindow(this);
							
							refGeneralManager.getSingelton().logMsg(
									this.getClass().getSimpleName() + " [" +
									this.getId() + "] (parent of)==> " +
									"(SwtJoglGLCanvasViewRep)=[" +
									idArray[i] +									
									"]-->(GLCanvasHeatmap2DColumn)=[" +
									idArray[i+1] + "]",
									LoggerType.STATUS);
						} 
						catch (NullPointerException npe2)
						{
							refGeneralManager.getSingelton().logMsg(
									"SwtJoglGLCanvasViewRep id=[" +
									idArray[i] +									
									"] matched; second id=[" +
									idArray[i+1] +
									"] for GLCanvasHeatmap2DColumn did not match!",
									LoggerType.MINOR_ERROR_XML);
						} //try-catch
						
					} //if ( canvasUSer.getId() ==  idArray[i+1] ) {
				} //while ( iterGLCanvasUser.hasNext() )
			} 
			catch (NullPointerException npe)
			{
				refGeneralManager.getSingelton().logMsg(
						"SwtJoglGLCanvasViewRep id=[" +
						idArray[i] +
						"] did not match; second id=[" +
						idArray[i+1] +
						"] was not compared and is ignored!",
						LoggerType.MINOR_ERROR_XML);
				
				//throw npe;
			} // try-catch		
			
			i += 2;
			
		} //while ( i<(idArray.length) ) 
		
	}
}
