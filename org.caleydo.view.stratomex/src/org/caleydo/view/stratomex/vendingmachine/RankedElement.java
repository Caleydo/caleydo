/**
 * 
 */
package org.caleydo.view.stratomex.vendingmachine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;

/**
 * @author Marc Streit
 * 
 */
public class RankedElement
	extends Row
	implements Comparable<RankedElement> {

	private static final int RANK_NUMBER_WIDTH = 22;
	public static final int DATASET_COLOR_INDICATOR_WIDTH = 20;
	private static final int COLUMN_PRERSPECTIVE_WIDTH = 120;
	private static final int GROUP_TABLE_PERSPECTIVE_WIDTH = 80;
	private static final int SCORE_BAR_WIDTH = 150;

	private float score;

	private TablePerspective columnTablePerspective;

	private TablePerspective groupTablePerspective;

	private TablePerspective referenceTablePerspective;

	private int rank;

	public RankedElement(float score, TablePerspective columnTablePerspective,
			TablePerspective groupTablePerspective, TablePerspective referenceTablePerspective) {

		this.score = score;
		this.columnTablePerspective = columnTablePerspective;
		this.groupTablePerspective = groupTablePerspective;
		this.referenceTablePerspective = referenceTablePerspective;
	}

	/**
	 * @return the score, see {@link #score}
	 */
	public float getScore() {
		return score;
	}

	/**
	 * @param rank setter, see {@link #rank}
	 */
	public void setRank(int rank) {
		this.rank = rank;
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

	public void createLayout(AGLView vendingMachine) {

		BigDecimal bd = new BigDecimal(score).setScale(2, RoundingMode.HALF_EVEN);
		float score = bd.floatValue();

		ElementLayout rankNumberLayout = new ElementLayout("rankNumberLayout");
		rankNumberLayout.setPixelSizeX(RANK_NUMBER_WIDTH);
		LabelRenderer rankLabelRenderer = new LabelRenderer(vendingMachine, rank + ".");
		rankLabelRenderer.setAlignment(LabelRenderer.ALIGN_RIGHT);
		rankNumberLayout.setRenderer(rankLabelRenderer);
		append(rankNumberLayout);

		ElementLayout dataSetIndicatorLayout = new ElementLayout("dataSetIndicatorLayout");
		dataSetIndicatorLayout.setRenderer(new ColorRenderer(columnTablePerspective
				.getDataDomain().getColor().getRGBA()));
		dataSetIndicatorLayout.setPixelSizeX(DATASET_COLOR_INDICATOR_WIDTH);
		append(dataSetIndicatorLayout);

		ElementLayout spacerLayout = new ElementLayout("spacerLayout");
		spacerLayout.setPixelSizeX(7);
		append(spacerLayout);

		ElementLayout columnPerspectiveLayout = new ElementLayout("columnPerspectiveLayout");
		if (columnTablePerspective != null)
			columnPerspectiveLayout.setRenderer(new LabelRenderer(vendingMachine,
					columnTablePerspective.getRecordPerspective().getLabel()));
		columnPerspectiveLayout.setPixelSizeX(COLUMN_PRERSPECTIVE_WIDTH);
		append(columnPerspectiveLayout);

		ElementLayout groupPerspectiveLayout = new ElementLayout("columnPerspectiveLayout");
		if (groupTablePerspective != null)
			groupPerspectiveLayout.setRenderer(new LabelRenderer(vendingMachine,
					groupTablePerspective.getLabel()));
		groupPerspectiveLayout.setPixelSizeX(GROUP_TABLE_PERSPECTIVE_WIDTH);
		append(groupPerspectiveLayout);

		if (referenceTablePerspective != null) {

			ElementLayout scoreBarLayout = new ElementLayout("scoreBarLayout");
			scoreBarLayout.setRenderer(new ScoreBarRenderer(score, referenceTablePerspective
					.getDataDomain().getColor()));
			LabelRenderer scoreNumberRenderer = new LabelRenderer(vendingMachine, "  " + score);
			scoreNumberRenderer.usePaddingBottom(true);
			scoreBarLayout.addForeGroundRenderer(scoreNumberRenderer);
			// scoreBarLayout.setPixelSizeX(SCORE_BAR_WIDTH);
			append(scoreBarLayout);
		}

		append(spacerLayout);
	}
}
