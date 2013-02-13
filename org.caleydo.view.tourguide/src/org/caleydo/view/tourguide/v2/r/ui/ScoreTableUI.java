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
package org.caleydo.view.tourguide.v2.r.ui;

import gleem.linalg.Vec2f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.v2.r.model.ScoreColumn;
import org.caleydo.view.tourguide.v2.r.model.ScoreTable;
import org.caleydo.view.tourguide.v2.r.ui.RowHeightLayouts.IRowHeightLayout;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreTableUI extends GLElementContainer implements IGLLayout {
	private static final int COMBINED_HEADER = 0;
	private static final int ALIGN_SEPARATOR = 1;

	private int first_item = 2;

	private final ScoreTable table;
	private final IRowHeightLayout rowLayout = RowHeightLayouts.LINEAR;
	private int combinedAlign = 0;

	private int[] pickingIDs = null;
	private IPickingListener selectRowListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onRowPick(pick.getObjectID(), pick);
		}
	};

	public ScoreTableUI(ScoreTable table) {
		this.table = table;
		final PropertyChangeListener layoutOnChange = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				layout();
				repaint();
			}
		};
		this.table.addPropertyChangeListener(ScoreTable.PROP_SELECTED_ROW, layoutOnChange);
		this.table.addPropertyChangeListener(ScoreTable.PROP_ORDER, layoutOnChange);
		for (ScoreColumn col : table.getVisibleColumns()) {
			col.addPropertyChangeListener(ScoreColumn.PROP_WEIGHT, layoutOnChange);
		}
		setLayout(this);

		createItems();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		this.pickingIDs = new int[this.table.getNumRows()];
		for (int i = 0; i < this.pickingIDs.length; ++i)
			this.pickingIDs[i] = context.registerPickingListener(selectRowListener, i);
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(selectRowListener);
		this.pickingIDs = null;
		super.takeDown();
	}

	private void createItems() {
		float w = 0;
		this.add(new ScoreCombinedHeaderItemUI(table));
		int combinedSize = table.getCombinedColumns().size();
		for (int i = 0; i <= table.getNumVisibleColumns(); ++i) {
			if (i == combinedAlign)
				this.add(new AlignSeparatorUI(i));
			else if (i < combinedSize)
				this.add(new CombinedSeparatorUI(i));
			else
				this.add(new SeparatorUI(i));
		}
		this.add(new SeparatorUI(-combinedSize)); // one for the first of the extra columns

		this.first_item = 2 + table.getNumVisibleColumns() + 1;

		this.add(new GLElement());
		for (ScoreColumn col : table.getVisibleColumns()) {
			this.add(new ScoreHeaderItemUI(col));
			w += col.getWeight() + 3;
		}
		setSize(w, Float.NaN);

		for (int i = 0; i < table.getNumRows(); ++i) {
			this.add(new RowLabelUI(table, i));
			for (ScoreColumn col : table.getVisibleColumns()) {
				add(new ScoreItemUI(table, col, i));
			}
		}
	}

	public void moveColumnNextTo(ScoreColumn column, int index) {
		int source = table.moveColumn(index, column);
		if (source < 0) // invalid operation
			return;
		if (source < index) // 1. remove -> index will change to -1
			index--;
		// update column from source to index
		for (int i = -1; i < table.getNumRows(); ++i) {
			List<GLElement> row = this.row(i);
			row.add(index + 1, row.get(source + 1));
		}
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		System.out.println("compute layout");
		float[] rowHeights = rowLayout.compute(table.getNumRows(), table.getSelectedRow(), h);

		final float labelWidth = 100;
		final float th_height = 60;

		{ // combined header
			final IGLLayoutElement combined = children.get(COMBINED_HEADER);
			float wc = 0;
			for (ScoreColumn col : table.getCombinedColumns())
				wc += col.getWeight() + 3;
			combined.setBounds(labelWidth, 0, wc - 3, 40);
		}

		float x = labelWidth + 3;
		float y = 40;
		{ // separators
			List<? extends IGLLayoutElement> separators = children.subList(2, first_item);
			Iterator<? extends IGLLayoutElement> it = separators.iterator();

			float x_act = x;
			for (ScoreColumn col : table.getCombinedColumns()) {
				IGLLayoutElement elem = it.next();
				elem.setBounds(x_act - 3, y, 3, h);
				x_act += col.getWeight() + 3;
			}

		}

		{ // headers
			Iterator<? extends IGLLayoutElement> it = children.subList(first_item + 1,
					table.getNumVisibleColumns() + first_item + 1)
					.iterator();
			for (ScoreColumn col : table.getVisibleColumns()) {
				it.next().setBounds(x, y, col.getWeight(), th_height);
				x += col.getWeight() + 3;
			}
			x -= 3;
		}

		{// items
			Iterator<? extends IGLLayoutElement> it = children.subList(first_item + table.getNumVisibleColumns() + 1,
					children.size())
					.iterator();
			y += th_height;
			for (int i = 0; i < table.getNumRows(); ++i) {
				if (i >= rowHeights.length) // hide rest
					break;
				float rowHeight = rowHeights[i];

				float combinedOffset = 0;
				it.next().setBounds(0, y, labelWidth, rowHeight); // label
				x = labelWidth + 3;
				for (ScoreColumn col : table.getCombinedColumns()) {
					IGLLayoutElement elem = it.next();
					float v = col.weight(col.getNormalized(i).asFloat());
					elem.setBounds(x, y, v, rowHeight);
					x += v + 3;
					combinedOffset += col.getWeight();
				}
				x = labelWidth + 3 + combinedOffset;
				for (ScoreColumn col : table.getExtraColumns()) {
					IGLLayoutElement elem = it.next();
					float v = col.weight(col.getNormalized(i).asFloat());
					elem.setBounds(x, y, v, rowHeight);
					x += col.getWeight() + 3;
				}
				y += rowHeight + 2;
			}
			// hide rest
			while (it.hasNext()) {
				it.next().setSize(0, 0);
			}
		}
		return false;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final GL2 gl = g.gl;
		final float z = g.z();

		List<GLElement> header = header();

		// render a highlight of all combined columns
		{
			Vec2f topleft = header.get(1).getLocation();
			GLElement last = header.get(table.getCombinedColumns().size());
			Vec2f topright = last.getLocation().plus(last.getSize());
			g.color(0.95f, 0.95f, 0.95f)
					.fillRect(topleft.x(), topleft.y(), topright.x() - topleft.x(), h - topleft.y());
		}

		// render the quad strip of between the columns
		int coli = 0;
		for (ScoreColumn c : this.table.getVisibleColumns()) {
			g.color(c.getBackgroundColor());
			GLElement th = header.get(coli + 1);
			List<GLElement> col = this.col(coli);
			gl.glBegin(GL2.GL_QUAD_STRIP);
			Vec2f xy = th.getLocation();
			Vec2f wh = th.getSize();
			gl.glVertex3f(xy.x(), xy.y() + wh.y(), z);
			gl.glVertex3f(xy.x() + wh.x(), xy.y() + wh.y(), z);
			for (GLElement elem : col) {
				xy = elem.getLocation();
				wh = elem.getSize();
				if (wh.x() <= 0)
					break;
				gl.glVertex3f(xy.x(), xy.y() + 2, z);
				gl.glVertex3f(xy.x() + wh.x(), xy.y() + 2, z);
				gl.glVertex3f(xy.x(), xy.y() + wh.y() - 2, z);
				gl.glVertex3f(xy.x() + wh.x(), xy.y() + wh.y() - 2, z);
			}
			g.gl.glEnd();
			coli++;
		}

		// highlight selected row
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			GLElement row = get(selectedRow, 0);
			g.color(Color.YELLOW).fillRect(0, row.getLocation().y(), w, row.getSize().y());
		}

		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		List<GLElement> col0 = rowHeader();
		for (int i = 0; i < col0.size(); ++i) {
			g.pushName(pickingIDs[i]);
			GLElement r = col0.get(i);
			g.fillRect(r.getLocation().x(), r.getLocation().y(), w, r.getSize().y());
			g.popName();
		}
		super.renderPickImpl(g, w, h);
	}

	private GLElement get(int row, int col) {
		row++; // skip header
		col++; // skip label
		int cols = table.getNumVisibleColumns() + 1;
		return get(first_item + row * cols + col);
	}

	private List<GLElement> row(int row) {
		row++; // skip header
		int cols = table.getNumVisibleColumns() + 1;
		List<GLElement> l = this.asList();
		return l.subList(first_item + row * cols, first_item + (row + 1) * cols);
	}

	private List<GLElement> header() {
		return row(-1);
	}

	private List<GLElement> rowHeader() {
		return col(-1);
	}

	private List<GLElement> col(final int col) {
		return new AbstractList<GLElement>() {
			@Override
			public GLElement get(int index) {
				return ScoreTableUI.this.get(index, col);
			}

			@Override
			public int size() {
				return table.getNumRows();
			}
		};
	}

	/**
	 * @param objectID
	 * @param pick
	 */
	protected void onRowPick(int row, Pick pick) {
		if (pick.getPickingMode() == PickingMode.CLICKED)
			table.setSelectedRow(row);
	}

	public static void main(String[] args) {
		GLSandBox.main(args, new ScoreTableUI(ScoreTable.demo()));
	}

	/**
	 * @param index
	 */
	public void alignCombined(int index) {
		// TODO Auto-generated method stub

	}
}