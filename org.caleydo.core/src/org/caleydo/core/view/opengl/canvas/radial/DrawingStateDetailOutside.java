package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.radial.DrawingController;
import org.caleydo.core.view.opengl.canvas.radial.DrawingState;
import org.caleydo.core.view.opengl.canvas.radial.DrawingStrategyManager;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.radial.PDDrawingStrategy;
import org.caleydo.core.view.opengl.canvas.radial.PartialDisc;

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

	public DrawingStateDetailOutside(DrawingController drawingController, GLRadialHierarchy radialHierarchy) {

		super(drawingController, radialHierarchy);
		bInitialDraw = true;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		calculateDrawingParameters(pdCurrentRootElement, pdCurrentMouseOverElement, pdCurrentSelectedElement,
			fXCenter, fYCenter);

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		PDDrawingStrategy dsRainbow =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW);
		PDDrawingStrategy dsSelected =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
		PDDrawingStrategy dsTransparent =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_TRANSPARENT);

		boolean bMouseOverElementInDetailOutside = false;
		if (pdCurrentMouseOverElement != null) {
			bMouseOverElementInDetailOutside =
				pdCurrentMouseOverElement.hasParent(pdCurrentSelectedElement, iDisplayedDetailViewDepth - 1);
		}

		if ((pdCurrentMouseOverElement == pdCurrentSelectedElement) || (bMouseOverElementInDetailOutside)) {

			PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			dsLabelDecorator.setDrawingStrategy(dsRainbow);
			pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, 3);

			dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			dsLabelDecorator.setDrawingStrategy(dsSelected);
			pdCurrentMouseOverElement.setPDDrawingStrategy(dsLabelDecorator);

		}
		else {
			pdCurrentSelectedElement.setPDDrawingStrategyChildren(dsRainbow, pdCurrentSelectedElement
				.getCurrentDepth());
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(dsSelected);
			}
		}
		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, fDetailViewDiscWidth,
			iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(dsTransparent, pdCurrentSelectedElement
			.getCurrentDepth());
		pdCurrentRootElement.drawHierarchyFull(gl, glu, fOverviewDiscWidth, iDisplayedOverviewDepth);

		// Draw the MouseOverElement again at last for correct antialiasing in overview
		if (pdCurrentMouseOverElement != null) {
			if ((!bMouseOverElementInDetailOutside)
				&& (pdCurrentMouseOverElement != pdCurrentSelectedElement)) {
				PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
				dsLabelDecorator.setDrawingStrategy(dsSelected);
				if (pdCurrentMouseOverElement != pdCurrentRootElement) {
					dsLabelDecorator.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
				}
				else {
					dsLabelDecorator.drawFullCircle(gl, glu, pdCurrentMouseOverElement);
				}
			}
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(dsRainbow, pdCurrentSelectedElement
			.getCurrentDepth());

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

		bInitialDraw = false;
	}

	private void calculateDrawingParameters(PartialDisc pdCurrentRootElement,
		PartialDisc pdCurrentMouseOverElement, PartialDisc pdCurrentSelectedElement, float fXCenter,
		float fYCenter) {

		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();
		float fDetailViewScreenPercentage;
		int iSelectedElementDepth = pdCurrentSelectedElement.getCurrentDepth();

		if (iMaxDisplayedHierarchyDepth <= MIN_DISPLAYED_DETAIL_DEPTH + 1) {
			fDetailViewScreenPercentage = MIN_DETAIL_SCREEN_PERCENTAGE;
		}
		else {
			float fPercentageStep =
				(MAX_DETAIL_SCREEN_PERCENTAGE - MIN_DETAIL_SCREEN_PERCENTAGE)
					/ ((float) (iMaxDisplayedHierarchyDepth - MIN_DISPLAYED_DETAIL_DEPTH - 1));

			fDetailViewScreenPercentage =
				MIN_DETAIL_SCREEN_PERCENTAGE + (iSelectedElementDepth - MIN_DISPLAYED_DETAIL_DEPTH)
					* fPercentageStep;
		}

		iDisplayedDetailViewDepth = Math.min(iMaxDisplayedHierarchyDepth, iSelectedElementDepth);
		fDetailViewDiscWidth =
			Math.min(fXCenter * fDetailViewScreenPercentage, fYCenter * fDetailViewScreenPercentage)
				/ iDisplayedDetailViewDepth;

		float fOverviewScreenPercentage =
			100.0f - (fDetailViewScreenPercentage + (100.0f - USED_SCREEN_PERCENTAGE));
		iDisplayedOverviewDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement
				.getHierarchyDepth(iMaxDisplayedHierarchyDepth));

		float fTotalOverviewWidth =
			Math.min(fXCenter * fOverviewScreenPercentage, fYCenter * fOverviewScreenPercentage);
		fOverviewDiscWidth = fTotalOverviewWidth / iDisplayedOverviewDepth;

		fDetailViewInnerRadius = fTotalOverviewWidth + Math.min(fXCenter * 0.1f, fYCenter * 0.1f);

		float fSelectedElementStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();
		if (bInitialDraw)
			fDetailViewStartAngle = fSelectedElementStartAngle + (fSelectedElementAngle / 2.0f) + 180;

	}

	@Override
	public void handleClick(PartialDisc pdClicked) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdClicked != pdRealRootElement && pdClicked.hasChildren()) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW), 3);
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentRootElement(pdClicked.getParent());
				radialHierarchy.setCurrentSelectedElement(pdClicked.getParent());
				radialHierarchy.setCurrentMouseOverElement(pdClicked.getParent());
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT);
				bInitialDraw = true;
			}
			else {
				pdCurrentSelectedElement.simulateDrawHierarchyAngular(fDetailViewDiscWidth,
					iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentRootElement(pdClicked);
				radialHierarchy.setCurrentMouseOverElement(pdClicked);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT);
				bInitialDraw = true;
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
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW), 3);
			}

			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleDoubleClick(PartialDisc pdClicked) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdClicked != pdCurrentRootElement && pdClicked.hasChildren() && pdClicked.getCurrentDepth() > 1) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW), 3);
			}

			if (pdClicked == pdCurrentSelectedElement) {
				radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);
				radialHierarchy.setCurrentMouseOverElement(pdCurrentRootElement);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);
				bInitialDraw = true;
			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentMouseOverElement(pdClicked);
				bInitialDraw = true;
			}

			radialHierarchy.setDisplayListDirty();
		}
	}
}
