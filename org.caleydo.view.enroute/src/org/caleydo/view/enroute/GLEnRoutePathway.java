/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.virtualarray.events.PerspectiveUpdatedEvent;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.view.SetMinViewSizeEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepBasedEventFactory;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.event.FitToViewWidthEvent;
import org.caleydo.view.enroute.event.PathRendererChangedEvent;
import org.caleydo.view.enroute.event.ShowContextElementSelectionDialogEvent;
import org.caleydo.view.enroute.mappeddataview.ChooseContextDataDialog;
import org.caleydo.view.enroute.mappeddataview.MappedDataRenderer;
import org.caleydo.view.enroute.path.EnRoutePathRenderer;
import org.caleydo.view.enroute.path.SelectedPathUpdateStrategy;
import org.caleydo.view.pathway.GLPathway;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Main view class for the linearized pathway view.
 *
 * @author Christian Partl
 * @author Alexander Lex
 */

public class GLEnRoutePathway extends AGLView implements IMultiTablePerspectiveBasedView,
		IEventBasedSelectionManagerUser, IPathwayRepresentation {

	public static String VIEW_TYPE = "org.caleydo.view.enroute";
	public static String VIEW_NAME = "enRoute";

	protected final static String EMPTY_VIEW_TEXT_LINE_ONE = "Please select a path of nodes using the Pathway View ";
	protected final static String EMPTY_VIEW_TEXT_LINE_TWO = "and assign data to enRoute using the Data-View Integrator.";

	protected final static int DATA_COLUMN_WIDTH_PIXELS = 350;
	protected final static int TOP_SPACING_MAPPED_DATA = 10;
	protected final static int SIDE_SPACING_MAPPED_DATA = 10;
	protected final static int SPACING_PIXELS = 2;

	/**
	 * The top-level table perspectives as set externally through the {@link IMultiTablePerspectiveBasedView} interface.
	 */
	private ArrayList<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();

	/**
	 * The table perspectives resolved based on the {@link GroupList}s of the {@link #tablePerspectives}. That means
	 * that this list contains a tablePerspective for every experiment group in one of the TablePerspectives in
	 * {@link #tablePerspectives}.
	 */
	private ArrayList<TablePerspective> resolvedTablePerspectives = new ArrayList<TablePerspective>();

	/** A contextual perspective which has the same column ID Type but a different row ID Type */
	private TablePerspective contextualPerspective = null;

	/**
	 * The {@link IDataDomain}s for which data is displayed in this view.
	 */
	private Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();
	/**
	 * The renderer for the experimental data of the nodes in the linearized pathways.
	 */
	private MappedDataRenderer mappedDataRenderer;

	/**
	 * Determines whether the layout needs to be updated. This is a more severe update than only the display list
	 * update.
	 */
	private boolean isLayoutDirty = true;

	/**
	 * Determines whether a new path was set and has not been rendered yet.
	 */
	private boolean pathRendererChanged = true;

	/**
	 * The current minimum width in Pixels of this view.
	 */
	private int minWidth = 0;

	/**
	 * Determines, whether the rendered content is fit to the width of the view. (With an absolute view minimum
	 * remaining)
	 */
	private boolean fitToViewWidth = true;

	/** The id type of the rows that are shown in context with the selected path */
	private IDType primaryRowIDType = IDType.getIDType("DAVID");
	/** The id type of the columns for ALL datasets */
	private IDType columnIDType;

	/** The id type for the rows for contextual data */
	private IDType contextRowIDTYpe;

	private EventBasedSelectionManager geneSelectionManager;
	private EventBasedSelectionManager metaboliteSelectionManager;
	private EventBasedSelectionManager sampleSelectionManager;

	private AddTablePerspectivesListener addTablePerspectivesListener;
	private RemoveTablePerspectiveListener removeTablePerspectiveListener;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private int layoutDisplayListIndex = -1;

	private EnRoutePathRenderer pathRenderer;

	private LayoutManager layoutManager;

	private String pathwayPathEventSpace;

	private int minHeight = 0;

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLEnRoutePathway(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		geneSelectionManager = new EventBasedSelectionManager(this, primaryRowIDType);
		geneSelectionManager.registerEventListeners();

		metaboliteSelectionManager = new EventBasedSelectionManager(this, IDType.getIDType("METABOLITE"));
		metaboliteSelectionManager.registerEventListeners();

		List<GeneticDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class);

		// FIXME this is a hack that can be resolved by an "imprint" function when adding the first perspective
		if (dataDomains.size() != 0) {
			columnIDType = dataDomains.get(0).getSampleIDType().getIDCategory().getPrimaryMappingType();
			sampleSelectionManager = new EventBasedSelectionManager(this, columnIDType);
			sampleSelectionManager.registerEventListeners();
		}

		pathRenderer = new EnRoutePathRenderer(this, new ArrayList<TablePerspective>());
		pathRenderer.setUpdateStrategy(new SelectedPathUpdateStrategy(pathRenderer,
				GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE));
		mappedDataRenderer = new MappedDataRenderer(this);
	}

	@Override
	public void init(GL2 gl) {

		displayListIndex = gl.glGenLists(1);
		layoutDisplayListIndex = gl.glGenLists(1);
		textRenderer = new CaleydoTextRenderer(24);

		detailLevel = EDetailLevel.HIGH;

		pathRenderer.setTextRenderer(textRenderer);
		pathRenderer.setPixelGLConverter(pixelGLConverter);
		pathRenderer.init();

		mappedDataRenderer.init();

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		layoutManager.setUseDisplayLists(true);
		ElementLayout pathElementLayout = new ElementLayout();
		MultiFormRenderer mr = new MultiFormRenderer(this, false);
		mr.addLayoutRenderer(pathRenderer, null, new DefaultVisInfo(), true);
		pathElementLayout.setPixelSizeX(pathRenderer.getMinWidthPixels());
		pathElementLayout.setRenderer(mr);
		layoutManager.setBaseElementLayout(pathElementLayout);
		layoutManager.updateLayout();

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (glKeyListener != null)
					glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

	}

	@Override
	public void displayRemote(GL2 gl) {
		processEvents();
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (isLayoutDirty) {
			layoutManager.updateLayout();
		}

		layoutManager.render(gl);
		if (pathRenderer.getPathNodes().isEmpty()) {
			if (isDisplayListDirty) {
				renderEmptyViewInfo(gl, displayListIndex);
				isDisplayListDirty = false;
			}
			gl.glCallList(displayListIndex);
		} else {
			if (isLayoutDirty) {
				updateLayout();

				float dataRowPositionX = pixelGLConverter.getGLWidthForPixelWidth(pathRenderer.getMinWidthPixels());
				float topSpacing = pixelGLConverter.getGLWidthForPixelWidth(TOP_SPACING_MAPPED_DATA);

				gl.glNewList(layoutDisplayListIndex, GL2.GL_COMPILE);
				gl.glPushMatrix();
				gl.glTranslatef(dataRowPositionX, topSpacing, 0);
				mappedDataRenderer.renderBaseRepresentation(gl);
				gl.glPopMatrix();
				gl.glEndList();

				isLayoutDirty = false;
			}

			if (isDisplayListDirty) {
				buildDisplayList(gl, displayListIndex);
				isDisplayListDirty = false;
			}

			gl.glCallList(layoutDisplayListIndex);
			gl.glCallList(displayListIndex);
		}

		checkForHits(gl);
	}

	/**
	 * Renders information what to do in order to see data in the view.
	 *
	 * @param gl
	 * @param displayListIndex
	 */
	private void renderEmptyViewInfo(GL2 gl, int displayListIndex) {
		gl.glNewList(displayListIndex, GL2.GL_COMPILE);
		renderEmptyViewText(gl, new String[] { EMPTY_VIEW_TEXT_LINE_ONE, EMPTY_VIEW_TEXT_LINE_TWO,
				"Refer to http://help.caleydo.org for more information." });
		gl.glEndList();
	}

	/**
	 * Updates the layout of the view.
	 */
	private void updateLayout() {
		// float branchColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		// float pathwayColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);
		// float pathwayTextColumnWidth = pixelGLConverter
		// .getGLWidthForPixelWidth(EnRoutePathRenderer.PATHWAY_TITLE_COLUMN_WIDTH_PIXELS);
		float dataRowPositionX = pixelGLConverter.getGLWidthForPixelWidth(pathRenderer.getMinWidthPixels());
		float topSpacing = pixelGLConverter.getGLWidthForPixelWidth(TOP_SPACING_MAPPED_DATA);
		float sideSpacing = pixelGLConverter.getGLHeightForPixelHeight(SIDE_SPACING_MAPPED_DATA);

		float dataRowHeight = pixelGLConverter.getGLHeightForPixelHeight(pathRenderer.getSizeConfig().getRowHeight());

		mappedDataRenderer.setGeometry(viewFrustum.getWidth() - dataRowPositionX - sideSpacing, viewFrustum.getHeight()
				- 2 * topSpacing, dataRowPositionX, topSpacing, dataRowHeight);

		mappedDataRenderer.setLinearizedNodes(pathRenderer.getPathNodes());
		int minMappedDataRendererWidthPixels = mappedDataRenderer.getMinWidthPixels();

		adaptViewSize(minMappedDataRendererWidthPixels + pathRenderer.getMinWidthPixels() + SIDE_SPACING_MAPPED_DATA,
				pathRenderer.getMinHeightPixels());

		mappedDataRenderer.updateLayout();
	}

	private void buildDisplayList(final GL2 gl, int displayListIndex) {

		gl.glNewList(displayListIndex, GL2.GL_COMPILE);

		// float branchColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		// float pathwayColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);
		// float pathwayTextColumnWidth = pixelGLConverter
		// .getGLWidthForPixelWidth(EnRoutePathRenderer.PATHWAY_TITLE_COLUMN_WIDTH_PIXELS);
		float dataRowPositionX = pixelGLConverter.getGLWidthForPixelWidth(pathRenderer.getMinWidthPixels());
		float topSpacing = pixelGLConverter.getGLWidthForPixelWidth(TOP_SPACING_MAPPED_DATA);

		gl.glPushMatrix();
		gl.glTranslatef(dataRowPositionX, topSpacing, 0);
		mappedDataRenderer.renderHighlightElements(gl);
		gl.glPopMatrix();

		gl.glEndList();

	}

	/**
	 * Adapts the view height to the maximum of the specified minimum view heights, if necessary.
	 *
	 * @param minViewWidth
	 *            Minimum width required.
	 * @param minViewHeightRequiredByPath
	 *            View height in pixels required by the linearized path and its rows.
	 */
	private void adaptViewSize(int minViewWidth, int minViewHeightRequiredByPath) {
		boolean updateWidth = minViewWidth > parentGLCanvas.getWidth()
				|| (minViewWidth < parentGLCanvas.getWidth() && (minViewWidth > minWidth || minViewWidth + 3 < minWidth));

		boolean updateHeight = false;

		if (pathRendererChanged || parentGLCanvas.getHeight() < minViewHeightRequiredByPath) {
			// System.out.println("setting min height:" + minViewHeightPixels);
			pathRendererChanged = false;
			updateHeight = true;
		}

		if (updateWidth || updateHeight) {

			// System.out.println("setting min width:" + minViewWidth);
			if (fitToViewWidth) {
				minWidth = pathRenderer.getMinWidthPixels() + DATA_COLUMN_WIDTH_PIXELS;
			} else {
				minWidth = updateWidth ? minViewWidth + 3 : pathRenderer.getMinWidthPixels() + DATA_COLUMN_WIDTH_PIXELS;
			}

			setMinViewSize(minWidth, minViewHeightRequiredByPath + 3);
		}
		minHeight = minViewHeightRequiredByPath + 3;
	}

	private void setMinViewSize(int minWidthPixels, int minHeightPixels) {
		SetMinViewSizeEvent event = new SetMinViewSizeEvent(this);
		event.setMinViewSize(minWidthPixels, minHeightPixels);
		eventPublisher.triggerEvent(event);
		// System.out.println("minsize: " + minHeightPixels);
		setLayoutDirty();
	}

	@Override
	public int getMinPixelHeight() {
		return minHeight;
	}

	@Override
	public int getMinPixelWidth() {
		return minWidth;
	}

	@Override
	public ASerializedMultiTablePerspectiveBasedView getSerializableRepresentation() {
		SerializedEnRoutePathwayView serializedForm = new SerializedEnRoutePathwayView();
		serializedForm.setViewID(this.getID());
		serializedForm.setFitToViewWidth(fitToViewWidth);
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		listeners.register(this);

		addTablePerspectivesListener = new AddTablePerspectivesListener();
		addTablePerspectivesListener.setHandler(this);
		addTablePerspectivesListener.setEventSpace(pathwayPathEventSpace);
		eventPublisher.addListener(AddTablePerspectivesEvent.class, addTablePerspectivesListener);

		removeTablePerspectiveListener = new RemoveTablePerspectiveListener();
		removeTablePerspectiveListener.setHandler(this);
		removeTablePerspectiveListener.setEventSpace(pathwayPathEventSpace);
		eventPublisher.addListener(RemoveTablePerspectiveEvent.class, removeTablePerspectiveListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addTablePerspectivesListener != null) {
			eventPublisher.removeListener(addTablePerspectivesListener);
			addTablePerspectivesListener = null;
		}

		if (removeTablePerspectiveListener != null) {
			eventPublisher.removeListener(removeTablePerspectiveListener);
			removeTablePerspectiveListener = null;
		}

		listeners.unregisterAll();

		geneSelectionManager.unregisterEventListeners();
		metaboliteSelectionManager.unregisterEventListeners();
		sampleSelectionManager.unregisterEventListeners();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		setLayoutDirty();
		// System.out.println("reshape: " + x + ", " + y + ", " + width + "x" +
		// height);
	}

	/**
	 * Resolves the {@link #contextualPerspective} to sub-perspectives matching the perspectives in
	 * {@link #resolvedTablePerspectives}
	 */
	private void updateContextualPerspectives() {
		if (contextualPerspective == null) {
			mappedDataRenderer.setContextualTablePerspectives(null);
			return;
		}

		ATableBasedDataDomain contextualDataDomain = contextualPerspective.getDataDomain();
		// Perspective contextualColumnPerspective = contextualPerspective.getPerspective();
		Perspective contextualRowPerspective = contextualPerspective.getPerspective(contextRowIDTYpe);

		ArrayList<TablePerspective> contextTablePerspectives = new ArrayList<>();
		for (TablePerspective resolvedGeneTablePerspective : resolvedTablePerspectives) {

			Perspective primaryColumnPerspective = resolvedGeneTablePerspective.getPerspective(columnIDType);

			Perspective contextualColumnPerspective = contextualDataDomain
					.convertForeignPerspective(primaryColumnPerspective);

			if (contextualColumnPerspective == null) {
				Logger.log(new Status(IStatus.ERROR, this.toString(),
						"Failed to convert the primary to the contex perspective"));
			} else {
				TablePerspective resolvedContextTablePerspective;

				if (contextualDataDomain.getDimensionIDType().equals(contextRowIDTYpe)) {
					resolvedContextTablePerspective = new TablePerspective(contextualDataDomain,
							contextualColumnPerspective, contextualRowPerspective);
				} else if (contextualDataDomain.getRecordIDType().equals(contextRowIDTYpe)) {
					resolvedContextTablePerspective = new TablePerspective(contextualDataDomain,
							contextualColumnPerspective, contextualRowPerspective);
				} else {
					throw new IllegalStateException("Context DD and IDTypes don't match up.");

				}
				contextTablePerspectives.add(resolvedContextTablePerspective);
			}
		}

		mappedDataRenderer.setContextualTablePerspectives(contextTablePerspectives);
	}

	@Override
	public void addTablePerspective(TablePerspective newTablePerspective) {
		ArrayList<TablePerspective> newTablePerspectives = new ArrayList<TablePerspective>(1);
		newTablePerspectives.add(newTablePerspective);
		addTablePerspectives(newTablePerspectives);
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {

		if (newTablePerspectives == null || newTablePerspectives.isEmpty())
			return;
		// check for contextual perspectives and check whether ID Types match first
		Iterator<TablePerspective> tpIterator = newTablePerspectives.iterator();
		while (tpIterator.hasNext()) {

			TablePerspective newTablePerspective = tpIterator.next();
			if (!newTablePerspective.getDataDomain().hasIDCategory(columnIDType)) {
				Logger.log(new Status(IStatus.WARNING, this.toString(),
						"Can't add perspective since it doesn't match ID types: " + newTablePerspective));
				tpIterator.remove();
				continue;
			}
			if (!newTablePerspective.getDataDomain().hasIDCategory(primaryRowIDType)) {

				contextRowIDTYpe = newTablePerspective.getDataDomain().getOppositeIDType(columnIDType);
				if (mappedDataRenderer.getContextRowIDs() == null || mappedDataRenderer.getContextRowIDs().isEmpty()) {
					// if this is the first time we select the compound
					ShowContextElementSelectionDialogEvent contextEvent = new ShowContextElementSelectionDialogEvent(
							newTablePerspective.getPerspective(contextRowIDTYpe));
					eventPublisher.triggerEvent(contextEvent);

				}
				// true for contextual perspective
				contextualPerspective = newTablePerspective;

				tpIterator.remove();
			}
		}

		if (newTablePerspectives.isEmpty()) {
			updateContextualPerspectives();
			setLayoutDirty();
			return;
		}

		tablePerspectives.addAll(newTablePerspectives);
		resolveSubTablePerspectives(newTablePerspectives);

		// if (!isRenderedRemote())
		// pathRenderer.setTablePerspectives(new ArrayList<>(resolvedTablePerspectives));
		mappedDataRenderer.setGeneTablePerspectives(resolvedTablePerspectives);
		for (TablePerspective tablePerspective : newTablePerspectives) {
			dataDomains.add(tablePerspective.getDataDomain());
		}

		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
		setLayoutDirty();
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	@Override
	public ArrayList<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * Creates new table perspectives for every group in a gene-group-list of every table perspective. If no group lists
	 * are present, the original table perspective is added.
	 */
	private void resolveSubTablePerspectives(List<TablePerspective> newTablePerspectives) {
		for (TablePerspective tablePerspective : newTablePerspectives) {
			GeneticDataDomain dataDomain = (GeneticDataDomain) tablePerspective.getDataDomain();

			List<TablePerspective> newlyResovedTablePerspectives;
			if (dataDomain.isGeneRecord()) {
				newlyResovedTablePerspectives = tablePerspective.getDimensionSubTablePerspectives();
			} else {
				newlyResovedTablePerspectives = tablePerspective.getRecordSubTablePerspectives();
			}

			if (newlyResovedTablePerspectives != null) {
				resolvedTablePerspectives.addAll(newlyResovedTablePerspectives);

			} else {
				resolvedTablePerspectives.add(tablePerspective);
			}
		}
		updateContextualPerspectives();

	}

	@Override
	public boolean isDataView() {
		return true;
	}

	/**
	 * @return the rowSelectionManager, see {@link #rowSelectionManager}
	 */
	public EventBasedSelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
	}

	/**
	 * @return the metaboliteSelectionManager, see {@link #metaboliteSelectionManager}
	 */
	public EventBasedSelectionManager getMetaboliteSelectionManager() {
		return metaboliteSelectionManager;
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		return new HashSet<IDataDomain>(dataDomains);
	}

	@Override
	public void removeTablePerspective(TablePerspective tablePerspective) {
		if (tablePerspective == contextualPerspective)
			contextualPerspective = null;

		for (TablePerspective t : tablePerspectives) {
			if (t == tablePerspective) {
				IDataDomain dataDomain = t.getDataDomain();
				boolean removeDataDomain = true;
				for (TablePerspective tp : tablePerspectives) {
					if (tp != t && tp.getDataDomain() == dataDomain) {
						removeDataDomain = false;
						break;
					}
				}

				if (removeDataDomain) {
					dataDomains.remove(dataDomain);
				}
				break;
			}
		}
		Iterator<TablePerspective> tablePerspectiveIterator = tablePerspectives.iterator();

		while (tablePerspectiveIterator.hasNext()) {
			TablePerspective t = tablePerspectiveIterator.next();
			if (t == tablePerspective) {
				tablePerspectiveIterator.remove();
			}
		}
		resolvedTablePerspectives.clear();
		// TODO - this is maybe not the most elegant way to remove the resolved
		// sub-data containers
		resolveSubTablePerspectives(tablePerspectives);

		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		setLayoutDirty();
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		gl.glDeleteLists(layoutDisplayListIndex, 1);
		if (layoutManager != null) {
			layoutManager.destroy(gl);
		} else {
			pathRenderer.destroy(gl);
		}
		mappedDataRenderer.destroy(gl);
	}

	public void setLayoutDirty() {
		isLayoutDirty = true;
		setDisplayListDirty();
	}

	/**
	 * @return the fitWidthToScreen, see {@link #fitToViewWidth}
	 */
	public boolean isFitWidthToScreen() {
		return fitToViewWidth;
	}

	/**
	 * @param fitToViewWidth
	 *            setter, see {@link #fitToViewWidth}
	 */
	@ListenTo
	public void onFitToViewWidth(FitToViewWidthEvent event) {
		this.fitToViewWidth = event.isFitToViewWidth();
		minWidth = 0;
		setLayoutDirty();
	}

	@ListenTo
	public void onPathRendererChanged(PathRendererChangedEvent event) {
		if (event.getPathRenderer() == pathRenderer) {
			setLayoutDirty();
			pathRendererChanged = true;
		}
	}

	@ListenTo
	public void updatePerspective(PerspectiveUpdatedEvent event) {
		checkAndUpdatePerspectives(event, resolvedTablePerspectives);
		checkAndUpdatePerspectives(event, tablePerspectives);
		checkAndUpdatePerspectives(event, mappedDataRenderer.getContextualTablePerspectives());

	}

	private void checkAndUpdatePerspectives(PerspectiveUpdatedEvent event, List<TablePerspective> perspectivesToCheck) {
		Iterator<TablePerspective> pIterator = perspectivesToCheck.iterator();
		while (pIterator.hasNext()) {
			TablePerspective tPerspective = pIterator.next();

			if (tPerspective.getRecordPerspective().equals(event.getPerspective())
					|| tPerspective.getDimensionPerspective().equals(event.getPerspective())) {
				updateLayout();
			} else if (tPerspective.getRecordPerspective().getCrossDatasetID() != null
					&& event.getPerspective().getCrossDatasetID() != null
					&& tPerspective.getRecordPerspective().getCrossDatasetID()
							.equals(event.getPerspective().getCrossDatasetID())) {

				Perspective perspective = tPerspective.getDataDomain()
						.convertForeignPerspective(event.getPerspective());

				tPerspective.setRecordPerspective(perspective);
				// TablePerspective newTPerstpective = new TablePerspective(tPerspective.getDataDomain(), perspective,
				// tPerspective.getDimensionPerspective());
				// pIterator.remove();
				// pIterator.
				setLayoutDirty();
			} else if (tPerspective.getDimensionPerspective().getCrossDatasetID() != null
					&& event.getPerspective().getCrossDatasetID() != null
					&& tPerspective.getDimensionPerspective().getCrossDatasetID()
							.equals(event.getPerspective().getCrossDatasetID())) {
				Perspective newPerspective = tPerspective.getDataDomain().convertForeignPerspective(
						event.getPerspective());
				tPerspective.setDimensionPerspective(newPerspective);
				setLayoutDirty();
			}
		}

	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		setDisplayListDirty();

	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.all;
	}

	/**
	 * Sets the event space for path related events.
	 */
	public void setPathwayPathEventSpace(String pathwayPathEventSpace) {
		pathRenderer.getUpdateStrategy().setPathwayPathEventSpace(pathwayPathEventSpace);
		this.pathwayPathEventSpace = pathwayPathEventSpace;
	}

	/**
	 * @return the sampleSelectionManager, see {@link #sampleSelectionManager}
	 */
	public EventBasedSelectionManager getSampleSelectionManager() {
		return sampleSelectionManager;
	}

	/**
	 * @return the pathRenderer, see {@link #pathRenderer}
	 */
	public EnRoutePathRenderer getPathRenderer() {
		return pathRenderer;
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		setLayoutDirty();
	}

	@Override
	public PathwayGraph getPathway() {
		if (pathRenderer != null)
			return pathRenderer.getPathway();
		return null;
	}

	@Override
	public List<PathwayGraph> getPathways() {
		if (pathRenderer != null)
			return pathRenderer.getPathways();
		return null;
	}

	@Override
	public Rectangle2D getVertexRepBounds(PathwayVertexRep vertexRep) {
		if (pathRenderer != null)
			return pathRenderer.getVertexRepBounds(vertexRep);
		return null;
	}

	@Override
	public List<Rectangle2D> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		if (pathRenderer != null)
			return pathRenderer.getVertexRepsBounds(vertexRep);
		return null;
	}

	@Override
	public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
		if (pathRenderer != null)
			pathRenderer.addVertexRepBasedContextMenuItem(item);

	}

	@ListenTo
	public void showContextSelectionDialog(final ShowContextElementSelectionDialogEvent event) {

		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				ChooseContextDataDialog dialog = new ChooseContextDataDialog(shell, event.getPerspective());
				dialog.create();
				dialog.setBlockOnOpen(true);

				if (dialog.open() == IStatus.OK) {

					// FIXME this might not be thread-safe
					List<Integer> selectedItems = dialog.getSelectedItems();
					mappedDataRenderer.setContextRowIDs(selectedItems);
					setLayoutDirty();

				}
			}

		});
	}

	@Override
	public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode) {
		if (pathRenderer != null)
			pathRenderer.addVertexRepBasedSelectionEvent(eventFactory, pickingMode);
	}

}
