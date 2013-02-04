/**
 *
 */
package org.caleydo.view.enroute;

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
public class SelectionColorCalculator {

	/**
	 * The color that is used for the {@link SelectionType} with the highest
	 * priority. If {@link SelectionType#NORMAL} has the highest priority,
	 * {@link #baseColor} is used.
	 */
	private Color primaryColor;
	/**
	 * The color that is used for the {@link SelectionType} with the second
	 * highest priority. If there is no second <code>SelectionType</code>,
	 * {@link #primaryColor} is used.
	 */
	private Color secondaryColor;
	/**
	 * The color that is used for {@link SelectionType#NORMAL}.
	 */
	private Color baseColor;

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

	public SelectionColorCalculator() {
		this(new Color());
	}

	public void calculateColors(List<SelectionType> selectionTypes) {

		if (selectionTypes.size() != 0
				&& !selectionTypes.get(0).equals(SelectionType.NORMAL)
				&& selectionTypes.get(0).isVisible()) {
			float[] color = selectionTypes.get(0).getColor();
			primaryColor.setRGBA(new float[] {color[0], color[1], color[2], 1});

			if (selectionTypes.size() > 1
					&& !selectionTypes.get(1).equals(SelectionType.NORMAL)
					&& selectionTypes.get(1).isVisible()) {
				color = selectionTypes.get(1).getColor();
				secondaryColor.setRGBA(new float[] {color[0], color[1], color[2], 1});
			} else {
				secondaryColor.setRGBA(primaryColor.getRGBA());
			}
		} else {

			primaryColor.setRGBA(baseColor.getRGBA());
			secondaryColor.setRGBA(baseColor.getRGBA());
		}
	}

	/**
	 * @param baseColor
	 *            setter, see {@link #baseColor}
	 */
	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
	}

	/**
	 * @return the baseColor, see {@link #baseColor}
	 */
	public Color getBaseColor() {
		return baseColor;
	}

	/**
	 * @return the primaryColor, see {@link #primaryColor}
	 */
	public Color getPrimaryColor() {
		return primaryColor;
	}

	/**
	 * @return the secondaryColor, see {@link #secondaryColor}
	 */
	public Color getSecondaryColor() {
		return secondaryColor;
	}

}
