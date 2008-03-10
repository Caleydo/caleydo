package org.geneview.core.view.opengl.canvas.parcoords;


import gleem.linalg.Vec3f;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
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
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.ESelectionMode;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.manager.view.SelectionManager;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.util.EIconTextures;
import org.geneview.core.view.opengl.util.GLCoordinateUtils;
import org.geneview.core.view.opengl.util.GLIconTextureManager;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;
import org.geneview.core.view.opengl.util.infoarea.GLInfoAreaManager;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

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
implements IMediatorReceiver, IMediatorSender 
{
	
	private float fAxisSpacing = 0;
		
	private int iGLDisplayListIndexLocal;
	private int iGLDisplayListIndexRemote;
	private int iGLDisplayListToCall = 0;
		
	// flag whether one array should be a polyline or an axis
	private boolean bRenderArrayAsPolyline = false;
	// flag whether the whole data or the selection should be rendered
	private boolean bRenderSelection = true;
	// flag whether to take measures against occlusion or not
	private boolean bPreventOcclusion = true;	
	
	// Specify the current input data type for the axis and polylines
	// Is used for meta information, such as captions
	private EInputDataType eAxisDataType = EInputDataType.EXPERIMENT;
	private EInputDataType ePolylineDataType = EInputDataType.GENE;
	
	// Specify which type of selection is currently active
	private ESelectionType eWhichContentSelection = ESelectionType.EXTERNAL_SELECTION;
	private ESelectionType eWhichStorageSelection = ESelectionType.STORAGE_SELECTION;
	
	// the list of all selection arrays
	private EnumMap<ESelectionType, ArrayList<Integer>> mapSelections;
	
	// the currently active selection arrays for content and storage 
	// (references to mapSelection entries)
	private ArrayList<Integer> alContentSelection;
	private ArrayList<Integer> alStorageSelection;
	
	private boolean bIsDisplayListDirtyLocal = true;
	private boolean bIsDisplayListDirtyRemote = true;
	
	private boolean bIsDraggingActive = false;
	private EPickingType draggedObject;
		
	private int iNumberOfAxis = 0;
	
	private float[] fArGateTipHeight;
	private float[] fArGateBottomHeight;
	private int iDraggedGateNumber = 0;
	
	private float fXTranslation = 0;
	private float fYTranslation = 0;
	
	private ParCoordsRenderStyle renderStyle;
	
	// internal management of polyline selections, use 
	// EPolylineSelectionType for types 
	private GenericSelectionManager polyLineSelectionManager;
	

	// internal management of axis selections, use
	// EAxisSelectionTypes for types
	private GenericSelectionManager axisSelectionManager;
	
	private TextRenderer textRenderer;	
	
	//protected HashMap <ESelectionType, ISetSelection> hashSetSelection;
	
	private boolean bRenderInfoArea = false;
	private boolean bInfoAreaFirstTime = false;
	
	private IGenomeIdManager IDManager;	
	
	
	private ArrayList<IStorage> alDataStorages;
	
	private DecimalFormat decimalFormat;
	
	private SelectionManager extSelectionManager;
	
	
	// holds the textures for the icons
	private GLIconTextureManager iconTextureManager;
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
		renderStyle = new ParCoordsRenderStyle(viewFrustum);		
		
		// initialize polyline selection manager
		ArrayList<String> alSelectionType = new ArrayList<String>();
		for(EPolyLineSelectionType selectionType : EPolyLineSelectionType.values())
		{
			alSelectionType.add(selectionType.getString());
		}		
		polyLineSelectionManager = new GenericSelectionManager(
				alSelectionType, EPolyLineSelectionType.NORMAL.getString());	
		
		// initialize axis selection manager
		alSelectionType = new ArrayList<String>();
		for(EAxisSelectionType selectionType : EAxisSelectionType.values())
		{
			alSelectionType.add(selectionType.getString());
		}
		axisSelectionManager = new GenericSelectionManager(
				alSelectionType, EAxisSelectionType.NORMAL.getString());
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false);
		
		decimalFormat = new DecimalFormat("#####.##");
		IDManager = generalManager.getSingelton().getGenomeIdManager();
		mapSelections = new EnumMap<ESelectionType, ArrayList<Integer>>(ESelectionType.class);	
	
		extSelectionManager = generalManager.
			getSingelton().getViewGLCanvasManager().getSelectionManager();
	
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
		
		glToolboxRenderer = new GLParCoordsToolboxRenderer(gl, generalManager, iUniqueId, new Vec3f (0, 0, 0), true, renderStyle);
		
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
		glToolboxRenderer = new GLParCoordsToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, renderStyle);
		
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
		iconTextureManager = new GLIconTextureManager(gl);	
		// initialize selection to an empty array with 
