/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.radial;

import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.clusterer.EDrawingStateType;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.util.GLPrimitives;

/**
 * In this drawing state the radial hierarchy is drawn at the center of the
 * screen. Other drawing states can be reached by selecting and alternatively
 * selecting partial discs.
 * 
 * @author Christian Partl
 */

public class DrawingStateFullHierarchy extends ADrawingState {

	private int iDisplayedHierarchyDepth;
	private PartialDisc pdCurrentMouseOverElement;
	private SelectionType parentIndicatorType;

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
	public DrawingStateFullHierarchy(DrawingController drawingController,
			GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
	}

	/**
	 * Initializes drawing strategies for all selected elements of the radial
	 * hierarchy's selection manager.
	 * 
	 * @param mapSelectedDrawingStrategies
	 *            Map is filled with key-value pairs where the key is a selected
	 *            partial disc and its value is the corresponding drawing
	 *            strategy.
	 */
	private void initDrawingStrategies(
			HashMap<PartialDisc, PDDrawingStrategySelected> mapSelectedDrawingStrategies) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();
		pdCurrentMouseOverElement = null;

		iDisplayedHierarchyDepth = Math.min(iMaxDisplayedHierarchyDepth,
				pdCurrentRootElement.getDepth());

		DrawingStrategyManager drawingStrategyManager = radialHierarchy
				.getDrawingStrategyManager();

		APDDrawingStrategy dsDefault = drawingStrategyManager.getDefaultDrawingStrategy();

		SelectionManager selectionManager = radialHierarchy.getSelectionManager();
		Set<Integer> setSelection = selectionManager.getElements(SelectionType.SELECTION);
		Set<Integer> setMouseOver = selectionManager
				.getElements(SelectionType.MOUSE_OVER);

		pdCurrentRootElement.setPDDrawingStrategyChildren(dsDefault,
				iDisplayedHierarchyDepth);

		HashMap<PartialDisc, SelectionType> mapSelectedElements = new HashMap<PartialDisc, SelectionType>();
		HashMap<PartialDisc, SelectionType> mapChildIndictatorElements = new HashMap<PartialDisc, SelectionType>();

		boolean bIsNewSelection = radialHierarchy.isNewSelection();

