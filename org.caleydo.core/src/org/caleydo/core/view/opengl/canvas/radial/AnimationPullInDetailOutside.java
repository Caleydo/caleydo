package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * * This class represents the animation where the detail view is pulled in to the full hierarchy. When the
 * animation is finished the follow up drawing state ({@link DrawingStateFullHierarchy}) will become active.
 * 
 * @author Christian Partl
 */
public class AnimationPullInDetailOutside
	extends ADrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.3f;

	private MovementValue mvDetailViewWidth;
	private MovementValue mvOverviewWidth;
	private MovementValue mvDetailViewInnerRadius;
	private MovementValue mvDetailViewStartAngle;
	private MovementValue mvDetailViewAngle;
	private int iDisplayedDetailViewDepth;
	private int iDisplayedOverviewDepth;
	private int iAnimationPart;

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
	public AnimationPullInDetailOutside(DrawingController drawingController,
		GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (!bAnimationStarted) {
			initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement, pdCurrentRootElement);
			iAnimationPart = 1;
			bAnimationStarted = true;
			radialHierarchy.setAnimationActive(true);
		}

		moveValues(dTimePassed);

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedOverviewDepth);
		pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDrawingStrategy(EPDDrawingStrategyType.INVISIBLE), iDisplayedDetailViewDepth);

		pdCurrentRootElement.drawHierarchyFull(gl, glu, mvOverviewWidth.getMovementValue(),
			iDisplayedOverviewDepth);

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedDetailViewDepth);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvDetailViewWidth.getMovementValue(),
			iDisplayedDetailViewDepth, mvDetailViewStartAngle.getMovementValue(), mvDetailViewAngle
				.getMovementValue(), mvDetailViewInnerRadius.getMovementValue());

		if (haveMovementValuesReachedTargets()) {
			iAnimationPart++;
			if (iAnimationPart == 2) {
				initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement, pdCurrentRootElement);
			}
			if (iAnimationPart > 2) {
				bAnimationStarted = false;
			}
		}

		if (!bAnimationStarted) {
			ADrawingState dsNext =
				drawingController.getDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentMouseOverElement(pdCurrentRootElement);
			radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentRootElement, pdCurrentRootElement,
				radialHierarchy.getMaxDisplayedHierarchyDepth());
			radialHierarchy.setDisplayListDirty();
		}
	}

	/**
	 * Initializes the first part of the animation which restores the original angles of the elements in the
	 * detail view. Particularly all movement values needed for this part are initialized.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 * @param pdCurrentRootElement
	 *            Current root partial disc.
	 */
	private void initAnimationFirstPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement,
		PartialDisc pdCurrentRootElement) {

		iDisplayedDetailViewDepth = pdCurrentSelectedElement.getCurrentDepth();
		iDisplayedOverviewDepth = pdCurrentRootElement.getCurrentDepth();

		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedElementInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();

		pdCurrentRootElement.simulateDrawHierarchyFull(pdCurrentRootElement.getCurrentWidth(),
			iDisplayedOverviewDepth);

		float fCurrentSelectedElementTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedElementTargetStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();

		if (fCurrentSelectedElementTargetStartAngle < fCurrentSelectedElementStartAngle) {
			fCurrentSelectedElementTargetStartAngle += 360;
		}

		mvDetailViewAngle =
			createNewMovementValue(fCurrentSelectedElementAngle, fCurrentSelectedElementTargetAngle,
				fAnimationDuration);
		mvDetailViewStartAngle =
			createNewMovementValue(fCurrentSelectedElementStartAngle,
				fCurrentSelectedElementTargetStartAngle, fAnimationDuration);
		mvDetailViewInnerRadius =
			createNewMovementValue(fCurrentSelectedElementInnerRadius, fCurrentSelectedElementInnerRadius,
				fAnimationDuration);
		mvDetailViewWidth =
			createNewMovementValue(fCurrentSelectedElementWidth, fCurrentSelectedElementWidth,
				fAnimationDuration);
		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fCurrentRootWidth, fAnimationDuration);
	}

	/**
	 * Initializes the second part of the animation which pulls in the elements from the detail view to the
	 * full hierarchy. Particularly all movement values needed for this part are initialized.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 * @param pdCurrentRootElement
	 *            Current root partial disc.
	 */
	private void initAnimationSecondPart(float fXCenter, float fYCenter,
		PartialDisc pdCurrentSelectedElement, PartialDisc pdCurrentRootElement) {

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();
		float fCurrentSelecedElementInnderRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();

		float fHierarchyOuterRadius =
			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE);
		float fTargetWidth = fHierarchyOuterRadius / iDisplayedOverviewDepth;

		pdCurrentRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedOverviewDepth);

		float fCurrentSelectedElementTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();

		alMovementValues.clear();

		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fTargetWidth, fAnimationDuration);
		mvDetailViewWidth =
			createNewMovementValue(fCurrentSelectedElementWidth, fTargetWidth, fAnimationDuration);
		mvDetailViewInnerRadius =
			createNewMovementValue(fCurrentSelecedElementInnderRadius,
				fCurrentSelectedElementTargetInnerRadius, fAnimationDuration);
		mvDetailViewAngle =
			createNewMovementValue(fCurrentSelectedElementAngle, fCurrentSelectedElementAngle,
				fAnimationDuration);
		mvDetailViewStartAngle =
			createNewMovementValue(fCurrentSelectedElementStartAngle, fCurrentSelectedElementStartAngle,
				fAnimationDuration);
	}

}