//		s
		initSelections();
		initPolyLineLists();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) 
	{		
		pickingManager.handlePicking(iUniqueId, gl, true);
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
		//GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		// FIXME: translation here not nice, operations are not in display lists
		if(bIsDraggingActive)
		{			
			gl.glTranslatef(fXTranslation, fYTranslation, 0.0f);
			//gl.glScalef(fScaling, fScaling, 1.0f);
			handleDragging(gl);
	
			//gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
			gl.glTranslatef(-fXTranslation, -fYTranslation, 0.0f);			
		}
		if(bRenderInfoArea)
		{
			gl.glTranslatef(fXTranslation, fYTranslation, 0.0f);
			//gl.glScalef(fScaling, fScaling, 1.0f);
			
//			infoAreaManager.renderInfoArea(gl, bInfoAreaFirstTime);
			bInfoAreaFirstTime = false;
		
			//gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
			gl.glTranslatef(-fXTranslation, -fYTranslation, 0.0f);
		}

		gl.glCallList(iGLDisplayListToCall);
		
		gl.glTranslatef(fXTranslation - renderStyle.getXSpacing(), 
				fYTranslation - renderStyle.getBottomSpacing(), 0.0f);
		glToolboxRenderer.render(gl);
		gl.glTranslatef(-fXTranslation + renderStyle.getXSpacing(),
				-fYTranslation + renderStyle.getBottomSpacing(), 0.0f);
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
		bRenderInfoArea = false;
		EInputDataType eTempType = eAxisDataType;
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
		if(bRenderSelection)
		{
			eWhichContentSelection = ESelectionType.EXTERNAL_SELECTION;
		}
		else
			eWhichContentSelection = ESelectionType.COMPLETE_SELECTION;
		refresh();
		//initPolyLineLists();
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
	
	/**
	 * Reset all selections and deselections
	 */
	public void resetSelections()
	{
		for(int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = renderStyle.getGateYOffset() -
				renderStyle.getGateTipHeight();
		}
		polyLineSelectionManager.clearSelections();
		axisSelectionManager.clearSelections();
		
		bRenderInfoArea = false;
	}
	
	/**
	 * Build everything new but the data base
	 */
	public void refresh()
	{
		initPolyLineLists();
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		bRenderInfoArea = false;
	}
	
	
	private void initSelections()
	{
		// TODO: check if I only get in here once
		alDataStorages.clear();
		
		
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
		
		ArrayList<Integer> alTempList = alSetSelection.get(0).getSelectionIdArray();
//		A iArTemp = ;
//		for(int iCount = 0; iCount < iArTemp.length; iCount++)
//		{
//			alTempList.add(iArTemp[iCount]);
//		}
		
		mapSelections.put(ESelectionType.EXTERNAL_SELECTION, alTempList);

		//int iStorageLength = alDataStorages.get(0).getArrayFloat().length;
		int iStorageLength = 1000;
		alTempList = new ArrayList<Integer>(iStorageLength);
		// initialize full list
		for(int iCount = 0; iCount < iStorageLength; iCount++)
		{
			alTempList.add(iCount);
		}
		
		mapSelections.put(ESelectionType.COMPLETE_SELECTION, alTempList);
		
		alTempList = new ArrayList<Integer>();
		
		for(int iCount = 0; iCount < alDataStorages.size(); iCount++)
		{
			alTempList.add(iCount);
		}
		
		mapSelections.put(ESelectionType.STORAGE_SELECTION, alTempList);
	}
	
	
	/**
	 * Initializes the array lists that contain the data. 
	 * Must be run at program start, 
	 * every time you exchange axis and polylines and
	 * every time you change storages or selections
	 */
	private void initPolyLineLists()
	{						
		polyLineSelectionManager.resetSelectionManager();
		
		int iNumberOfEntriesToRender = 0;		

		alContentSelection = mapSelections.get(eWhichContentSelection);
		alStorageSelection = mapSelections.get(eWhichStorageSelection);
		iNumberOfEntriesToRender = alContentSelection.size();
	
		int iNumberOfPolyLinesToRender = 0;		
		
		// if true one array corresponds to one polyline, number of arrays is number of polylines
		if (bRenderArrayAsPolyline)
		{			
			iNumberOfPolyLinesToRender = alStorageSelection.size();
			iNumberOfAxis = iNumberOfEntriesToRender;			
		}
		// render polylines across storages - first element of storage 1 to n makes up polyline
		else
		{						
			iNumberOfPolyLinesToRender = iNumberOfEntriesToRender;
			iNumberOfAxis = alStorageSelection.size();
		}
		
		fArGateTipHeight = new float[iNumberOfAxis];
		fArGateBottomHeight = new float[iNumberOfAxis];
		
		for(int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = renderStyle.getGateYOffset() - 
				renderStyle.getGateTipHeight();
		}
			
		// this for loop executes once per polyline
		for (int iPolyLineCount = 0; iPolyLineCount < iNumberOfPolyLinesToRender; iPolyLineCount++)
		{	
			if(bRenderArrayAsPolyline)
				polyLineSelectionManager.initialAdd(alStorageSelection.get(iPolyLineCount));
			else
				polyLineSelectionManager.initialAdd(alContentSelection.get(iPolyLineCount));
		}
		
		// this for loop executes one per axis
		for (int iAxisCount = 0; iAxisCount < iNumberOfAxis; iAxisCount++)
		{
			if(bRenderArrayAsPolyline)
				axisSelectionManager.initialAdd(alContentSelection.get(iAxisCount));
			else
				axisSelectionManager.initialAdd(alStorageSelection.get(iAxisCount));
		}
		
		//fScaling = 1f;
		
		System.out.println("Frustum height: " + (viewFrustum.getTop() - viewFrustum.getBottom()));
		System.out.println("Frustum right: " + (viewFrustum.getRight()));
		System.out.println("Frustum left: " + (viewFrustum.getLeft()));
		
//		fXTranslation = viewFrustum.getLeft() + renderStyle.getXSpacing();
//		fYTranslation = viewFrustum.getBottom() + renderStyle.getBottomSpacing();
//		
		fXTranslation = renderStyle.getXSpacing();
		fYTranslation = renderStyle.getBottomSpacing();
	
		fAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);
		
	}
	

	
	private void buildPolyLineDisplayList(final GL gl, int iGLDisplayListIndex)
	{		
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);	

