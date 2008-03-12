package org.geneview.core.view.opengl.canvas.heatmap;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

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
import org.geneview.core.view.opengl.canvas.parcoords.ESelectionType;
import org.geneview.core.view.opengl.util.GLSharedObjects;
import org.geneview.core.view.opengl.util.GLToolboxRenderer;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;
import org.geneview.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.geneview.core.view.opengl.util.selection.GenericSelectionManager;

/**
 * 
 * @author Alexander Lex
 *
 */

public class GLCanvasHeatMap 
extends AGLCanvasStorageBasedView

{
	
	private HeatMapRenderStyle renderStyle;
	
	ColorMapping colorMapper;
	
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
		initData();
		initLists();
		renderStyle = new HeatMapRenderStyle(viewFrustum, verticalSelectionManager, alContentSelection);
	}

	@Override
	public void initLocal(GL gl) 
	{
		eWhichContentSelection = ESelectionType.COMPLETE_SELECTION;
		bRenderStorageHorizontally = true;
		init(gl);
	}

	@Override
	public void initRemote(GL gl, int iRemoteViewID,
			JukeboxHierarchyLayer layer,
			PickingJoglMouseListener pickingTriggerMouseAdapter) 
	{
		eWhichContentSelection = ESelectionType.EXTERNAL_SELECTION;
		bRenderStorageHorizontally = true;
		
		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, renderStyle);
		
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
		//pickingManager.handlePicking(iUniqueId, gl, true);
		
		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
		

		
		
	}
	
	@Override
	public void display(GL gl) 
	{
		//GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		//GLSharedObjects.drawAxis(gl);
		renderHeatMap(gl);
		renderSelection(gl, EViewInternalSelectionType.MOUSE_OVER);
		renderSelection(gl, EViewInternalSelectionType.SELECTION);
		
		
	}

	@Override
	public ArrayList<String> getInfo() {

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Heat Map");
		return alInfo;
	}

	@Override
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) 
	{
		switch (pickingType)
		{
		case FIELD_SELECTION:
			switch (pickingMode)
			{
			case MOUSE_OVER:
				verticalSelectionManager.clearSelection(
						EViewInternalSelectionType.MOUSE_OVER);
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.MOUSE_OVER, externalID);
				
				break;
			case CLICKED:
				verticalSelectionManager.clearSelection(
						EViewInternalSelectionType.SELECTION);
				verticalSelectionManager.addToType(
						EViewInternalSelectionType.SELECTION, externalID);
				//pickingManager.flushHits(iUniqueId, pickingType);
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
		if(bRenderStorageHorizontally)
		{
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
				iCount++;
				fXPosition += vecFieldWidthAndHeight.x();					
			}			
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
			//int iColumnIndex = 0;
			// TODO
			int iColumnIndex = alContentSelection.indexOf(iCurrentColumn);
			Vec2f vecFieldWidthAndHeight = renderStyle.getFieldWidthAndHeight(iColumnIndex);
// TODO: render vertically
			//			if(bRenderStorageHorizontally)
//			{
				
				fHeight = alStorageSelection.size() * vecFieldWidthAndHeight.y();
				fXPosition = renderStyle.getXDistanceAt(iColumnIndex);
				fYPosition = renderStyle.getYCenter() - vecFieldWidthAndHeight.y() * alStorageSelection.size() / 2;
				
//			}
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
		
		if(!bRenderStorageHorizontally)
		{
//			ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
//			float fYValue;
//			float fXValue;
//			int iCount = 0;
//			for(Integer iCurrent : alStorageSelection)
//			{
//				fYValue = alDataStorages.get(iCurrent).getArrayFloat()[iStorageIndex];
//				fYValue = fYValue * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
//				fXValue = iCount * fAxisSpacing + renderStyle.getXSpacing();
//				alPoints.add(new Vec3f(fXValue, fYValue, 0));
//				iCount++;
//			}		
//		
//			elementRep = new SelectedElementRep(iUniqueId, alPoints);
			elementRep = new SelectedElementRep(iUniqueId, 0, 0, 0);
		}
		else
		{	
			int iContentIndex = alContentSelection.indexOf(iStorageIndex);
			Vec2f vecFieldWithAndHeight = renderStyle.getFieldWidthAndHeight(iContentIndex);
			float fXValue = renderStyle.getXDistanceAt(iContentIndex) + vecFieldWithAndHeight.x() / 2;// + renderStyle.getXSpacing();
		
			float fYValue = renderStyle.getYCenter() + vecFieldWithAndHeight.y() * alStorageSelection.size() / 2;
			
			elementRep = new SelectedElementRep(iUniqueId, fXValue, fYValue, 0);
//			ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
//			alPoints.add(new Vec3f(fXValue, renderStyle.getBottomSpacing(), 0));
//			alPoints.add(new Vec3f(fXValue, renderStyle.getBottomSpacing() + 
//					renderStyle.getAxisHeight(), 0));
			
			//elementRep = new SelectedElementRep(iUniqueId, alPoints);
		}
		return elementRep;
	}

}
