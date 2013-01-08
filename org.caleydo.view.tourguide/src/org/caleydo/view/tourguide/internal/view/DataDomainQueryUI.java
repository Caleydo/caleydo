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
package org.caleydo.view.tourguide.internal.view;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createLabel;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSpacer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.Dims;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.api.query.DataDomainQuery;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.query.filter.SpecificDataDomainFilter;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.event.SelectDimensionSelectionEvent;
import org.caleydo.view.tourguide.internal.renderer.AdvancedTextureRenderer;
import org.caleydo.view.tourguide.internal.renderer.DecorationTextureRenderer;
import org.caleydo.view.tourguide.internal.score.ExternalGroupLabelScore;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.spi.query.filter.IDataDomainFilter;

/**
 * TODO: support filter, ala cutoff or categorical levels
 *
 * @author Samuel Gratzl
 *
 */
public class DataDomainQueryUI extends Column {
	private static final String TOGGLE_DATA_DOMAIN = "TOGGLE_DATA_DOMAIN";

	private static final int COL0_BUTTON = 20;
	private static final int COL1_DATADOMAIN_TYPE = 16;
	private static final int COL3_NAME = 100;
	private static final int ROW_HEIGHT = 18;

	private final List<DataDomainRow> rows = new ArrayList<>();
	private final AGLView view;

	private final ElementLayout colSpacer = createXSpacer(3);

