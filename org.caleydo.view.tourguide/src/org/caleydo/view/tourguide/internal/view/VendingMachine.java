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
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
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
import org.caleydo.core.view.opengl.layout2.LayoutRendererAdapter;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.api.query.DataDomainQuery;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.internal.Activator;
import org.caleydo.view.tourguide.internal.SerializedTourGuideView;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.external.ImportExternalScoreCommand;
import org.caleydo.view.tourguide.internal.renderer.DecorationTextureRenderer;
import org.caleydo.view.tourguide.v2.r.model.ScoreTable;
import org.caleydo.view.tourguide.v2.r.ui.ScoreTableUI;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
	private DataDomainQueryUI dataDomainQueryUI;
	private final DragAndDropController dndController;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private StratomexAdapter stratomex = new StratomexAdapter();
	private DataDomainQuery dataDomainQuery;
	private ScoreQuery scoreQuery;

	private CaleydoTextRenderer textLargeRenderer;

	private IGLKeyListener keyListener;

	private boolean computing = false;

	private final PropertyChangeListener recomputeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			recomputeScores();
		}
	};
	private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() == null)
				onHideDataDomain((IDataDomain) evt.getOldValue());
			else
				onShowDataDomain((IDataDomain) evt.getNewValue());
		}
	};

	public VendingMachine(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		// override with a custom texture loader
		super.textureManager = new TextureManager(Activator.getResourceLoader());

		dndController = new DragAndDropController(this);
	}

	public void setQuery(ScoreQuery query) {
		if (this.scoreQuery != null) {
			scoreQuery.removePropertyChangeListener(recomputeListener);
		}
		if (this.dataDomainQuery != null) {
			dataDomainQuery.removePropertyChangeListener(DataDomainQuery.PROP_SELECTION, selectionListener);
			dataDomainQuery.removePropertyChangeListener(DataDomainQuery.PROP_FILTER, recomputeListener);
			dataDomainQuery.removePropertyChangeListener(DataDomainQuery.PROP_DIMENSION_SELECTION, recomputeListener);
		}
		this.scoreQuery = query;
		scoreQuery.addPropertyChangeListener(recomputeListener);

		this.dataDomainQuery = this.scoreQuery.getQuery();

		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_SELECTION, selectionListener);
		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_FILTER, recomputeListener);
		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_DIMENSION_SELECTION, recomputeListener);

		if (this.scoreQueryUI != null)
			this.scoreQueryUI.setQuery(scoreQuery);
		if (this.dataDomainQueryUI != null)
			this.dataDomainQueryUI.setQuery(dataDomainQuery);
	}


	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);


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

		if (scoreQuery == null)
			setQuery(new ScoreQuery(new DataDomainQuery()));

		mainColumn.append(ElementLayouts.create().height(300).width(200)
				.render(new LayoutRendererAdapter(this, Activator.getResourceLocator(), new ScoreTableUI(ScoreTable
						.demo()))).build());


		dataDomainQueryUI = new DataDomainQueryUI(this);
		dataDomainQueryUI.setQuery(dataDomainQuery);
		listeners.register(dataDomainQueryUI);


		mainColumn.append(dataDomainQueryUI);

		mainColumn.append(ElementLayouts.createYSpacer(20));

		scoreQueryUI = new ScoreQueryUI(this, this.stratomex, dndController);
		scoreQueryUI.setQuery(scoreQuery);
		listeners.register(scoreQueryUI);

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

		dndController.handleDragging(gl, glMouseListener);
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
		listeners.register(this);
		listeners.register(stratomex);
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
		layoutManager.destroy(gl);

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

	@ListenTo
	void onScoreQueryReady(ScoreQueryReadyEvent event) {
		if (event.getSender() != getScoreQuery())
			return;
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



	@ListenTo
	void onImportExternalScore(ImportExternalScoreEvent event) {
		if (event.getSender() != getDataDomainQueryUI())
			return;
		Display.getDefault().asyncExec(
				new ImportExternalScoreCommand(event.getDataDomain(), event.isInDimensionDirection(), event.getType(),
						scoreQueryUI));
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
	@ListenTo
	private void onStratomexRemoveBrick(RemoveTablePerspectiveEvent event) {
		if (!stratomex.is(event.getReceiver()))
			return;
		stratomex.removeBrick(event.getTablePerspectiveID());
		final ScoringElement selected = this.scoreQueryUI.getSelected();
		if (selected != null && selected.getPerspective() != null
				&& selected.getPerspective().getID() == event.getTablePerspectiveID()) {
			this.scoreQueryUI.setSelected(-1);
		}
		this.scoreQueryUI.updateAddToStratomexState();
	}

	@ListenTo
	void onStratomexAddBricks(AddTablePerspectivesEvent event) {
		if (!stratomex.is(event.getReceiver()))
			return;
		stratomex.addBricks(event.getTablePerspectives());
		this.scoreQueryUI.updateAddToStratomexState();
	}

	@ListenTo
	void onStratomexReplaceBricks(ReplaceTablePerspectiveEvent event) {
		if (!stratomex.is(event.getViewID()))
			return;
		stratomex.replaceBricks(event.getOldPerspective(), event.getNewPerspective());
	}


	public ScoreQuery getScoreQuery() {
		return scoreQuery;
	}

	public DataDomainQueryUI getDataDomainQueryUI() {
		return this.dataDomainQueryUI;
	}

	/**
	 * @param view
	 */
	public void cloneFrom(VendingMachine view) {
		this.setQuery(view.getScoreQuery().clone());
		this.switchToStratomex(view.stratomex.get());
		this.recomputeScores();
	}

	public DataDomainQuery getDataDomainQuery() {
		return dataDomainQuery;
	}

	public void attachToStratomex() {
		this.stratomex.attach();
	}

	public void detachFromStratomex() {
		this.stratomex.detach();
	}

}
