package org.caleydo.view.visbricks.brick;

/**
 * View Types for views contained in a brick.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
public enum EContainedViewType {
	CATEGORY(true), HEATMAP_VIEW(true), PARCOORDS_VIEW(false), HISTOGRAM_VIEW(false), TAGCLOUD_VIEW(
			false), OVERVIEW_HEATMAP(false), OVERVIEW_HEATMAP_COMPACT(false), PATHWAY_VIEW(
			false), DUMMY_VIEW(false), PATHWAY_COMPACT(false), PATHWAYS_SUMMARY(false), PATHWAYS_SUMMARY_COMPACT(
			false);

	private boolean useProportionalHeight;

	/**
	 * 
	 */
	private EContainedViewType(boolean useProportionalHeight) {
		this.useProportionalHeight = useProportionalHeight;
	}

	/**
	 * @return the useProportionalHeight, see {@link #useProportionalHeight}
	 */
	public boolean isUseProportionalHeight() {
		return useProportionalHeight;
	}
}
