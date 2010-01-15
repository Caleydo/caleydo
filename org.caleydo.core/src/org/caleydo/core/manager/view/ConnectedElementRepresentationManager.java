package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.view.selection.AddSelectionEvent;
import org.caleydo.core.manager.event.view.selection.ClearConnectionsEvent;
import org.caleydo.core.manager.event.view.selection.ClearTransformedConnectionsEvent;
import org.caleydo.core.manager.event.view.selection.NewConnectionsEvent;
import org.caleydo.core.manager.execution.ADisplayLoopEventHandler;
import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.listener.AddSelectionListener;
import org.caleydo.core.manager.view.listener.ClearConnectionsListener;
import org.caleydo.core.manager.view.listener.ClearTransformedConnectionsListener;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;

/**
 * <p>
 * Selection manager that manages selections and their {@link SelectedElementRep}.
 * </p>
 * <p>
 * The manager is able to identify identical selections in different views. Selections have selection
 * representations. Selection representations store their containing view and the x/y/z position in the view
 * area.
 * </p>
 * <p>
 * The manager manages also the transformed selections vertices of the selections for remote rendered views
 * and the projected x/y canvas coordinates. The projected coordinates are used to draw connection lines
 * across window borders e.g. with the help of a IGroupwareManager.
 * </p>
 * <p>
 * Adding and clearing of selections is done with events. Therefore this manager should is a
 * {@link ADisplayLoopEventHandler} and should be added to a {@link DisplayLoopExecution} to handle the
 * incoming events during each display loop cycle.
 * </p>
 * <p>
 * The purpose of this manager is to make selections available to an external instance that connects them, for
 * example the {@link AGLConnectionLineRenderer}
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public class ConnectedElementRepresentationManager
	extends ADisplayLoopEventHandler {

	/** Stored reference for common usage */
	protected IGeneralManager generalManager;

	/** Stored reference for common usage */
	protected IEventPublisher eventPublisher;

	/** Stores a {@link ConnectionMap} for each possible type as originally provided by the views. */
	HashMap<EIDType, ConnectionMap> sourceConnectionsByType;

	/**
	 * Stores a {@link ConnectionMap} with only transformed selection-points as defined by the transformation
	 * needed within remote rendered views.
	 */
	HashMap<EIDType, ConnectionMap> transformedConnectionsByType;

	/**
	 * Stores {@link CanvasConnectionMap}s with only transformed selection-points as defined by the
	 * transformation needed within remote rendered views.
	 */
	HashMap<EIDType, CanvasConnectionMap> canvasConnectionsByType;

	ClearConnectionsListener clearConnectionsListener;
	ClearTransformedConnectionsListener clearTransformedConnectionsListener;
	AddSelectionListener addSelectionListener;

	/** <code>true</code if there are new vertices in the list of 2D canvas conneciton vertices */
	protected boolean newCanvasVertices = false;

	/**
	 * Constructor.
	 */
	protected ConnectedElementRepresentationManager() {
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

		sourceConnectionsByType = new HashMap<EIDType, ConnectionMap>();
		transformedConnectionsByType = new HashMap<EIDType, ConnectionMap>();
		canvasConnectionsByType = new HashMap<EIDType, CanvasConnectionMap>();

		registerEventListeners();
	}

	/**
	 * Sends event to add a selection to a specific tree. The data type is determined by the
	 * {@link SelectedElementRep}, the connection id has to be specified manually
	 * 
	 * @param iConnectionID
	 *            the connection ID - one connection id per connection line tree
	 * @param selectedElementRep
	 *            the selected element rep associated with the tree specified
	 */
	public void addSelection(int connectionID, final SelectedElementRep selectedElementRep) {
		AddSelectionEvent event = new AddSelectionEvent();
		event.setConnectionID(connectionID);
		event.setSelectedElementRep(selectedElementRep);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Adds a selection to a specific tree. The data type is determined by the {@link SelectedElementRep}, the
	 * connection id has to be specified manually
	 * 
	 * @param iConnectionID
	 *            the connection ID - one connection id per connection line tree
	 * @param selectedElementRep
	 *            the selected element rep associated with the tree specified
	 */
	public void handleAddSelectionEvent(int connectionID, final SelectedElementRep selectedElementRep) {
		ConnectionMap tmpHash = sourceConnectionsByType.get(selectedElementRep.getIDType());

		if (tmpHash == null) {
			tmpHash = new ConnectionMap();
			sourceConnectionsByType.put(selectedElementRep.getIDType(), tmpHash);
		}

		if (!tmpHash.containsKey(connectionID)) {
			tmpHash.put(connectionID, new SelectedElementRepList());
		}

		tmpHash.get(connectionID).add(selectedElementRep);
		eventPublisher.triggerEvent(new NewConnectionsEvent());
	}

	/**
	 * Remove a particular selection
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 */
	public void removeSelection(final int iElementID, SelectedElementRep selectedElementRep) {

		if (sourceConnectionsByType.containsKey(iElementID)) {
			sourceConnectionsByType.get(iElementID).remove(selectedElementRep);
			sourceConnectionsByType.remove(iElementID);
		}
	}

	/**
	 * Replace all selections with new selection
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 */
	public void replaceSelection(final int iElementID, SelectedElementRep selectedElementRep) {
		clear(selectedElementRep.getIDType());
		handleAddSelectionEvent(iElementID, selectedElementRep);
	}

	/**
	 * Get a list of all occurring {@link EIDTypes}
	 * 
	 * @return a Set of EIDType
	 */
	public Set<EIDType> getOccuringIDTypes() {
		return sourceConnectionsByType.keySet();
	}

	/**
	 * Get a list or IDs of all selected elements of a type
	 * 
	 * @return a Set of IDs
	 */
	public Set<Integer> getIDList(EIDType idType) {
		return sourceConnectionsByType.get(idType).keySet();
	}

	/**
	 * Get a representation of a particular element
	 * 
	 * @param iDType
	 *            the type of the object to be connected (e.g. gene expression, clinical)
	 * @param iElementID
	 *            the id of the object to be connected
	 * @return a list of the representations of the points
	 */
	public ArrayList<SelectedElementRep> getSelectedElementRepsByElementID(EIDType idType,
		final int iElementID) {

		ArrayList<SelectedElementRep> tempList = sourceConnectionsByType.get(idType).get(iElementID);

		if (tempList == null)
			throw new IllegalArgumentException("SelectionManager: No representations for this element ID");
		return tempList;
	}

	/**
	 * Clear all selections and representations
	 */
	public void clearAll() {
		sourceConnectionsByType.clear();
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
	}

	/**
	 * Sends event to clear all selections of a given type
	 */
	public void clear(EIDType idType) {
		ClearConnectionsEvent event = new ClearConnectionsEvent();
		event.setIdType(idType);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Clear all selections of a given type
	 */
	public void handleClearEvent(EIDType idType) {
		ConnectionMap tmp = sourceConnectionsByType.get(idType);
		if (tmp != null) {
			tmp.clear();
		}
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
	}

	/**
	 * Clear all selections of a given type that belong to a certain view
	 * 
	 * @param idType
	 *            the type to be cleared
	 * @param iViewID
	 *            the id of the view
	 */
	public void clearByViewAndType(EIDType idType, int iViewID) {
		ConnectionMap hashReps = sourceConnectionsByType.get(idType);
		if (hashReps == null)
			return;
		for (int iElementID : hashReps.keySet()) {
			ArrayList<SelectedElementRep> alRep = hashReps.get(iElementID);

			Iterator<SelectedElementRep> iterator = alRep.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getSourceViewID() == iViewID) {
					iterator.remove();
				}
			}
		}
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
	}

	/**
	 * Clear all elements of a view, regardless of their type.
	 * 
	 * @param iViewID
	 *            the view which id's should be removed
	 */
	public void clearByView(int iViewID) {
		for (EIDType idType : sourceConnectionsByType.keySet()) {
			clearByViewAndType(idType, iViewID);
		}
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
	}

	public void clearByConnectionID(EIDType idType, int iConnectionID) {
		sourceConnectionsByType.get(idType).remove(iConnectionID);
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
	}

	public void clearTransformedConnections() {
		ClearTransformedConnectionsEvent event = new ClearTransformedConnectionsEvent();
		eventPublisher.triggerEvent(event);
	}

	public void handleClearTransformedConnectionsEvent() {
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
		GeneralManager.get().getEventPublisher().triggerEvent(new NewConnectionsEvent());
	}

	public void clearCanvasConnections() {
		canvasConnectionsByType.clear();
	}

	public void doViewRelatedTransformation(GL gl, ISelectionTransformer transformer) {
		boolean newTransformedPoints = false;
		newTransformedPoints = transformer.transform(sourceConnectionsByType, transformedConnectionsByType);

		IGroupwareManager gm = GeneralManager.get().getGroupwareManager();
		// if (gm != null && gm.isGroupwareConnectionLinesEnabled()) {
		if (newTransformedPoints) {
			String networkName = null;
			if (gm != null) {
				networkName = gm.getNetworkManager().getNetworkName();
			}
			else {
				networkName = "visLinks";
			}
			transformer.project(gl, networkName, transformedConnectionsByType, canvasConnectionsByType);
			newCanvasVertices = true;
		}
		// }
	}

	/**
	 * To be executed during the display loop with help of a {@link DisplayLoopExecution}
	 */
	@Override
	public void run() {
		processEvents();
	}

	/**
	 * Registers the event listeners.
	 */
	private void registerEventListeners() {
		clearConnectionsListener = new ClearConnectionsListener();
		clearConnectionsListener.setHandler(this);
		eventPublisher.addListener(ClearConnectionsEvent.class, clearConnectionsListener);

		clearTransformedConnectionsListener = new ClearTransformedConnectionsListener();
		clearTransformedConnectionsListener.setHandler(this);
		eventPublisher.addListener(ClearTransformedConnectionsEvent.class,
			clearTransformedConnectionsListener);

		addSelectionListener = new AddSelectionListener();
		addSelectionListener.setHandler(this);
		eventPublisher.addListener(AddSelectionEvent.class, addSelectionListener);
	}

	@SuppressWarnings("unused")
	private void unregisterEventListeners() {
		if (clearConnectionsListener != null) {
			eventPublisher.removeListener(clearConnectionsListener);
			clearConnectionsListener = null;
		}
		if (clearTransformedConnectionsListener != null) {
			eventPublisher.removeListener(clearTransformedConnectionsListener);
			clearTransformedConnectionsListener = null;
		}
		if (addSelectionListener != null) {
			eventPublisher.removeListener(addSelectionListener);
			addSelectionListener = null;
		}
	}

	/**
	 * Gets all {@link CanvasConnectionMap}s by {@link EIDType} containing the finally transformed and
	 * to-canvas-projected selection vertices.
	 * 
	 * @return 2D canvas selection vertices
	 */
	public HashMap<EIDType, CanvasConnectionMap> getCanvasConnectionsByType() {
		return canvasConnectionsByType;
	}

	/**
	 * Gets all {@link ConnectionMap}s by {@link EIDType} containing transformed selection vertices. The
	 * remoteViewID field of the contained {@link SelectedElementRep}s references to the view, the coordinates
	 * are related to.
	 * 
	 * @return
	 */
	public HashMap<EIDType, ConnectionMap> getTransformedConnectionsByType() {
		return transformedConnectionsByType;
	}

	/**
	 * <code>true</code> if there are new vertices for 2D connection line drawing, <code>false</code>
	 * otherwise
	 * 
	 * @return flag if new connection line vertices exists
	 */
	public boolean isNewCanvasVertices() {
		return newCanvasVertices;
	}

	/**
	 * Sets the status of flag to indicate if new 2D connection line vertices exists.
	 * 
	 * @newCanvasVertices new value for the flag
	 */
	public void setNewCanvasVertices(boolean newCanvasVertices) {
		this.newCanvasVertices = newCanvasVertices;
	}

}
