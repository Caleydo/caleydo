/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.clusterer.EDrawingStateType;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.util.GLPrimitives;

/**
 * This drawing state draws the full hierarchy at the center of the screen, just
 * smaller than in {@link DrawingStateFullHierarchy}. Additionally an element
 * and its subtree (until a certain threshold) is drawn around the overview for
 * as detail view. Other drawing states can be reached by selecting and
 * alternatively selecting partial discs.
 *
 * @author Christian Partl
 */
@XmlType
public class DrawingStateDetailOutside extends ADrawingState {

	private float fDetailViewStartAngle;
	private float fDetailViewDiscWidth;
	private float fDetailViewInnerRadius;
	private float fOverviewDiscWidth;
	private int iDisplayedDetailViewDepth;
	private int iDisplayedOverviewDepth;
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
	public DrawingStateDetailOutside(DrawingController drawingController,
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
	 * @param mapChildIndicatedStrategies
	 *            Map is filled with key-value pairs where the key is a partial
	 *            disc that has a highlighted child indicator and its value is
	 *            the corresponding drawing strategy.
	 */
	private boolean initDrawingStrategies(
			HashMap<PartialDisc, APDDrawingStrategy> mapSelectedDrawingStrategies,
			HashMap<PartialDisc, APDDrawingStrategyChildIndicator> mapChildIndicatedStrategies) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		pdCurrentMouseOverElement = null;

		DrawingStrategyManager drawingStrategyManager = radialHierarchy
				.getDrawingStrategyManager();

		APDDrawingStrategy dsDefault = drawingStrategyManager.getDefaultDrawingStrategy();

		SelectionManager selectionManager = radialHierarchy.getSelectionManager();
		Set<Integer> setSelection = selectionManager.getElements(SelectionType.SELECTION);
		Set<Integer> setMouseOver = selectionManager
				.getElements(SelectionType.MOUSE_OVER);

		pdCurrentRootElement.setPDDrawingStrategyChildren(dsDefault,
				iDisplayedOverviewDepth);

		HashMap<PartialDisc, SelectionType> mapSelectedElements = new HashMap<PartialDisc, SelectionType>();
		HashMap<PartialDisc, SelectionType> mapChildIndictatorElements = new HashMap<PartialDisc, SelectionType>();

		boolean bIsNewSelection = radialHierarchy.isNewSelection();

		// Take the mouse over element from the selected elements, if any and if
		// displayed.
		for (Integer elementID : setSelection) {
			PartialDisc pdSelected = radialHierarchy.getPartialDisc(elementID);
			if (pdSelected != null) {
				if (pdSelected.isCurrentlyDisplayed(pdCurrentRootElement,
						iDisplayedOverviewDepth)) {
					pdCurrentMouseOverElement = pdSelected;
					break;
				}
			}
		}

		for (Integer elementID : setSelection) {
			PartialDisc pdSelected = radialHierarchy.getPartialDisc(elementID);
			if (pdSelected != null) {

				if (pdCurrentMouseOverElement == null && bIsNewSelection) {
					return false;
				}

				PartialDisc pdIndicated = pdSelected
						.getFirstVisibleElementOnParentPathToRoot(pdCurrentRootElement,
								iDisplayedOverviewDepth);

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
							iDisplayedOverviewDepth)) {
						mapSelectedElements.put(pdMouseOver, SelectionType.MOUSE_OVER);
						pdCurrentMouseOverElement = pdMouseOver;
						continue;
					}
				}

