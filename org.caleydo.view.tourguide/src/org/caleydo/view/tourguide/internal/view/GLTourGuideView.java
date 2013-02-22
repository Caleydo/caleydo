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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.internal.SerializedTourGuideView;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.view.col.PerspectiveRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.view.tourguide.internal.view.model.DataDomainQueries;
import org.caleydo.view.tourguide.internal.view.ui.DataDomainQueryUI;
import org.caleydo.view.tourguide.v3.config.RankTableConfigBase;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts;
import org.caleydo.view.tourguide.v3.model.RankRankColumnModel;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;
import org.caleydo.view.tourguide.v3.ui.TableBodyUI;
import org.caleydo.view.tourguide.v3.ui.TableHeaderUI;

/**
 * @author Samuel Gratzl
 *
 */
public class GLTourGuideView extends AGLElementView implements IGLKeyListener {
	public static final String VIEW_TYPE = "org.caleydo.view.tool.tourguide";
	public static final String VIEW_NAME = "Tour Guide";

	private StratomexAdapter stratomex = new StratomexAdapter();
	private final RankTableModel table;

	private final DataDomainQueries dataDomainQueries;

	private StackedRankColumnModel stacked;

	public GLTourGuideView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);

		this.table = new RankTableModel(new RankTableConfigBase());
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((PerspectiveRow) evt.getOldValue(), (PerspectiveRow) evt.getNewValue());
			}
		});
		this.table.addPropertyChangeListener(RankTableModel.PROP_REGISTER, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() != null)
					eventListeners.unregister(evt.getOldValue());
				if (evt.getNewValue() != null)
					eventListeners.register(evt.getNewValue());
			}
		});

		this.table.addColumn(new RankRankColumnModel());
		this.table.addColumn(new PerspectiveRankColumnModel(stratomex));
		this.stacked = new StackedRankColumnModel();
		this.table.addColumn(stacked);
		this.table.addColumn(new SizeRankColumnModel());

		dataDomainQueries = new DataDomainQueries(table);
	}

	private TourGuideVis getVis() {
		return (TourGuideVis) getRoot();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		eventListeners.register(stratomex);
		this.canvas.addKeyListener(this);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		this.stratomex.cleanUp();
		canvas.removeKeyListener(this);
		super.dispose(drawable);
	}

	@Override
	protected GLElement createRoot() {
		return new TourGuideVis();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		stratomex.sendDelayedEvents();
		super.display(drawable);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTourGuideView();
	}

	protected void onSelectRow(PerspectiveRow old, PerspectiveRow new_) {
		// stratomex.updatePreview(old, new_, getVisibleColumns(new_), null);
	}

	@Override
	public void keyPressed(IKeyEvent e) {
		if (e.isKey(ESpecialKey.DOWN))
			table.selectNextRow();
		else if (e.isKey(ESpecialKey.UP))
			table.selectPreviousRow();
	}

	@Override
	public void keyReleased(IKeyEvent e) {

	}

	public void attachToStratomex() {
		this.stratomex.attach();
	}

	public void detachFromStratomex() {
		this.stratomex.detach();
	}

	public void switchToStratomex(GLStratomex stratomex) {
		if (this.stratomex.setStratomex(stratomex))
			repaint();
	}

	@ListenTo
	private void onStratomexRemoveBrick(RemoveTablePerspectiveEvent event) {
		if (!stratomex.is(event.getReceiver()))
			return;
		stratomex.removeBrick(event.getTablePerspectiveID());

		PerspectiveRow selected = (PerspectiveRow) table.getSelectedRow();
		if (selected != null && selected.getPerspective() != null
				&& selected.getPerspective().getID() == event.getTablePerspectiveID()) {
			this.table.setSelectedRow(-1);
		}

		// this.scoreQueryUI.updateAddToStratomexState();
	}

	// private void recomputeScores() {
	// if (scoreQuery.isJobRunning()) {
	// if (!this.computing) {
	// this.computing = true;
	// getComputeDecoration().setImagePath(EIconTextures.LOADING_CIRCLE.getFileName());
	// }
	// } else {
	// GeneralManager.get().getEventPublisher().triggerEvent(new ScoreQueryReadyEvent(this.scoreQuery));
	// }
	//
	// }
	//
	// private DecorationTextureRenderer getComputeDecoration() {
	// DecorationTextureRenderer deco = (DecorationTextureRenderer) this.mainColumn.get(this.mainColumn.size() - 1)
	// .getForegroundRenderer().get(0);
	// return deco;
	// }

	@ListenTo(sendToMe = true)
	void onImportExternalScore(ImportExternalScoreEvent event) {
		// Display.getDefault().asyncExec(
		// new ImportExternalScoreCommand(event.getDataDomain(), event.isInDimensionDirection(), event.getType(),
		// scoreQueryUI));
	}

	@ListenTo
	void onStratomexAddBricks(AddTablePerspectivesEvent event) {
		if (!stratomex.is(event.getReceiver()))
			return;
		stratomex.addBricks(event.getTablePerspectives());
		// this.scoreQueryUI.updateAddToStratomexState();
	}

	@ListenTo
	void onStratomexReplaceBricks(ReplaceTablePerspectiveEvent event) {
		if (!stratomex.is(event.getViewID()))
			return;
		stratomex.replaceBricks(event.getOldPerspective(), event.getNewPerspective());
	}

	public void cloneFrom(GLTourGuideView view) {
		// TODO Auto-generated method stub

	}



	private class TourGuideVis extends GLElementContainer {
		public TourGuideVis() {
			setLayout(GLLayouts.flowVertical(0));
			this.add(new DataDomainQueryUI(dataDomainQueries));
			this.add(new TableHeaderUI(table));
			this.add(new TableBodyUI(table, RowHeightLayouts.LINEAR));
		}
	}
}

