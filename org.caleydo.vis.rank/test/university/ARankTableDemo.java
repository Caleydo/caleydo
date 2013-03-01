/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package university;

import java.awt.Dimension;
import java.io.IOException;

import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.ui.ColumnPoolUI;
import org.caleydo.vis.rank.ui.TableBodyUI;
import org.caleydo.vis.rank.ui.TableHeaderUI;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ARankTableDemo extends GLSandBox {
	private static final char TOGGLE_ALIGN_ALL = 't';

	protected final RankTableModel table;

	public ARankTableDemo() {
		super(new GLElementContainer(GLLayouts.flowVertical(0)), new GLPadding(5), new Dimension(800, 600));
		this.table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isInteractive() {
				return true;
			}
		});
		try {
			createModel();
		} catch (NoSuchFieldException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		canvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyPressed(IKeyEvent e) {
				if (e.isKey(ESpecialKey.DOWN))
					table.selectNextRow();
				else if (e.isKey(ESpecialKey.UP))
					table.selectPreviousRow();
				else if (e.isControlDown() && (e.isKey(TOGGLE_ALIGN_ALL))) {
					// short cut for align all
					for(StackedRankColumnModel stacked : Iterables.filter(table.getColumns(), StackedRankColumnModel.class)) {
						stacked.setAlignAll(!stacked.isAlignAll());
					}
				}
			}

			@Override
			public void keyReleased(IKeyEvent e) {

			}
		});
		createUI();

	}

	protected abstract void createModel() throws IOException, NoSuchFieldException;

	private void createUI() {
		// visual part
		GLElementContainer root = (GLElementContainer) getRoot();
		root.add(new TableHeaderUI(table));
		root.add(new TableBodyUI(table, RowHeightLayouts.LINEAR));

		root.add(new ColumnPoolUI(table));
	}

	public static float toFloat(String[] l, int i) {
		String v = l[i];
		if (v.equalsIgnoreCase("-"))
			return Float.NaN;
		return Float.parseFloat(v);
	}
}
