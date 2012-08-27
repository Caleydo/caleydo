/**
 * 
 */
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;

/**
 * Determines the colors for objects of that want to encode more than one
 * selection type in their color.
 * 
 * @author Christian Partl
 * 
 */
class SelectionColorCalculator {

	/**
	 * The color that is used for the {@link SelectionType} with the highest
	 * priority. If {@link SelectionType#NORMAL} has the highest priority,
	 * {@link #baseColor} is used.
	 */
	Color primaryColor;
	/**
	 * The color that is used for the {@link SelectionType} with the second
	 * highest priority. If there is no second <code>SelectionType</code>,
	 * {@link #primaryColor} is used.
	 */
	Color secondaryColor;
	/**
	 * The color that is used for {@link SelectionType#NORMAL}.
	 */
	Color baseColor;

	/**
	 * 
	 * 
	 * @param baseColor
	 */
	public SelectionColorCalculator(Color baseColor) {
		this.baseColor = baseColor;
		primaryColor = new Color();
		secondaryColor = new Color();
	}

	void calculateColors(List<SelectionType> selectionTypes) {

		if (selectionTypes.size() != 0
				&& !selectionTypes.get(0).equals(SelectionType.NORMAL)
				&& selectionTypes.get(0).isVisible()) {
			primaryColor.setRGBA(selectionTypes.get(0).getColor());

			if (selectionTypes.size() > 1
					&& !selectionTypes.get(1).equals(SelectionType.NORMAL)
					&& selectionTypes.get(1).isVisible()) {
				secondaryColor.setRGBA(selectionTypes.get(1).getColor());
			} else {
				secondaryColor.setRGBA(primaryColor.getRGBA());
			}
		} else {

			primaryColor.setRGBA(baseColor.getRGBA());
			secondaryColor.setRGBA(baseColor.getRGBA());
		}
	}

}
