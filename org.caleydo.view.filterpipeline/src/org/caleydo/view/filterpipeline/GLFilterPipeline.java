/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filterpipeline;

import gleem.linalg.Vec2f;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.FilterManager;
import org.caleydo.core.data.filter.RecordMetaOrFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.ReEvaluateFilterListEvent;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.filterpipeline.listener.FilterUpdateListener;
import org.caleydo.view.filterpipeline.listener.ReEvaluateFilterListener;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeEvent;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeEvent.FilterType;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeListener;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;
import org.caleydo.view.filterpipeline.representation.Background;
import org.caleydo.view.filterpipeline.representation.FilterRepresentation;
import org.caleydo.view.filterpipeline.representation.FilterRepresentationMetaOrAdvanced;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Filter pipeline
 *
 * @author Thomas Geymayer
 */

public class GLFilterPipeline extends ATableBasedView implements IRadialMenuListener {
	public static String VIEW_TYPE = "org.caleydo.view.filterpipeline";

	public static String VIEW_NAME = "Filter Pipeline";

	private FilterPipelineRenderStyle renderStyle;
	private DragAndDropController dragAndDropController;
	private SelectionManager selectionManager;
	private List<FilterItem> filterList = new LinkedList<FilterItem>();

	private FilterUpdateListener filterUpdateListener;
	private SetFilterTypeListener setFilterTypeListener;
	private ReEvaluateFilterListener reEvaluateFilterListener;

	private FilterType filterType = FilterType.RECORD;

	/**
	 * First filter to be displayed. All filters before are hidden and the height of the first filter shall fill the
	 * whole view.
	 */
	private int firstFilter = 0;

	/**
	 * The filtered items of this filter will be ignored, so that we can see what the filter pipeline would look like
	 * without this filter.
	 *
	 * Set to -1 if no filter should be ignored.
	 */
	private int ignoredFilter = -1;

	/**
	 * The filter which should be showed in full size, which showing all filtered items, even those which don't arrive
	 * as input because they have been filtered before.
	 *
	 * Set to -1 if no filter should be showed full sized.
	 */
	private int fullSizedFilter = -1;

	/**
	 * The size a filter should have in OpenGL units. The x value is absolute, the y value is per 100 elements
	 */
	private Vec2f filterSize = null;

	private boolean pipelineNeedsUpdate = true;
	private Vec2f mousePosition = new Vec2f();

	private Background background = null;
	private RadialMenu radialMenu = null;
	// private FilterMenu filterMenu = null;

	private boolean bControlPressed = false;
	private long mouseOverTimeStamp = Long.MAX_VALUE;

	/**
	 * Constructor.
	 *
	 */
	public GLFilterPipeline(IGLCanvas glCanvas, ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum, VIEW_TYPE, VIEW_NAME);

