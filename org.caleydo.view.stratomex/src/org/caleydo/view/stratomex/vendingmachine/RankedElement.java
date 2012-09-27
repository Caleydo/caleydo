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
import org.caleydo.core.view.opengl.layout.util.SpacerRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * @author Marc Streit
 * 
 */
public class RankedElement
	extends Row
	implements Comparable<RankedElement> {

	private float score;

	private TablePerspective columnTablePerspective;

	private TablePerspective groupTablePerspective;

	private int rank;

	private CaleydoTextRenderer textRenderer;

	public RankedElement(float score, TablePerspective columnTablePerspective,
			TablePerspective groupTablePerspective, CaleydoTextRenderer textRenderer) {

		this.score = score;
		this.columnTablePerspective = columnTablePerspective;
		this.groupTablePerspective = groupTablePerspective;
		this.textRenderer = textRenderer;
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
		rankNumberLayout.setPixelSizeX(50);
		rankNumberLayout
				.setRenderer(new LabelRenderer(vendingMachine, Integer.toString(rank)));
		append(rankNumberLayout);

		ElementLayout dataSetIndicatorLayout = new ElementLayout("dataSetIndicatorLayout");
		dataSetIndicatorLayout.setRenderer(new SpacerRenderer(false));
		dataSetIndicatorLayout.addBackgroundRenderer(new ColorRenderer(groupTablePerspective
				.getDataDomain().getColor().getRGBA()));
		dataSetIndicatorLayout.setPixelSizeX(30);

		append(dataSetIndicatorLayout);

		ElementLayout columnPerspectiveLayout = new ElementLayout("columnPerspectiveLayout");
		if (columnTablePerspective != null)
			columnPerspectiveLayout.setRenderer(new LabelRenderer(vendingMachine,
					columnTablePerspective.getRecordPerspective().getLabel()));
		columnPerspectiveLayout.setPixelSizeX(150);
		append(columnPerspectiveLayout);

		ElementLayout groupPerspectiveLayout = new ElementLayout("columnPerspectiveLayout");
		if (groupTablePerspective != null)
			groupPerspectiveLayout.setRenderer(new LabelRenderer(vendingMachine,
					groupTablePerspective.getLabel()));
		groupPerspectiveLayout.setPixelSizeX(100);
		append(groupPerspectiveLayout);
		
		ElementLayout scoreBarLayout = new ElementLayout("scoreBarLayout");
			scoreBarLayout.setRenderer(new ScoreBarRenderer(score));
		scoreBarLayout.setPixelSizeX(100);
		append(scoreBarLayout);
	}
}
