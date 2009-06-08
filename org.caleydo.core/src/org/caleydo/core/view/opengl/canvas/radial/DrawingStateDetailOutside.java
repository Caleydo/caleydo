package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.ESelectionType;

public class DrawingStateDetailOutside
	extends DrawingState {

	private static final float MIN_DETAIL_SCREEN_PERCENTAGE = 0.4f;
	private static final float MAX_DETAIL_SCREEN_PERCENTAGE = 0.6f;
	private static final float USED_SCREEN_PERCENTAGE = 0.8f;
	private static final int MIN_DISPLAYED_DETAIL_DEPTH = 2;

	private float fDetailViewStartAngle;
	private float fDetailViewDiscWidth;
	private float fDetailViewInnerRadius;
	private float fOverviewDiscWidth;
	private int iDisplayedDetailViewDepth;
	private int iDisplayedOverviewDepth;
	private boolean bInitialDraw;

	public DrawingStateDetailOutside(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
		bInitialDraw = true;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
		PartialDisc pdHighlightedChildIndicator = null;

		calculateDrawingParameters(pdCurrentRootElement, pdCurrentSelectedElement, fXCenter, fYCenter);

		if (iDisplayedDetailViewDepth < MIN_DISPLAYED_DETAIL_DEPTH) {
			radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
			radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
			radialHierarchy.setCurrentMouseOverElement(pdCurrentRootElement);
			drawingController.setDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);
			radialHierarchy.setDisplayListDirty();

			navigationHistory.replaceCurrentHistoryEntry(drawingController.getCurrentDrawingState(),
				pdCurrentRootElement, pdCurrentRootElement, radialHierarchy.getMaxDisplayedHierarchyDepth());
			drawingController.draw(fXCenter, fYCenter, gl, glu);
			
			bInitialDraw = true;
			
			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getElementID());

			return;
		}

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		PDDrawingStrategy dsDefault = DrawingStrategyManager.get().getDefaultDrawingStrategy();
		PDDrawingStrategy dsSelected =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
		PDDrawingStrategy dsTransparent =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_TRANSPARENT);

		pdCurrentRootElement.setPDDrawingStrategyChildren(dsDefault, iDisplayedOverviewDepth);

		boolean bMouseOverElementInDetailOutside = false;
		boolean bIsMouseOverElementDisplayed = true;
		boolean bIsMouseOverElementParentOfCurrentRoot = false;
		boolean bIsNewSelection = radialHierarchy.isNewSelection();

		if (pdCurrentMouseOverElement != null) {
			bMouseOverElementInDetailOutside =
				pdCurrentMouseOverElement.hasParent(pdCurrentSelectedElement, iDisplayedDetailViewDepth - 1);
			if (!bMouseOverElementInDetailOutside)
				bMouseOverElementInDetailOutside = pdCurrentMouseOverElement == pdCurrentSelectedElement;

			if (bIsNewSelection) {
				int iParentPathLength = pdCurrentMouseOverElement.getParentPathLength(pdCurrentRootElement);
				if ((iParentPathLength >= iDisplayedOverviewDepth) || (iParentPathLength == -1)) {
					PartialDisc pdParent = pdCurrentMouseOverElement.getParent();
					if (pdParent == null) {
						pdCurrentRootElement = pdCurrentMouseOverElement;
					}
					else {
						pdCurrentRootElement = pdParent;
					}
					radialHierarchy.setCurrentRootElement(pdCurrentRootElement);
					radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
					drawingController.setDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);

					navigationHistory.addNewHistoryEntry(drawingController.getCurrentDrawingState(),
						pdCurrentRootElement, pdCurrentRootElement, radialHierarchy
							.getMaxDisplayedHierarchyDepth());
					
					drawingController.draw(fXCenter, fYCenter, gl, glu);

					bInitialDraw = true;
					
					radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getElementID());
					
					return;
				}
			}
			else {
				if (pdCurrentMouseOverElement != pdCurrentRootElement) {
					ArrayList<PartialDisc> alParentPath =
						pdCurrentMouseOverElement.getParentPath(pdCurrentRootElement);
					if (alParentPath != null) {
						if (alParentPath.size() >= iDisplayedOverviewDepth) {
							DrawingStrategyManager drawingStategyManager = DrawingStrategyManager.get();
							PDDrawingStrategyChildIndicator dsDefaultHighlightedChildIndicator =
								(PDDrawingStrategyChildIndicator) drawingStategyManager
									.createDrawingStrategy(drawingStategyManager.getDefaultStrategyType());

							dsDefaultHighlightedChildIndicator
								.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
							pdHighlightedChildIndicator =
								alParentPath.get(alParentPath.size() - iDisplayedOverviewDepth);
							pdHighlightedChildIndicator
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
		}

		if (bIsMouseOverElementDisplayed) {
			PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			dsLabelDecorator.setDrawingStrategy(dsDefault);

			if (bMouseOverElementInDetailOutside) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, Math.min(
					iDisplayedDetailViewDepth, RadialHierarchyRenderStyle.MAX_LABELING_DEPTH));
			}
			else if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(dsLabelDecorator);
			}
		}

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, fDetailViewDiscWidth,
			iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

		// The mouse over element has to be drawn (again in using different drawing strategy) at last for
		// correct antialiasing
		if (bMouseOverElementInDetailOutside) {
			if(bIsNewSelection) {
				PDDrawingStrategySelected dsExternalSelected =
					(PDDrawingStrategySelected) DrawingStrategyManager.get().createDrawingStrategy(
						DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
				dsExternalSelected.setBorderColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
				dsSelected = dsExternalSelected;
			}
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(dsTransparent, iDisplayedDetailViewDepth);
		if ((pdHighlightedChildIndicator != null)
			&& (pdHighlightedChildIndicator.hasParent(pdCurrentSelectedElement, iDisplayedDetailViewDepth))) {
			PDDrawingStrategyTransparent dsTransparentHighlightedChildIndicator =
				(PDDrawingStrategyTransparent) DrawingStrategyManager.get().createDrawingStrategy(
					DrawingStrategyManager.PD_DRAWING_STRATEGY_TRANSPARENT);
			dsTransparentHighlightedChildIndicator
				.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
			pdHighlightedChildIndicator.setPDDrawingStrategy(dsTransparentHighlightedChildIndicator);
		}

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fOverviewDiscWidth, iDisplayedOverviewDepth);

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(0, 1, 1);
		GLPrimitives
			.renderPartialDiscBorder(gl, glu, pdCurrentSelectedElement.getCurrentInnerRadius(),
				pdCurrentSelectedElement.getCurrentInnerRadius() + fOverviewDiscWidth
					* iDisplayedDetailViewDepth, pdCurrentSelectedElement.getCurrentStartAngle(),
				pdCurrentSelectedElement.getCurrentAngle(), 100, 2);
		gl.glPopAttrib();

		// The mouse over element has to be drawn (again in using different drawing strategy) at last for
		// correct antialiasing
		if ((pdCurrentMouseOverElement != null) && (bIsMouseOverElementDisplayed)) {
			if(bIsNewSelection) {
				PDDrawingStrategySelected dsExternalSelected =
					(PDDrawingStrategySelected) DrawingStrategyManager.get().createDrawingStrategy(
						DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
				dsExternalSelected.setBorderColor(RadialHierarchyRenderStyle.SELECTED_COLOR);
				dsSelected = dsExternalSelected;
			}
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		if (bIsMouseOverElementParentOfCurrentRoot) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor3fv(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR, 0);
			GLPrimitives.renderCircle(gl, glu, fOverviewDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fOverviewDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

		bInitialDraw = false;
	}

	private void calculateDrawingParameters(PartialDisc pdCurrentRootElement,
		PartialDisc pdCurrentSelectedElement, float fXCenter, float fYCenter) {

		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();

		float fDetailViewScreenPercentage;
		int iSelectedElementDepth = pdCurrentSelectedElement.getHierarchyDepth();
		int iDepthToRoot = pdCurrentSelectedElement.getParentPathLength(pdCurrentRootElement);

		iDisplayedDetailViewDepth =
			Math.min(iMaxDisplayedHierarchyDepth - iDepthToRoot, iSelectedElementDepth);

		if (iMaxDisplayedHierarchyDepth <= MIN_DISPLAYED_DETAIL_DEPTH + 1) {
			fDetailViewScreenPercentage = MIN_DETAIL_SCREEN_PERCENTAGE;
		}
		else {
			float fPercentageStep =
				(MAX_DETAIL_SCREEN_PERCENTAGE - MIN_DETAIL_SCREEN_PERCENTAGE)
					/ ((float) (iMaxDisplayedHierarchyDepth - MIN_DISPLAYED_DETAIL_DEPTH - 1));

			fDetailViewScreenPercentage =
				MIN_DETAIL_SCREEN_PERCENTAGE + (iDisplayedDetailViewDepth - MIN_DISPLAYED_DETAIL_DEPTH)
					* fPercentageStep;
		}

		fDetailViewDiscWidth =
			Math.min(fXCenter * fDetailViewScreenPercentage, fYCenter * fDetailViewScreenPercentage)
				/ iDisplayedDetailViewDepth;

		float fOverviewScreenPercentage =
			100.0f - (fDetailViewScreenPercentage + (100.0f - USED_SCREEN_PERCENTAGE));
		iDisplayedOverviewDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement.getHierarchyDepth());

		float fTotalOverviewWidth =
			Math.min(fXCenter * fOverviewScreenPercentage, fYCenter * fOverviewScreenPercentage);
		fOverviewDiscWidth = fTotalOverviewWidth / iDisplayedOverviewDepth;

		fDetailViewInnerRadius = fTotalOverviewWidth + Math.min(fXCenter * 0.1f, fYCenter * 0.1f);

		if (bInitialDraw) {
			fDetailViewStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		}

	}

	@Override
	public void handleSelection(PartialDisc pdSelected) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdSelected != pdRealRootElement && pdSelected.hasChildren()) {

			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), RadialHierarchyRenderStyle.MAX_LABELING_DEPTH);
			}
			if (pdSelected == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_PARENT_ROOT_ELEMENT);
				
				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getParent().getElementID());
			}
			else {
				pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), iDisplayedDetailViewDepth);
				pdCurrentSelectedElement.simulateDrawHierarchyAngular(fDetailViewDiscWidth,
					iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				radialHierarchy.setCurrentSelectedElement(pdSelected);
				radialHierarchy.setCurrentRootElement(pdSelected);
				radialHierarchy.setCurrentMouseOverElement(pdSelected);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT);
				
				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdSelected.getElementID());
			}

			bInitialDraw = true;
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver) {
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdMouseOver != pdCurrentMouseOverElement) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), RadialHierarchyRenderStyle.MAX_LABELING_DEPTH);
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
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdSelected.hasChildren() && pdSelected.getCurrentDepth() > 1) {

			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), RadialHierarchyRenderStyle.MAX_LABELING_DEPTH);
			}

			if (pdSelected == pdCurrentSelectedElement || pdSelected == pdCurrentRootElement) {

				pdCurrentSelectedElement.simulateDrawHierarchyAngular(fDetailViewDiscWidth,
					iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				DrawingState dsNext =
					drawingController
						.getDrawingState(DrawingController.DRAWING_STATE_ANIM_PULL_IN_DETAIL_OUTSIDE);
				radialHierarchy.setAnimationActive(true);

				drawingController.setDrawingState(dsNext);
				
				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getElementID());
			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdSelected);
				radialHierarchy.setCurrentMouseOverElement(pdSelected);

				DrawingState dsNext =
					drawingController
						.getDrawingState(DrawingController.DRAWING_STATE_ANIM_POP_OUT_DETAIL_OUTSIDE);
				radialHierarchy.setAnimationActive(true);

				drawingController.setDrawingState(dsNext);
				
				radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentRootElement.getElementID());
			}

			bInitialDraw = true;
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public PartialDisc getSelectedElement() {
		return radialHierarchy.getCurrentSelectedElement();
	}
}
