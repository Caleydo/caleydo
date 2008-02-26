package org.geneview.core.view.opengl.canvas.parcoords;


import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.media.opengl.GL;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.renderstyle.ParCoordsRenderStyle;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.ESelectionMode;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.miniview.AGLParCoordsMiniView;
import org.geneview.core.view.opengl.util.GLCoordinateUtils;
import org.geneview.core.view.opengl.util.GLTextInfoAreaRenderer;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * 
 * @author Alexander Lex (responsible for PC)
 * @author Marc Streit
 * 
 * This class is responsible for rendering the parallel coordinates
 *
 */
public class GLCanvasParCoords3D 
extends AGLCanvasUser
implements IMediatorReceiver, IMediatorSender {
	
	
	private enum RenderMode
	{
		ALL,
		NORMAL,
		SELECTION,
		MOUSE_OVER,
		DESELECTED
	}

	// how much room between the axis?
	private float fAxisSpacing = 0.5f;
	// how high is an axis - do NOT change
	private static final float MAX_HEIGHT = 1; 
		
	private int iGLDisplayListIndexLocal;
	private int iGLDisplayListIndexRemote;
	private int iGLDisplayListToCall = 0;
		
	// flag whether one array should be a polyline or an axis
	private boolean bRenderArrayAsPolyline = false;
	// flag whether the whole data or the selection should be rendered
	private boolean bRenderSelection = false;
	// flag whether to take measures against occlusion or not
	private boolean bPreventOcclusion = true;
	
	private EInputDataTypes eAxisDataType = EInputDataTypes.EXPERIMENTS;
	private EInputDataTypes ePolylineDataType = EInputDataTypes.GENES;
	
	private boolean bIsDisplayListDirtyLocal = true;
	private boolean bIsDisplayListDirtyRemote = true;
	
	private boolean bIsDraggingActive = false;
	private EPickingType draggedObject;
		
	private int iNumberOfAxis = 0;
	private float[] fArGateTipHeight;
	private float[] fArGateBottomHeight;
	private int iDraggedGateNumber = 0;
	
	private float fScaling = 0;
	private float fXTranslation = 0;
	private float fYTranslation = 0;
	

	
	private ParCoordsRenderStyle renderStyle;
	
	private PolylineSelectionManager polyLineSelectionManager;
	
	private TextRenderer textRenderer;
	
	private GLTextInfoAreaRenderer infoRenderer; 
	private boolean bUpdateInfoArea = false;
	private boolean bRenderInfoArea = false;
	
	private IGenomeIdManager IDManager;

	
	ArrayList<IStorage> alDataStorages;
	
	DecimalFormat decimalFormat;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasParCoords3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) 
	{
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
	
		alDataStorages = new ArrayList<IStorage>();
		renderStyle = new ParCoordsRenderStyle();		
		polyLineSelectionManager = new PolylineSelectionManager();	
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false);
		
		infoRenderer = new GLTextInfoAreaRenderer();
		
		decimalFormat = new DecimalFormat("#####.##");
		IDManager = generalManager.getSingelton().getGenomeIdManager();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
		
		toolboxRenderer = new GLParCoordsToolboxRenderer(generalManager, iUniqueId, new Vec3f (0, 0, 0), true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.geneview.core.view.opengl.util.JukeboxHierarchyLayer, org.geneview.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{
		toolboxRenderer = new GLParCoordsToolboxRenderer(generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true);
		
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
	
		iGLDisplayListIndexRemote = gl.glGenLists(1);	
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) 
	{
		// initialize selection to an empty array with 
		ISetSelection tmpSelection = alSetSelection.get(0);		
		// TODO: only for tests, should be {}
		int[] iArTmpSelectionIDs = {3, 6, 9, 12};
		tmpSelection.setSelectionIdArray(iArTmpSelectionIDs);
			
		initPolyLineLists();				
		
//		gl.glClearColor(ParCoordsRenderStyle.CANVAS_COLOR.x(), 
//						ParCoordsRenderStyle.CANVAS_COLOR.y(), 
//						ParCoordsRenderStyle.CANVAS_COLOR.z(),
//						ParCoordsRenderStyle.CANVAS_COLOR.w());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) 
	{		
		pickingManager.handlePicking(iUniqueId, gl, false);
		if(bIsDisplayListDirtyLocal)
		{
			buildPolyLineDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;			
		}	
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		display(gl);
		
		checkForHits(gl);
		
		
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) 
	{		
		if(bIsDisplayListDirtyRemote)
		{
			buildPolyLineDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}	
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);
		
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) 
	{	
		// FIXME: scaling here not too nice, operations are not in display lists
		if(bIsDraggingActive)
		{			
			gl.glTranslatef(fXTranslation, fYTranslation, 0.0f);
			gl.glScalef(fScaling, fScaling, 1.0f);
			handleDragging(gl);
	
			gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
			gl.glTranslatef(-fXTranslation, -fYTranslation, 0.0f);			
		}
		if(bRenderInfoArea)
		{
			gl.glTranslatef(fXTranslation, fYTranslation, 0.0f);
			gl.glScalef(fScaling, fScaling, 1.0f);
		
			//Point tempPoint = pickingTriggerMouseAdapter.getPickedPoint();
			infoRenderer.renderInfoArea(gl);
		
			gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
			gl.glTranslatef(-fXTranslation, -fYTranslation, 0.0f);
		}

		

		gl.glCallList(iGLDisplayListToCall);
		toolboxRenderer.render(gl);
		
		
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glLineWidth(3.0f);
		
//		// should be grey and is
//		gl.glBegin(GL.GL_LINES);
//		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
//		gl.glVertex3f(0.0f, 1.0f, 0.0f);
//		gl.glEnd();
//		
//		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
//		gl.glColor3f(1.0f, 0.0f, 0.0f);
//		
//		// should be red and is
//		gl.glBegin(GL.GL_LINES);
//		gl.glVertex3f(-1.0f, 2.0f, 0.0f);
//		gl.glVertex3f(0.0f, 2.0f, 0.0f);
//		gl.glEnd();
//		
//		gl.glPopAttrib();
//		
//		// should be grey but is red
//		gl.glBegin(GL.GL_LINES);
//		gl.glVertex3f(-1.0f, 3.0f, 0.0f);
//		gl.glVertex3f(0.0f, 3.0f, 0.0f);
//		gl.glEnd();
		
		
	
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
		EInputDataTypes eTempType = eAxisDataType;
		eAxisDataType = ePolylineDataType;
		ePolylineDataType = eTempType;
		initPolyLineLists();
	}
	
	/**
	 * Choose whether to render just the selection or all data
	 * 
	 * @param bRenderSelection if true renders only the selection, else renders everything in the data
	 */
	public void renderSelection(boolean bRenderSelection)
	{
		this.bRenderSelection = bRenderSelection;	
		initPolyLineLists();
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
	
	public void resetSelections()
	{
		for(int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = 0;
		}
		polyLineSelectionManager.clearDeselection();
		polyLineSelectionManager.clearMouseOver();
		polyLineSelectionManager.clearSelection();
	}
	
	/**
	 * Initializes the array lists that contain the data. 
	 * Must be run at program start, 
	 * every time you exchange axis and polylines and
	 * every time you change storages or selections
	 */
	private void initPolyLineLists()
	{		
		// TODO: check if I only get in here once
		alDataStorages.clear();
		polyLineSelectionManager.clearAll();
		if (alSetData == null)
			return;
		
		if (alSetSelection == null)
			return;				
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
						
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				alDataStorages.add(tmpSet.getStorageByDimAndIndex(0, 0));
			}
		}		
		int iNumberOfEntriesToRender = 0;		
		
		// decide whether to render a selection from the storage - virtual array mechanism 
		// or to render all 
		if(bRenderSelection)
		{
			//iNumberOfStoragesToRender = alDataStorages.size();
			iNumberOfEntriesToRender = alSetSelection.get(0).getSelectionIdArray().length;
		}	
		else
		{
			//iNumberOfEntriesToRender = alDataStorages.get(0).getArrayFloat().length;
			iNumberOfEntriesToRender = 20;
		}
		
	
		int iNumberOfPolyLinesToRender = 0;		
		
		// if true one array corresponds to one polyline, number of arrays is number of polylines
		if (bRenderArrayAsPolyline)
		{			
			iNumberOfPolyLinesToRender = alDataStorages.size();
			iNumberOfAxis = iNumberOfEntriesToRender;			
		}
		// render polylines across storages - first element of storage 1 to n makes up polyline
		else
		{						
			iNumberOfPolyLinesToRender = iNumberOfEntriesToRender;
			iNumberOfAxis = alDataStorages.size();
		}
		
		fArGateTipHeight = new float[iNumberOfAxis];
		fArGateBottomHeight = new float[iNumberOfAxis];
		
		for(int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = ParCoordsRenderStyle.GATE_NEGATIVE_Y_OFFSET - ParCoordsRenderStyle.GATE_TIP_HEIGHT;
		}
			
		// this for loop executes once per polyline
		for (int iPolyLineCount = 0; iPolyLineCount < iNumberOfPolyLinesToRender; iPolyLineCount++)
		{	
			if(!bRenderSelection || bRenderArrayAsPolyline)
				polyLineSelectionManager.initialAdd(iPolyLineCount);
			else
				polyLineSelectionManager.initialAdd(alSetSelection.get(0).getSelectionIdArray()[iPolyLineCount]);
		}
		
		fScaling = 2.5f;
		
		fXTranslation = (viewFrustum.getRight() - viewFrustum.getLeft()
				-(iNumberOfAxis-1)*fAxisSpacing*fScaling)/2.0f;
		fYTranslation = (viewFrustum.getTop() - viewFrustum.getBottom() - fScaling)/2.0f;
	
		
	}
	
	private void buildPolyLineDisplayList(final GL gl, int iGLDisplayListIndex)
	{		
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		gl.glTranslatef(fXTranslation, fYTranslation, 0.0f);
		gl.glScalef(fScaling, fScaling, 1.0f);
		
		renderCoordinateSystem(gl, iNumberOfAxis);	
		
		renderPolylines(gl, RenderMode.DESELECTED);
		renderPolylines(gl, RenderMode.NORMAL);
		renderPolylines(gl, RenderMode.MOUSE_OVER);
		renderPolylines(gl, RenderMode.SELECTION);		
		
		renderGates(gl, iNumberOfAxis);				
		
		gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
		gl.glTranslatef(-fXTranslation, -fYTranslation, 0.0f);		
		gl.glEndList();
	}
	
	private void renderPolylines(GL gl, RenderMode renderMode)
	{				
		
		Set<Integer> setDataToRender = null;
		float fZDepth = 0.0f;
		
		switch (renderMode)
		{
			case NORMAL:
				setDataToRender = polyLineSelectionManager.getNormalPolylines();
				if(bPreventOcclusion)
				{
					Vec4f occlusionPrevColor = renderStyle.getPolylineOcclusionPrevColor(setDataToRender.size());
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
				gl.glLineWidth(ParCoordsRenderStyle.POLYLINE_LINE_WIDTH);
				break;
			case SELECTION:	
				setDataToRender = polyLineSelectionManager.getSelectedPolylines();
				gl.glColor4f(ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.x(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.y(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.z(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.w());
				gl.glLineWidth(ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				setDataToRender = polyLineSelectionManager.getMouseOverPolylines();
				gl.glColor4f(ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.x(),
						ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.y(),
						ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.z(),
						ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.w());
				gl.glLineWidth(ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH);
				break;
			case DESELECTED:	
				setDataToRender = polyLineSelectionManager.getDeselectedPolylines();				
				Vec4f deselectedOccPrevColor = renderStyle.getPolylineDeselectedOcclusionPrevColor(setDataToRender.size());
				
				gl.glColor4f(deselectedOccPrevColor.x(),
						deselectedOccPrevColor.y(),
						deselectedOccPrevColor.z(),
						deselectedOccPrevColor.w());
				gl.glLineWidth(ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH);
				break;
			default:
				setDataToRender = polyLineSelectionManager.getNormalPolylines();
		}
	
		
		//int iNumberOfPolyLinesToRender = setDataToRender.size();
		
		Iterator<Integer> dataIterator = setDataToRender.iterator();
		// this for loop executes once per polyline
		while(dataIterator.hasNext())
		{
			int iPolyLineID = dataIterator.next();
			if(renderMode != RenderMode.DESELECTED)
				gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.POLYLINE_SELECTION, iPolyLineID));	

			
			IStorage currentStorage = null;
			
			// decide on which storage to use when array is polyline
			if(bRenderArrayAsPolyline)
			{
				int iWhichStorage = 0;

				iWhichStorage = iPolyLineID;
				
				currentStorage = alDataStorages.get(iWhichStorage);
			}
			
			float fPreviousXValue = 0;
			float fPreviousYValue = 0;
			float fCurrentXValue = 0;
			float fCurrentYValue = 0;

			// this loop executes once per axis
			for (int iVertricesCount = 0; iVertricesCount < iNumberOfAxis; iVertricesCount++)
			{
				int iStorageIndex = 0;
				
				// get the index if array as polyline
				if (bRenderArrayAsPolyline)
				{
					// if only selection should be rendered we get the ids out of
					// the selection array
					if (bRenderSelection)
					{
						iStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iVertricesCount];
					} 
					else
					{
						iStorageIndex = iVertricesCount;
					}
				}
				// get the storage and the storage index for the different cases				
				else
				{
					currentStorage = alDataStorages.get(iVertricesCount);					
					
					iStorageIndex = iPolyLineID;					
				}			
								
				fCurrentXValue = iVertricesCount * fAxisSpacing;
				fCurrentYValue = currentStorage.getArrayFloat()[iStorageIndex];
				if(iVertricesCount != 0)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(fPreviousXValue, fPreviousYValue, fZDepth);
					gl.glVertex3f(fCurrentXValue, fCurrentYValue, fZDepth);	
					gl.glEnd();
				}
				
				if(renderMode == RenderMode.SELECTION || renderMode == RenderMode.MOUSE_OVER)
				{
					renderYValues(gl, fCurrentXValue, fCurrentYValue, renderMode);					
				}				
				
				fPreviousXValue = fCurrentXValue;
				fPreviousYValue = fCurrentYValue;
			}			
			
			
			if(renderMode != RenderMode.DESELECTED)
				gl.glPopName();			
		}		
	}
	

	private void renderCoordinateSystem(GL gl, final int iNumberAxis)
	{
		textRenderer.setColor(0, 0, 0, 1);

		// draw X-Axis
		gl.glColor4f(ParCoordsRenderStyle.X_AXIS_COLOR.x(),
					ParCoordsRenderStyle.X_AXIS_COLOR.y(),
					ParCoordsRenderStyle.X_AXIS_COLOR.z(),
					ParCoordsRenderStyle.X_AXIS_COLOR.w());
				
		gl.glLineWidth(ParCoordsRenderStyle.X_AXIS_LINE_WIDTH);
		
		gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);		
	
		gl.glVertex3f(-0.1f, 0.0f, 0.0f); 
		gl.glVertex3f(((iNumberAxis-1) * fAxisSpacing)+0.1f, 0.0f, 0.0f);
			
		gl.glEnd();
		gl.glPopName();
		
		// draw all Y-Axis

		gl.glColor4f(ParCoordsRenderStyle.Y_AXIS_COLOR.x(),
				ParCoordsRenderStyle.Y_AXIS_COLOR.y(),
				ParCoordsRenderStyle.Y_AXIS_COLOR.z(),
				ParCoordsRenderStyle.Y_AXIS_COLOR.w());
			
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
			
		
		int iCount = 0;
		while (iCount < iNumberAxis)
		{
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.Y_AXIS_SELECTION, iCount));
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(iCount * fAxisSpacing, 0.0f, 0.0f);
			gl.glVertex3f(iCount * fAxisSpacing, MAX_HEIGHT, 0.0f);
			gl.glVertex3f(iCount * fAxisSpacing - ParCoordsRenderStyle.AXIS_MARKER_WIDTH, MAX_HEIGHT, 0.0f);
			gl.glVertex3f(iCount * fAxisSpacing + ParCoordsRenderStyle.AXIS_MARKER_WIDTH, MAX_HEIGHT, 0.0f);			
			gl.glEnd();				
			gl.glPopName();
			
			
			String sAxisLabel = null;
			switch (eAxisDataType) 
			{
			case EXPERIMENTS:
				sAxisLabel = "Exp." + iCount;
				break;
			case GENES:				
				//System.out.println("Accession Code: " +sAccessionCode);	
				
				// TODO: iCount is wrong here, if selection is active
				sAxisLabel = getAccessionNumberFromStorageIndex(iCount);
				//sAxisLabel = generalManager.getSingelton().getGenomeIdManager().getIdStringFromStringByMapping(sAccessionCode, EGenomeMappingType.ACCESSION_2_GENE_NAME);
				
				break;
			default:
				sAxisLabel = "No Label";
			}
			
			gl.glRotatef(90, 0, 0, 1);
			textRenderer.begin3DRendering();	
			textRenderer.draw3D(sAxisLabel, MAX_HEIGHT + 0.01f, - iCount * fAxisSpacing, 0, ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR);
			textRenderer.end3DRendering();
			gl.glRotatef(-90, 0, 0, 1);
			
			textRenderer.begin3DRendering();
			// TODO: set this to real values once we have more than normalized values
			textRenderer.draw3D(String.valueOf(MAX_HEIGHT),
								iCount * fAxisSpacing + 2 * ParCoordsRenderStyle.AXIS_MARKER_WIDTH,
								MAX_HEIGHT, 0, 0.002f);
			textRenderer.end3DRendering();
			
			iCount++;
		}				
	}
	
	private void renderGates(GL gl, int iNumberAxis)
	{
		
		gl.glColor4f(ParCoordsRenderStyle.GATE_COLOR.x(),
				ParCoordsRenderStyle.GATE_COLOR.y(),
				ParCoordsRenderStyle.GATE_COLOR.z(),
				ParCoordsRenderStyle.GATE_COLOR.w());
		
		int iCount = 0;
		while (iCount < iNumberAxis)
		{
			float fCurrentPosition = iCount * fAxisSpacing;
			
			// The tip of the gate (which is pickable)
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.LOWER_GATE_TIP_SELECTION, iCount));
			gl.glBegin(GL.GL_POLYGON);
			// variable
			gl.glVertex3f(fCurrentPosition + ParCoordsRenderStyle.GATE_WIDTH,
						fArGateTipHeight[iCount] - ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition,
						fArGateTipHeight[iCount],
						0.001f);			
			// variable
			gl.glVertex3f(fCurrentPosition - ParCoordsRenderStyle.GATE_WIDTH,
						fArGateTipHeight[iCount] - ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.001f);
			gl.glEnd();
			gl.glPopName();
			
			renderYValues(gl, fCurrentPosition, fArGateTipHeight[iCount], RenderMode.NORMAL);
			
			
			// The body of the gate
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.LOWER_GATE_BODY_SELECTION, iCount));
			gl.glBegin(GL.GL_POLYGON);
			// bottom
			gl.glVertex3f(fCurrentPosition - ParCoordsRenderStyle.GATE_WIDTH,
						fArGateBottomHeight[iCount] + ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.0001f);
			// constant
			gl.glVertex3f(fCurrentPosition + ParCoordsRenderStyle.GATE_WIDTH,
						fArGateBottomHeight[iCount] + ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.0001f);
			// top
			gl.glVertex3f(fCurrentPosition + ParCoordsRenderStyle.GATE_WIDTH,
						fArGateTipHeight[iCount] - ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.0001f);			
			// top
			gl.glVertex3f(fCurrentPosition - ParCoordsRenderStyle.GATE_WIDTH,
						fArGateTipHeight[iCount] - ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.0001f);
			gl.glEnd();
			gl.glPopName();
			
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.LOWER_GATE_BOTTOM_SELECTION, iCount));			
			// The bottom of the gate 
			gl.glBegin(GL.GL_POLYGON);
			// variable
			gl.glVertex3f(fCurrentPosition + ParCoordsRenderStyle.GATE_WIDTH,
						fArGateBottomHeight[iCount] + ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition,
						fArGateBottomHeight[iCount],
						0.001f);			
			// variable
			gl.glVertex3f(fCurrentPosition - ParCoordsRenderStyle.GATE_WIDTH,
						fArGateBottomHeight[iCount] + ParCoordsRenderStyle.GATE_TIP_HEIGHT,
						0.001f);
			gl.glEnd();
			gl.glPopName();
			
			renderYValues(gl, fCurrentPosition, fArGateBottomHeight[iCount], RenderMode.NORMAL);
			
			
			iCount++;
		}
	}
	
	private void renderYValues(GL gl, float fXOrigin, float fYOrigin, RenderMode renderMode)
	{
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
		gl.glColor4f(ParCoordsRenderStyle.Y_AXIS_COLOR.x(),
				ParCoordsRenderStyle.Y_AXIS_COLOR.y(),
				ParCoordsRenderStyle.Y_AXIS_COLOR.z(),
				ParCoordsRenderStyle.Y_AXIS_COLOR.w());
		
//		gl.glBegin(GL.GL_LINES);
//		gl.glVertex3f(fXOrigin - ParCoordsRenderStyle.AXIS_MARKER_WIDTH, fYOrigin, 0.002f);	
//		gl.glVertex3f(fXOrigin + ParCoordsRenderStyle.AXIS_MARKER_WIDTH, fYOrigin, 0.002f);
//		gl.glEnd();
		
		Rectangle2D tempRectangle = textRenderer.getBounds(decimalFormat.format(fYOrigin));
		float fBackPlaneWidth = (float)tempRectangle.getWidth() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR;
		float fBackPlaneHeight = (float)tempRectangle.getHeight() * ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR;
		float fXTextOrigin = fXOrigin + 2 * ParCoordsRenderStyle.AXIS_MARKER_WIDTH;
		float fYTextOrigin = fYOrigin;
		
		//System.out.println(					+ ", " + );
		gl.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXTextOrigin, fYTextOrigin, 0.002f);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin, 0.002f);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin + fBackPlaneHeight, 0.002f);
		gl.glVertex3f(fXTextOrigin, fYTextOrigin + fBackPlaneHeight, 0.002f);
		gl.glEnd();
		
		textRenderer.begin3DRendering();
		// TODO: set this to real values once we have more than normalized values
		textRenderer.draw3D(decimalFormat.format(fYOrigin),
							fXTextOrigin,
							fYTextOrigin,
							0.0021f, ParCoordsRenderStyle.SMALL_FONT_SCALING_FACTOR);
		textRenderer.end3DRendering();
		gl.glPopAttrib();

		
		// TODO: remove this when pop works
		switch(renderMode)
		{
			case SELECTION:	
				gl.glColor4f(ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.x(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.y(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.z(),
						ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR.w());
				gl.glLineWidth(ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				gl.glColor4f(ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.x(),
						ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.y(),
						ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.z(),
						ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR.w());
				gl.glLineWidth(ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH);
				break;
		}
	}
	
	
	

	private void handleDragging(GL gl)
	{	
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		
		float[] fArTargetWorldCoordinates = GLCoordinateUtils.
			convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);	
		
		float height = fArTargetWorldCoordinates[1];
		if (draggedObject == EPickingType.LOWER_GATE_TIP_SELECTION)
		{
			float fLowerLimit = fArGateBottomHeight[iDraggedGateNumber] + 2 * ParCoordsRenderStyle.GATE_TIP_HEIGHT;
			
			if (height > 1)
			{
				height = 1;
			}
			else if (height < 0)
			{
				height = 0;
			}				
			else if (height < fLowerLimit)
			{
				height = fLowerLimit;
			}
			
			fArGateTipHeight[iDraggedGateNumber] = height;
		}
		else if (draggedObject == EPickingType.LOWER_GATE_BOTTOM_SELECTION)
		{
			float fLowerLimit = ParCoordsRenderStyle.GATE_NEGATIVE_Y_OFFSET - ParCoordsRenderStyle.GATE_TIP_HEIGHT;
			float fUpperLimit = fArGateTipHeight[iDraggedGateNumber] - 2 * ParCoordsRenderStyle.GATE_TIP_HEIGHT;
			
			if (height > 1 - ParCoordsRenderStyle.GATE_TIP_HEIGHT)
			{
					height = 1 - ParCoordsRenderStyle.GATE_TIP_HEIGHT;
			}			
			else if (height < fLowerLimit)
			{
				height = ParCoordsRenderStyle.GATE_NEGATIVE_Y_OFFSET - ParCoordsRenderStyle.GATE_TIP_HEIGHT;
			}
			else if (height > fUpperLimit)
			{
				height = fUpperLimit;
			}
			
			fArGateBottomHeight[iDraggedGateNumber] =  height;
		}
		else if (draggedObject == EPickingType.LOWER_GATE_BODY_SELECTION)
		{
			
		}
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;		
	
		if(pickingTriggerMouseAdapter.wasMouseReleased())
		{
			bIsDraggingActive = false;
		}
		handleUnselection(iDraggedGateNumber);		
	}
	
	/**
	 * Unselect all lines that are deselected with the gates
	 * @param iAxisNumber
	 */
	private void handleUnselection(int iAxisNumber)
	{	
		IStorage currentStorage = null;
		
		// for every polyline
		for (int iPolylineCount = 0; iPolylineCount < polyLineSelectionManager.getNumberOfPolylines(); iPolylineCount++)
		{	
			int iStorageIndex = 0;
			
			// get the index if array as polyline
			if (bRenderArrayAsPolyline)
			{
				currentStorage = alDataStorages.get(iPolylineCount);
				// if only selection should be rendered we get the ids out of
				// the selection array
				if (bRenderSelection)
				{
					iStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iAxisNumber];
				} 
				else
				{
					iStorageIndex = iAxisNumber;
				}
			}
			// get the storage and the storage index for the different cases				
			else
			{
				
				if(!bRenderSelection)					
					iStorageIndex = iPolylineCount;	
				else
					iStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iPolylineCount];
			
				currentStorage = alDataStorages.get(iAxisNumber);						
			}							
			float fCurrentValue = currentStorage.getArrayFloat()[iStorageIndex];
			if(fCurrentValue < fArGateTipHeight[iAxisNumber] 
			                                    && fCurrentValue > fArGateBottomHeight[iAxisNumber])
			{			
				if(!bRenderSelection || bRenderArrayAsPolyline)
					polyLineSelectionManager.addDeselection(iPolylineCount);
				else
					polyLineSelectionManager.addDeselection(alSetSelection.get(0).getSelectionIdArray()[iPolylineCount]);
			}
			else
			{
				boolean bIsBlocked = false;
				
				// every axis
				for (int iLocalAxisCount = 0; iLocalAxisCount < iNumberOfAxis; iLocalAxisCount++)
				{					
					int iLocalStorageIndex = 0;
					if(bRenderArrayAsPolyline)
					{
						if(!bRenderSelection)					
							iLocalStorageIndex = iLocalAxisCount;	
						else
							iLocalStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iLocalAxisCount];
						
						fCurrentValue = currentStorage.getArrayFloat()[iLocalStorageIndex];
						if(fCurrentValue < fArGateTipHeight[iLocalAxisCount] 
						                                    && fCurrentValue > fArGateBottomHeight[iLocalAxisCount])
						{						
							bIsBlocked = true;
							break;
						}			
					}
					else
					{
						//iLocalStorage = alDataStorages.get(iCount);
						//iLocalStorageIndex = iPolylineCount;
						if(!bRenderSelection)					
							iLocalStorageIndex = iPolylineCount;
						else
							iLocalStorageIndex = alSetSelection.get(0).getSelectionIdArray()[iPolylineCount];
						fCurrentValue = alDataStorages.get(iLocalAxisCount).getArrayFloat()[iLocalStorageIndex];
						if(fCurrentValue < fArGateTipHeight[iLocalAxisCount] 
						                                    && fCurrentValue > fArGateBottomHeight[iLocalAxisCount])
						{
							bIsBlocked = true;
							break;
						}						
					}							
				}
				if (!bIsBlocked)
				{
					if(!bRenderSelection || bRenderArrayAsPolyline)
						polyLineSelectionManager.removeDeselection(iPolylineCount);
					else
						polyLineSelectionManager.removeDeselection(alSetSelection.get(0).getSelectionIdArray()[iPolylineCount]);
				}				
			}
		}		
	} 
	
	private void checkForHits(GL gl)
	{
		ArrayList<Pick> alHits = null;		
	
		alHits = pickingManager.getHits(iUniqueId, EPickingType.POLYLINE_SELECTION);		
		if(alHits != null)
		{			
			if (alHits.size() != 0 )
			{
				boolean bSelectionCleared = false;
				boolean bMouseOverCleared = false;					
				
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
					//alNormalPolylines.remove(new Integer(iExternalID));
						
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:						
							// TODO: if replace
							if(!bSelectionCleared)
							{
								bSelectionCleared = true;
								polyLineSelectionManager.clearSelection();								
							}
							polyLineSelectionManager.addSelection(iExternalID);
							
							if (ePolylineDataType == EInputDataTypes.GENES)
							{
								
								int iAccessionID = getAccesionIDFromStorageIndex(iExternalID);
								String sAccessionNumber = getAccessionNumberFromStorageIndex(iExternalID);								
								
								ArrayList<String> sContent = new ArrayList<String>();									
								sContent.add(sAccessionNumber);
								sContent.add("Test");
								sContent.add("Test2");
								infoRenderer.setData(sContent, tempPick.getPickedPoint(), new AGLParCoordsMiniView());
								// TODO check where to unset it
								bRenderInfoArea = true;
								//bUpdateInfoArea = true;
								
								if (iAccessionID == -1)
									break;
								
								// Write currently selected vertex to selection set
								// and trigger update event
								int[] iArTmpSelectionId = new int[1];
								int[] iArTmpDepth = new int[1];
								iArTmpSelectionId[0] = iAccessionID;
								iArTmpDepth[0] = 0;
								alSetSelection.get(0).getWriteToken();
								alSetSelection.get(0).updateSelectionSet(iUniqueId, iArTmpSelectionId, iArTmpDepth, new int[0]);
								alSetSelection.get(0).returnWriteToken();
							}
							break;	
						case MOUSE_OVER:
							// TODO: if replace
							if(!bMouseOverCleared)
							{
								bMouseOverCleared = true;
								polyLineSelectionManager.clearMouseOver();
							}
							//if(!polyLineSelectionManager.isPolylineDeselected(iExternalID))
							polyLineSelectionManager.addMouseOver(iExternalID);
							break;
						case DRAGGED:
							// no drag action for polylines
							break;						
						default:
							throw new GeneViewRuntimeException(
									"Parallel Coordinates: No such picking mode", 
									GeneViewRuntimeExceptionType.VIEW);
						
					}					
				}
				pickingManager.flushHits(iUniqueId, EPickingType.POLYLINE_SELECTION);
				// FIXME: this happens every time when something is selected
				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;
				//bRenderPolylineSelection = true;
			}				
		}
		alHits = pickingManager.getHits(iUniqueId, EPickingType.X_AXIS_SELECTION);		
		if(alHits != null)
		{				
			if (alHits.size() != 0 )
			{
				//System.out.println("X Axis Selected");
				pickingManager.flushHits(iUniqueId, EPickingType.X_AXIS_SELECTION);				
			}
		}
		alHits = pickingManager.getHits(iUniqueId, EPickingType.Y_AXIS_SELECTION);		
		if(alHits != null)
		{		
			if (alHits.size() != 0 )
			{
				//System.out.println("Y Axis Selected");
				pickingManager.flushHits(iUniqueId, EPickingType.Y_AXIS_SELECTION);			
			}			
		}
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.LOWER_GATE_TIP_SELECTION);
		if(alHits != null)
		{		
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
					
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:						
							System.out.println("Gate Selected");
							//bIsDisplayListDirty = true;
							break;
						case DRAGGED:
							//System.out.println("Gate Dragged");
							
						
							bIsDraggingActive = true;
							draggedObject = EPickingType.LOWER_GATE_TIP_SELECTION;
							iDraggedGateNumber = iExternalID;
							break;
						default:
							// do nothing
					}
				}
				pickingManager.flushHits(iUniqueId, EPickingType.LOWER_GATE_TIP_SELECTION);
				
			}			
		}
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.LOWER_GATE_BOTTOM_SELECTION);
		if(alHits != null)
		{		
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
					
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:						
							System.out.println("Gate Selected");
							//bIsDisplayListDirty = true;
							break;
						case DRAGGED:
							System.out.println("Gate Dragged");
						
						
							bIsDraggingActive = true;
							draggedObject = EPickingType.LOWER_GATE_BOTTOM_SELECTION;
							iDraggedGateNumber = iExternalID;
							break;
						default:
							// do nothing
					}
				}
				pickingManager.flushHits(iUniqueId, EPickingType.LOWER_GATE_BOTTOM_SELECTION);
				
			}			
		}
		
		alHits = pickingManager.getHits(iUniqueId, EPickingType.PC_ICON_SELECTION);
		if(alHits != null)
		{		
			if (alHits.size() != 0 )
			{
				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
					
					switch (tempPick.getPickingMode())
					{						
						case CLICKED:	
							if(iExternalID == EIconIDs.TOGGLE_RENDER_ARRAY_AS_POLYLINE.ordinal())
							{							
								if (bRenderArrayAsPolyline == true)
									renderArrayAsPolyline(false);
								else
									renderArrayAsPolyline(true);
							}
							else if(iExternalID == EIconIDs.TOGGLE_PREVENT_OCCLUSION.ordinal())
							{
								if (bPreventOcclusion == true)
									preventOcclusion(false);
								else
									preventOcclusion(true);
							}
							else if(iExternalID == EIconIDs.TOGGLE_RENDER_SELECTION.ordinal())
							{
								if (bRenderSelection == true)
									renderSelection(false);
								else
									renderSelection(true);
							}
							else if(iExternalID == EIconIDs.RESET_SELECTIONS.ordinal())
							{
								resetSelections();
							}
							
							
							bIsDisplayListDirtyLocal = true;
							bIsDisplayListDirtyRemote = true;
							pickingManager.flushHits(iUniqueId, EPickingType.PC_ICON_SELECTION);
							break;
						default:
							// do nothing
					}
				}
				pickingManager.flushHits(iUniqueId, EPickingType.PC_ICON_SELECTION);
				
			}			
		}
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.geneview.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) 
	{
		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(Object eventTrigger, ISet updatedSet): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		// contains all genes in center pathway (not yet)
		int[] iArSelection = refSetSelection.getSelectionIdArray();

		// contains type - 0 for not selected 1 for selected
		int[] iArGroup = refSetSelection.getGroupArray();
		// iterate here		
		int[] iArSelectionStorageIndices = convertAccessionToExpressionIndices(iArSelection);
		iArSelectionStorageIndices = cleanSelection(iArSelectionStorageIndices);
		setSelection(iArSelectionStorageIndices);
		
		int iSelectedAccessionID = 0;
		int iSelectedStorageIndex = 0;
		
		for(int iSelectionCount = 0; iSelectionCount < iArSelectionStorageIndices.length;  iSelectionCount++)
		{
			// TODO: set this to 1 resp. later to a enum as soon as I get real data
			if(iArGroup[iSelectionCount] == 0)
			{
				iSelectedAccessionID = iArSelection[iSelectionCount];
				iSelectedStorageIndex = iArSelectionStorageIndices[iSelectionCount];
				
				String sAccessionCode = generalManager.getSingelton().getGenomeIdManager()
					.getIdStringFromIntByMapping(iSelectedAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
			
				System.out.println("Accession Code: " +sAccessionCode);			
				System.out.println("Expression stroage index: " +iSelectedStorageIndex);
				
				if (iSelectedStorageIndex >= 0)
				{						
					if(!bRenderArrayAsPolyline)
					{
				
						//			if (alSelectedPolylines.contains(iExpressionStorageIndex))
						//			{
						polyLineSelectionManager.clearSelection();
						polyLineSelectionManager.addSelection(iSelectedStorageIndex);	
						bIsDisplayListDirtyLocal = true;
						bIsDisplayListDirtyRemote = true;
						//			}
							
						Iterator<ISet> iterSetData = alSetData.iterator();
						while (iterSetData.hasNext())
						{
							ISet tmpSet = iterSetData.next();
								
							if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
							{
									alDataStorages.add(tmpSet.getStorageByDimAndIndex(0, 0));
							}
						}	
							
						float fYValue = alDataStorages.get(0).getArrayFloat()[iSelectedStorageIndex];
						
						generalManager.getSingelton().getViewGLCanvasManager().getSelectionManager()
							.modifySelection(iSelectedAccessionID, new SelectedElementRep(iUniqueId, 0.0f, fYValue), ESelectionMode.AddPick);
					}
					else
					{
						System.out.println("Highlighting for Axis not implemented yet");
						generalManager.getSingelton().getViewGLCanvasManager().getSelectionManager()
						.modifySelection(iSelectedAccessionID, new SelectedElementRep(iUniqueId, 0.0f, 0), ESelectionMode.AddPick);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(Object eventTrigger): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
	}
	
	protected int[] cleanSelection(int[] iArSelection)
	{
	
		for (int iCount = 0; iCount < iArSelection.length; iCount++)
		{
			if(iArSelection[iCount] == -1)
				continue;		
			
			iArSelection[iCount] = iArSelection[iCount] / 1000;	
			System.out.println("Storageindexalex: " + iArSelection[iCount]);
		}		
		
		return iArSelection;
		
	}
	
	protected void setSelection(int[] iArSelection)
	{
	
		alSetSelection.get(0).setSelectionIdArray(iArSelection);
		initPolyLineLists();
	}
	
	protected int[] convertAccessionToExpressionIndices(int[] iArSelection)
	{
		int[] iArSelectionStorageIndices = new int[iArSelection.length];
		
		for(int iCount = 0; iCount < iArSelection.length; iCount++)
		{
			iArSelectionStorageIndices[iCount] = generalManager.getSingelton().getGenomeIdManager()
				.getIdIntFromIntByMapping(iArSelection[iCount], EGenomeMappingType.ACCESSION_2_MICROARRAY_EXPRESSION);
		}		
		return iArSelectionStorageIndices;
	}
	
	private int getAccesionIDFromStorageIndex(int index)
	{
		int iAccessionID = IDManager.getIdIntFromIntByMapping(index*1000+770, 
				EGenomeMappingType.MICROARRAY_EXPRESSION_2_ACCESSION);
		return iAccessionID;
	}
	
	private String getAccessionNumberFromStorageIndex(int index)
	{
			
		// Convert expression storage ID to accession ID
		int iAccessionID = IDManager.getIdIntFromIntByMapping(index*1000+770, 
					EGenomeMappingType.MICROARRAY_EXPRESSION_2_ACCESSION);
		String sAccessionNumber = IDManager.getIdStringFromIntByMapping(iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
		if(sAccessionNumber == "")
			return "Unkonwn Gene";
		else
			return sAccessionNumber;
		
	}
	
}
