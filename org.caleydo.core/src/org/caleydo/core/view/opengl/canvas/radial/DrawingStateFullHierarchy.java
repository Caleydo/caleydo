package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;
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
	private PartialDisc pdCurrentMouseOverElement;
	private ESelectionType parentIndicatorType;

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

	/**
	 * Initializes drawing strategies for all selected elements of the radial hierarchy's selection manager.
	 * 
	 * @param mapSelectedDrawingStrategies
	 *            Map is filled with key-value pairs where the key is a selected partial disc and its value is
	 *            the corresponding drawing strategy.
	 */
	private void initDrawingStrategies(HashMap<PartialDisc, APDDrawingStrategy> mapSelectedDrawingStrategies) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();
		pdCurrentMouseOverElement = null;

		iDisplayedHierarchyDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

		DrawingStrategyManager drawingStrategyManager = DrawingStrategyManager.get();

		APDDrawingStrategy dsDefault = drawingStrategyManager.getDefaultDrawingStrategy();

		SelectionManager selectionManager = radialHierarchy.getSelectionManager();
		Set<Integer> setSelection = selectionManager.getElements(ESelectionType.SELECTION);
		Set<Integer> setMouseOver = selectionManager.getElements(ESelectionType.MOUSE_OVER);

		pdCurrentRootElement.setPDDrawingStrategyChildren(dsDefault, iDisplayedHierarchyDepth);

		HashMap<PartialDisc, ESelectionType> mapSelectedElements = new HashMap<PartialDisc, ESelectionType>();
		HashMap<PartialDisc, ESelectionType> mapChildIndictatorElements =
			new HashMap<PartialDisc, ESelectionType>();

		boolean bIsNewSelection = radialHierarchy.isNewSelection();

		// Take the mouse over element from the selected elements, if any and if displayed.
		for (Integer elementID : setSelection) {
			PartialDisc pdSelected = radialHierarchy.getPartialDisc(elementID);
			if (pdSelected != null) {
				if (pdSelected.isCurrentlyDisplayed(pdCurrentRootElement, iDisplayedHierarchyDepth)) {
					pdCurrentMouseOverElement = pdSelected;
					break;
				}
			}
		}

		for (Integer elementID : setSelection) {
			PartialDisc pdSelected = radialHierarchy.getPartialDisc(elementID);
			if (pdSelected != null) {

				if (pdCurrentMouseOverElement == null && bIsNewSelection) {

					PartialDisc pdParent = pdSelected.getParent();
					if (pdParent == null) {
						pdCurrentRootElement = pdSelected;
					}
					else {
						pdCurrentRootElement = pdParent;
					}
					iDisplayedHierarchyDepth =
						Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

					radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
					radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
					navigationHistory.addNewHistoryEntry(this, pdCurrentRootElement, pdCurrentRootElement,
						iMaxDisplayedHierarchyDepth);
					mapSelectedElements.put(pdSelected, ESelectionType.SELECTION);
					pdCurrentMouseOverElement = pdSelected;
					continue;
				}

				PartialDisc pdIndicated =
					pdSelected.getFirstVisibleElementOnParentPathToRoot(pdCurrentRootElement,
						iDisplayedHierarchyDepth);

				if (pdIndicated == pdSelected) {
					mapSelectedElements.put(pdSelected, ESelectionType.SELECTION);
				}
				else if (pdIndicated == null) {
					parentIndicatorType = ESelectionType.SELECTION;
				}
				else {
					mapChildIndictatorElements.put(pdIndicated, ESelectionType.SELECTION);
				}

			}
		}

		for (Integer elementID : setMouseOver) {
			PartialDisc pdMouseOver = radialHierarchy.getPartialDisc(elementID);
			if (pdMouseOver != null) {

				if (pdCurrentMouseOverElement == null) {
					if (pdMouseOver.isCurrentlyDisplayed(pdCurrentRootElement, iDisplayedHierarchyDepth)) {
						mapSelectedElements.put(pdMouseOver, ESelectionType.MOUSE_OVER);
						pdCurrentMouseOverElement = pdMouseOver;
						continue;
					}
				}

				PartialDisc pdIndicated =
					pdMouseOver.getFirstVisibleElementOnParentPathToRoot(pdCurrentRootElement,
						iDisplayedHierarchyDepth);

				if (pdIndicated == pdMouseOver) {
					if (!mapSelectedElements.containsKey(pdMouseOver))
						mapSelectedElements.put(pdMouseOver, ESelectionType.MOUSE_OVER);
				}
				else if (pdIndicated == null) {
					if (parentIndicatorType != ESelectionType.SELECTION)
						parentIndicatorType = ESelectionType.MOUSE_OVER;
				}
				else {
					if (!mapChildIndictatorElements.containsKey(pdIndicated))
						mapChildIndictatorElements.put(pdIndicated, ESelectionType.MOUSE_OVER);
				}
			}
		}

		for (PartialDisc pdSelected : mapSelectedElements.keySet()) {
			PDDrawingStrategySelected dsCurrent =
				(PDDrawingStrategySelected) drawingStrategyManager
					.createDrawingStrategy(EPDDrawingStrategyType.SELECTED);

			if (mapSelectedElements.get(pdSelected) == ESelectionType.SELECTION) {
				dsCurrent.setBorderColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
			}
			if (mapChildIndictatorElements.containsKey(pdSelected)) {
				if (mapChildIndictatorElements.get(pdSelected) == ESelectionType.SELECTION) {
					dsCurrent.setChildIndicatorColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
				}
				else {
					dsCurrent.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
				}
				mapChildIndictatorElements.remove(pdSelected);
			}

			mapSelectedDrawingStrategies.put(pdSelected, dsCurrent);
			pdSelected.setPDDrawingStrategy(dsCurrent);
		}

		for (PartialDisc pdIndicated : mapChildIndictatorElements.keySet()) {
			APDDrawingStrategyChildIndicator dsCurrent =
				(APDDrawingStrategyChildIndicator) drawingStrategyManager
					.createDrawingStrategy(drawingStrategyManager.getDefaultDrawingStrategy()
						.getDrawingStrategyType());

			if (mapChildIndictatorElements.get(pdIndicated) == ESelectionType.SELECTION) {
				dsCurrent.setChildIndicatorColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
			}
			else {
				dsCurrent.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
			}

			pdIndicated.setPDDrawingStrategy(dsCurrent);
		}

		if (pdCurrentMouseOverElement != null) {
			APDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			pdCurrentMouseOverElement.decoratePDDrawingStrategyChildren(dsLabelDecorator, Math.min(
				RadialHierarchyRenderStyle.MAX_LABELING_DEPTH, iDisplayedHierarchyDepth));
		}

	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		HashMap<PartialDisc, APDDrawingStrategy> mapSelectedDrawingStrategies =
			new HashMap<PartialDisc, APDDrawingStrategy>();
		parentIndicatorType = ESelectionType.NORMAL;

		initDrawingStrategies(mapSelectedDrawingStrategies);

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		float fHierarchyOuterRadius =
			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

		// The selected elements have to be drawn (again using their own drawing strategy) at last for
		// correct antialiasing

		for (PartialDisc pdSelected : mapSelectedDrawingStrategies.keySet()) {
			APDDrawingStrategy dsCurrent = mapSelectedDrawingStrategies.get(pdSelected);
			dsCurrent.drawPartialDisc(gl, glu, pdSelected);
		}

		if (parentIndicatorType != ESelectionType.NORMAL) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			if (parentIndicatorType == ESelectionType.SELECTION)
				gl.glColor3fv(RadialHierarchyRenderStyle.SELECTED_COLOR, 0);
			else
				gl.glColor3fv(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR, 0);
			GLPrimitives.renderCircle(glu, fDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

		radialHierarchy.setNewSelection(false);
	}

	@Override
	public void handleSelection(PartialDisc pdSelected) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (pdSelected != pdRealRootElement && pdSelected.hasChildren()) {
			
			pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
				.getDefaultDrawingStrategy(), iDisplayedHierarchyDepth);
			
			if (pdSelected == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				drawingController.setDrawingState(EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT);
			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				drawingController.setDrawingState(EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT);
			}
			radialHierarchy.setDisplayListDirty();
		}
		else {
			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected, pdCurrentRootElement);
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver) {

		if (pdMouseOver != pdCurrentMouseOverElement) {
			radialHierarchy.setNewSelection(ESelectionType.MOUSE_OVER, pdMouseOver, radialHierarchy
				.getCurrentRootElement());
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void handleAlternativeSelection(PartialDisc pdSelected) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (pdSelected != pdCurrentRootElement && pdSelected.hasChildren()
			&& pdSelected.getCurrentDepth() > 1) {

			pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
				.getDefaultDrawingStrategy(), iDisplayedHierarchyDepth);

			radialHierarchy.setCurrentSelectedElement(pdSelected);
			drawingController.setDrawingState(EDrawingStateType.ANIMATION_POP_OUT_DETAIL_OUTSIDE);
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
