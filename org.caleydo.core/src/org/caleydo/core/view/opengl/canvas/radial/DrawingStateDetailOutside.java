package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeMouseOverEvent;

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

		if (pdCurrentMouseOverElement != null) {
			bMouseOverElementInDetailOutside =
				pdCurrentMouseOverElement.hasParent(pdCurrentSelectedElement, iDisplayedDetailViewDepth - 1);
			if (!bMouseOverElementInDetailOutside)
				bMouseOverElementInDetailOutside = pdCurrentMouseOverElement == pdCurrentSelectedElement;

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
						pdHighlightedChildIndicator.setPDDrawingStrategy(dsDefaultHighlightedChildIndicator);
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
			dsLabelDecorator.setDrawingStrategy(dsDefault);

			if (bMouseOverElementInDetailOutside) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, 3);
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
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(dsTransparent, pdCurrentSelectedElement
			.getCurrentDepth());
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
					.getDefaultDrawingStrategy(), 3);
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_PARENT_ROOT_ELEMENT);
			}
			else {
				pdCurrentSelectedElement.simulateDrawHierarchyAngular(fDetailViewDiscWidth,
					iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentRootElement(pdClicked);
				radialHierarchy.setCurrentMouseOverElement(pdClicked);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT);
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
					.getDefaultDrawingStrategy(), 3);
			}

			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);
			radialHierarchy.setDisplayListDirty();
		}

		//TODO: Maybe test if (pdMouseOver != pdCurrentMouseOverElement)
		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		ClusterNodeMouseOverEvent event = new ClusterNodeMouseOverEvent();
		event.setSender(this);
		event.setClusterNumber(pdMouseOver.getElementID());

		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleDoubleClick(PartialDisc pdClicked) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdClicked.hasChildren() && pdClicked.getCurrentDepth() > 1) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), 3);
			}

			if (pdClicked == pdCurrentSelectedElement || pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentMouseOverElement(pdCurrentRootElement);

				pdCurrentSelectedElement.simulateDrawHierarchyAngular(fDetailViewDiscWidth,
					iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				DrawingState dsNext =
					drawingController
						.getDrawingState(DrawingController.DRAWING_STATE_ANIM_PULL_IN_DETAIL_OUTSIDE);
				radialHierarchy.setAnimationActive(true);

				drawingController.setDrawingState(dsNext);
				navigationHistory.addNewHistoryEntry(dsNext, pdCurrentRootElement, pdCurrentRootElement,
					radialHierarchy.getMaxDisplayedHierarchyDepth());
			}
			else {

				// pdCurrentSelectedElement.simulateDrawHierarchyAngular(fDetailViewDiscWidth,
				// iDisplayedDetailViewDepth, fDetailViewStartAngle, 360, fDetailViewInnerRadius);

				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentMouseOverElement(pdClicked);

				DrawingState dsNext =
					drawingController
						.getDrawingState(DrawingController.DRAWING_STATE_ANIM_POP_OUT_DETAIL_OUTSIDE);
				radialHierarchy.setAnimationActive(true);

				drawingController.setDrawingState(dsNext);
				navigationHistory.addNewHistoryEntry(dsNext, pdCurrentRootElement, pdCurrentRootElement,
					radialHierarchy.getMaxDisplayedHierarchyDepth());
			}

			bInitialDraw = true;
			radialHierarchy.setDisplayListDirty();
		}
	}
}