				PartialDisc pdIndicated = pdMouseOver
						.getFirstVisibleElementOnParentPathToRoot(pdCurrentRootElement,
								iDisplayedOverviewDepth);

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
				dsCurrent.setBorderColor(SelectionType.SELECTION.getColor().getRGB());
			}
			if (mapChildIndictatorElements.containsKey(pdSelected)) {
				if (mapChildIndictatorElements.get(pdSelected) == SelectionType.SELECTION) {
					dsCurrent.setChildIndicatorColor(SelectionType.SELECTION.getColor().getRGB());
				} else {
					dsCurrent.setChildIndicatorColor(SelectionType.MOUSE_OVER.getColor().getRGB());
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
				dsCurrent.setChildIndicatorColor(SelectionType.SELECTION.getColor().getRGB());
			} else {
				dsCurrent.setChildIndicatorColor(SelectionType.MOUSE_OVER.getColor().getRGB());
			}

			pdIndicated.setPDDrawingStrategy(dsCurrent);
			mapChildIndicatedStrategies.put(pdIndicated, dsCurrent);
		}

		if (pdCurrentMouseOverElement != null) {
			APDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator(
					radialHierarchy.getDataDomain().getColorMapper());

			if (isPartialDiscInDetailOutside(pdCurrentMouseOverElement)) {
				pdCurrentMouseOverElement.decoratePDDrawingStrategyChildren(
						dsLabelDecorator, Math.min(
								RadialHierarchyRenderStyle.MAX_LABELING_DEPTH,
								iDisplayedOverviewDepth));
			} else {
				dsLabelDecorator.setDrawingStrategy(pdCurrentMouseOverElement
						.getDrawingStrategy());
				pdCurrentMouseOverElement.setPDDrawingStrategy(dsLabelDecorator);
			}
		}
		return true;
	}

	/**
	 * Determines whether the specified partial disc is drawn in the detail
	 * view.
	 *
	 * @param pdToTest
	 *            Partial disc of interest.
	 * @return True, if the specified partial disc is drawn in the detail view,
	 *         false otherwise.
	 */
	private boolean isPartialDiscInDetailOutside(PartialDisc pdToTest) {
		if ((pdToTest != null)
				&& (pdToTest == radialHierarchy.getCurrentSelectedElement()))
			return true;
		return pdToTest.hasParent(radialHierarchy.getCurrentSelectedElement(),
				iDisplayedDetailViewDepth - 1);
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy
				.getCurrentSelectedElement();

		calculateDrawingParameters(pdCurrentRootElement, pdCurrentSelectedElement,
				fXCenter, fYCenter);

		if (iDisplayedDetailViewDepth < RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH) {
			radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
			radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
			drawingController
					.setDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);

			navigationHistory
					.replaceCurrentHistoryEntry(
							drawingController.getCurrentDrawingState(),
							pdCurrentRootElement, pdCurrentRootElement,
							radialHierarchy.getMaxDisplayedHierarchyDepth());
			drawingController.draw(fXCenter, fYCenter, gl, glu);
			return;
		}

		HashMap<PartialDisc, APDDrawingStrategy> mapSelectedDrawingStrategies = new HashMap<PartialDisc, APDDrawingStrategy>();
		HashMap<PartialDisc, APDDrawingStrategyChildIndicator> mapChildIndicatedStrategies = new HashMap<PartialDisc, APDDrawingStrategyChildIndicator>();
		parentIndicatorType = SelectionType.NORMAL;

		boolean bContinueDrawing = initDrawingStrategies(mapSelectedDrawingStrategies,
				mapChildIndicatedStrategies);
		if (!bContinueDrawing) {

			radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
			radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
			drawingController
					.setDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);

			navigationHistory
					.addNewHistoryEntry(drawingController.getCurrentDrawingState(),
							pdCurrentRootElement, pdCurrentRootElement,
							radialHierarchy.getMaxDisplayedHierarchyDepth());

			drawingController.draw(fXCenter, fYCenter, gl, glu);
			return;
		}

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		DrawingStrategyManager drawingStrategyManager = radialHierarchy
				.getDrawingStrategyManager();
		//
		APDDrawingStrategy dsDefault = drawingStrategyManager.getDefaultDrawingStrategy();
		APDDrawingStrategyChildIndicator dsTransparent = (APDDrawingStrategyChildIndicator) drawingStrategyManager
				.createDrawingStrategy(dsDefault.getDrawingStrategyType());
		dsTransparent
				.setTransparency(RadialHierarchyRenderStyle.PARTIAL_DISC_TRANSPARENCY);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, fDetailViewDiscWidth,
				iDisplayedDetailViewDepth, fDetailViewStartAngle, 360,
				fDetailViewInnerRadius);

		// The selected elements have to be drawn (again using their own drawing
		// strategy) at last for
		// correct antialiasing

		for (PartialDisc pdSelected : mapSelectedDrawingStrategies.keySet()) {
			if (isPartialDiscInDetailOutside(pdSelected)) {
				APDDrawingStrategy dsCurrent = mapSelectedDrawingStrategies
						.get(pdSelected);
				dsCurrent.drawPartialDisc(gl, glu, pdSelected);
			}
		}

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(dsTransparent,
				iDisplayedDetailViewDepth);

		for (PartialDisc pdIndicated : mapChildIndicatedStrategies.keySet()) {
			if (isPartialDiscInDetailOutside(pdIndicated)) {
				APDDrawingStrategyChildIndicator dsCurrent = mapChildIndicatedStrategies
						.get(pdIndicated);
				dsCurrent
						.setTransparency(RadialHierarchyRenderStyle.PARTIAL_DISC_TRANSPARENCY);
				pdIndicated.setPDDrawingStrategy(dsCurrent);
			}
		}

		// if ((pdHighlightedChildIndicator != null)
		// && (pdHighlightedChildIndicator.hasParent(pdCurrentSelectedElement,
		// iDisplayedDetailViewDepth))) {
		// APDDrawingStrategyChildIndicator
		// dsTransparentHighlightedChildIndicator =
		// (APDDrawingStrategyChildIndicator)
		// DrawingStrategyManager.get().createDrawingStrategy(
		// dsDefault.getDrawingStrategyType());
		// dsTransparentHighlightedChildIndicator
		// .setTransparency(RadialHierarchyRenderStyle.PARTIAL_DISC_TRANSPARENCY);
		// dsTransparentHighlightedChildIndicator
		// .setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
		// pdHighlightedChildIndicator.setPDDrawingStrategy(dsTransparentHighlightedChildIndicator);
		// }

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fOverviewDiscWidth,
				iDisplayedOverviewDepth);

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(0, 1, 1);
		GLPrimitives.renderPartialDiscBorder(gl, glu,
				pdCurrentSelectedElement.getCurrentInnerRadius(),
				pdCurrentSelectedElement.getCurrentInnerRadius() + fOverviewDiscWidth
						* iDisplayedDetailViewDepth,
				pdCurrentSelectedElement.getCurrentStartAngle(),
				pdCurrentSelectedElement.getCurrentAngle(), 100, 2);
		gl.glPopAttrib();

		// The mouse over element has to be drawn (again in using different
		// drawing strategy) at last for
		// correct antialiasing
		for (PartialDisc pdSelected : mapSelectedDrawingStrategies.keySet()) {
			APDDrawingStrategy dsCurrent = mapSelectedDrawingStrategies.get(pdSelected);
			dsCurrent.drawPartialDisc(gl, glu, pdSelected);
		}

		if (parentIndicatorType != SelectionType.NORMAL) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			if (parentIndicatorType == SelectionType.SELECTION)
				gl.glColor3fv(SelectionType.SELECTION.getColor().getRGB(), 0);
			else
				gl.glColor3fv(SelectionType.MOUSE_OVER.getColor().getRGB(), 0);
			GLPrimitives.renderCircle(glu, fOverviewDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fOverviewDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f,
				fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

		pdCurrentSelectedElement.setCurrentStartAngle(fDetailViewStartAngle);
	}

	/**
	 * Calculates several parameters which are necessary for drawing. I.e.
	 * screen space for detail and overview are calculated as well as widths of
	 * partial discs etc.
	 *
	 * @param pdCurrentRootElement
	 *            Current root element of the overview.
	 * @param pdCurrentSelectedElement
	 *            Current selected element, i.e. the root element of the detail
	 *            view.
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 */
	private void calculateDrawingParameters(PartialDisc pdCurrentRootElement,
			PartialDisc pdCurrentSelectedElement, float fXCenter, float fYCenter) {

		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();

		float fDetailViewScreenPercentage;
		int iSelectedElementDepth = pdCurrentSelectedElement.getDepth();
		int iDepthToRoot = pdCurrentSelectedElement
				.getParentPathLength(pdCurrentRootElement);

		iDisplayedDetailViewDepth = Math.min(iMaxDisplayedHierarchyDepth - iDepthToRoot,
				iSelectedElementDepth);

		if (iMaxDisplayedHierarchyDepth <= RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH + 1) {
			fDetailViewScreenPercentage = RadialHierarchyRenderStyle.MIN_DETAIL_SCREEN_PERCENTAGE;
		} else {
			float fPercentageStep = (RadialHierarchyRenderStyle.MAX_DETAIL_SCREEN_PERCENTAGE - RadialHierarchyRenderStyle.MIN_DETAIL_SCREEN_PERCENTAGE)
					/ ((iMaxDisplayedHierarchyDepth
							- RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH - 1));

			fDetailViewScreenPercentage = RadialHierarchyRenderStyle.MIN_DETAIL_SCREEN_PERCENTAGE
					+ (iDisplayedDetailViewDepth - RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH)
					* fPercentageStep;
		}

		fDetailViewDiscWidth = Math.min(fXCenter * fDetailViewScreenPercentage, fYCenter
				* fDetailViewScreenPercentage)
				/ iDisplayedDetailViewDepth;

		float fOverviewScreenPercentage = 1.0f - (fDetailViewScreenPercentage
				+ (1.0f - RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE) + RadialHierarchyRenderStyle.DETAIL_RADIUS_DELTA_SCREEN_PERCENTAGE);
		iDisplayedOverviewDepth = Math.min(iMaxDisplayedHierarchyDepth,
				pdCurrentRootElement.getDepth());

		float fTotalOverviewWidth = Math.min(fXCenter * fOverviewScreenPercentage,
				fYCenter * fOverviewScreenPercentage);
		fOverviewDiscWidth = fTotalOverviewWidth / iDisplayedOverviewDepth;

		fDetailViewInnerRadius = fTotalOverviewWidth
				+ Math.min(
						fXCenter
								* RadialHierarchyRenderStyle.DETAIL_RADIUS_DELTA_SCREEN_PERCENTAGE,
						fYCenter
								* RadialHierarchyRenderStyle.DETAIL_RADIUS_DELTA_SCREEN_PERCENTAGE);

		// if (bInitialDraw) {
		fDetailViewStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		// }

	}

	@Override
	public void handleSelection(PartialDisc pdSelected) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy
				.getCurrentSelectedElement();

		if (pdSelected != pdRealRootElement && pdSelected.hasChildren()) {

			pdCurrentRootElement.setPDDrawingStrategyChildren(radialHierarchy
					.getDrawingStrategyManager().getDefaultDrawingStrategy(),
					iDisplayedOverviewDepth);

			if (pdSelected == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				drawingController
						.setDrawingState(EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT);
			} else {
				pdCurrentSelectedElement.setPDDrawingStrategyChildren(radialHierarchy
						.getDrawingStrategyManager().getDefaultDrawingStrategy(),
						iDisplayedDetailViewDepth);
				pdCurrentSelectedElement.simulateDrawHierarchyAngular(
						fDetailViewDiscWidth, iDisplayedDetailViewDepth,
						fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				radialHierarchy.setCurrentSelectedElement(pdSelected);
				radialHierarchy.setCurrentRootElement(pdSelected);
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
		PartialDisc pdCurrentSelectedElement = radialHierarchy
				.getCurrentSelectedElement();

		if (pdSelected.hasChildren() && pdSelected.getCurrentDepth() > 1) {

			pdCurrentRootElement.setPDDrawingStrategyChildren(radialHierarchy
					.getDrawingStrategyManager().getDefaultDrawingStrategy(),
					iDisplayedOverviewDepth);

			if (pdSelected == pdCurrentSelectedElement
					|| pdSelected == pdCurrentRootElement) {

				pdCurrentSelectedElement.simulateDrawHierarchyAngular(
						fDetailViewDiscWidth, iDisplayedDetailViewDepth,
						fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				ADrawingState dsNext = drawingController
						.getDrawingState(EDrawingStateType.ANIMATION_PULL_IN_DETAIL_OUTSIDE);
				drawingController.setDrawingState(dsNext);
			} else {
				radialHierarchy.setCurrentSelectedElement(pdSelected);

				ADrawingState dsNext = drawingController
						.getDrawingState(EDrawingStateType.ANIMATION_POP_OUT_DETAIL_OUTSIDE);
				drawingController.setDrawingState(dsNext);
			}

			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public PartialDisc getSelectedElement() {
		return radialHierarchy.getCurrentSelectedElement();
	}

	@Override
	public EDrawingStateType getType() {
		return EDrawingStateType.DRAWING_STATE_DETAIL_OUTSIDE;
	}
}
