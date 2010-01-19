package org.caleydo.view.base;

/**
 * Enum for triggering view loading in RCP over the command line.
 * 
 * @author Marc Streit
 * @author Werner Puff
 */
public enum EStartViewType {

	glyphview("org.caleydo.view.glyph"), parcoords("org.caleydo.view.parcoords"), heatmap(
			"org.caleydo.view.heatmap.hierarchical"), bucket(
			"org.caleydo.view.bucket"), browser("org.caleydo.view.browser"), tabular(
			"org.caleydo.view.tabular"), radial("org.caleydo.view.radial"), histogram(
			"org.caleydo.view.histogram"), dendrogram_horizontal(
			"org.caleydo.view.dendrogram.horizontal"), dendrogram_vertical(
			"org.caleydo.view.dendrogram.vertical"), scatterplot(
			"org.caleydo.view.scatterplot"), dataflipper(
			"org.caleydo.view.dataflipper");

	private String viewID;

	/**
	 * Constructor
	 * 
	 * @param viewID
	 *            view ID related to the command-line argument
	 */
	private EStartViewType(String viewID) {
		this.viewID = viewID;
	}

	public String getViewID() {
		return viewID;
	}

	// @SuppressWarnings("unchecked")
	// public Class<? extends ASerializedView> getSerializedViewClass() {
	// try {
	// return (Class<? extends ASerializedView>)
	// Class.forName(serializedViewClassName);
	// }
	// catch (ClassNotFoundException e) {
	// //TODO print error message that view cannot be loaded
	// e.printStackTrace();
	// }
	//		
	// return null;
	// }
}