//		if(bIsDraggingActive)
//			handleDragging(gl);
		gl.glTranslatef(fXTranslation, fYTranslation, 0.0f);
		//gl.glScalef(fScaling, fScaling, 1.0f);
		
		renderCoordinateSystem(gl, iNumberOfAxis);	
		
		renderPolylines(gl, EPolyLineSelectionType.DESELECTED);
		renderPolylines(gl, EPolyLineSelectionType.NORMAL);
		renderPolylines(gl, EPolyLineSelectionType.MOUSE_OVER);
		renderPolylines(gl, EPolyLineSelectionType.SELECTION);		
		
		renderGates(gl, iNumberOfAxis);				
		
		//gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
		gl.glTranslatef(-fXTranslation, -fYTranslation, 0.0f);		
		gl.glEndList();
	}
	
	private void renderPolylines(GL gl, EPolyLineSelectionType renderMode)
	{				
		
		Set<Integer> setDataToRender = null;
		float fZDepth = 0.0f;
		
		switch (renderMode)
		{
			case NORMAL:
				setDataToRender = polyLineSelectionManager.getElements(
							renderMode.getString());
				if(bPreventOcclusion)				
					gl.glColor4fv(renderStyle.
							getPolylineOcclusionPrevColor(setDataToRender.size()), 0);									
				else
					gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR, 0);
				
				gl.glLineWidth(ParCoordsRenderStyle.POLYLINE_LINE_WIDTH);
				break;
			case SELECTION:	
				setDataToRender = polyLineSelectionManager.getElements(
						renderMode.getString());
				gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				setDataToRender = polyLineSelectionManager.getElements(
						renderMode.getString());
				gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH);
				break;
			case DESELECTED:	
				setDataToRender = polyLineSelectionManager.getElements(
						renderMode.getString());				
				gl.glColor4fv(renderStyle.
						getPolylineDeselectedOcclusionPrevColor(setDataToRender.size()),
						0);
				gl.glLineWidth(ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH);
				break;
			default:
				setDataToRender = polyLineSelectionManager.getElements(
						EPolyLineSelectionType.NORMAL.getString());
		}
		
		Iterator<Integer> dataIterator = setDataToRender.iterator();
		// this for loop executes once per polyline
		while(dataIterator.hasNext())
		{
			int iPolyLineID = dataIterator.next();
			if(renderMode != EPolyLineSelectionType.DESELECTED)
				gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.POLYLINE_SELECTION, iPolyLineID));
			
			IStorage currentStorage = null;
			
			// decide on which storage to use when array is polyline
			if(bRenderArrayAsPolyline)
			{
				int iWhichStorage = iPolyLineID;				
				//currentStorage = alDataStorages.get(alStorageSelection.get(iWhichStorage));
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
					iStorageIndex = alContentSelection.get(iVertricesCount);
				}
				// get the storage and the storage index for the different cases				
				else
				{				
					currentStorage = alDataStorages.get(alStorageSelection.get(iVertricesCount));					
					iStorageIndex = iPolyLineID;					
				}			
								
				fCurrentXValue = iVertricesCount * fAxisSpacing;
				fCurrentYValue = currentStorage.getArrayFloat()[iStorageIndex];
				if(iVertricesCount != 0)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(fPreviousXValue, 
							fPreviousYValue * renderStyle.getAxisHeight(),
							fZDepth);
					gl.glVertex3f(fCurrentXValue, 
							fCurrentYValue * renderStyle.getAxisHeight(), 
							fZDepth);	
					gl.glEnd();
				}
				
				if(renderMode == EPolyLineSelectionType.SELECTION || renderMode == EPolyLineSelectionType.MOUSE_OVER)
				{
					renderYValues(gl, fCurrentXValue, fCurrentYValue * renderStyle.getAxisHeight(), renderMode);					
				}				
				
				fPreviousXValue = fCurrentXValue;
				fPreviousYValue = fCurrentYValue;
			}						
			
			if(renderMode != EPolyLineSelectionType.DESELECTED)
				gl.glPopName();			
		}		
	}
	

	private void renderCoordinateSystem(GL gl, final int iNumberAxis)
	{
		textRenderer.setColor(0, 0, 0, 1);

		// draw X-Axis
		gl.glColor4fv(ParCoordsRenderStyle.X_AXIS_COLOR, 0);
				
		gl.glLineWidth(ParCoordsRenderStyle.X_AXIS_LINE_WIDTH);
		
		gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);		
	
		gl.glVertex3f(-0.1f, 0.0f, 0.0f); 
		gl.glVertex3f(((iNumberAxis-1) * fAxisSpacing)+0.1f, 0.0f, 0.0f);
			
		gl.glEnd();
		gl.glPopName();
		
		// draw all Y-Axis
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);			
		
		Set<Integer> selectedSet = axisSelectionManager.getElements(EAxisSelectionType.SELECTION.getString());
		Set<Integer> mouseOverSet = axisSelectionManager.getElements(EAxisSelectionType.MOUSE_OVER.getString());
		ArrayList<Integer> alAxisSelection;
		
		if(bRenderArrayAsPolyline)
			alAxisSelection = alContentSelection;			
		else
			alAxisSelection = alStorageSelection;
			
		
		int iCount = 0;
		while (iCount < iNumberAxis)
		{
			if(selectedSet.contains(alAxisSelection.get(iCount)))				
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_SELECTED_COLOR, 0);
			else if (mouseOverSet.contains(alAxisSelection.get(iCount)))
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_COLOR, 0);
			else
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_COLOR, 0);
			
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.Y_AXIS_SELECTION, alAxisSelection.get(iCount)));
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(iCount * fAxisSpacing, 
					ParCoordsRenderStyle.Y_AXIS_LOW, 
					ParCoordsRenderStyle.AXIS_Z);
			gl.glVertex3f(iCount * fAxisSpacing, 
					renderStyle.getAxisHeight(),
					ParCoordsRenderStyle.AXIS_Z);
			gl.glVertex3f(iCount * fAxisSpacing - ParCoordsRenderStyle.AXIS_MARKER_WIDTH,
					renderStyle.getAxisHeight(), 
					ParCoordsRenderStyle.AXIS_Z);
			gl.glVertex3f(iCount * fAxisSpacing + ParCoordsRenderStyle.AXIS_MARKER_WIDTH,
					renderStyle.getAxisHeight(), 
					ParCoordsRenderStyle.AXIS_Z);			
			gl.glEnd();				
			gl.glPopName();
			
			
			String sAxisLabel = null;
			switch (eAxisDataType) 
			{
			case EXPERIMENT:
				sAxisLabel = "Exp." + iCount;
				//sAxisLabel = alSetData.get(alStorageSelection.get(iCount)).getLabel();
				break;
			case GENE:				
				sAxisLabel = getAccessionNumberFromStorageIndex(alContentSelection.get(iCount));
				break;
			default:
				sAxisLabel = "No Label";
			}
			gl.glPushAttrib(GL.GL_CURRENT_BIT);
			
			gl.glTranslatef(iCount * fAxisSpacing, renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing(), 0);
			gl.glRotatef(25, 0, 0, 1);
			textRenderer.begin3DRendering();	
			textRenderer.draw3D(sAxisLabel, 0, 0, 0, renderStyle.getSmallFontScalingFactor());
			textRenderer.end3DRendering();
			gl.glRotatef(-25, 0, 0, 1);
			gl.glTranslatef(-iCount * fAxisSpacing, -(renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing()), 0);
						
			textRenderer.begin3DRendering();
			// TODO: set this to real values once we have more than normalized values
			textRenderer.draw3D(String.valueOf(1),
								iCount * fAxisSpacing + 2 * ParCoordsRenderStyle.AXIS_MARKER_WIDTH,
								renderStyle.getAxisHeight(), 0, renderStyle.getSmallFontScalingFactor());
			textRenderer.end3DRendering();
			gl.glPopAttrib();
			
			// render Buttons
			
			int iNumberOfButtons = 0;
			if(iCount != 0 || iCount != iNumberAxis-1)			
				iNumberOfButtons = 4;			
			else
				iNumberOfButtons = 3;
		
			float fXButtonOrigin = 0;
			float fYButtonOrigin = 0;
			int iPickingID = -1;
			
			fXButtonOrigin = iCount * fAxisSpacing - 
				(iNumberOfButtons * renderStyle.getButtonWidht() +
				(iNumberOfButtons - 1) * renderStyle.getButtonSpacing()) / 2;
			fYButtonOrigin = -renderStyle.getAxisButtonYOffset();
			
			if(iCount != 0)
			{				
				iPickingID = pickingManager.getPickingID(iUniqueId, EPickingType.MOVE_AXIS_LEFT, iCount);
				renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID, EIconTextures.ARROW_LEFT);
			}			
		
			// remove button
			fXButtonOrigin = fXButtonOrigin + renderStyle.getButtonWidht() +
				renderStyle.getButtonSpacing();
			
			iPickingID = pickingManager.getPickingID(iUniqueId, EPickingType.REMOVE_AXIS, iCount);			
			renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID, EIconTextures.REMOVE);
			
			// duplicate axis button
			fXButtonOrigin = fXButtonOrigin + renderStyle.getButtonWidht() +
			renderStyle.getButtonSpacing();
			iPickingID = pickingManager.getPickingID(iUniqueId, EPickingType.DUPLICATE_AXIS, iCount);			
			renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID, EIconTextures.DUPLICATE);	
		
			if(iCount != iNumberAxis-1)
			{
				// right, move right button
				fXButtonOrigin = fXButtonOrigin + renderStyle.getButtonWidht() +
					renderStyle.getButtonSpacing();				
				iPickingID = pickingManager.getPickingID(iUniqueId, EPickingType.MOVE_AXIS_RIGHT, iCount);
				renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID, EIconTextures.ARROW_RIGHT);		
			}
			iCount++;
		}				
	}
	
	private void renderButton(GL gl, float fXButtonOrigin, 
				float fYButtonOrigin, 
				int iPickingID, 
				EIconTextures eIconTextures)
	{	
		
		Texture tempTexture = iconTextureManager.getIconTexture(eIconTextures);
		tempTexture.enable();
		tempTexture.bind();
		
		TextureCoords texCoords = tempTexture.getImageTexCoords();
		
		gl.glPushAttrib(GL.GL_CURRENT_BIT);
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushName(iPickingID);
		gl.glBegin(GL.GL_POLYGON);		
		
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom()); 
		gl.glVertex3f(fXButtonOrigin, 
				fYButtonOrigin, 
				ParCoordsRenderStyle.AXIS_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top()); 
		gl.glVertex3f(fXButtonOrigin, 
				fYButtonOrigin + renderStyle.getButtonWidht(),
				ParCoordsRenderStyle.AXIS_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
		gl.glVertex3f(fXButtonOrigin + renderStyle.getButtonWidht(),
				fYButtonOrigin + renderStyle.getButtonWidht(),
				ParCoordsRenderStyle.AXIS_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom()); 
		gl.glVertex3f(fXButtonOrigin + renderStyle.getButtonWidht(),
				fYButtonOrigin,
				ParCoordsRenderStyle.AXIS_Z);
		gl.glEnd();	
		gl.glPopName();
		gl.glPopAttrib();
		tempTexture.disable();
	}
	
	private void renderGates(GL gl, int iNumberAxis)
	{		
		gl.glColor4fv(ParCoordsRenderStyle.GATE_COLOR, 0);
		
		final float fGateWidth = renderStyle.getGateWidth();
		final float fGateTipHeight = renderStyle.getGateTipHeight();
		final float fGateYOffset = renderStyle.getGateYOffset();
		int iCount = 0;
		while (iCount < iNumberAxis)
		{			
			float fCurrentPosition = iCount * fAxisSpacing;
			
			// The tip of the gate (which is pickable)
			gl.glPushName(pickingManager.getPickingID(iUniqueId, 
					EPickingType.LOWER_GATE_TIP_SELECTION, iCount));
			gl.glBegin(GL.GL_POLYGON);
			// variable
			gl.glVertex3f(fCurrentPosition + fGateWidth,
						fArGateTipHeight[iCount] - fGateTipHeight,
						0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition,
						fArGateTipHeight[iCount],
						0.001f);			
			// variable
			gl.glVertex3f(fCurrentPosition - fGateWidth,
						fArGateTipHeight[iCount] - fGateTipHeight,
						0.001f);
			gl.glEnd();
			gl.glPopName();
			
			renderYValues(gl, fCurrentPosition, fArGateTipHeight[iCount], EPolyLineSelectionType.NORMAL);
			
			
			// The body of the gate
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.LOWER_GATE_BODY_SELECTION, iCount));
			gl.glBegin(GL.GL_POLYGON);
			// bottom
			gl.glVertex3f(fCurrentPosition - fGateWidth,
						fArGateBottomHeight[iCount] + fGateTipHeight,
						0.0001f);
			// constant
			gl.glVertex3f(fCurrentPosition + fGateWidth,
						fArGateBottomHeight[iCount] + fGateTipHeight,
						0.0001f);
			// top
			gl.glVertex3f(fCurrentPosition + fGateWidth,
						fArGateTipHeight[iCount] - fGateTipHeight,
						0.0001f);			
			// top
			gl.glVertex3f(fCurrentPosition - fGateWidth,
						fArGateTipHeight[iCount] - fGateTipHeight,
						0.0001f);
			gl.glEnd();
			gl.glPopName();
			
			gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.LOWER_GATE_BOTTOM_SELECTION, iCount));	
			// The bottom of the gate 
			gl.glBegin(GL.GL_POLYGON);
			// variable
			gl.glVertex3f(fCurrentPosition + fGateWidth,
						fArGateBottomHeight[iCount] + fGateTipHeight,
						0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition,
						fArGateBottomHeight[iCount],
						0.001f);			
			// variable
			gl.glVertex3f(fCurrentPosition - fGateWidth,
						fArGateBottomHeight[iCount] + fGateTipHeight,
						0.001f);
			gl.glEnd();
			gl.glPopName();
			
			renderYValues(gl, fCurrentPosition, fArGateBottomHeight[iCount], EPolyLineSelectionType.NORMAL);			
			
			iCount++;
		}
	}
	
	private void renderYValues(GL gl, float fXOrigin, float fYOrigin, EPolyLineSelectionType renderMode)
	{
		// don't render values that are below the y axis
		if(fYOrigin < 0)
			return;
		
		gl.glPushAttrib(GL.GL_CURRENT_BIT);
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
		gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_COLOR, 0);
		
		Rectangle2D tempRectangle = textRenderer.getBounds(decimalFormat.format(fYOrigin));
		float fBackPlaneWidth = (float)tempRectangle.getWidth() * renderStyle.getSmallFontScalingFactor();
		float fBackPlaneHeight = (float)tempRectangle.getHeight() * renderStyle.getSmallFontScalingFactor();
		float fXTextOrigin = fXOrigin + 2 * ParCoordsRenderStyle.AXIS_MARKER_WIDTH;
		float fYTextOrigin = fYOrigin;
		
		gl.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXTextOrigin, fYTextOrigin, 0.002f);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin, 0.002f);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin + fBackPlaneHeight, 0.002f);
		gl.glVertex3f(fXTextOrigin, fYTextOrigin + fBackPlaneHeight, 0.002f);
		gl.glEnd();
		
		textRenderer.begin3DRendering();
		// TODO: set this to real values once we have more than normalized values
		textRenderer.draw3D(decimalFormat.format(fYOrigin / renderStyle.getAxisHeight()),
							fXTextOrigin,
							fYTextOrigin,
							0.0021f, renderStyle.getSmallFontScalingFactor());
		textRenderer.end3DRendering();
		gl.glPopAttrib();

	}	

	private void handleDragging(GL gl)
	{	
//		bIsDisplayListDirtyLocal = true;
//		bIsDisplayListDirtyRemote = true;
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		
		float[] fArTargetWorldCoordinates = GLCoordinateUtils.
			convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);	
		
		float height = fArTargetWorldCoordinates[1];
		if (draggedObject == EPickingType.LOWER_GATE_TIP_SELECTION)
		{
			float fLowerLimit = fArGateBottomHeight[iDraggedGateNumber] + 
				2 * renderStyle.getGateTipHeight();
			
			if (height > renderStyle.getAxisHeight())
			{
				height = renderStyle.getAxisHeight();
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
			float fLowerLimit = renderStyle.getGateYOffset() 
				- renderStyle.getGateTipHeight();
			float fUpperLimit = fArGateTipHeight[iDraggedGateNumber] - 2 * renderStyle.getGateTipHeight();
			
			if (height > renderStyle.getAxisHeight() - renderStyle.getGateTipHeight())
			{
					height = renderStyle.getAxisHeight() - renderStyle.getGateTipHeight();
			}			
			else if (height < fLowerLimit)
			{
				height = renderStyle.getGateYOffset() - renderStyle.getGateTipHeight();
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
		for (int iPolylineCount = 0; iPolylineCount < polyLineSelectionManager.getNumberOfElements(); iPolylineCount++)
		{	
			int iStorageIndex = 0;
			
			// get the index if array as polyline
			if (bRenderArrayAsPolyline)
			{
				currentStorage = alDataStorages.get(alStorageSelection.get(iPolylineCount));

				iStorageIndex = alContentSelection.get(iAxisNumber);
			}
			// get the storage and the storage index for the different cases				
			else
			{
				iStorageIndex = alContentSelection.get(iPolylineCount);			
				currentStorage = alDataStorages.get(alStorageSelection.get(iAxisNumber));						
			}							
			float fCurrentValue = currentStorage.getArrayFloat()[iStorageIndex] * renderStyle.getAxisHeight();
			if(fCurrentValue < fArGateTipHeight[iAxisNumber] 
			                                    && fCurrentValue > fArGateBottomHeight[iAxisNumber])
			{	
				if(polyLineSelectionManager.checkStatus(EPolyLineSelectionType.SELECTION.getString(), iPolylineCount))
					bRenderInfoArea = false;
				
				if(bRenderArrayAsPolyline)
					polyLineSelectionManager.addToType(EPolyLineSelectionType.DESELECTED.getString(), 
							alStorageSelection.get(iPolylineCount));
				else
					polyLineSelectionManager.addToType(EPolyLineSelectionType.DESELECTED.getString(), 
							alContentSelection.get(iPolylineCount));
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
							iLocalStorageIndex = alContentSelection.get(iLocalAxisCount);
						
						fCurrentValue = currentStorage.getArrayFloat()[iLocalStorageIndex] * renderStyle.getAxisHeight();
						if(fCurrentValue < fArGateTipHeight[iLocalAxisCount] 
						                                    && fCurrentValue > fArGateBottomHeight[iLocalAxisCount])
						{						
							bIsBlocked = true;
							break;
						}			
					}
					else
					{					
						iLocalStorageIndex = alContentSelection.get(iPolylineCount);
						fCurrentValue = alDataStorages.get(alStorageSelection.get(iLocalAxisCount)).getArrayFloat()[iLocalStorageIndex] * renderStyle.getAxisHeight();
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
					if(bRenderArrayAsPolyline)
						polyLineSelectionManager.removeFromType(EPolyLineSelectionType.DESELECTED.getString(),
								alStorageSelection.get(iPolylineCount));
					else
						polyLineSelectionManager.removeFromType(EPolyLineSelectionType.DESELECTED.getString(),
								alContentSelection.get(iPolylineCount));
				}				
			}
		}		
	} 
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.geneview.core.manager.view.EPickingType, org.geneview.core.manager.view.EPickingMode, int, org.geneview.core.manager.view.Pick)
	 */
	protected void handleEvents(final EPickingType ePickingType, 
			final EPickingMode ePickingMode, 
			final int iExternalID,
			final Pick pick)
	{
		// Check if selection occurs in the pool layer of the bucket
		if (glToolboxRenderer.getContainingLayer().getCapacity() >= 10)
			return;
		
		switch (ePickingType)
		{
		case POLYLINE_SELECTION:
			switch (ePickingMode)
			{						
				case CLICKED:				
					
					Set<Integer> selectedSet = polyLineSelectionManager.getElements(EPolyLineSelectionType.SELECTION.getString());					
					polyLineSelectionManager.clearSelection(EPolyLineSelectionType.SELECTION.getString());							
					polyLineSelectionManager.addToType(EPolyLineSelectionType.SELECTION.getString(),
							iExternalID);
					
					if (ePolylineDataType == EInputDataType.GENE)
					{
						
						int iAccessionID = getAccesionIDFromStorageIndex(iExternalID);								
						System.out.println("Accession ID: " + iAccessionID);
						//generalManager.getSingelton().getViewGLCanvasManager().getInfoAreaManager()
						//	.setData(iAccessionID, ePolylineDataType, pick.getPickedPoint());
						bRenderInfoArea = true;
						bInfoAreaFirstTime = true;								
						
						// Write currently selected vertex to selection set
						// and trigger update event
						ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(2);
						//iAlTmpSelectionId.add(1);
						ArrayList<Integer> iAlTmpGroup = new ArrayList<Integer>(2);
						
						if (iAccessionID != -1)
						{						
							
							iAlTmpSelectionId.add(iAccessionID);
							iAlTmpGroup.add(2);
							extSelectionManager.modifySelection(iAccessionID, 
									createElementRep(iExternalID), ESelectionMode.ReplacePick);
						}							
							
						for(Integer iCurrent : selectedSet)
						{
							iAccessionID = getAccesionIDFromStorageIndex(iCurrent);
							if(iAccessionID != -1)
							{
								iAlTmpSelectionId.add(iAccessionID);
								iAlTmpGroup.add(-1);
							}
						}

						alSetSelection.get(0).getWriteToken();
						alSetSelection.get(0).updateSelectionSet(iUniqueId, 
								iAlTmpSelectionId, iAlTmpGroup, null);
						alSetSelection.get(0).returnWriteToken();

					

						
					}
					bIsDisplayListDirtyLocal = true;
					bIsDisplayListDirtyRemote = true;
					break;	
				case MOUSE_OVER:

					polyLineSelectionManager.clearSelection(EPolyLineSelectionType.MOUSE_OVER.getString());
					polyLineSelectionManager.addToType(EPolyLineSelectionType.MOUSE_OVER.getString(), iExternalID);
					bIsDisplayListDirtyLocal = true;
					bIsDisplayListDirtyRemote = true;
					break;					
				default:
				
			}
			pickingManager.flushHits(iUniqueId, ePickingType);
			break;
			
		case X_AXIS_SELECTION:
			pickingManager.flushHits(iUniqueId, ePickingType);
			break;
		case Y_AXIS_SELECTION:
			switch (ePickingMode)
			{
			case CLICKED:
				axisSelectionManager.clearSelection(
						EAxisSelectionType.SELECTION.getString());
				axisSelectionManager.addToType(
						EAxisSelectionType.SELECTION.getString(), iExternalID);
				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;
				break;
			case MOUSE_OVER:
				axisSelectionManager.clearSelection(
						EAxisSelectionType.MOUSE_OVER.getString());
				axisSelectionManager.addToType(
						EAxisSelectionType.MOUSE_OVER.getString(), iExternalID);
				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;
				break;
			}
			pickingManager.flushHits(iUniqueId, ePickingType);
			break;
		case LOWER_GATE_TIP_SELECTION:
			switch (ePickingMode)
			{
			case CLICKED:						
				System.out.println("Gate Selected");
				//bIsDisplayListDirty = true;
				break;
			case DRAGGED:							
				bIsDraggingActive = true;
				draggedObject = EPickingType.LOWER_GATE_TIP_SELECTION;
				iDraggedGateNumber = iExternalID;
//				bIsDisplayListDirtyLocal = true;
//				bIsDisplayListDirtyRemote = true;
				break;
			}
			pickingManager.flushHits(iUniqueId, ePickingType);
			break;
		case LOWER_GATE_BOTTOM_SELECTION:
			switch (ePickingMode)
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
			pickingManager.flushHits(iUniqueId, ePickingType);
		case PC_ICON_SELECTION:	
			switch (ePickingMode)
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
					break;
				default:
					// do nothing
			}
		
			pickingManager.flushHits(iUniqueId, EPickingType.PC_ICON_SELECTION);
			break;
		case REMOVE_AXIS:
			switch (ePickingMode)
			{						
				case CLICKED:	
					//int iSelection = 0;
					if(bRenderArrayAsPolyline)
					{
						alContentSelection.remove(iExternalID);	
					}
					else
					{
						alStorageSelection.remove(iExternalID);															
					}
					refresh();
					break;
				default:
					// do nothing
			}		
			pickingManager.flushHits(iUniqueId, EPickingType.REMOVE_AXIS);
			break;
		case MOVE_AXIS_LEFT:
			switch (ePickingMode)
			{						
				case CLICKED:	
					
					ArrayList<Integer> alSelection;
					if(bRenderArrayAsPolyline)							
						alSelection = alContentSelection;							
					else						
						alSelection = alStorageSelection;								
					
					if (iExternalID > 0 && iExternalID < alSelection.size())
					{
						int iTemp = alSelection.get(iExternalID - 1);
						alSelection.set(iExternalID - 1, alSelection.get(iExternalID));
						alSelection.set(iExternalID, iTemp);
						refresh();
					}

					break;
				default:
					// do nothing
			}
			pickingManager.flushHits(iUniqueId, EPickingType.MOVE_AXIS_LEFT);
			break;
		case MOVE_AXIS_RIGHT:	
				
			switch (ePickingMode)
			{						
				case CLICKED:	
					ArrayList<Integer> alSelection;
					if(bRenderArrayAsPolyline)							
						alSelection = alContentSelection;							
					else						
						alSelection = alStorageSelection;
					
					if (iExternalID >= 0 && iExternalID < alSelection.size()-1)
					{
						int iTemp = alSelection.get(iExternalID + 1);
						alSelection.set(iExternalID+1, alSelection.get(iExternalID));
						alSelection.set(iExternalID, iTemp);
						refresh();
					}						
					break;
				default:
					// do nothing
			}			
			pickingManager.flushHits(iUniqueId, EPickingType.MOVE_AXIS_RIGHT);
			break;
		case DUPLICATE_AXIS:			
			switch (ePickingMode)
			{						
				case CLICKED:	
					ArrayList<Integer> alSelection;
					if(bRenderArrayAsPolyline)							
						alSelection = alContentSelection;							
					else						
						alSelection = alStorageSelection;
					
//					if (iExternalID >= 0 && iExternalID < alSelection.size()-1)
//					{
						alSelection.add(iExternalID+1, alSelection.get(iExternalID));
						refresh();
//					}						
					break;
				default:
					// do nothing
			}		
			pickingManager.flushHits(iUniqueId, EPickingType.DUPLICATE_AXIS);			
			break;			
		default:
			// do nothing		
		
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
		ArrayList<Integer> iAlSelection = refSetSelection.getSelectionIdArray();

		// contains type - 0 for not selected 1 for selected
		ArrayList<Integer> iAlGroup = refSetSelection.getGroupArray();
		ArrayList<Integer> iAlOptional = refSetSelection.getOptionalDataArray();
		// iterate here		
		ArrayList<Integer> iAlSelectionStorageIndices = convertAccessionToExpressionIndices(iAlSelection);
		iAlSelectionStorageIndices = cleanSelection(iAlSelectionStorageIndices, iAlGroup);
		setSelection(iAlSelectionStorageIndices, iAlGroup, iAlOptional);
		
		int iSelectedAccessionID = 0;
		int iSelectedStorageIndex = 0;
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		
		for(int iSelectionCount = 0; iSelectionCount < iAlSelectionStorageIndices.size();  iSelectionCount++)
		{
			// TODO: set this to 1 resp. later to a enum as soon as I get real data
			if(iAlGroup.get(iSelectionCount) == 1)
			{
				iSelectedAccessionID = iAlSelection.get(iSelectionCount);
				iSelectedStorageIndex = iAlSelectionStorageIndices.get(iSelectionCount);
				
				String sAccessionCode = generalManager.getSingelton().getGenomeIdManager()
					.getIdStringFromIntByMapping(iSelectedAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
			
				System.out.println("Accession Code: " +sAccessionCode);			
				System.out.println("Expression stroage index: " +iSelectedStorageIndex);
				
				if (iSelectedStorageIndex >= 0)
				{						
					if(!bRenderArrayAsPolyline)
					{				
						// handle local selection
						polyLineSelectionManager.clearSelection(EPolyLineSelectionType.MOUSE_OVER.getString());
						polyLineSelectionManager.addToType(EPolyLineSelectionType.MOUSE_OVER.getString(), iSelectedStorageIndex);
						
						// handle external selection
						extSelectionManager.modifySelection(iSelectedAccessionID, 
								createElementRep(iSelectedStorageIndex), ESelectionMode.AddPick);
					}
					else
					{
						axisSelectionManager.clearSelection(EAxisSelectionType.MOUSE_OVER.getString());
						axisSelectionManager.addToType(EAxisSelectionType.MOUSE_OVER.getString(), iSelectedStorageIndex);
						
						extSelectionManager.modifySelection(iSelectedAccessionID, createElementRep(iSelectedStorageIndex), ESelectionMode.AddPick);
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
	
	private SelectedElementRep createElementRep(int iStorageIndex)
	{
		
		SelectedElementRep elemntRep;
		
		if(!bRenderArrayAsPolyline)
		{
			ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
			float fYValue;
			float fXValue;
			int iCount = 0;
			for(Integer iCurrent : alStorageSelection)
			{
				fYValue = alDataStorages.get(iCurrent).getArrayFloat()[iStorageIndex];
				fYValue = fYValue * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
				fXValue = iCount * fAxisSpacing + renderStyle.getXSpacing();
				alPoints.add(new Vec3f(fXValue, fYValue, 0));
				iCount++;
			}		
		
			elemntRep = new SelectedElementRep(iUniqueId, alPoints);
		
		}
		else
		{			
			float fXValue = alContentSelection.indexOf(iStorageIndex) 
				* fAxisSpacing + renderStyle.getXSpacing();
		
			ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
			alPoints.add(new Vec3f(fXValue, renderStyle.getBottomSpacing(), 0));
			alPoints.add(new Vec3f(fXValue, renderStyle.getBottomSpacing() + 
					renderStyle.getAxisHeight(), 0));
			
			elemntRep = new SelectedElementRep(iUniqueId, alPoints);
		}
		return elemntRep;
		
	}
	
	protected ArrayList<Integer>  cleanSelection(ArrayList<Integer> iAlSelection, ArrayList<Integer> iAlGroup)
	{
		ArrayList<Integer> alDelete = new ArrayList<Integer>(1);
		for (int iCount = 0; iCount < iAlSelection.size(); iCount++)
		{
			// TODO remove elements if -1
			if(iAlSelection.get(iCount) == -1)
			{
				alDelete.add(iCount);
				continue;		
			}
			iAlSelection.set(iCount, iAlSelection.get(iCount) / 1000);	
//			System.out.println("Storageindexalex: " + iAlSelection[iCount]);
		}		
		
		for(int iCount = alDelete.size()-1; iCount >= 0; iCount--)
		{
			iAlSelection.remove(iCount);
			iAlGroup.remove(iCount);
		}
		
		return iAlSelection;
	}
	
	protected void setSelection(ArrayList<Integer> iAlSelection, 
			ArrayList<Integer> iAlGroup,
			ArrayList<Integer> iAlOptional)
	{	
		alSetSelection.get(0).mergeSelection(iAlSelection, iAlGroup, iAlOptional);
		
		initSelections();
		initPolyLineLists();
	}
	
	protected ArrayList<Integer> convertAccessionToExpressionIndices(ArrayList<Integer> iAlSelection)
	{
		ArrayList<Integer> iAlSelectionStorageIndices = new ArrayList<Integer>();
		for(int iCount = 0; iCount < iAlSelection.size(); iCount++)
		{
			int iTmp = generalManager.getSingelton().getGenomeIdManager()
				.getIdIntFromIntByMapping(iAlSelection.get(iCount), EGenomeMappingType.ACCESSION_2_MICROARRAY_EXPRESSION);
			
			if (iTmp == -1)
				continue;
			
			iAlSelectionStorageIndices.add(iTmp);
		}
		
		return iAlSelectionStorageIndices;
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
		int iAccessionID = getAccesionIDFromStorageIndex(index);
		String sAccessionNumber = IDManager.getIdStringFromIntByMapping(iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
		if(sAccessionNumber == "")
			return "Unkonwn Gene";
		else
			return sAccessionNumber;		
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("No info available!");
		return sAlInfo;
	}
}
