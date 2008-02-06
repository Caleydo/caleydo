package org.geneview.core.view.opengl.canvas.parcoords;


import gleem.linalg.Vec4f;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.view.rep.renderstyle.ParCoordsRenderStyle;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.PickingManager;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;

import com.sun.opengl.util.BufferUtil;

/**
 * 
 * @author Alexander Lex
 * 
 * This class is responsible for rendering the parallel coordinates
 *
 */
public class GLCanvasParCoords3D extends AGLCanvasUser {

	

//	private IGeneralManager refGeneralManager;
	// how much room between the axis?
	private float axisSpacing;
		
	private int iGLDisplayListIndex;
	
	private PickingManager myPickingManager;
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	// flag whether one array should be a polyline or an axis
	private boolean bRenderArrayAsPolyline = true;
	// flag whether the whole data or the selection should be rendered
	// TODO check this
	private boolean bRenderSelection = true;
	// flag whether to take measures against occlusion or not
	private boolean bPreventOcclusion = false;
	
	private boolean bIsDisplayListDirty = true;
	
	private boolean bRenderPolylineSelection = false;
	
	private ParCoordsRenderStyle renderStyle;
	
	private HashMap<Integer, Integer> hashPolylinePickingIDToIndex;
	private HashMap<Integer, Integer> hashPolylineIndexToPickingID;
	
	
	private static final int POLYLINE_SELECTION = 1;
	private static final int AXIS_SELECTION = 2;		

	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param viewId
	 * @param parentContainerId
	 * @param label
	 */
	public GLCanvasParCoords3D(IGeneralManager refGeneralManager,
			int viewId,
			int parentContainerId,
			String label) 
	{
		super(refGeneralManager, null, viewId, parentContainerId, label);
		
		myPickingManager = refGeneralManager.getSingelton().getViewGLCanvasManager().getPickingManager();

		
		this.refViewCamera.setCaller(this);
		this.axisSpacing = 1;
		
		hashPolylinePickingIDToIndex = new HashMap<Integer, Integer>();
		hashPolylineIndexToPickingID = new HashMap<Integer, Integer>();
		
		pickingTriggerMouseAdapter = (PickingJoglMouseListener) openGLCanvasDirector
		.getJoglCanvasForwarder().getJoglMouseListener();
		
		renderStyle = new ParCoordsRenderStyle();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		
		
		ISetSelection tmpSelection = alSetSelection.get(0);
		
		int[] iArTmpSelectionIDs = {13, 18, 19, 20, 33, 36, 37, 38, 39, 40};
		//Collection<Integer> test = refGeneralManager.getSingelton().getGenomeIdManager()
		//	.getIdIntListByType(13770, EGenomeMappingType.MICROARRAY_EXPRESSION_2_ACCESSION);		
		
		tmpSelection.setSelectionIdArray(iArTmpSelectionIDs);
		
		gl.glClearColor(ParCoordsRenderStyle.CANVAS_COLOR.x(), 
						ParCoordsRenderStyle.CANVAS_COLOR.y(), 
						ParCoordsRenderStyle.CANVAS_COLOR.z(),
						ParCoordsRenderStyle.CANVAS_COLOR.w());
		

		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
	

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);		
		//gl.glRenderMode(GL.GL_SELECT);
		
