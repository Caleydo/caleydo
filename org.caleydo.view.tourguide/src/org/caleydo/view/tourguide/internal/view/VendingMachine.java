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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListeners;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.gui.util.RenameNameDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout.Dims;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row.HAlign;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.api.query.DataDomainQuery;
import org.caleydo.view.tourguide.api.query.ESorting;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.api.query.filter.CompareScoreFilter;
import org.caleydo.view.tourguide.api.query.filter.ECompareOperator;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.SerializedTourGuideView;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.CreateScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.CreateScoreEvent;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.RenameScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.event.ToggleNaNFilterScoreColumnEvent;
import org.caleydo.view.tourguide.internal.external.ImportExternalScoreCommand;
import org.caleydo.view.tourguide.internal.listener.CreateScoreListener;
import org.caleydo.view.tourguide.internal.listener.ImportExternalScoreListener;
import org.caleydo.view.tourguide.internal.listener.ScoreColumnListener;
import org.caleydo.view.tourguide.internal.listener.ScoreQueryReadyListener;
import org.caleydo.view.tourguide.internal.listener.StratomexTablePerspectiveListener;
import org.caleydo.view.tourguide.internal.renderer.DecorationTextureRenderer;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.spi.query.filter.IScoreFilter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * <p>
 * The vending machine for stratification and cluster comparisons using scoring approach.
 * </p>
 *
 * @author Marc Streit
 */

public class VendingMachine extends AGLView implements IGLRemoteRenderingView, ILayoutedElement {
	public static final String VIEW_TYPE = "org.caleydo.view.tool.tourguide";
	public static final String VIEW_NAME = "Tour Guide";


	private LayoutManager layoutManager;
	private Column mainColumn;
	private ScoreQueryUI scoreQueryUI;
	private DataDomainQueryUI dataDomainSelector;

	private final EventListeners listeners = new EventListeners();

	private StratomexAdapter stratomex = new StratomexAdapter();
	private DataDomainQuery dataDomainQuery = new DataDomainQuery();
	private ScoreQuery scoreQuery = new ScoreQuery(dataDomainQuery);

	private CaleydoTextRenderer textLargeRenderer;

	private IGLKeyListener keyListener;

	private boolean computing = false;

