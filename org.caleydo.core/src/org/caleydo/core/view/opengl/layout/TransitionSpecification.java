/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout;

/**
 * Class that handles transitions and animations for {@link ElementLayout}s.
 *
 * @author Alexander Lex
 *
 */
class TransitionSpecification {

	private ElementLayout layout;

	// pre-transition dimensions
	/** specifies how much this element is translated in x prior to transition */
	private float preTranslateX = 0;
	/** specifies how much this element is translated in y prior to transition */
	private float preTransalteY = 0;

	/** The width in actual OpenGL coordinates prior to transition */
	private float preSizeX = 0;
	/** The height in actual OpenGL coordinates prior to transition */
	private float preSizeY = 0;

	// post-transition dimensions
	/** specifies how much this element is translated in x prior to transition */
	private float postTranslateX = 0;
	/** specifies how much this element is translated in y prior to transition */
	private float postTransalteY = 0;

	/** The width in actual OpenGL coordinates prior to transition */
	private float postSizeX = 0;
	/** The height in actual OpenGL coordinates prior to transition */
	private float postSizeY = 0;

	/** Distance for translations in X */
	private float distanceTranslateX;
	/** Distance for translations in Y */
	private float distanceTranslateY;
	/** Size change for X */
	private float distanceSizeX;
	/** Size change for Y */
	private float distanceSizeY;

	/** Flag used to check whether the current transition is in the first step */
	private boolean isFirstTime = true;

	/** The time when we start the animation */
	private long startTime;
	/** Duration of the animation in ms */
	private long duration = 300;

	/**
	 * Initialize with state before the change occurs.
	 */
	TransitionSpecification(ElementLayout layout, float preTranslateX, float preTranslateY, float preSizeX,
			float preSizeY) {
		this.layout = layout;
		this.preTranslateX = preTranslateX;
		this.preTransalteY = preTranslateY;
		this.preSizeX = preSizeX;
		this.preSizeY = preSizeY;
	}

	/**
	 * Set values after the state change is complete
	 *
	 * @param postTranslateX
	 * @param postTranslateY
	 * @param postSizeX
	 * @param postSizeY
	 */
	void setPostTransitionValues(float postTranslateX, float postTranslateY, float postSizeX, float postSizeY) {
		this.postTranslateX = postTranslateX;
		this.postTransalteY = postTranslateY;
		this.postSizeX = postSizeX;
		this.postSizeY = postSizeY;

		calcDistances();
	}

	/**
	 * Call if this element should be deleted after the transition.
	 *
	 * @param transitionDirection
	 *            the direction of the removal
	 */
	void setPostTransitionDelete(ETransitionDirection transitionDirection) {
		this.postTranslateX = preTranslateX;
		this.postTransalteY = preTransalteY;
		this.postSizeX = preSizeX;
		this.postSizeY = preSizeX;

		switch (transitionDirection) {
		case VERTICAL:
			postSizeY = 0;
			break;
		case HORIZONTAL:
			postSizeX = 0;
			break;
		case DIAGONAL:
			postSizeX = 0;
			postSizeY = 0;
			break;
		default:
			assert false;
		}

		calcDistances();
	}

	/**
	 * Causes the element layout to appear.
	 *
	 * @param transitionDirection
	 *            the direction of the appearance
	 */

	void setToAppear(ETransitionDirection transitionDirection) {
		switch (transitionDirection) {
		case VERTICAL:
			preSizeY = 0;
			break;
		case HORIZONTAL:
			preSizeX = 0;
			break;
		case DIAGONAL:
			preSizeX = 0;
			preSizeY = 0;
			break;
		default:
			assert false;
		}

		calcDistances();

	}

	private void calcDistances() {
		distanceTranslateX = postTranslateX - preTranslateX;
		distanceTranslateY = postTransalteY - preTransalteY;
		distanceSizeX = postSizeX - preSizeX;
		distanceSizeY = postSizeY - preSizeY;
	}

	/** Returns the ID of the layout. */
	Integer getID() {
		return layout.getID();
	}

	/**
	 * Calculates the transition until the next step and sets the distances/sizes in the layout.
	 *
	 * @return true as long as the transition is active
	 */
	boolean nextStep() {
		if (isFirstTime) {
			startTime = System.currentTimeMillis();
			isFirstTime = false;

		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		if (elapsedTime < duration) {

			float factor = (float) elapsedTime / duration;
			layout.translateX = preTranslateX + distanceTranslateX * factor;
			layout.translateY = preTransalteY + distanceTranslateY * factor;

			layout.sizeScaledX = preSizeX + distanceSizeX * factor;
			layout.sizeScaledY = preSizeY + distanceSizeY * factor;

			layout.updateSpacings();

			return true;
		} else {
			layout.translateX = postTranslateX;
			layout.translateY = postTransalteY;

			layout.sizeScaledX = postSizeX;
			layout.sizeScaledY = postSizeY;

			layout.updateSpacings();

			isFirstTime = true;
			return false;
		}

	}

	/**
	 * @return the layout, see {@link #layout}
	 */
	ElementLayout getLayout() {
		return layout;
	}

}