		iGLDisplayListIndex = gl.glGenLists(1);				
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) 
	{	
		
		
		//gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		if(bIsDisplayListDirty)
		{
			gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
			renderScene(gl, false);
			if(bRenderPolylineSelection)
			{				
				renderScene(gl, bRenderPolylineSelection);			
			}
			
		
			gl.glEndList();
			bIsDisplayListDirty = false;
		}
			
		gl.glCallList(iGLDisplayListIndex);
		handlePicking(gl);
		
	}
	
	/**
	 * Choose whether to render one array as a polyline and every entry across arrays is an axis 
	 * or whether the array corresponds to an axis and every entry across arrays is a polyline
	 *  
	 * @param bRenderArrayAsPolyline if true array contents make up a polyline, else array is an axis
	 */
	public void renderArrayAsPolyline(boolean bRenderArrayAsPolyline)
	{
		this.bRenderArrayAsPolyline = bRenderArrayAsPolyline;
	}
	
	/**
	 * Choose whether to render just the selection or all data
	 * 
	 * @param bRenderSelection if true renders only the selection, else renders everything in the data
	 */
	public void renderSelection(boolean bRenderSelection)
	{
		this.bRenderSelection = bRenderSelection;		
	}
	
	/**
	 * Choose whether to take measures against occlusion or not
	 * 
	 * @param bPreventOcclusion
	 */
	public void preventOcclusion(boolean bPreventOcclusion)
	{
		this.bPreventOcclusion = bPreventOcclusion;
	}
	

	
	private void renderScene(GL gl, boolean bRenderPolylineSelection)
	{	
		
		int iNumberOfAxis = 0;
		
		if (alSetData == null)
			return;
		
		if (alSetSelection == null)
			return;
		
			
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		ArrayList<IStorage> alDataStorages = new ArrayList<IStorage>();
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
			// TODO: test
			//normalizeSet(tmpSet);
			
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				alDataStorages.add(tmpSet.getStorageByDimAndIndex(0, 0));
			}
		}		
		

		int iNumberOfStoragesToRender = 0;
		int iNumberOfEntriesToRender = 0;
		
		if(bRenderPolylineSelection)
		{			
			iNumberOfStoragesToRender = myPickingManager.getHits(this, POLYLINE_SELECTION).size();
		}
		else
		{
			iNumberOfStoragesToRender = alDataStorages.size();
		}
		
		
		if(bRenderSelection)
		{
			//iNumberOfStoragesToRender = alDataStorages.size();
			iNumberOfEntriesToRender = alSetSelection.get(0).getSelectionIdArray().length;
		}	
		else
		{
			//iNumberOfStoragesToRender = alDataStorages.size();
			//iNumberOfEntiresToRender = alDataStorages.get(0).getArrayFloat().length;
			iNumberOfEntriesToRender = 1000;
		}
		
		// color management
		if(bRenderPolylineSelection)
		{
			gl.glColor4f(ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.x(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.y(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.z(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.w());
		}
		else if(bPreventOcclusion)
		{
			Vec4f occlusionPrevColor = renderStyle.getPolylineOcclusionPrevColor(iNumberOfEntriesToRender);
			gl.glColor4f(occlusionPrevColor.x(),
						occlusionPrevColor.y(),
						occlusionPrevColor.z(),
						occlusionPrevColor.w());
		}
		else
		{
			gl.glColor4f(ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR.x(),
						ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR.y(),
						ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR.z(),
						ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR.w());
		}
		
		
		
		if (bRenderArrayAsPolyline)
		{
			iNumberOfAxis = iNumberOfEntriesToRender;
			
			// this for loop executes once per polyline
			for (int iStorageCount = 0; iStorageCount < iNumberOfStoragesToRender; iStorageCount++)
			{		
				// get picking ID and store it locally
				if(hashPolylineIndexToPickingID.get(iStorageCount) == null)
				{				
					int iPickingID = myPickingManager.getPickingID(this, POLYLINE_SELECTION);
					gl.glLoadName(iPickingID);	
					hashPolylinePickingIDToIndex.put(iPickingID, iStorageCount);	
					hashPolylineIndexToPickingID.put(iStorageCount, iPickingID);
				}
				else
				{
					gl.glLoadName(hashPolylineIndexToPickingID.get(iStorageCount));
				}
			
				gl.glBegin(GL.GL_LINE_STRIP);		
				
				int iWhichStorage = 0;
				if(bRenderPolylineSelection)
				{
					iWhichStorage = hashPolylinePickingIDToIndex.
						get(myPickingManager.getHits(this, POLYLINE_SELECTION).get(iStorageCount));
				}
				else
				{
					iWhichStorage = iStorageCount;
				}
				
				IStorage currentStorage = alDataStorages.get(iWhichStorage);					
		
				
				// this for loop executes once per axis
				for (int iCount = 0; iCount < iNumberOfEntriesToRender; iCount++)
				{
					int iStorageIndex = 0;
					
					// if only selection should be rendered we get the ids out of the selection array					
					if (bRenderSelection)
					{
						iStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iCount];
					}
					else
					{
						iStorageIndex = iCount;
					}							
					gl.glVertex3f(iCount * axisSpacing, 
							currentStorage.getArrayFloat()[iStorageIndex], 0.0f); 						
				}
				gl.glEnd();					
			}
		}
		else
		{	
			iNumberOfAxis = iNumberOfStoragesToRender;
			// this for loop executes once per polyline
			for (int iCount = 0; iCount < iNumberOfEntriesToRender; iCount++)
			{				
				gl.glBegin(GL.GL_LINE_STRIP);				
				
				// this for loop executes once per axis
				for (int iStorageCount = 0; iStorageCount < iNumberOfStoragesToRender; iStorageCount++)
				{
					IStorage currentStorage = alDataStorages.get(iStorageCount);
					
					int iStorageIndex = 0;
					if (bRenderSelection)
					{
						iStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iCount];
					}
					else
					{
						iStorageIndex = iCount;
					}
					
					gl.glVertex3f(iStorageCount * axisSpacing, 
							currentStorage.getArrayFloat()[iStorageIndex], 0.0f); 			
				}
				gl.glEnd();
				
				
			}	
				
		}
		
		// render the coordinate system only on the first run, not when we render the selection
		if(!bRenderPolylineSelection)
		{
			renderCoordinateSystem(gl, iNumberOfAxis, 1);		
		}
	}
	

	private void renderCoordinateSystem(GL gl, int numberParameters, float maxHeight)
	{		
		
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glLineWidth(3.0f);
				
		gl.glBegin(GL.GL_LINES);		
	
		gl.glVertex3f(-0.1f, 0.0f, 0.0f); 
		gl.glVertex3f(((numberParameters-1) * axisSpacing)+0.1f, 0.0f, 0.0f);
		
		//gl.glVertex3f(0.0f, 0.0f, 0.0f);
		//gl.glVertex3f(0.0f, maxHeight, 0.0f);			
		
		gl.glEnd();
		
		// draw all Y-Axis

		gl.glLineWidth(1.0f);
		gl.glBegin(GL.GL_LINES);	
		
		int count = 0;
		while (count < numberParameters)
		{
			gl.glVertex3f(count * axisSpacing, 0.0f, 0.0f);
			gl.glVertex3f(count * axisSpacing, maxHeight, 0.0f);
			count++;
		}
		
		gl.glEnd();	
		
	}
	
	private void handlePicking(GL gl)
	{
		Point pickPoint = null;

		boolean bMouseReleased =
			pickingTriggerMouseAdapter.wasMouseReleased();

		
		if (pickingTriggerMouseAdapter.wasMousePressed()
				|| bMouseReleased)
		{			
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			//bIsMouseOverPickingEvent = false;
		}
		else
		{
			return;
		}
		
		
		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glPushName(99);

		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix((double) pickPoint.x,
				(double) (viewport[3] - pickPoint.y),// 
				5.0, 5.0, viewport, 0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float h = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]);

		// FIXME: values have to be taken from XML file!!
		gl.glOrtho(-4.0f, 4.0f, -4*h, 4*h, 1.0f, 1000.0f);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		gl.glCallList(iGLDisplayListIndex);
		//renderPolyLines(gl);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		System.out.println("Picking Buffer: " + iArPickingBuffer[0]);
		processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint);
	}
	
	
	protected void processHits(final GL gl, int iHitCount,
			int iArPickingBuffer[], final Point pickPoint) 
	{

		myPickingManager.processHits(iHitCount, iArPickingBuffer, EPickingMode.ReplacePick);
		
		//System.out.println("Number of hits: " +iHitCount);
		
		
		
		//System.out.println("iPickedObjectID"+iPickedObjectId);
		
		// check if we hit a polyline
		if(myPickingManager.getHits(this, POLYLINE_SELECTION) != null)
		{
			ArrayList<Integer> tempList = myPickingManager.getHits(this, POLYLINE_SELECTION);
			
			if (tempList != null)
			{
				if (tempList.size() != 0 )
				{
					//System.out.println(tempList.get(0));
					bIsDisplayListDirty = true;
					bRenderPolylineSelection = true;
				}
			}
				
		}
		//myPickingManager.flushHits(iViewID, POLYLINE);
		//TODO: here decide whether to rerender 		
	}

}
