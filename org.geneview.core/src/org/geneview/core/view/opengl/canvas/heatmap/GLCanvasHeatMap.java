package org.geneview.core.view.opengl.canvas.heatmap;

import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.renderstyle.HeatMapRenderStyle;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.util.mapping.color.ColorMapping;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasStorageBasedView;
import org.geneview.core.view.opengl.canvas.parcoords.EInputDataType;
import org.geneview.core.view.opengl.canvas.parcoords.ESelectionType;
import org.geneview.core.view.opengl.util.GLToolboxRenderer;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;
import org.geneview.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.geneview.core.view.opengl.util.selection.GenericSelectionManager;

/**
 * Rendering the HeatMap
 * 
 * @author Alexander Lex
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
	
	@Override
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

	@Override
	public void initLocal(GL gl) 
	{
		eWhichContentSelection = ESelectionType.COMPLETE_SELECTION;
		bRenderHorizontally = true;
		init(gl);
	}

	@Override
	public void initRemote(GL gl, int iRemoteViewID,
			JukeboxHierarchyLayer layer,
			PickingJoglMouseListener pickingTriggerMouseAdapter) 
	{
		eWhichContentSelection = ESelectionType.EXTERNAL_SELECTION;
		bRenderHorizontally = true;
		
		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, renderStyle);
		
		containedHierarchyLayer = layer;
		
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		
		init(gl);
	}

	@Override
	public void displayLocal(GL gl) 
	{
		
		pickingManager.handlePicking(iUniqueId, gl, true);
	
		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void displayRemote(GL gl) 
	{		
		display(gl);
		checkForHits(gl);
//		pickingTriggerMouseAdapter.resetEvents();		
	}
	
	@Override
	public void display(GL gl) 
	{
		//GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		//GLSharedObjects.drawAxis(gl);
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
		renderHeatMap(gl);
		renderSelection(gl, EViewInternalSelectionType.MOUSE_OVER);
		renderSelection(gl, EViewInternalSelectionType.SELECTION);
		
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
	

	@Override
	public ArrayList<String> getInfo() {

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Heat Map");
		alInfo.add("Showing expression values of " + alContentSelection.size() + " genes");
		return alInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.geneview.core.manager.view.EPickingType, org.geneview.core.manager.view.EPickingMode, int, org.geneview.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) 
	{
//		// Check if selection occurs in the pool or memo layer of the bucket
//		if (containedHierarchyLayer != null 
//				&& containedHierarchyLayer.getCapacity() >= 5)
//		{
//			return;
//		}
		
		ArrayList<Integer> iAlOldSelection;
		switch (pickingType)
		{
		case FIELD_SELECTION:
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
				iAlOldSelection = 
					prepareSelection(verticalSelectionManager, EViewInternalSelectionType.SELECTION);					
				
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
				
				
				
				gl.glRotatef(90, 0, 0, 1);
				String sContent = getAccessionNumberFromStorageIndex(iContentIndex);
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
		float fLookupValue = alDataStorages.get(iStorageIndex).getArrayFloat()[iContentIndex];
		Vec3f vecMappingColor = colorMapper.colorMappingLookup(fLookupValue);
		gl.glColor3f(vecMappingColor.x(), vecMappingColor.y(), vecMappingColor.z());
		
		
		
		gl.glPushName(pickingManager.getPickingID(iUniqueId, EPickingType.FIELD_SELECTION, iContentIndex));
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

	@Override
	protected SelectedElementRep createElementRep(int iStorageIndex) 
	{
		
		SelectedElementRep elementRep;		

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
			elementRep = new SelectedElementRep(iUniqueId, fXValue, fYValue, 0);

		}
		else
		{
			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float)Math.PI/2);
			Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(iUniqueId, vecPoint.x(), vecPoint.y(), 0);

		}			
		return elementRep;
	}

	@Override
	protected void rePosition(int elementID) {

		// TODO Auto-generated method stub
		
	}

}
