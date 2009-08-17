package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionManager;

/**
 * In this drawing state the radial hierarchy is drawn at the center of the screen. Other drawing states can
 * be reached by selecting and alternatively selecting partial discs.
 * 
 * @author Christian Partl
 */

public class DrawingStateFullHierarchy
	extends ADrawingState {

	private int iDisplayedHierarchyDepth;

	/**
	 * Constructor.
	 * 
	 * @param drawingController
	 *            DrawingController that holds the drawing states.
	 * @param radialHierarchy
	 *            GLRadialHierarchy instance that is used.
	 * @param navigationHistory
	 *            NavigationHistory instance that shall be used.
	 */
	public DrawingStateFullHierarchy(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {
		
		
		SelectionManager selectionManager = radialHierarchy.getSelectionManager();
		Set<Integer> setSelection = selectionManager.getElements(ESelectionType.SELECTION);
		Set<Integer> setMouseOver = selectionManager.getElements(ESelectionType.MOUSE_OVER);

		
		
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		iDisplayedHierarchyDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

		boolean bIsMouseOverElementDisplayed = true;
		boolean bIsMouseOverElementParentOfCurrentRoot = false;
		boolean bIsNewSelection = radialHierarchy.isNewSelection();

		DrawingStrategyManager drawingStrategyManager = DrawingStrategyManager.get();
		APDDrawingStrategy dsDefault = drawingStrategyManager.getDefaultDrawingStrategy();

		pdCurrentRootElement.setPDDrawingStrategyChildren(dsDefault, iDisplayedHierarchyDepth);

		
		
		if (pdCurrentMouseOverElement != null) {

			if (pdCurrentMouseOverElement != pdCurrentRootElement) {

				if (bIsNewSelection) {
					int iParentPathLength =
						pdCurrentMouseOverElement.getParentPathLength(pdCurrentRootElement);
					if ((iParentPathLength >= iDisplayedHierarchyDepth) || (iParentPathLength == -1)) {
						PartialDisc pdParent = pdCurrentMouseOverElement.getParent();
						if (pdParent == null) {
							pdCurrentRootElement = pdCurrentMouseOverElement;
						}
						else {
							pdCurrentRootElement = pdParent;
						}
						radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
						radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);

						iDisplayedHierarchyDepth =
							Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

						navigationHistory.addNewHistoryEntry(this, pdCurrentRootElement,
							pdCurrentRootElement, iMaxDisplayedHierarchyDepth);
					}
				}
				else {
					ArrayList<PartialDisc> alParentPath =
						pdCurrentMouseOverElement.getParentPath(pdCurrentRootElement);
					if (alParentPath != null) {
						if (alParentPath.size() >= iDisplayedHierarchyDepth) {
							DrawingStrategyManager drawingStategyManager = DrawingStrategyManager.get();
							APDDrawingStrategyChildIndicator dsDefaultHighlightedChildIndicator =
								(APDDrawingStrategyChildIndicator) drawingStategyManager
									.createDrawingStrategy(dsDefault.getDrawingStrategyType());

							dsDefaultHighlightedChildIndicator
								.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
							alParentPath.get(alParentPath.size() - iDisplayedHierarchyDepth)
								.setPDDrawingStrategy(dsDefaultHighlightedChildIndicator);
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
				APDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
				dsLabelDecorator.setDrawingStrategy(dsDefault);
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, Math.min(
					RadialHierarchyRenderStyle.MAX_LABELING_DEPTH, iDisplayedHierarchyDepth));
			}
		}

		float fHierarchyOuterRadius =
			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

		// The mouse over element has to be drawn (again in using different drawing strategy) at last for
		// correct antialiasing
		if (bIsMouseOverElementDisplayed) {
			PDDrawingStrategySelected dsSelected =
				(PDDrawingStrategySelected) DrawingStrategyManager.get().createDrawingStrategy(
					EPDDrawingStrategyType.SELECTED);
			if (bIsNewSelection) {
				dsSelected.setBorderColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
			}
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		if (bIsMouseOverElementParentOfCurrentRoot) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor3fv(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR, 0);
			GLPrimitives.renderCircle(glu, fDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

	}

	@Override
	public void handleSelection(PartialDisc pdSelected, boolean broadcastSelection) {

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
				drawingController.setDrawingState(EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT);
//				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getParent()
//					.getElementID(), true, false, broadcastSelection);
			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				drawingController.setDrawingState(EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT);
//				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected.getElementID(), true,
//					false, broadcastSelection);
			}
			radialHierarchy.setDisplayListDirty();
		}
		else {
			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected, pdCurrentRootElement);
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver, boolean broadcastSelection) {

		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdMouseOver != pdCurrentMouseOverElement) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), Math.min(RadialHierarchyRenderStyle.MAX_LABELING_DEPTH,
					iDisplayedHierarchyDepth));
			}

			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);
			radialHierarchy.setNewSelection(ESelectionType.MOUSE_OVER, pdMouseOver, radialHierarchy.getCurrentRootElement());
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void handleAlternativeSelection(PartialDisc pdSelected, boolean broadcastSelection) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdSelected != pdCurrentRootElement && pdSelected.hasChildren()
			&& pdSelected.getCurrentDepth() > 1) {

			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy());
			}

			radialHierarchy.setCurrentSelectedElement(pdSelected);
			radialHierarchy.setCurrentMouseOverElement(pdSelected);
			drawingController.setDrawingState(EDrawingStateType.ANIMATION_POP_OUT_DETAIL_OUTSIDE);
//			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected.getElementID(), false, true,
//				broadcastSelection);
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public PartialDisc getSelectedElement() {
		return radialHierarchy.getCurrentRootElement();
	}

	@Override
	public EDrawingStateType getType() {
		return EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY;
	}

}
