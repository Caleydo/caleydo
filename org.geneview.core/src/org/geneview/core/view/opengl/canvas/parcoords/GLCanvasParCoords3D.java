package org.geneview.core.view.opengl.canvas.parcoords;


import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;

import javax.media.opengl.GL;

import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.renderstyle.ParCoordsRenderStyle;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasStorageBasedView;
import org.geneview.core.view.opengl.util.EIconTextures;
import org.geneview.core.view.opengl.util.GLCoordinateUtils;
import org.geneview.core.view.opengl.util.GLIconTextureManager;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;
import org.geneview.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.geneview.core.view.opengl.util.selection.GenericSelectionManager;

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
extends AGLCanvasStorageBasedView
{
	
	private float fAxisSpacing = 0;
		
	private int iGLDisplayListIndexLocal;
	private int iGLDisplayListIndexRemote;
	private int iGLDisplayListToCall = 0;
		


	// flag whether to take measures against occlusion or not
	private boolean bPreventOcclusion = true;	
	// flag whether one array should be a polyline or an axis
	//protected boolean bRenderHorizontally = false;
	
	// Specify the current input data type for the axis and polylines
	// Is used for meta information, such as captions
	private EInputDataType eAxisDataType = EInputDataType.EXPERIMENT;
	private EInputDataType ePolylineDataType = EInputDataType.GENE;
		
	private boolean bIsDraggingActive = false;
	private EPickingType draggedObject;
		
	private int iNumberOfAxis = 0;
	
	private float[] fArGateTipHeight;
	private float[] fArGateBottomHeight;
	private int iDraggedGateNumber = 0;
	
	private float fXDefaultTranslation = 0;
	private float fXTranslation = 0;
	private float fYTranslation = 0;
	
	private float fXTargetTranslation = 0;
	private boolean bIsTranslationActive = false;
	
	private ParCoordsRenderStyle renderStyle;	
	
	private boolean bRenderInfoArea = false;
	private boolean bInfoAreaFirstTime = false;
	
	private DecimalFormat decimalFormat;
	
	
	SelectedElementRep elementRep;
	
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
		ArrayList<EViewInternalSelectionType> alSelectionType = new ArrayList<EViewInternalSelectionType>();
		for(EViewInternalSelectionType selectionType : EViewInternalSelectionType.values())
		{
			alSelectionType.add(selectionType);
		}		
		horizontalSelectionManager = new GenericSelectionManager(
				alSelectionType, EViewInternalSelectionType.NORMAL);	
		
		// initialize axis selection manager
		alSelectionType = new ArrayList<EViewInternalSelectionType>();
		for(EViewInternalSelectionType selectionType : EViewInternalSelectionType.values())
		{
			alSelectionType.add(selectionType);
		}
		verticalSelectionManager = new GenericSelectionManager(
				alSelectionType, EViewInternalSelectionType.NORMAL);		
		
		decimalFormat = new DecimalFormat("#####.##");
		
		mapSelections = new EnumMap<ESelectionType, ArrayList<Integer>>(ESelectionType.class);	
		
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
		initData();
		
		fXDefaultTranslation = renderStyle.getXSpacing();
		fYTranslation = renderStyle.getBottomSpacing();
	
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) 
	{	
		if(bIsTranslationActive)
		{	
			doTranslation();
		}
		
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
		if(bIsTranslationActive)
		{	
			doTranslation();
		}
		
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
//		GLSharedObjects.drawAxis(gl);
//		GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		// FIXME: translation here not nice, operations are not in display lists
		if(bIsDraggingActive)
		{			
			gl.glTranslatef(fXDefaultTranslation + fXTranslation, fYTranslation, 0.0f);
			//gl.glScalef(fScaling, fScaling, 1.0f);
			handleDragging(gl);
	
			//gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
			gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);			
		}
