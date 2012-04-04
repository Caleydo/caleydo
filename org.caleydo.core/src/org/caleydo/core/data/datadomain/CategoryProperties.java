/**
 * 
 */
package org.caleydo.core.data.datadomain;

/**
 * @author alexsb
 * 
 */
public class CategoryProperties {

	int numberOfCategories;
	String colorScheme;

	/**
	 * @param numberOfCategories
	 *            setter, see {@link #numberOfCategories}
	 */
	public void setNumberOfCategories(int numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	/**
	 * @return the numberOfCategories, see {@link #numberOfCategories}
	 */
	public int getNumberOfCategories() {
		return numberOfCategories;
	}

	/**
	 * @param colorScheme
	 *            setter, see {@link #colorScheme}
	 */
	public void setColorScheme(String colorScheme) {
		this.colorScheme = colorScheme;
	}

	/**
	 * @return the colorSheme, see {@link #colorScheme}
	 */
	public String getColorScheme() {
		return colorScheme;
	}

}
