package org.caleydo.core.view.opengl.canvas.heatmap;

import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ccontainer.EDataKind;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.HeatMapRenderStyle;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.view.opengl.canvas.AGLCanvasStorageBasedView;
import org.caleydo.core.view.opengl.canvas.parcoords.EInputDataType;
import org.caleydo.core.view.opengl.canvas.parcoords.ESelectionType;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;
import org.caleydo.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.caleydo.core.view.opengl.util.selection.GenericSelectionManager;

/**
 * Rendering the HeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */
public class GLCanvasHeatMap 
extends AGLCanvasStorageBasedView
{
	private HeatMapRenderStyle renderStyle;
	
	private ColorMapping colorMapper;
	
	private EInputDataType eFieldDataType = EInputDataType.GENE;
	
	private boolean bRenderHorizontally = true;
	
	private Vec4f vecRotation = new Vec4f(-90, 0, 0, 1);   
	private Vec3f vecTranslation;
	
	private float fAnimationDefaultTranslation = 0;
	private float fAnimationTranslation = 0;
	
	private boolean bIsTranslationAnimationActive = false;
	private float fAnimationTargetTranslation = 0;
	
	private SelectedElementRep elementRep;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iViewId
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLCanvasHeatMap(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);		

		
		ArrayList<EViewInternalSelectionType> alSelectionTypes = new ArrayList<EViewInternalSelectionType>();
		alSelectionTypes.add(EViewInternalSelectionType.NORMAL);
		alSelectionTypes.add(EViewInternalSelectionType.MOUSE_OVER);
		alSelectionTypes.add(EViewInternalSelectionType.SELECTION);
		horizontalSelectionManager = new GenericSelectionManager(alSelectionTypes, EViewInternalSelectionType.NORMAL);
		verticalSelectionManager = new GenericSelectionManager(alSelectionTypes, EViewInternalSelectionType.NORMAL);
	
		
		colorMapper = new ColorMapping(0, 1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(GL gl) 
	{
		bRenderStorageHorizontally = true;
		initData();
		initLists();
		renderStyle = new HeatMapRenderStyle(
				viewFrustum, verticalSelectionManager, 
				alContentSelection, alStorageSelection.size(), true);
	
		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */
	public void initLocal(GL gl) 
	{
		eWhichContentSelection = ESelectionType.COMPLETE_SELECTION;
		bRenderHorizontally = true;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener, org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas) 
	{
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;
		
		eWhichContentSelection = ESelectionType.EXTERNAL_SELECTION;
		bRenderHorizontally = true;
		
		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, renderStyle);
		
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		
		iGLDisplayListIndexRemote = gl.glGenLists(1);	
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);	
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(GL gl) 
	{
		if(bIsTranslationAnimationActive)
		{	
			doTranslation();
		}
		
		pickingManager.handlePicking(iUniqueId, gl, true);
	
		if(bIsDisplayListDirtyLocal)
		{
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;			
		}	
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		
		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(GL gl) 
	{		
		if(bIsTranslationAnimationActive)
		{	
			bIsDisplayListDirtyRemote = true;
			doTranslation();
		}
		
		if(bIsDisplayListDirtyRemote)
		{
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}	
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		
		display(gl);
		checkForHits(gl);
//		pickingTriggerMouseAdapter.resetEvents();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(GL gl) 
	{
//		gl.glCallList(iGLDisplayListToCall);
		buildDisplayList(gl, iGLDisplayListIndexRemote);
	}
	
	private void buildDisplayList(final GL gl, int iGLDisplayListIndex)
	{
//		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);	
			
		gl.glClear( GL.GL_STENCIL_BUFFER_BIT);
		gl.glColorMask(false,false,false,false);
        gl.glClearStencil(0);  // Clear The Stencil Buffer To 0
        gl.glEnable(GL.GL_DEPTH_TEST);  // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);  // The Type Of Depth Testing To Do
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);						
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);												
		gl.glDisable(GL.GL_DEPTH_TEST);						
		
		// Clip region that renders in stencil buffer (in this case the frustum)
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glColorMask(true,true,true,true);
		gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		
		bRenderHorizontally = false;
		if(!bRenderHorizontally)
		{		
			gl.glTranslatef(vecTranslation.x(), 
					vecTranslation.y(), 
					vecTranslation.z());
			gl.glRotatef(vecRotation.x(),
					vecRotation.y(),
					vecRotation.z(),
					vecRotation.w());	
		}
		
		gl.glTranslatef(fAnimationTranslation, 0.0f, 0.0f);
		
		renderHeatMap(gl);
		renderSelection(gl, EViewInternalSelectionType.MOUSE_OVER);
		renderSelection(gl, EViewInternalSelectionType.SELECTION);	
		
		gl.glTranslatef(-fAnimationTranslation, 0.0f, 0.0f);

		if(!bRenderHorizontally)
		{			
			gl.glRotatef(-vecRotation.x(),
					vecRotation.y(),
					vecRotation.z(),
					vecRotation.w());
			gl.glTranslatef(-vecTranslation.x(),
					-vecTranslation.y(),
					-vecTranslation.z());
		}
		
		gl.glDisable(GL.GL_STENCIL_TEST);
		
//		gl.glEndList();
	}
	
	public void renderHorizontally(boolean bRenderHorizontally)
	{
		this.bRenderHorizontally = bRenderHorizontally;
	}
	
	protected void initLists()
	{		
		Set<Integer> setMouseOver = verticalSelectionManager.getElements(EViewInternalSelectionType.MOUSE_OVER);
		horizontalSelectionManager.resetSelectionManager();
		verticalSelectionManager.resetSelectionManager();			

		alContentSelection = mapSelections.get(eWhichContentSelection);
		alStorageSelection = mapSelections.get(eWhichStorageSelection);		
		if(renderStyle != null)
		{
			renderStyle.setContentSelection(alContentSelection);
		}
				
		int iNumberOfRowsToRender = alStorageSelection.size();
		int	iNumberOfColumns =  alContentSelection.size();				
			

		for (int iRowCount = 0; iRowCount < iNumberOfRowsToRender; iRowCount++)
		{				
			horizontalSelectionManager.initialAdd(alStorageSelection.get(iRowCount));				
		}
		
		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++)
		{			
			verticalSelectionManager.initialAdd(alContentSelection.get(iColumnCount));		
			if(setMouseOver.contains(alContentSelection.get(iColumnCount)))
			{
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.MOUSE_OVER, 
						alContentSelection.get(iColumnCount));
			}
		}
	}	

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Type: Heat Map");
		alInfo.add(alContentSelection.size() + " gene expression values");
		return alInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType, org.caleydo.core.manager.view.EPickingMode, int, org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) 
	{
		if (remoteRenderingGLCanvas != null)
		{
			// Check if selection occurs in the pool or memo layer of the remote rendered view (i.e. bucket, jukebox)
			if (remoteRenderingGLCanvas.getHierarchyLayerByGLCanvasListenerId(
					iUniqueId).getCapacity() > 5)
			{
				return;
			}
		}
		
		ArrayList<Integer> iAlOldSelection;
		switch (pickingType)
		{
		case HEAT_MAP_FIELD_SELECTION:
			switch (pickingMode)
			{
			case CLICKED:
				extSelectionManager.clear();
				iAlOldSelection = 
					prepareSelection(verticalSelectionManager, EViewInternalSelectionType.SELECTION);					
				
				verticalSelectionManager.clearSelection(
						EViewInternalSelectionType.SELECTION);
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.SELECTION, iExternalID);
				
				if (eFieldDataType == EInputDataType.GENE)
				{
					propagateGeneSelection(iExternalID, 2, iAlOldSelection);
					
				}
				
				break;				
				
			case MOUSE_OVER:
				extSelectionManager.clear();
				iAlOldSelection = prepareSelection(
						verticalSelectionManager, EViewInternalSelectionType.SELECTION);					
				
				verticalSelectionManager.clearSelection(
						EViewInternalSelectionType.MOUSE_OVER);
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.MOUSE_OVER, iExternalID);
				
				if(eFieldDataType == EInputDataType.GENE)
				{
					propagateGeneSelection(iExternalID, 1, iAlOldSelection);
					//generalManager.getSingelton().getViewGLCanvasManager().getInfoAreaManager()
					//.setData(iUniqueId, getAccesionIDFromStorageIndex(iExternalID), EInputDataType.GENE, getInfo());					
				}
				break;
			}	
			
			bIsDisplayListDirtyLocal = true;
			bIsDisplayListDirtyRemote = true;
			
			pickingManager.flushHits(iUniqueId, pickingType);
			break;
		}
	}	
	
	private void renderHeatMap(final GL gl)
	{	
		float fXPosition = 0;
		float fYPosition = 0;
		renderStyle.clearFieldWidths();
		
		// TODO: NullPointer if storage is empty
		Vec2f vecFieldWidthAndHeight = null;

		String sContent = "";  
//		for(Integer iStorageIndex : alStorageSelection)
//		{		
//			sContent = "Experiment " +iStorageIndex; // FIXME: from where should we get a proper name?
//			
//			// Render heat map experiment name
//			gl.glRotatef(45, 0, 0, 1);
//			textRenderer.setColor(0, 0, 0, 1);
//			textRenderer.begin3DRendering();
//			textRenderer.draw3D(sContent,						
//					 fYPosition,
//					-fXPosition,
//					0.01f,
//					renderStyle.getHeadingFontScalingFactor());
//			textRenderer.end3DRendering();
//			gl.glRotatef(-45, 0, 0, 1);
//		}
		
		int iCount = 0;
		for(Integer iContentIndex: alContentSelection)
		{	
			vecFieldWidthAndHeight = renderStyle.getAndInitFieldWidthAndHeight(iCount);
			fYPosition = renderStyle.getYCenter() - vecFieldWidthAndHeight.y() * alStorageSelection.size() / 2;
			
			for(Integer iStorageIndex : alStorageSelection)
			{				
				renderElement(gl, iStorageIndex, iContentIndex, 
						fXPosition, fYPosition, vecFieldWidthAndHeight);
				
				fYPosition += vecFieldWidthAndHeight.y();				
			}
			
			float fFontScaling = 0;
			if(vecFieldWidthAndHeight.x() > 0.1f)
			{			
				if(vecFieldWidthAndHeight.x() < 0.2f)
				{
					fFontScaling = renderStyle.getSmallFontScalingFactor();
				}
				else
				{
					fFontScaling = renderStyle.getHeadingFontScalingFactor();
				}
				
				// Render heat map element name
				gl.glRotatef(90, 0, 0, 1);
				sContent = getRefSeqFromStorageIndex(iContentIndex);
				textRenderer.setColor(0, 0, 0, 1);
				textRenderer.begin3DRendering();
				textRenderer.draw3D(sContent,						
						 (fYPosition + vecFieldWidthAndHeight.y() / 3),
						- (fXPosition + vecFieldWidthAndHeight.x() / 2),// - renderStyle.getXCenter(), 
						0.01f,
						fFontScaling);
				textRenderer.end3DRendering();
				gl.glRotatef(-90, 0, 0, 1);	
			}
			iCount++;
			fXPosition += vecFieldWidthAndHeight.x();	
		}			
	}
	
	private void renderElement(final GL gl, 
			final int iStorageIndex, 
			final int iContentIndex, 
			final float fXPosition,
			final float fYPosition,
			final Vec2f vecFieldWidthAndHeight)
	{
		float fLookupValue = alDataStorages.get(iStorageIndex).getFloat(EDataKind.NORMALIZED, iContentIndex);
		Vec3f vecMappingColor = colorMapper.colorMappingLookup(fLookupValue);
		gl.glColor3f(vecMappingColor.x(), vecMappingColor.y(), vecMappingColor.z());
		
		gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.HEAT_MAP_FIELD_SELECTION, iContentIndex));
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXPosition, fYPosition, HeatMapRenderStyle.FIELD_Z);
		gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), fYPosition, 
				HeatMapRenderStyle.FIELD_Z);
		gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), 
				fYPosition + vecFieldWidthAndHeight.y(), 
				HeatMapRenderStyle.FIELD_Z);
		gl.glVertex3f(fXPosition, 
				fYPosition + vecFieldWidthAndHeight.y(), 
				HeatMapRenderStyle.FIELD_Z);
		gl.glEnd();

		gl.glPopName();
	}
	
	private void renderSelection(final GL gl,
			EViewInternalSelectionType eSelectionType)
	{
		Set<Integer> selectedSet = verticalSelectionManager.getElements(eSelectionType);
		float fHeight = 0;
		float fXPosition = 0;
		float fYPosition = 0;
		
		switch(eSelectionType)
		{
		case SELECTION:
			gl.glColor4fv(HeatMapRenderStyle.SELECTED_COLOR, 0);
			gl.glLineWidth(HeatMapRenderStyle.SELECTED_LINE_WIDTH);
			break;
		case MOUSE_OVER:
			gl.glColor4fv(HeatMapRenderStyle.MOUSE_OVER_COLOR, 0);
			gl.glLineWidth(HeatMapRenderStyle.MOUSE_OVER_LINE_WIDTH);
			break;
		}
		
		for(Integer iCurrentColumn : selectedSet)
		{				
			int iColumnIndex = alContentSelection.indexOf(iCurrentColumn);
			if(iColumnIndex == -1)
				continue;
			Vec2f vecFieldWidthAndHeight = renderStyle.getFieldWidthAndHeight(iColumnIndex);

			fHeight = alStorageSelection.size() * vecFieldWidthAndHeight.y();
			fXPosition = renderStyle.getXDistanceAt(iColumnIndex);
			fYPosition = renderStyle.getYCenter() - vecFieldWidthAndHeight.y() * alStorageSelection.size() / 2;
				

			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(fXPosition, fYPosition, HeatMapRenderStyle.SELECTION_Z);
			gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), fYPosition, 
					HeatMapRenderStyle.SELECTION_Z);
			gl.glVertex3f(fXPosition + vecFieldWidthAndHeight.x(), 
					fYPosition + fHeight, 
					HeatMapRenderStyle.SELECTION_Z);
			gl.glVertex3f(fXPosition, 
					fYPosition + fHeight, 
					HeatMapRenderStyle.SELECTION_Z);
			gl.glEnd();
			
			fHeight = 0;
			fXPosition = 0;			
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasStorageBasedView#createElementRep(int)
	 */
	protected SelectedElementRep createElementRep(int iStorageIndex) 
	{
		int iContentIndex = alContentSelection.indexOf(iStorageIndex);
		renderStyle.clearFieldWidths();
		Vec2f vecFieldWithAndHeight = null;
		
		for(int iCount = 0; iCount <= iContentIndex; iCount++)
		{
			vecFieldWithAndHeight = renderStyle.getAndInitFieldWidthAndHeight(iCount);
		}
		
		
		// not good if vec is null
		float fXValue = renderStyle.getXDistanceAt(iContentIndex) + vecFieldWithAndHeight.x() / 2;// + renderStyle.getXSpacing();
		
		float fYValue = renderStyle.getYCenter() + vecFieldWithAndHeight.y() * alStorageSelection.size() / 2;
			
		if(bRenderHorizontally)
		{
			elementRep = new SelectedElementRep(iUniqueId, fXValue + fAnimationTranslation, fYValue, 0);

		}
		else
		{
			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float)Math.PI/2);
			Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(iUniqueId, vecPoint.x(), vecPoint.y() - fAnimationTranslation, 0);

		}			
		return elementRep;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasStorageBasedView#rePosition(int)
	 */
	protected void rePosition(int iElementID) {

		ArrayList<Integer> alSelection;
		if(bRenderStorageHorizontally)
		{
			alSelection = alContentSelection;
		
		}	
		else
		{
			alSelection = alStorageSelection;
			// TODO test this
		}
		
		float fCurrentPosition = alSelection.indexOf(iElementID) * renderStyle.getNormalFieldWidth();// + renderStyle.getXSpacing();
		
		float fFrustumLength = viewFrustum.getRight() - viewFrustum.getLeft();
		float fLength = (alSelection.size() - 1) * renderStyle.getNormalFieldWidth() + 1.5f; // MARC: 1.5 = correction of lens effect in heatmap

		fAnimationTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);
		
		if(-fAnimationTargetTranslation > fLength - fFrustumLength)
			fAnimationTargetTranslation = -(fLength - fFrustumLength + 2 * 0.00f);
		else if(fAnimationTargetTranslation > 0)
			fAnimationTargetTranslation = 0;
		else if(-fAnimationTargetTranslation < -fAnimationTranslation + fFrustumLength  / 2  - 0.00f && 
				-fAnimationTargetTranslation > -fAnimationTranslation - fFrustumLength / 2 + 0.00f)
		{
			fAnimationTargetTranslation = fAnimationTranslation;
			return;
		}
		
		bIsTranslationAnimationActive = true;
	}
	
	private void doTranslation()
	{
		float fDelta = 0;
		if(fAnimationTargetTranslation < fAnimationTranslation - 0.5f)
		{
			
			fDelta = -0.5f;
		
		}
		else if (fAnimationTargetTranslation > fAnimationTranslation + 0.5f)
		{
			fDelta = 0.5f;
		}
		else
		{
			fDelta = fAnimationTargetTranslation - fAnimationTranslation;
			bIsTranslationAnimationActive = false;
		}
		
		
		if(elementRep != null)
		{
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for(Vec3f currentPoint : alPoints)
			{
				currentPoint.setY(currentPoint.y() - fDelta);
			}			
		}
		
		fAnimationTranslation += fDelta;	
	}
}