		dragAndDropController = new DragAndDropController(this);
		glKeyListener = new GLFilterPipelineKeyListener(this);
	}

	@Override
	public void init(GL2 gl) {

		displayListIndex = gl.glGenLists(1);
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new FilterPipelineRenderStyle(viewFrustum);
		selectionManager = new SelectionManager(IDType.registerType("filter_" + hashCode(),
				IDCategory.registerCategory("filter"), EDataType.INTEGER));

		detailLevel = EDetailLevel.HIGH;

		background = new Background(uniqueID, pickingManager, renderStyle);

		radialMenu = new RadialMenu(this, textureManager.getIconTexture(EIconTextures.FILTER_PIPELINE_MENU_ITEM));
		// radialMenu.addEntry( null );
		// radialMenu.addEntry( null );
		radialMenu.addEntry(textureManager.getIconTexture(EIconTextures.FILTER_PIPELINE_DELETE));
		radialMenu.addEntry(textureManager.getIconTexture(EIconTextures.FILTER_PIPELINE_EDIT));

		// filterMenu = new FilterMenu(renderStyle, pickingManager, iUniqueID);

		if (textRenderer != null)
			textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 20));
		textRenderer.setColor(0, 0, 0, 1);

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
	}

	@Override
	public void initLocal(GL2 gl) {
		// Register keyboard listener to GL2 canvas
		final Composite parentComposite = parentGLCanvas.asComposite();
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
		// Register keyboard listener to GL2 canvas
		final Composite parentComposite = glParentView.getParentGLCanvas().asComposite();
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		// glMouseListener = getParentGLCanvas().getGLMouseListener();

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);

		updateFilterSize();
	}

	@Override
	public void display(GL2 gl) {
		// ---------------------------------------------------------------------
		// move...
		// ---------------------------------------------------------------------

		if (pipelineNeedsUpdate)
			updateFilterPipeline();

		if (glMouseListener.wasMouseReleased())
			radialMenu.handleMouseReleased();

		updateMousePosition(gl);

		radialMenu.handleDragging(mousePosition);

		// ---------------------------------------------------------------------
		// render...
		// ---------------------------------------------------------------------

		background.render(gl, textRenderer);

		// filter
		if (!filterList.isEmpty()) {
			// display an arrow to show hidden filters
			if (firstFilter > 0)
				displayCollapseArrow(gl, firstFilter - 1, 0.12f);

			for (FilterItem filter : filterList) {
				if (filter.getId() < firstFilter)
					// skip hidden filters
					continue;

				if (filter.getId() == firstFilter) {
					// show input for first filter
					textRenderer.renderText(gl, "" + filter.getInput().size(), 0.05f, filter.getRepresentation()
							.getPosition().y()
							+ filter.getRepresentation().getHeightLeft() + 0.05f, 0.9f, 0.004f, 20);
				} else {
					displayCollapseArrow(gl, filter.getId(), filter.getRepresentation().getPosition().x() - 0.062f
							* filterSize.x() - 0.15f);
				}

				if (filter.getId() == fullSizedFilter
						&& Calendar.getInstance().getTimeInMillis() - mouseOverTimeStamp > 500) {

					Vec2f pos = filter.getRepresentation().getPosition();

					float fullHeightLeft = (filterList.get(0).getInput().size() * filterSize.y()) / 100.f;

					float fullHeightRight = ((filterList.get(0).getInput().size() - filter.getSizeVADelta()) * filterSize
							.y()) / 100.f;

					gl.glBegin(GL2.GL_QUADS);
					gl.glColor4f(153 / 255.f, 213 / 255.f, 148 / 255.f, 0.3f);
					gl.glVertex2f(pos.x(), pos.y());
					gl.glVertex2f(pos.x(), pos.y() + fullHeightLeft);
					gl.glVertex2f(pos.x() + filterSize.x(), pos.y() + fullHeightRight);
					gl.glVertex2f(pos.x() + filterSize.x(), pos.y());
					gl.glEnd();

					// float fullUncertaintyHeightRight =
					// ((filterList.get(0).getInput()
					// .size() -
					// filter.getFilter().getVADeltaUncertainty().size()) *
					// filterSize
					// .y()) / 100.f;
					//
					// gl.glBegin(GL2.GL_QUADS);
					// gl.glColor4f(153 / 255.f, 213 / 255.f, 148 / 255.f,
					// 0.3f);
					// gl.glVertex2f(pos.x(), pos.y());
					// gl.glVertex2f(pos.x(), pos.y() +
					// fullUncertaintyHeightRight);
					// gl.glVertex2f(pos.x() + filterSize.x(), pos.y() +
					// fullUncertaintyHeightRight);
					// gl.glVertex2f(pos.x() + filterSize.x(), pos.y());
					// gl.glEnd();
				}

				filter.getRepresentation().updateSelections(selectionManager);
				filter.render(gl, textRenderer);
			}

			// filterMenu.render(gl, textRenderer);
			radialMenu.render(gl);
		}

		// call after all other rendering because it calls the onDrag methods
		// which need alpha blending...
		dragAndDropController.handleDragging(gl, glMouseListener);
	}

	private void displayCollapseArrow(GL2 gl, int id, float left) {
		int iPickingID = pickingManager.getPickingID(uniqueID, PickingType.FILTERPIPE_START_ARROW, id);
		float bottom = 0.025f;
		float halfSize = 0.075f;

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		Texture arrowTexture = textureManager.getIconTexture(EIconTextures.HEAT_MAP_ARROW);
		arrowTexture.enable(gl);
		arrowTexture.bind(gl);
		TextureCoords texCoords = arrowTexture.getImageTexCoords();

		gl.glPushName(iPickingID);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW_MATRIX);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glTranslatef(left + halfSize, bottom + halfSize, 0.001f);
		gl.glRotatef(id <= firstFilter ? -90 : 90, 0, 0, 1);

		gl.glBegin(GL2.GL_QUADS);
		{
			gl.glColor3f(0.9f, 1f, 0.9f);

			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex2f(-halfSize, -halfSize);

			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex2f(-halfSize, halfSize);

			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex2f(halfSize, halfSize);

			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex2f(halfSize, -halfSize);
		}
		gl.glEnd();

		gl.glPopMatrix();
		gl.glPopName();

		arrowTexture.disable(gl);

		gl.glPopAttrib();
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode, int externalID, Pick pick) {
		int newFullSizedFilter = -1;

		switch (pickingMode) {
		case CLICKED:
			dragAndDropController.clearDraggables();
		case MOUSE_OVER:
			// filterMenu.handleClearMouseOver();
			break;
		default:
			break;
		}

		switch (pickingType) {
		// -----------------------------------------------------------------
		case FILTERPIPE_FILTER:
			switch (pickingMode) {
			case MOUSE_OVER:
				// remove old mouse over
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.addToType(SelectionType.MOUSE_OVER, externalID);
				// try
				// {
				// filterMenu.setFilter(filterList.get(externalID));
				// }
				// catch (Exception e)
				// {
				// // maybe the filter has been destroyed in the meantime
				// filterMenu.setFilter(null);
				// }
				if (fullSizedFilter != externalID)
					mouseOverTimeStamp = Calendar.getInstance().getTimeInMillis();

				newFullSizedFilter = externalID;
				break;
			case CLICKED:
				if (!bControlPressed)
					selectionManager.clearSelection(SelectionType.SELECTION);

				// Toggle add/remove element to selection
				if (selectionManager.checkStatus(SelectionType.SELECTION, externalID)) {
					selectionManager.removeFromType(SelectionType.SELECTION, externalID);
				} else {
					selectionManager.addToType(SelectionType.SELECTION, externalID);
				}

				dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
				dragAndDropController.addDraggable(filterList.get(externalID).getRepresentation());
				break;
			case RIGHT_CLICKED:
				radialMenu.show(externalID, mousePosition);
				break;
			case DRAGGED:
				if (dragAndDropController.hasDraggables()) {
					if (glMouseListener.wasRightMouseButtonPressed())
						dragAndDropController.clearDraggables();
					// else if (!dragAndDropController.isDragging())
					// dragAndDropController.startDragging();
				}
				dragAndDropController.setDropArea(filterList.get(externalID));
				break;
			default:
				break;
			}
			break;
		// -----------------------------------------------------------------
		case FILTERPIPE_SUB_FILTER:
			// switch (pickingMode) {
			// case MOUSE_OVER:
			// filterMenu.handleIconMouseOver(externalID);
			// break;
			// }
			break;
		// -----------------------------------------------------------------
		case FILTERPIPE_START_ARROW:
			switch (pickingMode) {
			case CLICKED:
				firstFilter = externalID;
				updateFilterSize();
				//$FALL-THROUGH$
			case MOUSE_OVER:
				// reset all mouse over actions
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				// filterMenu.setFilter(null);
				break;
			default:
				break;
			}
			break;
		// -----------------------------------------------------------------
		case FILTERPIPE_BACKGROUND:
			switch (pickingMode) {
			case CLICKED:
				if (!bControlPressed)
					selectionManager.clearSelection(SelectionType.SELECTION);
				// break; Fall through...
			case MOUSE_OVER:
				// reset all mouse over actions
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				// filterMenu.setFilter(null);
				break;
			case DRAGGED:
				dragAndDropController.setDropArea(background);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		fullSizedFilter = newFullSizedFilter;

		if (fullSizedFilter == -1)
			mouseOverTimeStamp = Long.MAX_VALUE;
	}

	@Override
	public void handleRadialMenuSelection(int externalId, int selection) {
		ignoredFilter = -1;

		if (externalId >= filterList.size() || externalId < 0)
			return;

		FilterItem filter = filterList.get(externalId);

		switch (selection) {
		case 0:
			filter.triggerRemove();
			selectionManager.removeFromType(SelectionType.SELECTION, externalId);
			break;
		case 1:
			filter.showDetailsDialog();
			break;
		}
	}

	@Override
	public void handleRadialMenuHover(int externalId, int selection) {
		ignoredFilter = -1;

		switch (selection) {
		case 0: // remove
			ignoredFilter = externalId;
			break;
		}
	}

	private void updateMousePosition(GL2 gl) {
		try {
			float windowCoords[] = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl,
					glMouseListener.getPickedPoint().x, glMouseListener.getPickedPoint().y);

			mousePosition.set(windowCoords[0], windowCoords[1]);
		} catch (Exception e) {
			// System.out.println("Failed to get mouse position: "+e);
		}
	}

	private void updateFilterSize() {
		if (filterList.isEmpty())
			return;

		// ensure at least one valid filter is shown
		if (firstFilter >= filterList.size())
			firstFilter = filterList.size() - 1;
		else if (firstFilter < 0)
			firstFilter = 0;

		filterSize = new Vec2f((viewFrustum.getWidth() - 0.1f) / (filterList.size() - firstFilter),

		// 100 elements will be high exactly 1 unit. So we need to scale
		// it that the largest (== first) filter fits.
				(viewFrustum.getHeight() - 0.4f) / (filterList.get(firstFilter).getInput().size() / 100.f));

		Vec2f filterPosition = new Vec2f(0.12f, renderStyle.FILTER_SPACING_BOTTOM), width = new Vec2f(filterSize.x(), 0);

		filterSize.setX(filterSize.x() * 0.945f);

		for (FilterItem filter : filterList) {
			if (filter.getId() < firstFilter)
				continue;

			filter.getRepresentation().setPosition(filterPosition);
			filter.getRepresentation().setSize(filterSize);

			filterPosition.add(width);
		}

		background.setFilterList(filterList, firstFilter);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedFilterPipelineView serializedForm = new SerializedFilterPipelineView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return getClass().getCanonicalName();
	}

	@Override
	public void registerEventListeners() {
		filterUpdateListener = new FilterUpdateListener();
		filterUpdateListener.setHandler(this);
		eventPublisher.addListener(FilterUpdatedEvent.class, filterUpdateListener);

		reEvaluateFilterListener = new ReEvaluateFilterListener();
		reEvaluateFilterListener.setHandler(this);
		eventPublisher.addListener(ReEvaluateFilterListEvent.class, reEvaluateFilterListener);

		setFilterTypeListener = new SetFilterTypeListener();
		setFilterTypeListener.setHandler(this);
		eventPublisher.addListener(SetFilterTypeEvent.class, setFilterTypeListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (filterUpdateListener != null) {
			eventPublisher.removeListener(filterUpdateListener);
			filterUpdateListener = null;
		}

		if (reEvaluateFilterListener != null) {
			eventPublisher.removeListener(reEvaluateFilterListener);
			reEvaluateFilterListener = null;
		}

		if (setFilterTypeListener != null) {
			eventPublisher.removeListener(setFilterTypeListener);
			setFilterTypeListener = null;
		}
	}

	/**
	 * Rebuild the filter pipeline
	 */
	public void updateFilterPipeline() {
		pipelineNeedsUpdate = false;


		// Logger.log(new Status(IStatus.INFO, this.toString(),
		// "Filter update: filterType="
		// + filterType));

		filterList.clear();
		int filterID = 0;

		FilterManager recordFilterManager = tablePerspective.getRecordPerspective().getFilterManager();
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		FilterManager dimensionFilterManager = tablePerspective.getDimensionPerspective().getFilterManager();

		VirtualArray currentVA;
		FilterManager filterManager;

		// TODO this needs to be checked for generic handling
		if (filterType == FilterType.RECORD) {
			filterManager = recordFilterManager;
			currentVA = recordVA;
		} else {
			filterManager = dimensionFilterManager;
			currentVA = dimensionVA;
		}

		for (Filter filter : filterManager.getFilterPipe()) {
			FilterItem filterItem = new FilterItem(filterID++, filter, pickingManager, uniqueID);

			if (filter instanceof RecordMetaOrFilter)
				filterItem.setRepresentation(new FilterRepresentationMetaOrAdvanced(renderStyle, pickingManager,
						uniqueID));
			else
				filterItem.setRepresentation(new FilterRepresentation(renderStyle, pickingManager, uniqueID));

			filterList.add(filterItem);
		}

		// TODO move to separate function...

		for (FilterItem filter : filterList) {
			// filter items
			filter.setInput(currentVA);
			currentVA = filter.getOutput();
		}

		updateFilterSize();
	}

	@Override
	public void initData() {
		super.initData();

	}


	public void handleSetFilterTypeEvent(FilterType type) {
		if (filterType == type)
			return;

		filterType = type;
		updateFilterPipeline();
	}

	public void handleReEvaluateFilter(FilterType type) {
		if (filterType == type)
			updateFilterPipeline();
	}

	public void setControlPressed(boolean state) {
		bControlPressed = state;
	}


	@Override
	public boolean isDataView() {
		return false;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		ArrayList<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();
		tablePerspectives.add(tablePerspective);
		return tablePerspectives;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		// TODO Auto-generated method stub

	}
}