//		if(bRenderInfoArea)
//		{
//			gl.glTranslatef(fXDefaultTranslation + fXTranslation, fYTranslation, 0.0f);
//			//gl.glScalef(fScaling, fScaling, 1.0f);
//			
////			infoAreaManager.renderInfoArea(gl, bInfoAreaFirstTime);
//			bInfoAreaFirstTime = false;
//		
//			//gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
//			gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);
//		}

		gl.glCallList(iGLDisplayListToCall);
		
		gl.glTranslatef(fXDefaultTranslation - renderStyle.getXSpacing(), 
				fYTranslation - renderStyle.getBottomSpacing(), 0.0f);
		glToolboxRenderer.render(gl);
		gl.glTranslatef(-fXDefaultTranslation + renderStyle.getXSpacing(),
				-fYTranslation + renderStyle.getBottomSpacing(), 0.0f);
	}
		
	/**
	 * Choose whether to render one array as a polyline and every entry across arrays is an axis 
	 * or whether the array corresponds to an axis and every entry across arrays is a polyline
	 *  
	 * @param bRenderArrayAsPolyline if true array contents make up a polyline, else array is an axis
	 */
	public void renderStorageAsPolyline(boolean bRenderArrayAsPolyline)
	{
		this.bRenderStorageHorizontally = bRenderArrayAsPolyline;
		bRenderInfoArea = false;
		EInputDataType eTempType = eAxisDataType;
		eAxisDataType = ePolylineDataType;
		ePolylineDataType = eTempType;
		fXTranslation = 0;
		extSelectionManager.clear();
		initLists();
		
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
		horizontalSelectionManager.clearSelections();
		verticalSelectionManager.clearSelections();
		
		bRenderInfoArea = false;
	}
	
	/**
	 * Build everything new but the data base
	 */
	public void refresh()
	{
		initLists();
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		bRenderInfoArea = false;
	}
	
	
	/**
	 * Initializes the array lists that contain the data. 
	 * Must be run at program start, 
	 * every time you exchange axis and polylines and
	 * every time you change storages or selections	 * 
	 */
	protected void initLists()
	{						
		horizontalSelectionManager.resetSelectionManager();
		
		int iNumberOfEntriesToRender = 0;		

		alContentSelection = mapSelections.get(eWhichContentSelection);
		alStorageSelection = mapSelections.get(eWhichStorageSelection);
		iNumberOfEntriesToRender = alContentSelection.size();
	
		int iNumberOfPolyLinesToRender = 0;		
		
		// if true one array corresponds to one polyline, number of arrays is number of polylines
		if (bRenderStorageHorizontally)
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
		

			
		// this for loop executes once per polyline
		for (int iPolyLineCount = 0; iPolyLineCount < iNumberOfPolyLinesToRender; iPolyLineCount++)
		{	
			if(bRenderStorageHorizontally)
				horizontalSelectionManager.initialAdd(alStorageSelection.get(iPolyLineCount));
			else
				horizontalSelectionManager.initialAdd(alContentSelection.get(iPolyLineCount));
		}
		
		// this for loop executes one per axis
		for (int iAxisCount = 0; iAxisCount < iNumberOfAxis; iAxisCount++)
		{
			if(bRenderStorageHorizontally)
				verticalSelectionManager.initialAdd(alContentSelection.get(iAxisCount));
			else
				verticalSelectionManager.initialAdd(alStorageSelection.get(iAxisCount));
		}		
		fAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);
		
		initGates();
	}
	
	private void initGates()
	{
		fArGateTipHeight = new float[iNumberOfAxis];
		fArGateBottomHeight = new float[iNumberOfAxis];
		
		for(int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = renderStyle.getGateYOffset() - 
				renderStyle.getGateTipHeight();
		}
	}
	
	private void buildPolyLineDisplayList(final GL gl, int iGLDisplayListIndex)
	{		
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);	