		// Take the mouse over element from the selected elements, if any and if
		// displayed.
		for (Integer elementID : setSelection) {
			PartialDisc pdSelected = radialHierarchy.getPartialDisc(elementID);
			if (pdSelected != null) {
				if (pdSelected.isCurrentlyDisplayed(pdCurrentRootElement,
						iDisplayedHierarchyDepth)) {
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
					} else {
						pdCurrentRootElement = pdParent;
					}
					iDisplayedHierarchyDepth = Math.min(iMaxDisplayedHierarchyDepth,
							pdCurrentRootElement.getDepth());

					radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
					radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
					navigationHistory.addNewHistoryEntry(this, pdCurrentRootElement,
							pdCurrentRootElement, iMaxDisplayedHierarchyDepth);
					mapSelectedElements.put(pdSelected, SelectionType.SELECTION);
					pdCurrentMouseOverElement = pdSelected;
					continue;
				}

				PartialDisc pdIndicated = pdSelected
						.getFirstVisibleElementOnParentPathToRoot(pdCurrentRootElement,
								iDisplayedHierarchyDepth);

				if (pdIndicated == pdSelected) {
					mapSelectedElements.put(pdSelected, SelectionType.SELECTION);
				} else if (pdIndicated == null) {
					parentIndicatorType = SelectionType.SELECTION;
				} else {
					mapChildIndictatorElements.put(pdIndicated, SelectionType.SELECTION);
				}

			}
		}

		for (Integer elementID : setMouseOver) {
			PartialDisc pdMouseOver = radialHierarchy.getPartialDisc(elementID);
			if (pdMouseOver != null) {

				if (pdCurrentMouseOverElement == null) {
					if (pdMouseOver.isCurrentlyDisplayed(pdCurrentRootElement,
							iDisplayedHierarchyDepth)) {
						mapSelectedElements.put(pdMouseOver, SelectionType.MOUSE_OVER);
						pdCurrentMouseOverElement = pdMouseOver;
						continue;
					}
				}

				PartialDisc pdIndicated = pdMouseOver
						.getFirstVisibleElementOnParentPathToRoot(pdCurrentRootElement,
								iDisplayedHierarchyDepth);

				if (pdIndicated == pdMouseOver) {
					if (!mapSelectedElements.containsKey(pdMouseOver))
						mapSelectedElements.put(pdMouseOver, SelectionType.MOUSE_OVER);
				} else if (pdIndicated == null) {
					if (parentIndicatorType != SelectionType.SELECTION)
						parentIndicatorType = SelectionType.MOUSE_OVER;
				} else {
					if (!mapChildIndictatorElements.containsKey(pdIndicated))
						mapChildIndictatorElements.put(pdIndicated,
								SelectionType.MOUSE_OVER);
				}
			}
		}

		for (PartialDisc pdSelected : mapSelectedElements.keySet()) {
			PDDrawingStrategySelected dsCurrent = (PDDrawingStrategySelected) drawingStrategyManager
					.createDrawingStrategy(EPDDrawingStrategyType.SELECTED);

			if (mapSelectedElements.get(pdSelected) == SelectionType.SELECTION) {
				dsCurrent.setBorderColor(SelectionType.SELECTION.getColor());
			}
			if (mapChildIndictatorElements.containsKey(pdSelected)) {
				if (mapChildIndictatorElements.get(pdSelected) == SelectionType.SELECTION) {
					dsCurrent.setChildIndicatorColor(SelectionType.SELECTION.getColor());
				} else {
					dsCurrent.setChildIndicatorColor(SelectionType.MOUSE_OVER.getColor());
				}
				mapChildIndictatorElements.remove(pdSelected);
			}

			mapSelectedDrawingStrategies.put(pdSelected, dsCurrent);
			pdSelected.setPDDrawingStrategy(dsCurrent);
		}

		for (PartialDisc pdIndicated : mapChildIndictatorElements.keySet()) {
			APDDrawingStrategyChildIndicator dsCurrent = (APDDrawingStrategyChildIndicator) drawingStrategyManager
					.createDrawingStrategy(drawingStrategyManager
							.getDefaultDrawingStrategy().getDrawingStrategyType());

			if (mapChildIndictatorElements.get(pdIndicated) == SelectionType.SELECTION) {
				dsCurrent.setChildIndicatorColor(SelectionType.SELECTION.getColor());
			} else {
				dsCurrent.setChildIndicatorColor(SelectionType.MOUSE_OVER.getColor());
			}

			pdIndicated.setPDDrawingStrategy(dsCurrent);
		}

		if (pdCurrentMouseOverElement != null) {
			APDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator(
					radialHierarchy.getDataDomain().getColorMapper());
			pdCurrentMouseOverElement.decoratePDDrawingStrategyChildren(dsLabelDecorator,
					Math.min(RadialHierarchyRenderStyle.MAX_LABELING_DEPTH,
							iDisplayedHierarchyDepth));
		}

	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu) {

		HashMap<PartialDisc, PDDrawingStrategySelected> mapSelectedDrawingStrategies = new HashMap<PartialDisc, PDDrawingStrategySelected>();
		parentIndicatorType = SelectionType.NORMAL;

		initDrawingStrategies(mapSelectedDrawingStrategies);

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		float fHierarchyOuterRadius = Math.min(fXCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth,
				iDisplayedHierarchyDepth);

		// The selected elements have to be drawn (again using their own drawing
		// strategy) at last for
		// correct antialiasing

		for (PartialDisc pdSelected : mapSelectedDrawingStrategies.keySet()) {
			PDDrawingStrategySelected dsCurrent = mapSelectedDrawingStrategies
					.get(pdSelected);
			dsCurrent.drawPartialDisc(gl, glu, pdSelected);
		}

		if (parentIndicatorType != SelectionType.NORMAL) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			if (parentIndicatorType == SelectionType.SELECTION)
				gl.glColor3fv(SelectionType.SELECTION.getColor(), 0);
			else
				gl.glColor3fv(SelectionType.MOUSE_OVER.getColor(), 0);
			GLPrimitives.renderCircle(glu, fDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f,
				fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

		radialHierarchy.setNewSelection(false);
	}

	@Override
	public void handleSelection(PartialDisc pdSelected) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (pdSelected != pdRealRootElement && pdSelected.hasChildren()) {

			pdCurrentRootElement.setPDDrawingStrategyChildren(radialHierarchy
					.getDrawingStrategyManager().getDefaultDrawingStrategy(),
					iDisplayedHierarchyDepth);

			if (pdSelected == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				drawingController
						.setDrawingState(EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT);
			} else {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				drawingController
						.setDrawingState(EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT);
			}
			radialHierarchy.setDisplayListDirty();
		} else {
			radialHierarchy.setNewSelection(SelectionType.SELECTION, pdSelected);
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver) {

		if (pdMouseOver != pdCurrentMouseOverElement) {
			radialHierarchy.setNewSelection(SelectionType.MOUSE_OVER, pdMouseOver);
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void handleAlternativeSelection(PartialDisc pdSelected) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (pdSelected != pdCurrentRootElement && pdSelected.hasChildren()
				&& pdSelected.getCurrentDepth() > 1) {

			pdCurrentRootElement.setPDDrawingStrategyChildren(radialHierarchy
					.getDrawingStrategyManager().getDefaultDrawingStrategy(),
					iDisplayedHierarchyDepth);

			radialHierarchy.setCurrentSelectedElement(pdSelected);
			drawingController
					.setDrawingState(EDrawingStateType.ANIMATION_POP_OUT_DETAIL_OUTSIDE);
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