	private PropertyChangeListener selectionListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onSelectionChanged((IDataDomain) evt.getOldValue(), (IDataDomain) evt.getNewValue());
		}
	};
	private PropertyChangeListener filterListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onFilterChanged();
		}
	};

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
				onRightClicked(rows.get(pick.getObjectID()));
			}
		}, TOGGLE_DATA_DOMAIN);
		view.addTypePickingTooltipListener("Left-Click: Add/Remove to Query \nRight-Click: Open Context Menu",
				TOGGLE_DATA_DOMAIN);
		init();
	}

	public void init() {
		this.setGrabX(true);
		this.setBottomUp(false);

		final ElementLayout rowSpace = createYSpacer(3);

		int w = 0;

		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			Row row = new Row();
			addAll(rowSpace, mode.getAllDataDomains(), row);
			this.add(row);
			w += row.getPixelSizeY();
		}
		setPixelSizeY(w);
	}

	private void addAll(final ElementLayout rowSpace, Iterable<? extends IDataDomain> dataDomains, Row row) {
		row.setGrabX(true);

		Column numerical = new Column();
		numerical.setBottomUp(false);
		numerical.setPixelSizeX(COL0_BUTTON + 3 + COL1_DATADOMAIN_TYPE + 3 + COL3_NAME);
		numerical.setYDynamic(true);
		numerical.add(createYSpacer(20));
		row.add(numerical);
		int num = 0;

		Column categorical = new Column();
		categorical.setBottomUp(false);
		categorical.setGrabX(true);
		categorical.setYDynamic(true);
		categorical.add(createYSpacer(20));
		int cat = 0;
		row.add(categorical);

		int i = rows.size();
		for (IDataDomain dataDomain : dataDomains) {
			DataDomainRow r = new DataDomainRow(view, dataDomain, i++);
			rows.add(r);
			if (DataDomainOracle.isCategoricalDataDomain(dataDomain)) {
				categorical.add(r).add(rowSpace);
				cat++;
			} else {
				numerical.add(r).add(rowSpace);
				num++;
			}
		}
		row.setPixelSizeY(20 + Math.max(cat, num) * (3 + ROW_HEIGHT));
	}

	public void setQuery(DataDomainQuery query) {
		if (this.query != null) {
			this.query.removePropertyChangeListener(DataDomainQuery.PROP_SELECTION, selectionListener);
			this.query.removePropertyChangeListener(DataDomainQuery.PROP_FILTER, filterListener);
		}
		this.query = query;
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_SELECTION, selectionListener);
		this.query.addPropertyChangeListener(DataDomainQuery.PROP_FILTER, filterListener);
		Collection<IDataDomain> current = query.getSelection();

		for (DataDomainRow row : rows) {
			row.setSelected(current.contains(row.dataDomain));
			for (IDataDomainFilter f : query.getFilter()) {
				if (f instanceof SpecificDataDomainFilter
						&& ((SpecificDataDomainFilter) f).getDataDomain().equals(row.dataDomain)) {
					row.setFilter((SpecificDataDomainFilter) f);
					break;
				}
			}
		}
		if (this.layoutManager != null)
			updateSubLayout();
	}

	protected void onSelectionChanged(IDataDomain oldValue, IDataDomain newValue) {
		if (oldValue != null)
			getRow(oldValue).setSelected(false);
		if (newValue != null)
			getRow(newValue).setSelected(true);
	}

	protected void onFilterChanged() {
		for (DataDomainRow row : this.rows) {
			for (IDataDomainFilter f : query.getFilter()) {
				if (f instanceof SpecificDataDomainFilter
						&& ((SpecificDataDomainFilter) f).getDataDomain().equals(row.dataDomain)) {
					row.setFilter((SpecificDataDomainFilter) f);
					break;
				}
			}
		}
	}

	private DataDomainRow getRow(IDataDomain d) {
		for (DataDomainRow r : rows)
			if (r.dataDomain.equals(d))
				return r;
		return null;
	}

	public DataDomainQuery getQuery() {
		return query;
	}

	protected void toggleSelection(DataDomainRow dataDomainRow) {
		if (query == null)
			return;
		if (!dataDomainRow.isSelected())
			query.addSelection(dataDomainRow.dataDomain);
		else
			query.removeSelection(dataDomainRow.dataDomain);
	}

	protected void onRightClicked(DataDomainRow dataDomainRow) {
		ContextMenuCreator creator = view.getContextMenuCreator();
		if (dataDomainRow.getFilter() != null)
			creator.addContextMenuItem(new GenericContextMenuItem("Edit Filter", new EditDataDomainFilterEvent(dataDomainRow.getFilter(), this)));
		Collection<DimensionPerspective> dims = DataDomainQuery
				.getPossibleDimensionPerspectives(dataDomainRow.dataDomain);
		if (!dims.isEmpty()) {
			DimensionPerspective dim = query.getDimensionSelection(dataDomainRow.dataDomain);
			if (dim == null)
				dim = dims.iterator().next();
			GroupContextMenuItem item = new GroupContextMenuItem("Used Dimension Perspective");
			creator.addContextMenuItem(item);
			for (DimensionPerspective d : dims)
				item.add(new GenericContextMenuItem(d.getLabel(), EContextMenuType.CHECK, new SelectDimensionSelectionEvent(d,this)).setState(d == dim));
		}
		if (dataDomainRow.dataDomain instanceof ATableBasedDataDomain) {
			creator.addSeparator();
			ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dataDomainRow.dataDomain;
			creator.addContextMenuItem(new GenericContextMenuItem("Load Scoring for "
					+ dataDomain.getDimensionIDCategory().getCategoryName(), new ImportExternalScoreEvent(dataDomain,
					true, ExternalIDTypeScore.class)));
			creator.addContextMenuItem(new GenericContextMenuItem("Load Scoring for "
					+ dataDomain.getRecordIDCategory().getCategoryName(), new ImportExternalScoreEvent(dataDomain,
					false, ExternalIDTypeScore.class)));
			creator.addContextMenuItem(new GenericContextMenuItem("Load Grouping Scoring for "
					+ dataDomain.getDimensionIDCategory().getCategoryName(), new ImportExternalScoreEvent(dataDomain,
					true, ExternalGroupLabelScore.class)));
			creator.addContextMenuItem(new GenericContextMenuItem("Load Grouping Scoring for "
					+ dataDomain.getRecordIDCategory().getCategoryName(), new ImportExternalScoreEvent(dataDomain,
					false, ExternalGroupLabelScore.class)));
		}
	}

	private class DataDomainRow extends Row {
		private final IDataDomain dataDomain;
		private final ElementLayout button;
		private SpecificDataDomainFilter filter;

		public DataDomainRow(AGLView view, IDataDomain dataDomain, int i) {
			this.dataDomain = dataDomain;

			this.setGrabX(true);
			this.setPixelSizeY(ROW_HEIGHT);

			this.button = new ElementLayout("dataSetButtonLayout");
			button.setPixelSizeX(COL0_BUTTON);
			button.setRenderer(new AdvancedTextureRenderer(TourGuideRenderStyle.ICON_ACCEPT_DISABLE, view
					.getTextureManager()));
			button.addForeGroundRenderer(new DecorationTextureRenderer(null, view.getTextureManager(), Dims.xpixel(10),
					Dims.ypixel(10),
					HAlign.BOTTOM, VAlign.RIGHT));

			this.append(button);
			this.append(colSpacer);
			this.append(createColor(dataDomain.getColor(), COL1_DATADOMAIN_TYPE));
			this.append(colSpacer);
			this.append(createLabel(view, dataDomain, COL3_NAME));
			this.addBackgroundRenderer(new PickingRenderer(TOGGLE_DATA_DOMAIN, i, view));
		}

		/**
		 * @param filter
		 *            the filter to set
		 */
		public void setFilter(SpecificDataDomainFilter filter) {
			this.filter = filter;
			setHasFilter(!filter.isEmpty());
		}

		/**
		 * @return the filter, see {@link #filter}
		 */
		public SpecificDataDomainFilter getFilter() {
			return filter;
		}

		public boolean isSelected() {
			return TourGuideRenderStyle.ICON_ACCEPT.equals(getButtonRenderer().getImagePath());
		}

		private AdvancedTextureRenderer getButtonRenderer() {
			return (AdvancedTextureRenderer) button.getRenderer();
		}

		public void setSelected(boolean selected) {
			AdvancedTextureRenderer buttonRenderer = getButtonRenderer();
			if (selected)
				buttonRenderer.setImagePath(TourGuideRenderStyle.ICON_ACCEPT);
			else
				buttonRenderer.setImagePath(TourGuideRenderStyle.ICON_ACCEPT_DISABLE);
		}

		public void setHasFilter(boolean hasFilter) {
			DecorationTextureRenderer m = (DecorationTextureRenderer) button.getForegroundRenderer().get(0);
			if (hasFilter)
				m.setImagePath(TourGuideRenderStyle.ICON_FILTER);
			else
				m.setImagePath(null);
		}

	}
}