	public VendingMachine(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_SELECTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == null)
					onHideDataDomain((IDataDomain) evt.getOldValue());
				else
					onShowDataDomain((IDataDomain) evt.getNewValue());
			}
		});
		final PropertyChangeListener recomputeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				recomputeScores();
			}
		};
		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_FILTER, recomputeListener);
		scoreQuery.addPropertyChangeListener(recomputeListener);
	}


	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		textRenderer = new CaleydoTextRenderer(12);
		textLargeRenderer = new CaleydoTextRenderer(24);
		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);


		initLayouts();
		keyListener = new SelectChangeKeyListener(this.scoreQueryUI);
		this.parentGLCanvas.addKeyListener(keyListener);
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);

		if (busyState == EBusyState.ON) {
			renderBusyMode(gl);
		} else {
			display(gl);
		}
	}

	public void initLayouts() {
		mainColumn = new Column("mainColumn");
		mainColumn.setGrabX(true);
		mainColumn.setGrabY(true);
		mainColumn.setBottomUp(false);

		layoutManager.setBaseElementLayout(mainColumn);
		layoutManager.setUseDisplayLists(true);

		dataDomainSelector = new DataDomainQueryUI(this);
		dataDomainSelector.setQuery(dataDomainQuery);
		mainColumn.append(dataDomainSelector);

		mainColumn.append(ElementLayouts.createYSpacer(20));

		scoreQueryUI = new ScoreQueryUI(this, this.stratomex);
		scoreQueryUI.setQuery(scoreQuery);
		mainColumn.append(ElementLayouts.scrollAlbe(this, scoreQueryUI));
		// mainColumn.append(scoreQueryUI);

		mainColumn.get(mainColumn.size() - 1).addForeGroundRenderer(
				new DecorationTextureRenderer(null, this.textureManager, Dims.xpixel(100), Dims.ypixel(100),
						HAlign.CENTER, VAlign.CENTER));

		layoutManager.updateLayout();
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {
		if (!stratomex.hasOne()) {
			CaleydoTextRenderer tmp = textRenderer;
			textRenderer = textLargeRenderer; // overwrite for large font
			renderEmptyViewText(gl, "No Active Stratomex");
			textRenderer = tmp;
			return;
		}

		stratomex.sendDelayedEvents();

		checkForHits(gl);

		processEvents();

		layoutManager.render(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTourGuideView();
	}

	@Override
	public String toString() {
		return "TourGuide";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		listeners.register(CreateScoreEvent.class, new CreateScoreListener(this));
		listeners.register(new ScoreColumnListener(this), AddScoreColumnEvent.class, RenameScoreColumnEvent.class,
				RemoveScoreColumnEvent.class, CreateScoreColumnEvent.class, ToggleNaNFilterScoreColumnEvent.class);
		listeners.register(ScoreQueryReadyEvent.class, new ScoreQueryReadyListener(this));
		listeners.register(ImportExternalScoreEvent.class, new ImportExternalScoreListener(this));
		listeners.register(new StratomexTablePerspectiveListener(this), AddTablePerspectivesEvent.class, RemoveTablePerspectiveEvent.class, ReplaceTablePerspectiveEvent.class);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		listeners.unregisterAll();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return null;
	}


	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		this.stratomex.cleanUp();

		if (keyListener != null)
			this.parentGLCanvas.removeKeyListener(keyListener);
	}

	@Override
	public ElementLayout getLayout() {
		return mainColumn;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		layoutManager.updateLayout();
	}

	/**
	 * @param stratomex
	 */
	public void switchToStratomex(GLStratomex stratomex) {
		if (this.stratomex.setStratomex(stratomex))
			setDisplayListDirty();
	}

	private void recomputeScores() {
		if (scoreQuery.isJobRunning()) {
			if (!this.computing) {
				this.computing = true;
				getComputeDecoration().setImagePath(EIconTextures.LOADING_CIRCLE.getFileName());
			}
		} else {
			GeneralManager.get().getEventPublisher().triggerEvent(new ScoreQueryReadyEvent(this.scoreQuery));
		}

	}

	private DecorationTextureRenderer getComputeDecoration() {
		DecorationTextureRenderer deco = (DecorationTextureRenderer) this.mainColumn.get(this.mainColumn.size() - 1)
				.getForegroundRenderer().get(0);
		return deco;
	}

	public void onScoreQueryReady() {
		if (this.computing) {
			this.computing = false;
			getComputeDecoration().setImagePath(null);
		}
		scoreQueryUI.setData(scoreQuery.call());
	}

	private void onShowDataDomain(IDataDomain dataDomain) {
		recomputeScores();
	}

	private void onHideDataDomain(IDataDomain dataDomain) {
		// boolean isCurrentlyVisible = false;
		// for (ScoringElement sp : scoreQueryUI.getData()) {
		// if (sp.getStratification().getDataDomain().equals(dataDomain)) {
		// isCurrentlyVisible = true;
		// break;
		// }
		// }
		// if (!isCurrentlyVisible) // if no element is currently visible with this data domain just ignore that change
		// return;
		// we have to update the list
		recomputeScores();
	}

	public void onAddColumn(IScore... scores) {
		this.onAddColumn(Arrays.asList(scores));
	}

	public void onAddColumn(Collection<IScore> scores) {
		this.scoreQuery.addSelection(Lists.newArrayList(Iterables.transform(scores, Scores.get().registerScores)));
	}

	public void onRemoveColumn(IScore score, boolean removeFromSystem) {
		scoreQuery.sortBy(score, ESorting.NONE);
		scoreQuery.removeSelection(score);
		if (removeFromSystem) {
			Scores.get().remove(score);
		}
	}

	public void onImportExternalScore(final ATableBasedDataDomain dataDomain, boolean dimensionDirection,
			Class<? extends ISerializeableScore> type) {
		Display.getDefault().asyncExec(
				new ImportExternalScoreCommand(dataDomain, dimensionDirection, type, scoreQueryUI));
	}

	/**
	 * @return
	 */
	public ScoreQueryUI getScoreQueryUI() {
		return scoreQueryUI;
	}

	/**
	 * @param receiver
	 * @param tablePerspectiveID
	 */
	public void onStratomexRemoveBrick(IMultiTablePerspectiveBasedView receiver, int tablePerspectiveID) {
		if (!stratomex.is(receiver))
			return;
		stratomex.removeBrick(tablePerspectiveID);
		final ScoringElement selected = this.scoreQueryUI.getSelected();
		if (selected != null && selected.getPerspective() != null
				&& selected.getPerspective().getID() == tablePerspectiveID) {
			this.scoreQueryUI.setSelected(-1);
		}
	}

	public void onStratomexAddBricks(ITablePerspectiveBasedView receiver, Collection<TablePerspective> tablePerspectives) {
		if (!stratomex.is(receiver))
			return;
		stratomex.addBricks(tablePerspectives);
	}

	public void onStratomexReplaceBricks(Integer receiver, TablePerspective oldPerspective,
			TablePerspective newPerspective) {
		if (!stratomex.is(receiver))
			return;
		stratomex.replaceBricks(oldPerspective, newPerspective);
	}

	public void onRename(final DefaultLabelProvider l) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				String r = RenameNameDialog.show(getParentComposite().getShell(), "Rename '" + l.getLabel() + "' to",
						l.getLabel());
				if (r != null) {
					l.setLabel(r);
					setDisplayListDirty();
				}
			}
		});
	}

	public void onToggleNaNFilter(IScore score) {
		for (IScoreFilter s : scoreQuery.getFilter()) {
			if (!(s instanceof CompareScoreFilter))
				continue;
			CompareScoreFilter cs = ((CompareScoreFilter) s);
			if (cs.getReference() == score && cs.getOp() == ECompareOperator.IS_NOT_NA) {
				// remove the filter
				scoreQuery.removeFilter(s);
				return;
			}
		}
		// wasn't there add the filter
		scoreQuery.addFilter(new CompareScoreFilter(score, ECompareOperator.IS_NOT_NA, 0.5f));
	}

	public ScoreQuery getScoreQuery() {
		return scoreQuery;
	}

}
