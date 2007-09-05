/**
 * 
 */
package cerberus.view.opengl.canvas.heatmap;

import gleem.linalg.Vec2f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;


import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.selection.SetSelection;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.data.graph.core.PathwayGraph;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItem;
//import cerberus.data.graph.item.vertex.PathwayVertexGraphItemRep;
//import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
//import cerberus.manager.data.IGenomeIdManager;
import cerberus.manager.event.EventPublisher;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
//import cerberus.math.statistics.minmax.MinMaxDataInteger;
//import cerberus.view.jogl.mouse.PickingJoglMouseListener; //import cerberus.view.opengl.canvas.heatmap.AGLCanvasHeatmap2D;
import cerberus.view.opengl.canvas.heatmap.GLCanvasHeatmap2D;

/**
 * @author Michael Kalkusch
 * 
 * @see cerberus.view.opengl.IGLCanvasUser
 */
public class GLCanvasHeatmap2DColumn
//extends AGLCanvasHeatmap2D
		extends GLCanvasHeatmap2D
		implements IMediatorReceiver, IMediatorSender, IGLCanvasHeatmap2D {

	public static final int OFFSET = AGLCanvasHeatmap2D.OFFSET;

	public static final int X = AGLCanvasHeatmap2D.X;

	public static final int MIN = AGLCanvasHeatmap2D.MIN;

	public static final int Y = AGLCanvasHeatmap2D.Y;

	public static final int Z = AGLCanvasHeatmap2D.Z;

	public static final int MAX = AGLCanvasHeatmap2D.MAX;

	private float fSelectionPixel = 0.1f;

	private float fSelectionLineWidth = 2.0f;

	private boolean bRenderingStyleFlat = false;
	
	private int iSelectedMode_identifyier = 0;
	
	private float fIncX_forSelection;
	
	
	
	//private int[] iIndexPickedCoored = {-1,-1};
	
	/**
	 * used to store index, that shall not be rendered.
	 */
	private HashMap <Integer,Boolean> hashHighlightSelectionIndex; 
	
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

	private static final int iInitialSizeArrayList_Selection = 10;
	
	private ArrayList<ISet> alTargetSet;

	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHeatmap2DColumn(final IGeneralManager setGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(setGeneralManager, iViewId, iParentContainerId, sLabel);

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
	}
	
	private int addhighlightSelectionIndex_addItem( ArrayList < ArrayList <Integer>> container,
			final Integer value, 
			final int mode, 
			final int containerSize) {
		if ( mode < containerSize ) {
			/* allocate new */
			int iDifference = mode - containerSize;
			
			/* insert missing empty ArrayList <Integer> objects .. */
			for (int i=0; i<iDifference; i++) {
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
		
		while ( iter.hasNext()) {
			if ( iter.next().remove( value ) ) {
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
		
		while (iterIndex.hasNext()) {
			Integer buffer = iterIndex.next();
			int bufferMode = iterIndexMode.next().intValue();
			
			if ( bufferMode > 0 ) {
				iSizeContainer = addhighlightSelectionIndex_addItem(
					this.alHighlightSelectionIndex_sortByDepth,
					buffer,
					bufferMode,
					iSizeContainer);
			
				hashHighlightSelectionIndex.put(buffer, new Boolean(true));
			} else {
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

		/* write local hashExternalId_2_HeatmapIndex to global variables int[] selectedIndex and int[] selectedIndexMode .. */
		/* does size of arrays fit? */
		if (selectedIndex.length != updateIndex.size())
		{
			selectedIndex = new int[updateIndex.size()];
			selectedIndexMode = new int[updateIndex.size()];
		}

		for ( int index=0; index< updateIndex.size(); index++ ) {
			selectedIndex[index] = updateIndex.get(index);				
			selectedIndexMode[index] = updateIndexMode.get(index).intValue();
		
		}

	}

	public void setSelectedIndex(final int[] updateIndex,
			final int[] updateIndexMode) {

		/* integrity check.. */
		if (updateIndex.length != updateIndexMode.length)
		{
			assert false : "setSelectedIndex()  int [] updateIndex !=  int [] updateIndexMode !";
			return;
		}

		boolean bUpdate = false;
		for (int i = 0; i < updateIndex.length; i++)
		{
			if (hashExternalId_2_HeatmapIndex.containsKey(updateIndex[i]))
			{

				if (hashExternalId_2_HeatmapIndex.get(updateIndex[i]).intValue() != updateIndexMode[i])
				{
					hashExternalId_2_HeatmapIndex.put(updateIndex[i], updateIndexMode[i]);
					bUpdate = true;
				}
				/* else .. data is identical to data in HashMap! */

			} else
			{
				hashExternalId_2_HeatmapIndex.put(updateIndex[i], updateIndexMode[i]);
				bUpdate = true;
			}
		}

		if (bUpdate)
		{
			/* does size of arrays fit? */
			if (selectedIndex.length != hashExternalId_2_HeatmapIndex.size())
			{
				selectedIndex = new int[hashExternalId_2_HeatmapIndex.size()];
				selectedIndexMode = new int[hashExternalId_2_HeatmapIndex.size()];
			}

			Iterator<Integer> keys = hashExternalId_2_HeatmapIndex.keySet().iterator();
			Iterator<Integer> values = hashExternalId_2_HeatmapIndex.values().iterator();

			for (int index = 0; keys.hasNext(); index++)
			{
				selectedIndex[index] = keys.next().intValue();
				selectedIndexMode[index] = values.next().intValue();
			}
		} //if ( bUpdate ) {

	}

	private void renderGL_Selection(GL gl) {

		if (selectedIndex.length < 1)
		{
			/* nothing to render.. */
			return;
		}

		/* public void displayHeatmap(GL gl) { */

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		/**
		 * force update ...
		 */

		float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
				/ (float) (iValuesInRow);

//		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[X][MIN])
//				/ (float) (iValuesInColum);

		/* Y_min .. Y_max*/
		for (int index = 0; index < selectedIndex.length; index++)
		{
			float fNowY = viewingFrame[Y][MIN];
			float fNextY = viewingFrame[Y][MAX];

			float fNowX = viewingFrame[Y][MIN] + fIncX * selectedIndex[index];
			float fNextX = fNowX + fIncX;

			gl.glBegin(GL.GL_TRIANGLE_FAN);

			gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
			gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

			gl.glEnd();
			gl.glPopName();

		} //for (int yCoord_name=0; yCoord_name<iValuesInRow; yCoord_name++)

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
//				System.out.print(" nearer than previouse hit! ");				
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

		int[] selectedIndexArray = addPickedPoint(fIndexPickedCoored,
				(float) resultPickPointCoord[0],
				(float) resultPickPointCoord[1] );
		
		if ( selectedIndexArray != null ) {

			/** notify other listeners .. */
			notifyReceiver_PickedObject(selectedIndexArray, iSelectedMode_identifyier);
		}

		
		return true;
	}

	/* -----   END: PICKING  ----- */
	/* --------------------------- */

	private void notifyReceiver_PickedObject( final int [] resultPickPointCoord, final int iModeValue) {
		
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

	@Override
	public void renderPart(GL gl) {

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

			createDisplayLists(gl);

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

		renderSelection_ownArea(gl);
		gl.glTranslatef(0, 0, -AGLCanvasHeatmap2D.fPickingBias);
		gl.glColor3f(1, 1, 0);
		renderGLAllQuadRectangle(gl, this.fIndexPickedCoored);

		gl.glColor3f(0.8f, 0.8f, 0);
		renderGLAllQuadDots(gl, this.fIndexPickedCoored);

		gl.glColor3f(0, 0, 0.8f);
		renderGL_Selection(gl);

		if ( bEnablePicking ) {
			gl.glTranslatef(0, 0, AGLCanvasHeatmap2D.fPickingBias);
			renderGL_picketPoints(gl);
		}
		
		gl.glEnable(GL.GL_LIGHTING);

		//System.err.println(" Heatmap2D ! .render(GLCanvas canvas)");
		
		gl.glPopMatrix();
	}

	public void renderGL_picketPoints(GL gl) {

		if ((selectedIndex.length > 0) || (!fIndexPickedCoored.isEmpty()))
		{

			int iRenderIndexRangeX = iRenderIndexStop - iRenderIndexStart;

			float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
					/ (float) (alTargetSet.size());
			float fNowY = viewingFrame[Y][MIN];
			float fNextY = fNowY + fIncY;

			float fIncX = (viewingFrame[X][MAX] - viewingFrame[X][MIN])
					/ (float) (iRenderIndexRangeX + 1);

			float fNowX = viewingFrame[X][MIN];
			float fNextX = fNowX + fIncX;

			if (!fIndexPickedCoored.isEmpty())
			{

				/* -------------------------- */
				/* show picked points .. */
				Iterator<Vec2f> iterPickedPoints = fIndexPickedCoored
						.iterator();

				gl.glColor3f(1.0f, 1.0f, 0);
				for (int i = 0; iterPickedPoints.hasNext(); i++)
				{
					Vec2f pickedIndex = iterPickedPoints.next();

					/* TOP */
					fNowX = viewingFrame[X][MIN] + fIncX * pickedIndex.x();
					fNextX = viewingFrame[X][MIN] + fIncX
							* (pickedIndex.x() + 1);
					fNowY = viewingFrame[Y][MIN] + fIncY * pickedIndex.y();
					fNextY = viewingFrame[Y][MIN] + fIncY
							* (pickedIndex.y() + 1);

					gl.glBegin(GL.GL_TRIANGLE_FAN);

					gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
					gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

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
					gl.glColor3f(0, 0, 1);

					if ((selectedIndex[index_X_H] >= iRenderIndexStart)
							&& (selectedIndex[index_X_H] < iRenderIndexStop))
					{

						fNowX = viewingFrame[X][MIN] + fIncX
								* selectedIndex[index_X_H];
						fNextX = fNowX + fIncX;

						/* LINES */
						fNowY = viewingFrame[Y][MIN];
						fNextY = viewingFrame[Y][MAX];

						gl.glBegin(GL.GL_LINES);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);

						gl.glEnd();

						/* TOP */
						fNowY = viewingFrame[Y][MIN] - fSelectionPixel
								* (selectedIndexMode[index_X_H] + 1);
						fNextY = viewingFrame[Y][MIN];

						gl.glBegin(GL.GL_TRIANGLE_FAN);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

						gl.glEnd();

						/* BOTTOM */
						fNowY = viewingFrame[Y][MAX];
						fNextY = viewingFrame[Y][MAX] + fSelectionPixel
								* (selectedIndexMode[index_X_H] + 1);

						gl.glBegin(GL.GL_TRIANGLE_FAN);

						gl.glVertex3f(fNowX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNowY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNextX, fNextY, viewingFrame[Z][MIN]);
						gl.glVertex3f(fNowX, fNextY, viewingFrame[Z][MIN]);

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
	private void initColorMapping(final float fColorMappingShiftFromMean) {

		if (refMinMaxDataInteger.isValid())
		{
			fColorMappingLowValue = (float) refMinMaxDataInteger.getMin(0);
			fColorMappingHighValue = (float) refMinMaxDataInteger.getMax(0);
			fColorMappingMiddleValue = (float) refMinMaxDataInteger.getMean(0)
					* fColorMappingShiftFromMean;

			fColorMappingLowRange = fColorMappingMiddleValue
					- fColorMappingLowValue;
			fColorMappingHighRangeDivisor = 1.0f / (fColorMappingHighValue - fColorMappingMiddleValue);
			fColorMappingLowRangeDivisor = 1.0f / (fColorMappingLowRange * (1.0f + fColorMappingPercentageStretch));

			float fValuesInColum = (float) refMinMaxDataInteger.getItems(0)
					/ (float) iValuesInRow;

			iValuesInColum = (int) (fValuesInColum);
		} else
		{
			System.err.println("Error while init color mapping for Heatmap!");
		}
	}

	protected void colorMapping(final GL gl, final int iValue) {

		float fValue = fColorMappingMiddleValue - (float) iValue;

		if (fValue < 0.0f)
		{
			// range [fColorMappingLowValue..fColorMappingMiddleValue[

			float fScale = (fColorMappingLowRange + fValue)
					* fColorMappingLowRangeDivisor;
			gl.glColor3f(0, 1.0f - fScale, 0);

			return;
		}
		//else

		//range [fColorMappingMiddleValue..fColorMappingHighValue]
		float fScale = (fValue) * fColorMappingHighRangeDivisor;

		gl.glColor3f(fScale, 0, 0);

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
	}

	/* (non-Javadoc)
	 * @see cerberus.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setTargetSetId(int)
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
			initColorMapping(fColorMappingShiftFromMean);
		}

	}

	public void displayHeatmap(GL gl) {
		
		if ( alTargetSet.isEmpty())
		{
			return;
		}
		
		
		if ( bRenderingStyleFlat ) {
			displayHeatmap_FlatStyle(gl);
		} else {		
			displayHeatmap_SortedStyle(gl);
		}
		
		/* Sourrounding box */
		float fBias_Z = viewingFrame[Z][MIN] + 0.0001f;
		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
		/ (float) (alTargetSet.size());

		gl.glColor3f(1.0f, 1.0f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MAX], fBias_Z);
		gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MAX], fBias_Z);
		gl.glEnd();

		/** Render each experiment in a row .. */
		if (alTargetSet.size() > 1)
		{
			gl.glBegin(GL.GL_LINES);

			/* start with index=1 and goto alTargetSet.size() */
			for (int i = 1; i < alTargetSet.size(); i++)
			{
				gl.glVertex3f(viewingFrame[X][MIN], viewingFrame[Y][MIN]
						+ fIncY * i, fBias_Z);
				gl.glVertex3f(viewingFrame[X][MAX], viewingFrame[Y][MIN]
						+ fIncY * i, fBias_Z);
			}

			gl.glEnd();
		}
		
	}

	private void displayHeatmap_SortedStyle(GL gl) {
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

	private void renderSelection_ownArea(GL gl) {
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
		
		/* reset rendering parameters.. */
		float fOffsetY= 2.0f; //(viewingFrame[X][MAX] - viewingFrame[X][MIN]);
		float fIncY = (viewingFrame[Y][MAX] - viewingFrame[Y][MIN])
		/ (float) (alTargetSet.size());
			
		float fNowY = viewingFrame[Y][MIN] + fOffsetY;
		float fNextY = fNowY + fIncY;
		
		/* use global variable.. */
		float fIncX = fIncX_forSelection;
		
		float fXOffset = ((viewingFrame[X][MAX] - viewingFrame[X][MIN])
			 - (selectedIndex.length + 1) * fIncX) * 0.5f;
		
		float fNowX = viewingFrame[X][MIN] + fXOffset;
		float fNextX = fNowX + fIncX;
		
		
		/* copy references to raw data int[] .. */
		ArrayList <int[]> bufferValueArrays = new ArrayList <int[]> ();
		ArrayList <Integer> bufferValueArrays_Offset = new ArrayList <Integer> ();
		
		Iterator <ISet> iterTargetSet = alTargetSet.iterator();
		
		while (iterTargetSet.hasNext()) {
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
		
		
		for ( int i=0; i < selectedIndex.length; i++) {
			
					
				int iCurrentIndex = selectedIndex[i];
				
				Iterator<int[]> iterRawDataArrays = bufferValueArrays.iterator();
				Iterator<Integer> iterRawDataArrays_Offset = bufferValueArrays_Offset.iterator();
				
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
							
				fNowX = fNextX;
				fNextX += fIncX;
				
				/* reset Y.. */
				fNowY = viewingFrame[Y][MIN] + fOffsetY;
				fNextY = fNowY + fIncY;
				
				/* search for data values now.. */
				
		
		} //for ( int i=0; i < selectedIndex.length; i++) {
		
		/* Sourrounding box */
		gl.glColor3f(1.0f, 1.0f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(viewingFrame[X][MIN] + fXOffset, viewingFrame[Y][MIN]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glVertex3f(fNowX, viewingFrame[Y][MIN]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glVertex3f(fNowX, viewingFrame[Y][MAX]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glVertex3f(viewingFrame[X][MIN] + fXOffset, viewingFrame[Y][MAX]+ fOffsetY, viewingFrame[Z][MIN]);
		gl.glEnd();
				
		/* ---  Render selected once more  --- */
		/* ----------------------------------- */
	
	}
	
	private void displayHeatmap_FlatStyle(GL gl) {

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

		/* public void displayHeatmap(GL gl) { */

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

		/* public void displayHeatmap(GL gl) { */

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
	 * @see cerberus.view.opengl.canvas.heatmap.IGLCanvasHeatmap2D#setSelectionItems(int[], int[], int[], int[])
	 */
	public void setSelectionItems(int[] selectionStartAtIndexX,
			int[] selectionLengthX, int[] selectionStartAtIndexY,
			int[] selectionLengthY) {

		super.setSelectionItems(selectionStartAtIndexX, selectionLengthX,
				selectionStartAtIndexY, selectionLengthY);

	}

	public void updateReceiver(Object eventTrigger) {

		System.err.println("UPDATE BINGO !");
	}

	
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		System.err.println("UPDATE BINGO !");

		System.err.println(" UPDATE SET: " + updatedSet.toString());

		if ( this.hashExternalId_2_HeatmapIndex == null ) {
			init_External2InternalHashMap();
		}
		
		IStorage[] storage = updatedSet.getStorageByDim(0);
		IVirtualArray[] virtualArray = updatedSet.getVirtualArrayByDim(0);

		ArrayList<Integer> resultBuffer = new ArrayList<Integer>(10);
		ArrayList<Integer> resultBufferMode = new ArrayList<Integer>(10);

		for (int i = 0; i < virtualArray.length; i++)
		{
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
						System.err.print("GLCanvasHeatmap2DColumn receiveUpdate [" + intBuffer[j] + "=>" + indexInt
								+ "], ");

						resultBuffer.add(indexInt);
						
						if ( intBufferMode.length != 0 ) {												
							resultBufferMode.add(intBufferMode[j]);
						} else {
							resultBufferMode.add( iSelectedMode_identifyier );
						}
						
					} else
					{
						System.err.print("[" + intBuffer[j] + "=> ?? ], ");
					}
				}
				          				
			
			}
		}

		if (!resultBuffer.isEmpty())
		{
			/* copy selection into local data structure .. */
			setSelectedIndex_asArrayList(resultBuffer, resultBufferMode);
		}

	}

}
