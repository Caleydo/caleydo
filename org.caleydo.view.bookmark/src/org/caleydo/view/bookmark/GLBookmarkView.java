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
package org.caleydo.view.bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.event.data.RemoveBookmarkEvent;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.eclipse.swt.widgets.Composite;

/**
 * The list heat map that shows elements on the right of a view that have been
 * selected. It is registered to special listeners that are triggered in such a
 * event. Other than that it is equivalent to the {@link GLHeatMap}
 * 
 * @author Alexander Lex
 */
public class GLBookmarkView extends ATableBasedView {

	public static String VIEW_TYPE = "org.caleydo.view.bookmark";
	
	public static String VIEW_NAME = "Bookmarks";


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

	protected ATableBasedDataDomain dataDomain;

	/** The class responsible for rendering the template */
	private LayoutManager layoutManager;

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

			int pickingID = pickingManager.getPickingID(uniqueID,
					PickingType.BOOKMARK_ELEMENT, idCount);
			pickingIDToBookmarkContainer.put(idCount++, new Pair<IDCategory, Integer>(
					container.getCategory(), privateID));
			return pickingID;
		}

		private Pair<IDCategory, Integer> getPrivateID(int externalID) {
			return pickingIDToBookmarkContainer.get(externalID);
		}

		private void reset() {
			idCount = 0;
			pickingIDToBookmarkContainer = new HashMap<Integer, Pair<IDCategory, Integer>>();
		}
	}

	/**
	 * Constructor.
	 */
	public GLBookmarkView(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
	
		renderStyle = new BookmarkRenderStyle(viewFrustum);

		bookmarkContainers = new ArrayList<ABookmarkContainer<?>>();
		hashCategoryToBookmarkContainer = new HashMap<IDCategory, ABookmarkContainer<?>>();

		pickingIDManager = new PickingIDManager();

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		layoutManager.updateLayout();
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
		if (isDisplayListDirty) {
			isDisplayListDirty = false;
			buildDisplayList(gl, displayListIndex);
		}

		gl.glCallList(displayListIndex);
		checkForHits(gl);
	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
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
		layoutManager.render(gl);
		gl.glEndList();
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		switch (pickingType) {
		case BOOKMARK_ELEMENT:
			Pair<IDCategory, Integer> pair = pickingIDManager.getPrivateID(externalID);
			hashCategoryToBookmarkContainer.get(pair.getFirst()).handleEvents(
					pickingType, pickingMode, pair.getSecond(), pick);
		}
	}

	/**
	 * @param <IDDataType>
	 * @param event
	 */
	public <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {

		if (dataDomain.getDataDomainID() != event.getDataDomainID())
			return;

		ABookmarkContainer<?> container = hashCategoryToBookmarkContainer.get(event
				.getIDType().getIDCategory());
		if (container == null)
			throw new IllegalStateException("Can not handle bookmarks of type "
					+ event.getIDType().getIDCategory());

		container.handleNewBookmarkEvent(event);
		layoutManager.updateLayout();
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
		layoutManager.updateLayout();
		setDisplayListDirty();
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		init(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedBookmarkView serializedForm = new SerializedBookmarkView(
				this);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
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

	CaleydoTextRenderer getMinSizeTextRenderer() {
		return textRenderer;
	}

	PickingIDManager getBookmarkPickingIDManager() {
		return pickingIDManager;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		Column mainColumn = new Column("baseBookmarkColumn");
		mainColumn.setFrameColor(1, 0, 0, 1);
		mainColumn.setYDynamic(true);
		mainColumn.setBottomUp(false);
		// mainColumn.setPixelGLConverter(pixelGLConverter);
		layoutManager.setBaseElementLayout(mainColumn);

		RecordBookmarkContainer geneContainer = new RecordBookmarkContainer(this,
				dataDomain.getRecordIDCategory(), dataDomain.getRecordIDCategory()
						.getPrimaryMappingType());
		mainColumn.append(geneContainer.getLayout());

		hashCategoryToBookmarkContainer.put(dataDomain.getRecordIDCategory(),
				geneContainer);
		bookmarkContainers.add(geneContainer);

		DimensionBookmarkContainer experimentContainer = new DimensionBookmarkContainer(
				this);
		mainColumn.append(experimentContainer.getLayout());
		hashCategoryToBookmarkContainer.put(dataDomain.getDimensionIDCategory(),
				experimentContainer);
		bookmarkContainers.add(experimentContainer);
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		// TODO Auto-generated method stub
		
	}
}
