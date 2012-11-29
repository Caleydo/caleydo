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
package org.caleydo.view.tourguide.vendingmachine;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createLabel;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSpacer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tourguide.data.DataDomainQuery;
import org.caleydo.view.tourguide.event.ImportExternalScoreEvent;

/**
 * TODO: support filter, ala cutoff or categorical levels
 *
 * @author Samuel Gratzl
 *
 */
public class DataDomainQueryUI extends Column {
	private static final String TOGGLE_DATA_DOMAIN = "TOGGLE_DATA_DOMAIN";
	private static final String DATADOMAIN_SELECTION = "DATADOMAIN_SELECTION";
	private static final int COL0_BUTTON = 20;
	private static final int COL1_DATADOMAIN_TYPE = 16;
	private static final int COL3_NAME = -1;
	private static final int ROW_HEIGHT = 18;

	private final List<DataDomainRow> rows = new ArrayList<>();
	private final AGLView view;

	private final ElementLayout colSpacer = createXSpacer(3);

	private DataDomainQuery query;

	public DataDomainQueryUI(AGLView view) {
		this.view = view;
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				toggleSelection(rows.get(pick.getObjectID()));
			}

			@Override
			public void rightClicked(Pick pick) {
				toggleFilteredSelection(rows.get(pick.getObjectID()));
			}
		}, TOGGLE_DATA_DOMAIN);
		view.addTypePickingTooltipListener("Toggle consider this Data Domain for scoring", TOGGLE_DATA_DOMAIN);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void rightClicked(Pick pick) {
				onOpenDataDomainContextMenu(rows.get(pick.getObjectID()));
			}
		}, DATADOMAIN_SELECTION);
		init();
	}


	public void init() {
		this.setBottomUp(false);
		setGrabX(true);
		setYDynamic(true);

		final ElementLayout rowSpace = createYSpacer(3);

		this.add(createYSpacer(20));

		int i = 0;
		for (ATableBasedDataDomain dataDomain : DataDomainQuery.allDataDomains()) {
			DataDomainRow r = new DataDomainRow(view, dataDomain, i++);
			rows.add(r);
			this.add(r).add(rowSpace);
		}
	}

	public void setQuery(DataDomainQuery query) {
		this.query = query;
		Collection<ATableBasedDataDomain> current = query.getSelection();
		for (DataDomainRow row : rows)
			row.setSelected(current.contains(row.dataDomain));
		if (this.layoutManager != null)
			updateSubLayout();
	}

	public DataDomainQuery getQuery() {
		return query;
	}

	protected void toggleSelection(DataDomainRow dataDomainRow) {
		dataDomainRow.toggleSelected();
		if (query == null)
			return;
		if (dataDomainRow.isSelected())
			query.addSelection(dataDomainRow.dataDomain);
		else
			query.removeSelection(dataDomainRow.dataDomain);
	}

	protected void toggleFilteredSelection(DataDomainRow dataDomainRow) {
		// TODO Auto-generated method stub
	}

	protected void onOpenDataDomainContextMenu(DataDomainRow dataDomainRow) {
		ContextMenuCreator creator = view.getContextMenuCreator();
		ATableBasedDataDomain dataDomain = dataDomainRow.dataDomain;
		for (IDCategory cat : Arrays.asList(dataDomain.getDimensionIDCategory(), dataDomain.getRecordIDCategory())) {
			creator.addContextMenuItem(new GenericContextMenuItem("Load Scoring for "
					+ cat.getCategoryName(), new ImportExternalScoreEvent(cat, dataDomain)));
		}
	}

	private class DataDomainRow extends Row {
		private final Button button;
		private final ATableBasedDataDomain dataDomain;

		public DataDomainRow(AGLView view, ATableBasedDataDomain dataDomain, int i) {
			this.dataDomain = dataDomain;
			this.button = new Button(TOGGLE_DATA_DOMAIN, i, EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);
			this.setGrabX(true);
			this.setPixelSizeY(ROW_HEIGHT);

			this.append(createToggleButton(view));
			this.append(colSpacer);
			this.append(createColor(dataDomain.getColor(), COL1_DATADOMAIN_TYPE));
			this.append(colSpacer);
			ElementLayout l = createLabel(view, dataDomain, COL3_NAME);
			l.addBackgroundRenderer(new PickingRenderer(DATADOMAIN_SELECTION, i, view));
			this.append(l);
		}


		private ElementLayout createToggleButton(AGLView view) {
			ElementLayout elem = new ElementLayout("dataSetButtonLayout");
			elem.setPixelSizeX(COL0_BUTTON);
			elem.setRenderer(new ButtonRenderer(button, view).setZCoordinate(1));
			return elem;
		}

		public boolean isSelected() {
			return button.isSelected();
		}
		public void toggleSelected() {
			button.setSelected(!isSelected());
		}

		public void setSelected(boolean selected) {
			button.setSelected(selected);
		}

	}
}
