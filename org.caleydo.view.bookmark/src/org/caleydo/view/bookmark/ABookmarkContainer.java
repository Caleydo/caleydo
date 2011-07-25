package org.caleydo.view.bookmark;

import java.util.Iterator;

import javax.media.opengl.GL2;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.VABasedSelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.data.RemoveBookmarkEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.view.bookmark.GLBookmarkView.PickingIDManager;
import org.caleydo.view.bookmark.contextmenu.BookmarkContextMenuItemContainer;

/**
 * <p>
 * Base class for bookmark containers. A bookmark container is a container for
 * bookmarks belonging to a specific category, as specified in
 * {@link EIDCategory}. Therefore each container has to be uniquely associated
 * with a category.
 * </p>
 * <p>
 * Bookmark containers are no independent views, but depend on
 * {@link GLBookmarkView} for all the public interfaces to the rest of the
 * system.
 * </p>
 * <p>
 * The bookmark container holds a list of {@link ABookmark}s which are free to
 * some degree on how to display the information, for example by using text or
 * simple visualizations.
 * </p>
 * <p>
 * Every bookmark container holds its own selection manager which it has to use
 * to manage the selections of its items. The selections are synchronized with
 * the rest of the system.
 * </p>
 * <p>
 * Bookmark containers do not allow duplicate entries - every element can be
 * present only once.
 * </p>
 * 
 * @author Alexander Lex
 */
