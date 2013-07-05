/**
 * 
 */
package org.caleydo.view.enroute.event;

import org.caleydo.core.event.AEvent;

/**
 * Event that determines whether the width of enRoute's content should fit to
 * the width of the view.
 * 
 * @author Christian Partl
 * 
 */
public class FitToViewWidthEvent extends AEvent {

	/**
	 * Determines whether the width of enRoute's content should fit to the width
	 * of the view.
	 */
	private boolean fitToViewWidth;

	
	public FitToViewWidthEvent(boolean fitToViewWidth) {
		this.fitToViewWidth = fitToViewWidth;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param fitToViewWidth
	 *            setter, see {@link #fitToViewWidth}
	 */
	public void setFitToViewWidth(boolean fitToViewWidth) {
		this.fitToViewWidth = fitToViewWidth;
	}

	/**
	 * @return the fitToViewWidth, see {@link #fitToViewWidth}
	 */
	public boolean isFitToViewWidth() {
		return fitToViewWidth;
	}

}
