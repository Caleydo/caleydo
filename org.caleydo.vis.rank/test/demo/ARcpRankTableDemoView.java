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
package demo;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.config.RankTableUIConfigs;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.ui.RankTableUI;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ARcpRankTableDemoView extends ARcpGLViewPart {
	private static final char TOGGLE_ALIGN_ALL = 't';

	public ARcpRankTableDemoView() {
		super(SView.class);
		serializedView = new SView();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLView(glCanvas, getViewGUIID(), getViewGUIID());
		initializeView();
		createPartControlGL();
	}

	/**
	 * @return
	 */
	public abstract RankTableDemo.IModelBuilder createModel();

	class GLView extends AGLElementView {
		protected final RankTableModel table;

		public GLView(IGLCanvas glCanvas, String viewType, String viewName) {
			super(glCanvas, viewType, viewName);
			this.table = new RankTableModel(new RankTableConfigBase());

			canvas.addKeyListener(new IGLKeyListener() {
				@Override
				public void keyPressed(IKeyEvent e) {
					if (e.isKey(ESpecialKey.DOWN))
						table.selectNextRow();
					else if (e.isKey(ESpecialKey.UP))
						table.selectPreviousRow();
					else if (e.isControlDown() && (e.isKey(TOGGLE_ALIGN_ALL))) {
						// short cut for align all
						for (StackedRankColumnModel stacked : Iterables.filter(table.getColumns(),
								StackedRankColumnModel.class)) {
							stacked.setAlignAll(!stacked.isAlignAll());
						}
					}
				}

				@Override
				public void keyReleased(IKeyEvent e) {

				}
			});
			try {
				createModel().apply(table);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public ASerializedView getSerializableRepresentation() {
			return new SView();
		}

		@Override
		protected GLElement createRoot() {
			RankTableUI root = new RankTableUI();
			root.init(table, RankTableUIConfigs.DEFAULT, RowHeightLayouts.FISH_EYE, RowHeightLayouts.UNIFORM);
			return root;
		}
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SView();
	}



	@XmlRootElement
	public static class SView extends ASerializedView {
		@Override
		public String getViewType() {
			return "";
		}
	}

}
