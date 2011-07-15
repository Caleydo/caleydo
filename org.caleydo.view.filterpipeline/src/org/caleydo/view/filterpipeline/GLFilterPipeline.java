package org.caleydo.view.filterpipeline;

import gleem.linalg.Vec2f;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.HistogramCreator;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaOrFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.ReEvaluateContentFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListEvent;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.event.view.filterpipeline.SetFilterTypeEvent;
import org.caleydo.core.manager.event.view.filterpipeline.SetFilterTypeEvent.FilterType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.filterpipeline.listener.FilterUpdateListener;
import org.caleydo.view.filterpipeline.listener.ReEvaluateFilterListener;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeListener;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;
import org.caleydo.view.filterpipeline.representation.Background;
import org.caleydo.view.filterpipeline.representation.FilterRepresentation;
import org.caleydo.view.filterpipeline.representation.FilterRepresentationMetaOrAdvanced;
import org.caleydo.view.filterpipeline.representation.FilterRepresentationSNR;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Filter pipeline
 * 
 * @author Thomas Geymayer
 */

public class GLFilterPipeline extends AStorageBasedView implements IViewCommandHandler,
		ISelectionUpdateHandler, IRadialMenuListener {

	public final static String VIEW_TYPE = "org.caleydo.view.filterpipeline";

	private FilterPipelineRenderStyle renderStyle;
	private DragAndDropController dragAndDropController;
	private SelectionManager selectionManager;
	private List<FilterItem<?>> filterList = new LinkedList<FilterItem<?>>();

	private FilterUpdateListener filterUpdateListener;
	private SetFilterTypeListener setFilterTypeListener;
	private ReEvaluateFilterListener reEvaluateFilterListener;

	private FilterType filterType = FilterType.CONTENT;

	/**
	 * First filter to be displayed. All filters before are hidden and the
	 * height of the first filter shall fill the whole view.
	 */
	private int firstFilter = 0;

	/**
	 * The filtered items of this filter will be ignored, so that we can see
	 * what the filter pipeline would look like without this filter.
	 * 
	 * Set to -1 if no filter should be ignored.
	 */
	private int ignoredFilter = -1;

	/**
	 * The filter which should be showed in full size, which showing all
	 * filtered items, even those which don't arrive as input because they have
	 * been filtered before.
	 * 
	 * Set to -1 if no filter should be showed full sized.
	 */
	private int fullSizedFilter = -1;

	/**
	 * The size a filter should have in OpenGL units. The x value is absolute,
	 * the y value is per 100 elements
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
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLFilterPipeline(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum);

		viewType = GLFilterPipeline.VIEW_TYPE;
		dragAndDropController = new DragAndDropController(this);
		glKeyListener = new GLFilterPipelineKeyListener(this);
	}

	@Override
	public void init(GL2 gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new FilterPipelineRenderStyle(viewFrustum);
		selectionManager = new SelectionManager(IDType.registerType("filter_"
				+ hashCode(), IDCategory.registerCategory("filter"), EStorageType.INT));

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		background = new Background(uniqueID, pickingManager, renderStyle);

		radialMenu = new RadialMenu(this, textureManager.getIconTexture(gl,
				EIconTextures.FILTER_PIPELINE_MENU_ITEM));
		// radialMenu.addEntry( null );
		// radialMenu.addEntry( null );
		radialMenu.addEntry(textureManager.getIconTexture(gl,
				EIconTextures.FILTER_PIPELINE_DELETE));
		radialMenu.addEntry(textureManager.getIconTexture(gl,
				EIconTextures.FILTER_PIPELINE_EDIT));

		// filterMenu = new FilterMenu(renderStyle, pickingManager, iUniqueID);

		if (textRenderer != null)
			textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 20));
		textRenderer.setColor(0, 0, 0, 1);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
	}

	@Override
	public void initLocal(GL2 gl) {
		// Register keyboard listener to GL2 canvas
		parentGLCanvas.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {
		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
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

			for (FilterItem<?> filter : filterList) {
				if (filter.getId() < firstFilter)
					// skip hidden filters
					continue;

				if (filter.getId() == firstFilter) {
					// show input for first filter
					textRenderer.renderText(gl, "" + filter.getInput().size(), 0.05f,
							filter.getRepresentation().getPosition().y()
									+ filter.getRepresentation().getHeightLeft() + 0.05f,
							0.9f, 0.004f, 20);
				} else {
					displayCollapseArrow(gl, filter.getId(), filter.getRepresentation()
							.getPosition().x()
							- 0.062f * filterSize.x() - 0.15f);
				}

				if (filter.getId() == fullSizedFilter
						&& Calendar.getInstance().getTimeInMillis() - mouseOverTimeStamp > 500) {

					Vec2f pos = filter.getRepresentation().getPosition();

					float fullHeightLeft = (filterList.get(0).getInput().size() * filterSize
							.y()) / 100.f;

					float fullHeightRight = ((filterList.get(0).getInput().size() - filter
							.getSizeVADelta()) * filterSize.y()) / 100.f;

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
		int iPickingID = pickingManager.getPickingID(uniqueID,
				EPickingType.FILTERPIPE_START_ARROW, id);
		float bottom = 0.025f;
		float halfSize = 0.075f;

		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		Texture arrowTexture = textureManager.getIconTexture(gl,
				EIconTextures.HEAT_MAP_ARROW);
		arrowTexture.enable();
		arrowTexture.bind();
		TextureCoords texCoords = arrowTexture.getImageTexCoords();

		gl.glPushName(iPickingID);

		gl.glMatrixMode(GL2.GL_MODELVIEW_MATRIX);
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

		arrowTexture.disable();

		gl.glPopAttrib();
	}

	@Override
	public String getShortInfo() {
		return "Filterpipeline " + filterType;
	}

	@Override
	public String getDetailedInfo() {
		return "Filterpipeline " + filterType;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {
		int newFullSizedFilter = -1;

		switch (pickingMode) {
		case CLICKED:
			dragAndDropController.clearDraggables();
		case MOUSE_OVER:
			// filterMenu.handleClearMouseOver();
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
				dragAndDropController.addDraggable(filterList.get(externalID)
						.getRepresentation());
				break;
			case RIGHT_CLICKED:
				radialMenu.show(externalID, mousePosition);
				break;
			case DRAGGED:
				if (dragAndDropController.hasDraggables()) {
					if (glMouseListener.wasRightMouseButtonPressed())
						dragAndDropController.clearDraggables();
					else if (!dragAndDropController.isDragging())
						dragAndDropController.startDragging();
				}
				dragAndDropController.setDropArea(filterList.get(externalID));
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
				// break; Fall through...
			case MOUSE_OVER:
				// reset all mouse over actions
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				// filterMenu.setFilter(null);
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
			}
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

		FilterItem<?> filter = filterList.get(externalId);

		switch (selection) {
		case 0:
			filter.triggerRemove();
			selectionManager.removeFromType(SelectionType.SELECTION, externalId);
			break;
		case 1:
			try {
				filter.showDetailsDialog();
			} catch (Exception e) {
				System.out.println("Failed to show details dialog: " + e);
			}
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
			float windowCoords[] = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl,
							glMouseListener.getPickedPoint().x,
							glMouseListener.getPickedPoint().y);

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

		filterSize = new Vec2f((viewFrustum.getWidth() - 0.1f)
				/ (filterList.size() - firstFilter),

		// 100 elements will be high exactly 1 unit. So we need to scale
		// it that the largest (== first) filter fits.
				(viewFrustum.getHeight() - 0.4f)
						/ (filterList.get(firstFilter).getInput().size() / 100.f));

		Vec2f filterPosition = new Vec2f(0.12f, renderStyle.FILTER_SPACING_BOTTOM), width = new Vec2f(
				filterSize.x(), 0);

		filterSize.setX(filterSize.x() * 0.945f);

		for (FilterItem<?> filter : filterList) {
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
		eventPublisher.addListener(ReEvaluateContentFilterListEvent.class,
				reEvaluateFilterListener);
		eventPublisher.addListener(ReEvaluateStorageFilterListEvent.class,
				reEvaluateFilterListener);

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

		Logger.log(new Status(IStatus.INFO, this.toString(), "Filterupdate: filterType="
				+ filterType));

		filterList.clear();
		int filterID = 0;

		for (Filter<?> filter : filterType == FilterType.CONTENT ? dataDomain
				.getContentFilterManager().getFilterPipe() : dataDomain
				.getStorageFilterManager().getFilterPipe()) {
			FilterItem<?> filterItem = new FilterItem(filterID++, filter, pickingManager,
					uniqueID);

			if (filter instanceof ContentMetaOrFilter)
				filterItem.setRepresentation(new FilterRepresentationMetaOrAdvanced(
						renderStyle, pickingManager, uniqueID));
			else
				filterItem.setRepresentation(new FilterRepresentation(renderStyle,
						pickingManager, uniqueID));

			filterList.add(filterItem);
		}

		// TODO move to separate function...
		VirtualArray<?, ?, ?> currentVA = filterType == FilterType.CONTENT ? dataDomain
				.getContentFilterManager().getBaseVA().clone() : dataDomain
				.getStorageFilterManager().getBaseVA().clone();

		for (FilterItem<?> filter : filterList) {
			// filter items
			filter.setInput(currentVA);
			currentVA = filter.getOutput().clone();
		}

		updateFilterSize();
	}

	@Override
	public void initData() {
		super.initData();

		performDataUncertaintyFilter();
	}

	private void performDataUncertaintyFilter() {

		ISet set = dataDomain.getSet();
		if (!set.containsUncertaintyData())
			return;

		if (set.getNormalizedUncertainty() != null)
			return;

		ContentFilter contentFilter = new ContentFilter();
		contentFilter.setDataDomain(dataDomain);
		contentFilter.setLabel("Signal-To-Noise Ratio Filter");

		set.calculateRawAverageUncertainty();

		Histogram histogram = HistogramCreator.createHistogram(set.getRawUncertainty());

		FilterRepresentationSNR filterRep = new FilterRepresentationSNR();
		filterRep.setFilter(contentFilter);
		filterRep.setSet(set);
		filterRep.setHistogram(histogram);
		contentFilter.setFilterRep(filterRep);
		contentFilter.openRepresentation();
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

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setControlPressed(boolean state) {
		bControlPressed = state;
	}

	@Override
	public void renderContext(boolean bRenderContext) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initLists() {
		// TODO Auto-generated method stub

	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}
}
