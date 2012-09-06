/**
 * 
 */
package org.caleydo.view.stratomex.vendingmachine;

import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * @author Marc Streit
 * 
 */
public class RankedElement
	implements Comparable<RankedElement> {

	private float score;

	private TablePerspective columnTablePerspective;

	private TablePerspective groupTablePerspective;

	public RankedElement(float score, TablePerspective columnTablePerspective,
			TablePerspective groupTablePerspective) {
		this.score = score;
		this.columnTablePerspective = columnTablePerspective;
		this.groupTablePerspective = groupTablePerspective;
	}

	/**
	 * @return the score, see {@link #score}
	 */
	public float getScore() {
		return score;
	}

	/**
	 * @return the columnTablePerspective, see {@link #columnTablePerspective}
	 */
	public TablePerspective getColumnTablePerspective() {
		return columnTablePerspective;
	}

	/**
	 * @return the groupTablePerspective, see {@link #groupTablePerspective}
	 */
	public TablePerspective getGroupTablePerspective() {
		return groupTablePerspective;
	}

	@Override
	public int compareTo(RankedElement comparedRankedElement) {
		if (score < comparedRankedElement.getScore())
			return 1;
		
		return -1;
	}
}
