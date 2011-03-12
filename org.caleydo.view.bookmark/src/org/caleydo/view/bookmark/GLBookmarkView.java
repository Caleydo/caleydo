package org.caleydo.view.bookmark;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.data.RemoveBookmarkEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * The list heat map that shows elements on the right of a view that have been
 * selected. It is registered to special listeners that are triggered in such a
 * event. Other than that it is equivalent to the {@link GLHeatMap}
 * 
 * @author Alexander Lex
 */
public class GLBookmarkView extends AGLView implements
		IDataDomainBasedView<ASetBasedDataDomain>, ISelectionUpdateHandler,
		ISelectionCommandHandler {

	public final static String VIEW_ID = "org.caleydo.view.bookmark";

	// private ColorMapping colorMapper;

	protected BookmarkRenderStyle renderStyle;

	/** A hash map that associated the Category with the container */
	private HashMap<IDCategory, ABookmarkContainer<?>> hashCategoryToBookmarkContainer;
	/** A list of bookmark containers, to preserve the ordering */
	private ArrayList<ABookmarkContainer<?>> bookmarkContainers;

	private BookmarkListener bookmarkListener;
	private SelectionUpdateListener selectionUpdateListener;
	private SelectionCommandListener selectionCommandListener;

	private PickingIDManager pickingIDManager;

	private RemoveBookmarkListener removeBookmarkListener;

	protected ASetBasedDataDomain dataDomain;

	private boolean contentChanged = true;

	/** The class responsible for rendering the template */
	private LayoutManager templateRenderer;

	/** The render template */
	private BookmarkTemplate bookmarkTemplate;

	private MinSizeTextRenderer textRenderer;

	class PickingIDManager {
		/**
		 * A hash map that hashes the picking ID of an element to the
		 * BookmarkContainer and the id internal to the bookmark container
		 */
		private HashMap<Integer, Pair<IDCategory, Integer>> pickingIDToBookmarkContainer;
		private int idCount = 0;

		private PickingIDManager() {
			pickingIDToBookmarkContainer = new HashMap<Integer, Pair<IDCategory, Integer>>();
		}

		public int getPickingID(ABookmarkContainer<?> container, int privateID) {

			int pickingID = pickingManager.getPickingID(iUniqueID,
					EPickingType.BOOKMARK_ELEMENT, idCount);
			pickingIDToBookmarkContainer.put(idCount++, new Pair<IDCategory, Integer>(
					container.getCategory(), privateID));
			return pickingID;
		}

		private Pair<IDCategory, Integer> getPrivateID(int iExternalID) {
			return pickingIDToBookmarkContainer.get(iExternalID);
		}

		private void reset() {
			idCount = 0;
			pickingIDToBookmarkContainer = new HashMap<Integer, Pair<IDCategory, Integer>>();
		}
	}

	/**
	 * Constructor.
	 */
	public GLBookmarkView(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum, true);
		viewType = GLBookmarkView.VIEW_ID;

		renderStyle = new BookmarkRenderStyle(viewFrustum);

		bookmarkContainers = new ArrayList<ABookmarkContainer<?>>();
		hashCategoryToBookmarkContainer = new HashMap<IDCategory, ABookmarkContainer<?>>();

		pickingIDManager = new PickingIDManager();

		templateRenderer = new LayoutManager(viewFrustum);
		bookmarkTemplate = new BookmarkTemplate();
		templateRenderer.setTemplate(bookmarkTemplate);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		Rectangle2D bounds = parentGLCanvas.getBounds();
		textRenderer.setWindowSize(bounds.getWidth(), bounds.getHeight());
		templateRenderer.updateLayout();

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		bookmarkListener = new BookmarkListener();
		bookmarkListener.setHandler(this);
		eventPublisher.addListener(BookmarkEvent.class, bookmarkListener);

		removeBookmarkListener = new RemoveBookmarkListener();
		removeBookmarkListener.setHandler(this);
		eventPublisher.addListener(RemoveBookmarkEvent.class, removeBookmarkListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (bookmarkListener != null) {
			eventPublisher.removeListener(bookmarkListener);
			bookmarkListener = null;
		}

		if (removeBookmarkListener != null) {
			eventPublisher.removeListener(removeBookmarkListener);
			removeBookmarkListener = null;
		}

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}
	}

	@Override
	public void display(GL2 gl) {

		gl.glCallList(iGLDisplayListToCall);
	}

	@Override
	protected void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
		// pickingIDManager.reset();
	}

	@Override
	public void displayRemote(GL2 gl) {

		if (bIsDisplayListDirtyRemote) {
			bIsDisplayListDirtyRemote = false;
			buildDisplayList(gl, iGLDisplayListIndexRemote);
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
		// pickingIDManager.reset();
	}

	/**
	 * Builds a display list of graphical elements that do not have to be
	 * updated in every frame.
	 * 
	 * @param gl
	 *            GL2 context.
	 * @param iGLDisplayListIndex
	 *            Index of display list.
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);
		templateRenderer.render(gl);
		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		switch (pickingType) {
		case BOOKMARK_ELEMENT:
			Pair<IDCategory, Integer> pair = pickingIDManager.getPrivateID(iExternalID);
			hashCategoryToBookmarkContainer.get(pair.getFirst()).handleEvents(
					pickingType, pickingMode, pair.getSecond(), pick);
		}
	}

	/**
	 * @param <IDDataType>
	 * @param event
	 */
	public <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {

		ABookmarkContainer<?> container = hashCategoryToBookmarkContainer.get(event
				.getIDType().getIDCategory());
		if (container == null)
			throw new IllegalStateException("Can not handle bookmarks of type "
					+ event.getIDType().getIDCategory());

		container.handleNewBookmarkEvent(event);
		templateRenderer.updateLayout();
		setDisplayListDirty();
	}

	public <IDDataType> void handleRemoveBookmarkEvent(
			RemoveBookmarkEvent<IDDataType> event) {
		ABookmarkContainer<?> container = hashCategoryToBookmarkContainer.get(event
				.getIDType().getIDCategory());
		if (container == null)
			throw new IllegalStateException("Can not handle bookmarks of type "
					+ event.getIDType().getIDCategory());

		container.handleRemoveBookmarkEvent(event);
		templateRenderer.updateLayout();
		setDisplayListDirty();
	}

	@Override
	public void init(GL2 gl) {
		Rectangle2D bounds = parentGLCanvas.getBounds();
		textRenderer = new MinSizeTextRenderer();
		textRenderer.setWindowSize(bounds.getWidth(), bounds.getHeight());
		bookmarkTemplate.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
	}

	@Override
	protected void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		init(gl);
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		init(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedBookmarkView serializedForm = new SerializedBookmarkView(
				dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// EIDCategory category = ;
		ABookmarkContainer<?> container = hashCategoryToBookmarkContainer
				.get(selectionDelta.getIDType().getIDCategory());
		if (container != null)
			container.handleSelectionUpdate(selectionDelta);

		setDisplayListDirty();
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
		ABookmarkContainer<?> container = hashCategoryToBookmarkContainer.get(category);
		if (container != null)
			container.handleSelectionCommand(selectionCommand);

		setDisplayListDirty();
	}

	ContextMenu getContextMenu() {
		return contextMenu;
	}

	MinSizeTextRenderer getMinSizeTextRenderer() {
		return textRenderer;
	}

	PickingIDManager getPickingIDManager() {
		return pickingIDManager;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		Column mainColumn = new Column();
		mainColumn.setYDynamic(true);
		mainColumn.setBottomUp(false);
		// mainColumn.setPixelGLConverter(pixelGLConverter);
		bookmarkTemplate.setBaseElementLayout(mainColumn);

		ContentBookmarkContainer geneContainer = new ContentBookmarkContainer(this,
				dataDomain.getContentIDCategory(),
				dataDomain.getPrimaryContentMappingType());
		mainColumn.appendElement(geneContainer.getLayout());

		hashCategoryToBookmarkContainer.put(dataDomain.getContentIDCategory(),
				geneContainer);
		bookmarkContainers.add(geneContainer);

		StorageBookmarkContainer experimentContainer = new StorageBookmarkContainer(this);
		mainColumn.appendElement(experimentContainer.getLayout());
		hashCategoryToBookmarkContainer.put(dataDomain.getStorageIDCategory(),
				experimentContainer);
		bookmarkContainers.add(experimentContainer);
	}

	public Menu createEditPopup() {
		Composite comp = getParentGLCanvas().getParentComposite();

		MenuManager menu = new MenuManager();
		Menu quickMenu = menu.createContextMenu(comp);
		Point location = new Point(100, 100);
		final MenuItem item = new MenuItem(quickMenu, SWT.PUSH);
		quickMenu.setLocation(location);
		quickMenu.setVisible(true);
		return quickMenu;

		// final Menu contextMenu = new
		// Menu(getParentGLCanvas().getParentComposite());

		// item.setText("Text");
		// return contextMenu;

	}
}
