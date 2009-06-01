package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.ESelectionType;

public class DrawingStateFullHierarchy
	extends DrawingState {

	private int iDisplayedHierarchyDepth;

	public DrawingStateFullHierarchy(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		iDisplayedHierarchyDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

		pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedHierarchyDepth);

		boolean bIsMouseOverElementDisplayed = true;
		boolean bIsMouseOverElementParentOfCurrentRoot = false;
		boolean bIsNewSelection = radialHierarchy.isNewSelection();

		if (pdCurrentMouseOverElement != null) {

			if (pdCurrentMouseOverElement != pdCurrentRootElement) {

				if (bIsNewSelection) {
					int iParentPathLength =
						pdCurrentMouseOverElement.getParentPathLength(pdCurrentRootElement);
					if((iParentPathLength >= iDisplayedHierarchyDepth) || (iParentPathLength == -1)) {
						PartialDisc pdParent = pdCurrentMouseOverElement.getParent();
						if(pdParent == null) {
							pdCurrentRootElement = pdCurrentMouseOverElement;
						}
						else {
							pdCurrentRootElement = pdParent;
						}
						radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
						radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
						
						iDisplayedHierarchyDepth =
							Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

						navigationHistory.addNewHistoryEntry(this, pdCurrentRootElement, pdCurrentRootElement, iMaxDisplayedHierarchyDepth);
					}
				}
				else {
					ArrayList<PartialDisc> alParentPath =
						pdCurrentMouseOverElement.getParentPath(pdCurrentRootElement);
					if (alParentPath != null) {
						if (alParentPath.size() >= iDisplayedHierarchyDepth) {
							DrawingStrategyManager drawingStategyManager = DrawingStrategyManager.get();
							PDDrawingStrategyChildIndicator dsDefault =
								(PDDrawingStrategyChildIndicator) drawingStategyManager
									.createDrawingStrategy(drawingStategyManager.getDefaultStrategyType());

							dsDefault.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
							alParentPath.get(alParentPath.size() - iDisplayedHierarchyDepth)
								.setPDDrawingStrategy(dsDefault);
							bIsMouseOverElementDisplayed = false;
						}
					}
					else {
						bIsMouseOverElementParentOfCurrentRoot = true;
						bIsMouseOverElementDisplayed = false;
					}
				}
			}

			if (bIsMouseOverElementDisplayed) {
				PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
				dsLabelDecorator.setDrawingStrategy(DrawingStrategyManager.get().getDefaultDrawingStrategy());
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, Math.min(
					RadialHierarchyRenderStyle.MAX_LABELING_DEPTH, iDisplayedHierarchyDepth));
			}
		}
		
		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

		// The mouse over element has to be drawn (again in using different drawing strategy) at last for
		// correct antialiasing
		if (bIsMouseOverElementDisplayed) {
			PDDrawingStrategySelected dsSelected =
				(PDDrawingStrategySelected) DrawingStrategyManager.get().createDrawingStrategy(
					DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
			if (bIsNewSelection) {
				dsSelected.setBorderColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
			}
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		if (bIsMouseOverElementParentOfCurrentRoot) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor3fv(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR, 0);
			GLPrimitives.renderCircle(gl, glu, fDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

	}

	@Override
	public void handleSelection(PartialDisc pdSelected) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdSelected != pdRealRootElement && pdSelected.hasChildren()) {

			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), Math.min(RadialHierarchyRenderStyle.MAX_LABELING_DEPTH,
					iDisplayedHierarchyDepth));
			}
			if (pdSelected == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_PARENT_ROOT_ELEMENT);
				
				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getParent().getElementID());
			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT);
				
				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected.getElementID());
			}
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver) {

		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdMouseOver != pdCurrentMouseOverElement) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), Math.min(RadialHierarchyRenderStyle.MAX_LABELING_DEPTH,
					iDisplayedHierarchyDepth));
			}

			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);
			radialHierarchy.setDisplayListDirty();
		}
		radialHierarchy.setNewSelection(ESelectionType.MOUSE_OVER, pdMouseOver.getElementID());
	}

	@Override
	public void handleAlternativeSelection(PartialDisc pdSelected) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdSelected != pdCurrentRootElement && pdSelected.hasChildren() && pdSelected.getCurrentDepth() > 1) {

			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy());
			}

			radialHierarchy.setCurrentSelectedElement(pdSelected);
			radialHierarchy.setCurrentMouseOverElement(pdSelected);
			drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_POP_OUT_DETAIL_OUTSIDE);
			radialHierarchy.setAnimationActive(true);
			radialHierarchy.setDisplayListDirty();
			
			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected.getElementID());
		}
	}

	@Override
	public PartialDisc getSelectedElement() {
		return radialHierarchy.getCurrentRootElement();
	}

}
