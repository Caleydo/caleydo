package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * This class represents the animation for a selected partial disc that becomes the new root element. When the
 * animation is finished the follow up drawing state ({@link DrawingStateFullHierarchy}) will become active.
 * 
 * @author Christian Partl
 */
@XmlType
public class AnimationNewRootElement
	extends ADrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.35f;

	private static final float TARGET_ROOT_ANGLE = 360.0f;
	private static final float TARGET_ROOT_INNER_RADIUS = 0.0f;

	private MovementValue mvCurrentRootAngle;
	private MovementValue mvCurrentRootStartAngle;
	private MovementValue mvCurrentRootInnerRadius;
	private MovementValue mvCurrentWidth;
	private MovementValue mvCurrentRootColorR;
	private MovementValue mvCurrentRootColorG;
	private MovementValue mvCurrentRootColorB;

	private int fTargetDepth;

	private PDDrawingStrategyFixedColor dsFixedColor;

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
	public AnimationNewRootElement(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {

		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (!bAnimationStarted) {	
			initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
			bAnimationStarted = true;
			radialHierarchy.setAnimationActive(true);
		}

		moveValues(dTimePassed);

		dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(), mvCurrentRootColorG
			.getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
			fTargetDepth, mvCurrentRootStartAngle.getMovementValue(), mvCurrentRootAngle.getMovementValue(),
			mvCurrentRootInnerRadius.getMovementValue());

		if (haveMovementValuesReachedTargets()) {
			bAnimationStarted = false;
		}

		if (!bAnimationStarted) {
			ADrawingState dsNext =
				drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);
			radialHierarchy.setCurrentMouseOverElement(pdCurrentSelectedElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement, pdCurrentSelectedElement,
				radialHierarchy.getMaxDisplayedHierarchyDepth());
			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdCurrentSelectedElement, pdCurrentSelectedElement);
			radialHierarchy.setDisplayListDirty();

		}
	}

	/**
	 * Initializes the animation, particularly initializes all movement values needed for the animation.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 */
	private void initAnimation(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {

		float fCurrentSelectedAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentSelectedStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();

		fTargetDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentSelectedElement
				.getHierarchyDepth());
		float fTargetWidth =
			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
				/ fTargetDepth;

		float fMidAngle = fCurrentSelectedStartAngle + (fCurrentSelectedAngle / 2.0f);
		fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();

		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}
		float fAngleToAdd = (fMidAngle > fCurrentSelectedStartAngle) ? -180 : 180;
		float fSelectedTargetStartAngle = fMidAngle + fAngleToAdd;

		float fArRGB[];
		if (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType() == EPDDrawingStrategyType.RAINBOW_COLOR) {
			ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
			fArRGB = cmRainbow.getColor(fMidAngle / 360);
		}
		else {
			ColorMapping cmExpression =
				ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
			fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
		}

		alMovementValues.clear();

		mvCurrentRootAngle =
			createNewMovementValue(fCurrentSelectedAngle, TARGET_ROOT_ANGLE, fAnimationDuration);
		mvCurrentRootStartAngle =
			createNewMovementValue(fCurrentSelectedStartAngle, fSelectedTargetStartAngle, fAnimationDuration);
		mvCurrentRootInnerRadius =
			createNewMovementValue(fCurrentSelectedInnerRadius, TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
		mvCurrentRootColorR =
			createNewMovementValue(fArRGB[0], RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0],
				fAnimationDuration);
		mvCurrentRootColorG =
			createNewMovementValue(fArRGB[1], RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1],
				fAnimationDuration);
		mvCurrentRootColorB =
			createNewMovementValue(fArRGB[2], RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2],
				fAnimationDuration);

		dsFixedColor =
			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
				EPDDrawingStrategyType.FIXED_COLOR);

		pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	}

	@Override
	public EDrawingStateType getType() {
		return EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT;
	}
}
