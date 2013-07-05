package demo;
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


import java.awt.Dimension;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.config.RankTableUIConfigs;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.ui.RankTableKeyListener;
import org.caleydo.vis.rank.ui.RankTableUI;
import org.caleydo.vis.rank.ui.RankTableUIMouseKeyListener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableDemo extends GLSandBox {

	protected final RankTableModel table;

	public RankTableDemo(Shell parentShell, String name, final IModelBuilder builder) {
		super(parentShell, name, createRoot(), new GLPadding(5),
				new Dimension(800, 600));
		this.table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table,
					ARankColumnModel model) {
				return builder.createAutoSnapshotColumns(table, model);
			}
		});
		try {
			builder.apply(table);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		canvas.addKeyListener(new RankTableKeyListener(table));
		createUI();

	}

	/**
	 * @return
	 */
	private static GLElement createRoot() {
		return new RankTableUI();
	}

	private void createUI() {
		// visual part
		RankTableUI root = (RankTableUI) getRoot();
		root.init(table, RankTableUIConfigs.DEFAULT, RowHeightLayouts.UNIFORM, RowHeightLayouts.FISH_EYE);

		RankTableUIMouseKeyListener l = new RankTableUIMouseKeyListener(root.findBody());
		this.canvas.addMouseListener(l);
		canvas.addKeyListener(l);
	}

	public static float toFloat(String[] l, int i) {
		if (i >= l.length)
			return Float.NaN;
		String v = l[i].trim();
		if (v.equalsIgnoreCase("-") || v.isEmpty() || v.equalsIgnoreCase("-"))
			return Float.NaN;
		int p = v.indexOf('-');
		if (p > 0 && v.charAt(p - 1) != 'e' && v.charAt(p - 1) != 'E')
			v = v.substring(0, p);
		try {
			return Float.parseFloat(v);
		} catch(NumberFormatException e) {
			System.err.println("parse error: "+v);
			return Float.NaN;
		}
	}

	public interface IModelBuilder {
		void apply(RankTableModel table) throws Exception;

		Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model);
	}
}
