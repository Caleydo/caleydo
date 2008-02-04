package org.geneview.core.view.opengl.canvas.parcoords;


import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.manager.IGeneralManager;
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
	
	private int iViewID;
	
	private int iGLDisplayListIndex;
	
	private PickingManager myPickingManager;
	private PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	// flag whether one array should be a polyline or an axis
	private boolean bRenderArrayAsPolyline = true;
	// flag whether the whole data or the selection should be rendered
	private boolean bRenderSelection = true;
	// flag whether to take measures against occlusion or not
	private boolean bPreventOcclusion = false;
	
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

		iViewID = viewId;
		
		myPickingManager = refGeneralManager.getSingelton().getViewGLCanvasManager().getPickingManager();

		
		this.refViewCamera.setCaller(this);
		this.axisSpacing = 1;
		
		pickingTriggerMouseAdapter = (PickingJoglMouseListener) openGLCanvasDirector
		.getJoglCanvasForwarder().getJoglMouseListener();	 
		
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
		
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);		
		//gl.glRenderMode(GL.GL_SELECT);
		
		iGLDisplayListIndex = gl.glGenLists(1);		
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		renderPolyLines(gl);
		gl.glEndList();
		
		
		
		
		
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) 
	{	
		
		
		//gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glCallList(iGLDisplayListIndex);
		handlePicking(gl);
		
	
		
		// dirty flag - new list auf gleichen index
		
		//renderPolyLines(gl);	
		// render coordinate system here
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
	

	
	private void renderPolyLines(GL gl)
	{	
		
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
		int iNumberOfEntiresToRender = 0;
		if(bRenderSelection)
		{
			iNumberOfStoragesToRender = alDataStorages.size();
			iNumberOfEntiresToRender = alSetSelection.get(0).getSelectionIdArray().length;
		}
		else
		{
			iNumberOfStoragesToRender = alDataStorages.size();
			//iNumberOfEntiresToRender = alDataStorages.get(0).getArrayFloat().length;
			iNumberOfEntiresToRender = 1000;
		}
		
		if(bPreventOcclusion)
		{
			gl.glColor4f(0.0f, 0.0f, 0.0f, 0.1f);
		}
		else
		{
			gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		}
		
		if (bRenderArrayAsPolyline)
		{
			// render all polylines
			for (int iStorageCount = 0; iStorageCount < iNumberOfStoragesToRender; iStorageCount++)
			{		
				// TODO
				
				gl.glLoadName(myPickingManager.getPickingID(iViewID, 0));		
				// render one polyline
				gl.glBegin(GL.GL_LINE_STRIP);		
				
				IStorage currentStorage = alDataStorages.get(iStorageCount);
				
				
				//int[] iArSelection = alSetSelection.get(0).getSelectionIdArray();
				for (int iCount = 0; iCount < iNumberOfEntiresToRender; iCount++)
				{
					int iStorageIndex = 0;
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
			renderCoordinateSystem(gl, iNumberOfEntiresToRender, 1);//currentStorage.getArrayFloat().length, 1);
		}
		else
		{	
			for (int iCount = 0; iCount < iNumberOfEntiresToRender; iCount++)
			{
				
				// render one polyline
				gl.glBegin(GL.GL_LINE_STRIP);				
				
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
			renderCoordinateSystem(gl, iNumberOfStoragesToRender, 1);			
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

		myPickingManager.calculateNearestHit(iHitCount, iArPickingBuffer);
		
		//System.out.println("Number of hits: " +iHitCount);
		
		
		
		//System.out.println("iPickedObjectID"+iPickedObjectId);
		
		if(myPickingManager.getHits(iViewID, 0) != null)
		{
			ArrayList<Integer> tempList = myPickingManager.getHits(iViewID, 0);
			
			if (tempList.size() != 0)
				System.out.println(tempList.get(0));
		}
		myPickingManager.flushHits(iViewID,0);
		
		
	}

}
