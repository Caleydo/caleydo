package org.caleydo.view.bookmark;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
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
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

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
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLBookmarkView(GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum) {

		super(glCanvas, label, viewFrustum, true);
		viewType = GLBookmarkView.VIEW_ID;

		renderStyle = new BookmarkRenderStyle(viewFrustum);

		bookmarkContainers = new ArrayList<ABookmarkContainer<?>>();
		hashCategoryToBookmarkContainer = new HashMap<IDCategory, ABookmarkContainer<?>>();

		pickingIDManager = new PickingIDManager();
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
	public void display(GL gl) {

		gl.glCallList(iGLDisplayListToCall);
	}

	@Override
	protected void displayLocal(GL gl) {

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
		pickingIDManager.reset();
	}

	@Override
	public void displayRemote(GL gl) {

		if (bIsDisplayListDirtyRemote) {
			bIsDisplayListDirtyRemote = false;
			buildDisplayList(gl, iGLDisplayListIndexRemote);
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
		pickingIDManager.reset();
	}

	/**
	 * Builds a display list of graphical elements that do not have to be
	 * updated in every frame.
	 * 
	 * @param gl
	 *            GL context.
	 * @param iGLDisplayListIndex
	 *            Index of display list.
	 */
	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		float currentHeight = viewFrustum.getHeight() - BookmarkRenderStyle.TOP_SPACING;
		for (ABookmarkContainer<?> container : bookmarkContainers) {
			container.getDimensions().setOrigins(0.0f, currentHeight);
			container.getDimensions().setWidth(viewFrustum.getWidth());
			currentHeight -= container.getDimensions().getHeight();
			container.render(gl);
		}

		gl.glEndList();

		if (contentChanged) {
			float height = 20; //TODO determine dynamically
			float width = 8; // TODO determine dynamically
			int minViewportHeight = (int) (parentGLCanvas.getHeight()
					/ viewFrustum.getHeight() * height) + 10;
			int minViewportWidth = (int) (parentGLCanvas.getWidth()
					/ viewFrustum.getWidth() * width) + 10;
			renderStyle.setMinViewDimensions(minViewportWidth, minViewportHeight, this);
			if (parentGLCanvas.getHeight() <= 0) {
				// Draw again in next frame where the viewport size is hopefully
				// correct
				setDisplayListDirty();
			}
			else {
				// at the moment we do not consider a content change and make the size adaption only once
				contentChanged = false;
			}
		}
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
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode ePickingMode, int iExternalID, Pick pick) {
		switch (ePickingType) {
		case BOOKMARK_ELEMENT:
			Pair<IDCategory, Integer> pair = pickingIDManager.getPrivateID(iExternalID);
			hashCategoryToBookmarkContainer.get(pair.getFirst()).handleEvents(
					ePickingType, ePickingMode, pair.getSecond(), pick);
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
		
		setDisplayListDirty();
	}

	@Override
	public void init(GL gl) {
	}

	@Override
	protected void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		init(gl);
	}

	@Override
	public void initRemote(GL gl, AGLView glParentView, GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

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

	CaleydoTextRenderer getTextRenderer() {
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

		ContentBookmarkContainer geneContainer = new ContentBookmarkContainer(this,
				dataDomain.getContentIDCategory(),
				dataDomain.getPrimaryContentMappingType());
		hashCategoryToBookmarkContainer.put(dataDomain.getContentIDCategory(),
				geneContainer);
		bookmarkContainers.add(geneContainer);

		ExperimentBookmarkContainer experimentContainer = new ExperimentBookmarkContainer(
				this);
		hashCategoryToBookmarkContainer.put(dataDomain.getStorageIDCategory(),
				experimentContainer);
		bookmarkContainers.add(experimentContainer);
	}
}