//		if(bIsDraggingActive)
//			handleDragging(gl);
		gl.glTranslatef(fXDefaultTranslation  + fXTranslation, fYTranslation, 0.0f);
		//gl.glScalef(fScaling, fScaling, 1.0f);
		
		renderCoordinateSystem(gl, iNumberOfAxis);	
		
		renderPolylines(gl, EViewInternalSelectionType.DESELECTED);
		renderPolylines(gl, EViewInternalSelectionType.NORMAL);
		renderPolylines(gl, EViewInternalSelectionType.MOUSE_OVER);
		renderPolylines(gl, EViewInternalSelectionType.SELECTION);		
		
		renderGates(gl, iNumberOfAxis);				
		
		//gl.glScalef(1/fScaling, 1/fScaling, 1.0f);
		gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);		
		gl.glEndList();
	}
	
	private void renderPolylines(GL gl, EViewInternalSelectionType renderMode)
	{				
		
		Set<Integer> setDataToRender = null;
		float fZDepth = 0.0f;
		
		switch (renderMode)
		{
			case NORMAL:
				setDataToRender = horizontalSelectionManager.getElements(renderMode);
				if(bPreventOcclusion)				
					gl.glColor4fv(renderStyle.
							getPolylineOcclusionPrevColor(setDataToRender.size()), 0);									
				else
					gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR, 0);
				
				gl.glLineWidth(ParCoordsRenderStyle.POLYLINE_LINE_WIDTH);
				break;
			case SELECTION:	
				setDataToRender = horizontalSelectionManager.getElements(renderMode);
				gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				setDataToRender = horizontalSelectionManager.getElements(renderMode);
				gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH);
				break;
			case DESELECTED:	
				setDataToRender = horizontalSelectionManager.getElements(renderMode);				
				gl.glColor4fv(renderStyle.
						getPolylineDeselectedOcclusionPrevColor(setDataToRender.size()),
						0);
				gl.glLineWidth(ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH);
				break;
			default:
				setDataToRender = horizontalSelectionManager.getElements(
						EViewInternalSelectionType.NORMAL);
		}
		
		Iterator<Integer> dataIterator = setDataToRender.iterator();
		// this for loop executes once per polyline
		while(dataIterator.hasNext())
		{
			int iPolyLineID = dataIterator.next();
			if(renderMode != EViewInternalSelectionType.DESELECTED)
				gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.POLYLINE_SELECTION, iPolyLineID));
			
			IStorage currentStorage = null;
			
			// decide on which storage to use when array is polyline
			if(bRenderStorageHorizontally)
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
				if (bRenderStorageHorizontally)
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
				
				if(renderMode == EViewInternalSelectionType.SELECTION 
						|| renderMode == EViewInternalSelectionType.MOUSE_OVER)
				{
					renderYValues(gl, fCurrentXValue, fCurrentYValue * renderStyle.getAxisHeight(), renderMode);					
				}				
				
				fPreviousXValue = fCurrentXValue;
				fPreviousYValue = fCurrentYValue;
			}						
			
			if(renderMode != EViewInternalSelectionType.DESELECTED)
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
		Set<Integer> selectedSet = verticalSelectionManager.getElements(EViewInternalSelectionType.SELECTION);
		Set<Integer> mouseOverSet = verticalSelectionManager.getElements(EViewInternalSelectionType.MOUSE_OVER);
		ArrayList<Integer> alAxisSelection;
		
		if(bRenderStorageHorizontally)
			alAxisSelection = alContentSelection;			
		else
			alAxisSelection = alStorageSelection;
			
		
		int iCount = 0;
		while (iCount < iNumberAxis)
		{
			if(selectedSet.contains(alAxisSelection.get(iCount)))	
			{
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_SELECTED_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH);
			}
			else if (mouseOverSet.contains(alAxisSelection.get(iCount)))
			{
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_LINE_WIDTH);
			}
			else
			{
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
			}
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
			gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
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
			gl.glPopName();
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
		
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
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
			
			renderYValues(gl, fCurrentPosition, fArGateTipHeight[iCount], EViewInternalSelectionType.NORMAL);
			
			
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
			
			renderYValues(gl, fCurrentPosition, fArGateBottomHeight[iCount], EViewInternalSelectionType.NORMAL);			
			
			iCount++;
		}
	}
	
	private void renderYValues(GL gl, float fXOrigin, float fYOrigin, EViewInternalSelectionType renderMode)
	{
		// don't render values that are below the y axis
		if(fYOrigin < 0)
			return;
		
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
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
		for (int iPolylineCount = 0; iPolylineCount < horizontalSelectionManager.getNumberOfElements(); iPolylineCount++)
		{	
			int iStorageIndex = 0;
			
			// get the index if array as polyline
			if (bRenderStorageHorizontally)
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
				if(horizontalSelectionManager.checkStatus(EViewInternalSelectionType.SELECTION, iPolylineCount))
					bRenderInfoArea = false;
				
				if(bRenderStorageHorizontally)
					horizontalSelectionManager.addToType(EViewInternalSelectionType.DESELECTED, 
							alStorageSelection.get(iPolylineCount));
				else
					horizontalSelectionManager.addToType(EViewInternalSelectionType.DESELECTED, 
							alContentSelection.get(iPolylineCount));
			}
			else
			{
				boolean bIsBlocked = false;
				
				// every axis
				for (int iLocalAxisCount = 0; iLocalAxisCount < iNumberOfAxis; iLocalAxisCount++)
				{					
					int iLocalStorageIndex = 0;
					if(bRenderStorageHorizontally)
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
					if(bRenderStorageHorizontally)
						horizontalSelectionManager.removeFromType(EViewInternalSelectionType.DESELECTED,
								alStorageSelection.get(iPolylineCount));
					else
						horizontalSelectionManager.removeFromType(EViewInternalSelectionType.DESELECTED,
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

		if (glToolboxRenderer.getContainingLayer() != null)
			if (glToolboxRenderer.getContainingLayer().getCapacity() >= 10)
				return;
		
		switch (ePickingType)
		{
		case POLYLINE_SELECTION:		
			switch (ePickingMode)
			{				
			
				case CLICKED:	
					
					ArrayList<Integer> iAlOldSelection = 
						prepareSelection(horizontalSelectionManager, EViewInternalSelectionType.SELECTION);					
									
					horizontalSelectionManager.clearSelection(EViewInternalSelectionType.SELECTION);							
					horizontalSelectionManager.addToType(EViewInternalSelectionType.SELECTION, 
							iExternalID);
					
					if (ePolylineDataType == EInputDataType.GENE)
					{
						propagateGeneSelection(iExternalID, iAlOldSelection);
						
					}
					bIsDisplayListDirtyLocal = true;
					bIsDisplayListDirtyRemote = true;
					break;	
				case MOUSE_OVER:
					if (ePolylineDataType == EInputDataType.GENE)
					{
						//propagateGeneSelection(iExternalID, iAlOldSelection);
						generalManager.getSingelton().getViewGLCanvasManager().getInfoAreaManager()
							.setData(iUniqueId, getAccesionIDFromStorageIndex(iExternalID), EInputDataType.GENE, getInfo());
					
					}
						
					horizontalSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);
					horizontalSelectionManager.addToType(EViewInternalSelectionType.MOUSE_OVER, iExternalID);
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
				ArrayList<Integer> iAlOldSelection = 
					prepareSelection(verticalSelectionManager, EViewInternalSelectionType.SELECTION);					
			
				
				verticalSelectionManager.clearSelection(
						EViewInternalSelectionType.SELECTION);
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.SELECTION, iExternalID);
				
				if(eAxisDataType == EInputDataType.GENE)
				{
					propagateGeneSelection(iExternalID, iAlOldSelection);
				}		
				
				rePosition(iExternalID);
				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;
				break;
			case MOUSE_OVER:
				verticalSelectionManager.clearSelection(
						EViewInternalSelectionType.MOUSE_OVER);
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.MOUSE_OVER, iExternalID);
				
				if(eAxisDataType == EInputDataType.GENE)
				{
					generalManager.getSingelton().getViewGLCanvasManager().getInfoAreaManager()
						.setData(iUniqueId, getAccesionIDFromStorageIndex(iExternalID), EInputDataType.GENE, getInfo());
				}
				
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
						if (bRenderStorageHorizontally == true)
							renderStorageAsPolyline(false);
						else
							renderStorageAsPolyline(true);
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
					if(bRenderStorageHorizontally)
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
					if(bRenderStorageHorizontally)							
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
					if(bRenderStorageHorizontally)							
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
					if(bRenderStorageHorizontally)							
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
	

	
	
	protected SelectedElementRep createElementRep(int iStorageIndex)
	{	
		
		if(!bRenderStorageHorizontally)
		{
			ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
			float fYValue;
			float fXValue;
			int iCount = 0;
			for(Integer iCurrent : alStorageSelection)
			{
				fYValue = alDataStorages.get(iCurrent).getArrayFloat()[iStorageIndex];
				fYValue = fYValue * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
				fXValue = iCount * fAxisSpacing + renderStyle.getXSpacing() + fXTranslation;
				alPoints.add(new Vec3f(fXValue, fYValue, 0));
				iCount++;
			}		
		
			elementRep = new SelectedElementRep(iUniqueId, alPoints);
		
		}
		else
		{			
			float fXValue = alContentSelection.indexOf(iStorageIndex) 
				* fAxisSpacing + renderStyle.getXSpacing() + fXTranslation;
		
			ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
			alPoints.add(new Vec3f(fXValue, renderStyle.getBottomSpacing(), 0));
			alPoints.add(new Vec3f(fXValue, renderStyle.getBottomSpacing() + 
					renderStyle.getAxisHeight(), 0));
			
			elementRep = new SelectedElementRep(iUniqueId, alPoints);
		}
		return elementRep;
		
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("Type: Parallel Coordinates");
		if(!bRenderStorageHorizontally)
		{
			sAlInfo.add("Showing genes as " + alContentSelection.size() +" polylines and experiments as " + iNumberOfAxis + " axis.");			
		}
		else
		{
			sAlInfo.add("Showing experiments as " + alStorageSelection.size() +" polylines and genes as " + iNumberOfAxis + " axis.");			
		}
		return sAlInfo;
	}
	
	protected void rePosition(int iElementID)
	{
		ArrayList<Integer> alSelection;
		if(bRenderStorageHorizontally)
		{
			alSelection = alContentSelection;
		
		}	
		else
		{
			alSelection = alStorageSelection;
			// TODO implement this
		}
		
		float fCurrentPosition = alSelection.indexOf(iElementID) * fAxisSpacing + renderStyle.getXSpacing();
		
		float fFrustumLength = viewFrustum.getRight() - viewFrustum.getLeft();
		float fLength = (alSelection.size() - 1) * fAxisSpacing;

		fXTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);
		
		if(-fXTargetTranslation > fLength - fFrustumLength)
			fXTargetTranslation = -(fLength - fFrustumLength + 2 * renderStyle.getXSpacing());
		else if(fXTargetTranslation > 0)
			fXTargetTranslation = 0;
		else if(-fXTargetTranslation < -fXTranslation + fFrustumLength  / 2  - renderStyle.getXSpacing() && 
				-fXTargetTranslation > -fXTranslation - fFrustumLength / 2 + renderStyle.getXSpacing())
		{
			fXTargetTranslation = fXTranslation;
			return;
		}
		
		bIsTranslationActive = true;
	}
	
	private void doTranslation()
	{
		float fDelta = 0;
		if(fXTargetTranslation < fXTranslation - 0.3)
		{
			
			fDelta = -0.3f;
		
		}
		else if (fXTargetTranslation > fXTranslation + 0.3)
		{
			fDelta = 0.3f;
		}
		else
		{
			fDelta = fXTargetTranslation - fXTranslation;
			bIsTranslationActive = false;
		}
		
		
		if(elementRep != null)
		{
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for(Vec3f currentPoint : alPoints)
			{
				currentPoint.setX(currentPoint.x() + fDelta);
			}			
		}
		
		fXTranslation += fDelta;
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote  = true;
	}
}