abstract class ABookmarkContainer<SelectionManagerType extends VABasedSelectionManager<?, ?, ?>>
		implements ILayoutedElement {

	/** The category of the container */
	IDCategory category;
	/** The type the container uses to internally store the data */
	IDType internalIDType;
	/**
	 * The containerDimensions (height, width, position, etc.) of the whole
	 * container
	 */
	// Dimensions containerDimensions;

	Column containerLayout;
	/** The name displayed as the heading in the sidebar */
	String categoryName;
	/**
	 * The list of bookmarks - each bookmark is unique, the ordering is relevant
	 */
	UniqueList<ABookmark> bookmarkItems;

	/**
	 * Reference to the internal picking id manger created by
	 * {@link GLBookmarkView}
	 */
	PickingIDManager pickingIDManager;

	/**
	 * The selection manager, that manages whether a particular element is
	 * selected in the bookmark list. It is a member of the abstract base class,
	 * but has to be created by the implementing instance.
	 */
	SelectionManagerType selectionManager;

	/**
	 * The creating and managing instance of this class. We need access to it
	 * here, because it provides all the view-specific facilities such as
	 * context menu etc.
	 */
	GLBookmarkView manager;

	/**
	 * Constructor
	 * 
	 * @param manager
	 *            The gl view managing the container.
	 * @param category
	 *            Every category in {@link EIDCategory} can have one bookmark
	 *            container, therefore you need to specify the concrete
	 *            category. The category should be specified by the concrete
	 *            subclass and therefore not be part of its constructor.
	 * @param internalIDType
	 *            the id type the container uses to internally store the
	 *            bookmarks
	 */
	ABookmarkContainer(GLBookmarkView manager, IDCategory category, IDType internalIDType) {
		this.internalIDType = internalIDType;
		this.manager = manager;
		this.category = category;
		this.categoryName = category.getCategoryName();
		this.pickingIDManager = manager.getBookmarkPickingIDManager();
		this.containerLayout = new Column("typeBookmarkColumn");

		// containerLayout.setDebug(true);
		containerLayout.setYDynamic(true);
		containerLayout.setRatioSizeX(1);
		containerLayout.setBottomUp(false);
		ContainerHeading heading = new ContainerHeading(manager);
		heading.setCaption(category.getCategoryName());
		containerLayout.append(heading.getLayout());
	}

	public ElementLayout getLayout() {
		return containerLayout;
	};

	/**
	 * Returns the containerDimensions {@link GLBookmarkView} needs to place the
	 * containers
	 * 
	 * @return
	 */
	// Dimensions getDimensions() {
	// return containerDimensions;
	// }

	/**
	 * Returns the category of the container
	 * 
	 * @return
	 */
	IDCategory getCategory() {
		return category;
	}

	/**
	 * Renders the heading for the category and the items.
	 * 
	 * @param gl
	 */
	void render(GL2 gl) {

	}

	/**
	 * Handles the picking events and triggers selection events
	 * 
	 * @param pickingMode
	 *            for example mouse-over or clicked
	 * @param externalID
	 *            the id specified when calling
	 *            {@link PickingIDManager#getPickingID(ABookmarkContainer, int)}
	 *            Internal to the specific BookmarkContainer
	 */
	void handleEvents(PickingType ePickingType, PickingMode pickingMode,
			Integer externalID, final Pick pick) {
		SelectionType selectionType;
		switch (ePickingType) {

		case BOOKMARK_ELEMENT:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;

				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;

				// PlatformUI.getWorkbench().getDisplay().asyncExec(new
				// Runnable() {
				// @Override
				// public void run() {
				// manager.getParentComposite()
				// .notifyListeners(SWT.MouseDown, new Event());
				//
				// Menu menu = new Menu(manager.getParentComposite().getShell(),
				// SWT.POP_UP);
				// Point point =
				// manager.getParentComposite()
				// .toDisplay(0, 0);
				// System.out.println(point);
				// menu.setLocation(point.x + pick.getPickedPoint().x, point.y
				// + pick.getPickedPoint().y);
				// MenuItem item = new MenuItem(menu, SWT.PUSH);
				// item.setText("Popup");
				// item = new MenuItem(menu, SWT.PUSH);
				// item.setText("Popup1");
				// item = new MenuItem(menu, SWT.PUSH);
				// item.setText("Popup2");
				// item = new MenuItem(menu, SWT.PUSH);
				// item.setText("Popup3");
				// item = new MenuItem(menu, SWT.PUSH);
				// item.setText("Popup4");
				// item = new MenuItem(menu, SWT.PUSH);
				// item.setText("Popup5");
				// //
				// manager.getParentComposite().setMenu(menu);
				// menu.setVisible(true);
				// }
				// });

				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//
////						final Shell shell = new Shell(SWT.EMBEDDED);// | SWT.NO_TRIM | SWT.ON_TOP);
//
////						final Shell shell = new Shell(manager.getParentComposite().getShell());
//						final Shell shell = manager.getParentComposite().getShell();
//
//						shell.notifyListeners(SWT.MouseDown, new Event());
//
//						final Menu menu = new Menu(shell, SWT.POP_UP);
//						Point point = manager.getParentComposite().toDisplay(0, 0);
//						System.out.println(point);
//						menu.setLocation(point.x + pick.getPickedPoint().x, point.y
//								+ pick.getPickedPoint().y);
////						shell.setLocation(point.x + pick.getPickedPoint().x, point.y
////								+ pick.getPickedPoint().y);
//						MenuItem item = new MenuItem(menu, SWT.PUSH);
//						item.setText("Popup");
//						item = new MenuItem(menu, SWT.PUSH);
//						item.setText("Popup1");
//						item = new MenuItem(menu, SWT.PUSH);
//						item.setText("Popup2");
//						item = new MenuItem(menu, SWT.PUSH);
//						item.setText("Popup3");
//						item = new MenuItem(menu, SWT.PUSH);
//						item.setText("Popup4");
//						item = new MenuItem(menu, SWT.PUSH);
//						item.setText("Popup5");
//						
//						menu.addMenuListener(new MenuListener() {
//							
//							@Override
//							public void menuShown(MenuEvent e) {
////								shell.setMenu(menu);
////								shell.notifyListeners(SWT.MouseDown, new Event());
//							}
//							
//							@Override
//							public void menuHidden(MenuEvent e) {
//				
////								shell.close();
//							}
//						});
//
//						shell.setMenu(menu);
//						menu.setVisible(true);
////						shell.setVisible(false);
////						shell.setSize(100, 100);
////						 shell.open();
//					}
//				});

				// PopupMenu menu = new PopupMenu();
				// MenuItem item = new MenuItem();
				// menu.add(item);
				// menu.addNotify();
				// menu.show(manager.getParentGLCanvas(), 0, 0);

				BookmarkContextMenuItemContainer bookmarkContextMenuItemContainer = new BookmarkContextMenuItemContainer();
				bookmarkContextMenuItemContainer.tableID(internalIDType, externalID);
				ContextMenu contextMenu = manager.getContextMenu();
				contextMenu.addItemContanier(bookmarkContextMenuItemContainer);

				if (manager.isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(), manager
							.getParentGLCanvas().getWidth(), manager.getParentGLCanvas()
							.getHeight());
					contextMenu.setMasterGLView(manager);
				}
				break;

			default:
				return;
			}

			if (selectionType == SelectionType.SELECTION) {

			}
			selectionManager.clearSelection(selectionType);
			selectionManager.addToType(selectionType, externalID);

			ISelectionDelta selectionDelta = selectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setDataDomainID(manager.getDataDomain().getDataDomainID());
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
			break;

		case BOOKMARK_CONTAINER_HEADING:

			break;
		}

	}

	/**
	 * Handles new bookmarks. Uses the information in the event.
	 * 
	 * @param <IDDataType>
	 *            The data type of the id, typically Integer or String
	 * @param event
	 *            The event containing the information about the new bookmark to
	 *            be added.
	 */
	abstract <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event);

	/**
	 * Handles the removal of bookmarks by using the informatinon in the event.
	 * 
	 * @param <IDDataType>
	 *            The data type of the id, typically Integer or String
	 * @param event
	 *            The event containing the information about the bookmark to be
	 *            removed.
	 */
	<IDDataType> void handleRemoveBookmarkEvent(RemoveBookmarkEvent<IDDataType> event) {
		Integer id = null;
		for (IDDataType tempID : event.getBookmarks()) {
			if (tempID instanceof Integer) {
				id = (Integer) tempID;
			} else
				throw new IllegalStateException("Can not handle strings for experiments");

			Iterator<ABookmark> iterator = bookmarkItems.iterator();

			while (iterator.hasNext()) {
				if (iterator.next().getID() == id) {
					iterator.remove();
					selectionManager.remove(id);
				}
			}

		}
		updateContainerSize();
	}

	/**
	 * Handles updates to the selections coming from external sources
	 * 
	 * @param selectionDelta
	 *            the information about the updates
	 */
	void handleSelectionUpdate(ISelectionDelta selectionDelta) {
		selectionManager.setDelta(selectionDelta);
	}

	/**
	 * Handles updates of the selection manager triggered by external sources.
	 * 
	 * @param selectionCommand
	 *            the information what to do with the selection manager
	 */
	void handleSelectionCommand(SelectionCommand selectionCommand) {
		selectionManager.executeSelectionCommand(selectionCommand);
	}

	void updateContainerSize() {
		// containerDimensions.setHeight(0.5f);
		// containerDimensions.setHeight(0);
		// containerDimensions.increaseHeight(BookmarkRenderStyle.CONTAINER_HEADING_SIZE);
		//
		// for (ABookmark bookmark : bookmarkItems) {
		// containerDimensions.increaseHeight(bookmark.getDimensions().getHeight()
		// * 2);
		// }

	}
}
