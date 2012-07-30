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
package org.caleydo.core.view.vislink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.selection.AddSelectionEvent;
import org.caleydo.core.event.view.selection.ClearConnectionsEvent;
import org.caleydo.core.event.view.selection.ClearTransformedConnectionsEvent;
import org.caleydo.core.event.view.selection.NewConnectionsEvent;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.execution.ADisplayLoopEventHandler;
import org.caleydo.core.util.execution.DisplayLoopExecution;
import org.caleydo.core.view.listener.AddSelectionListener;
import org.caleydo.core.view.listener.ClearConnectionsListener;
import org.caleydo.core.view.listener.ClearTransformedConnectionsListener;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;

/**
 * <p>
 * Selection manager that manages selections and their
 * {@link ElementConnectionInformation}.
 * </p>
 * <p>
 * The manager is able to identify identical selections in different views.
 * Selections have selection representations. Selection representations store
 * their containing view and the x/y/z position in the view area.
 * </p>
 * <p>
 * Multiple connection trees, distinguished by their ID Type are possible. This
 * allows to show relations between elements of different IDTypes at the same
 * type.
 * </p>
 * <p>
 * It is defined in the preference store whether Visual Links should be drawn on
 * Mouse-Over or on Click. The selection manager checks this and only allows
 * elements of the correct SelectionType to be added.
 * </p>
 * <p>
 * The manager manages also the transformed selections vertices of the
 * selections for remote rendered views and the projected x/y canvas
 * coordinates. The projected coordinates are used to draw connection lines
 * across window borders e.g. with the help of a IGroupwareManager.
 * </p>
 * <p>
 * Adding and clearing of selections is done with events. Therefore this manager
 * should is a {@link ADisplayLoopEventHandler} and should be added to a
 * {@link DisplayLoopExecution} to handle the incoming events during each
 * display loop cycle.
 * </p>
 * <p>
 * The purpose of this manager is to make selections available to an external
 * instance that connects them, for example the
 * {@link AGLConnectionLineRenderer}
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public class ConnectedElementRepresentationManager
	extends ADisplayLoopEventHandler {

	private volatile static ConnectedElementRepresentationManager instance;

	/** Stored reference for common usage */
	protected GeneralManager generalManager;

	/** Stored reference for common usage */
	protected EventPublisher eventPublisher;

	/**
	 * Stores a {@link ConnectionMap} for each possible type as originally
	 * provided by the views.
	 */
	HashMap<IDType, ConnectionMap> sourceConnectionsByType;

	/**
	 * Stores a {@link ConnectionMap} with only transformed selection-points as
	 * defined by the transformation needed within remote rendered views.
	 */
	HashMap<IDType, ConnectionMap> transformedConnectionsByType;

	/**
	 * Stores {@link CanvasConnectionMap}s with only transformed
	 * selection-points as defined by the transformation needed within remote
	 * rendered views.
	 */
	HashMap<IDType, CanvasConnectionMap> canvasConnectionsByType;

	ClearConnectionsListener clearConnectionsListener;
	ClearTransformedConnectionsListener clearTransformedConnectionsListener;
	AddSelectionListener addSelectionListener;

	/**
	 * <code>true</code if there are new vertices in the list of 2D canvas
	 * conneciton vertices
	 */
	protected boolean newCanvasVertices = false;

	/**
	 * Constructor.
	 */
	private ConnectedElementRepresentationManager() {
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

		sourceConnectionsByType = new HashMap<IDType, ConnectionMap>();
		transformedConnectionsByType = new HashMap<IDType, ConnectionMap>();
		canvasConnectionsByType = new HashMap<IDType, CanvasConnectionMap>();

		registerEventListeners();
	}

	public static ConnectedElementRepresentationManager get() {
		if (instance == null) {
			synchronized (ConnectedElementRepresentationManager.class) {
				// this is needed if two threads are waiting at the monitor at
				// the
				// time when singleton was getting instantiated
				if (instance == null)
					instance = new ConnectedElementRepresentationManager();
			}
		}
		return instance;
	}

	/**
	 * Sends event to add a selection to a specific tree. The data type is
	 * determined by the {@link ElementConnectionInformation}, the connection id
	 * has to be specified manually
	 * 
	 * @param iConnectionID the connection ID - one connection id per connection
	 *            line tree
	 * @param selectedElementRep the selected element rep associated with the
	 *            tree specified
	 * @param selectionType specify which selection type is associated with this
	 *            selection. If the selectionType should not be rendered at the
	 *            moment (due to user configuration) the call is ignored.
	 */
	public void addSelection(int connectionID,
			final ElementConnectionInformation selectedElementRep, SelectionType selectionType) {

		if (!isSelectionTypeRenderedWithVisuaLinks(selectionType))
			return;

		AddSelectionEvent event = new AddSelectionEvent();
		event.setConnectionID(connectionID);
		event.setSelectedElementRep(selectedElementRep);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Check, whether according to the preferences selections of this type
	 * should be shown as visual links.
	 * 
	 * @param selectionType the type you want to check.
	 * @return true if visual links are rendered for this type, else false
	 */
	public boolean isSelectionTypeRenderedWithVisuaLinks(SelectionType selectionType) {
		// check in preferences if we should draw connection lines for mouse
		// over
		if (!generalManager.getPreferenceStore().getBoolean(
				PreferenceConstants.VISUAL_LINKS_FOR_MOUSE_OVER)
				&& selectionType == SelectionType.MOUSE_OVER)
			return false;
		// check for selections
		if (!generalManager.getPreferenceStore().getBoolean(
				PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS)
				&& selectionType == SelectionType.SELECTION)
			return false;

		return true;
	}

	/**
	 * Adds a selection to a specific tree. The data type is determined by the
	 * {@link ElementConnectionInformation}, the connection id has to be
	 * specified manually
	 * 
	 * @param iConnectionID the connection ID - one connection id per connection
	 *            line tree
	 * @param selectedElementRep the selected element rep associated with the
	 *            tree specified
	 */
	public void handleAddSelectionEvent(int connectionID,
			final ElementConnectionInformation selectedElementRep) {
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
	public void removeSelection(final int iElementID,
			ElementConnectionInformation selectedElementRep) {

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
	 * @param selectionType specify which selection type is associated with this
	 *            selection. If the selectionType should not be rendered at the
	 *            moment (due to user configuration) the call is ignored.
	 */
	public void replaceSelection(final int iElementID,
			ElementConnectionInformation selectedElementRep, SelectionType selectionType) {
		if (!isSelectionTypeRenderedWithVisuaLinks(selectionType))
			return;
		clear(selectedElementRep.getIDType(), selectionType);
		handleAddSelectionEvent(iElementID, selectedElementRep);
	}

	/**
	 * Get a list of all occurring {@link EIDTypes}
	 * 
	 * @return a Set of EIDType
	 */
	public Set<IDType> getOccuringIDTypes() {
		return sourceConnectionsByType.keySet();
	}

	/**
	 * Get a list or IDs of all selected elements of a type
	 * 
	 * @return a Set of IDs
	 */
	public Set<Integer> getIDList(IDType idType) {
		return sourceConnectionsByType.get(idType).keySet();
	}

	/**
	 * Get a representation of a particular element
	 * 
	 * @param iDType the type of the object to be connected (e.g. gene
	 *            expression, clinical)
	 * @param iElementID the id of the object to be connected
	 * @return a list of the representations of the points
	 */
	public ArrayList<ElementConnectionInformation> getSelectedElementRepsByElementID(
			IDType idType, final int iElementID) {

		ArrayList<ElementConnectionInformation> tempList = sourceConnectionsByType.get(idType)
				.get(iElementID);

		if (tempList == null)
			throw new IllegalArgumentException(
					"SelectionManager: No representations for this element ID");
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
	 * Clears all connections of the given idType irrespective of the
	 * selectionType
	 * 
	 * @param idType
	 * @param selectionType specify which selection type is associated with this
	 *            clear. If the selectionType should not be rendered at the
	 *            moment (due to user configuration) the call is ignored.
	 */
	public void clear(IDType idType, SelectionType selectionType) {
		if (!isSelectionTypeRenderedWithVisuaLinks(selectionType))
			return;
		clear(idType);
	}

	/**
	 * Sends event to clear all selections of a given type, for a given
	 * selectionType. Should only be used for situations such as re-setting all
	 * selections, not for a clear before a update.
	 */
	public void clear(IDType idType) {
		ClearConnectionsEvent event = new ClearConnectionsEvent();
		event.setIdType(idType);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Clear all selections of a given type
	 */
	public void handleClearEvent(IDType idType) {
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
	 * @param idType the type to be cleared
	 * @param viewID the id of the view
	 */
	public void clearByViewAndType(IDType idType, int viewID) {
		ConnectionMap hashReps = sourceConnectionsByType.get(idType);
		if (hashReps == null)
			return;
		for (int iElementID : hashReps.keySet()) {
			ArrayList<ElementConnectionInformation> alRep = hashReps.get(iElementID);

			Iterator<ElementConnectionInformation> iterator = alRep.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getSourceViewID() == viewID) {
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
	 * @param viewID the view which id's should be removed
	 */
	public void clearByView(int viewID) {
		for (IDType idType : sourceConnectionsByType.keySet()) {
			clearByViewAndType(idType, viewID);
		}
		transformedConnectionsByType.clear();
		canvasConnectionsByType.clear();
	}

	// public void clearByConnectionID(IDType idType, int iConnectionID) {
	// sourceConnectionsByType.get(idType).remove(iConnectionID);
	// transformedConnectionsByType.clear();
	// canvasConnectionsByType.clear();
	// }

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

	public void doViewRelatedTransformation(GL2 gl, ISelectionTransformer transformer) {
		boolean newTransformedPoints = false;
		newTransformedPoints = transformer.transform(sourceConnectionsByType,
				transformedConnectionsByType);
	}

	/**
	 * To be executed during the display loop with help of a
	 * {@link DisplayLoopExecution}
	 */
	@Override
	public void run() {
		processEvents();
	}

	/**
	 * Registers the event listeners.
	 */
	@Override
	public void registerEventListeners() {
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

	@Override
	public void unregisterEventListeners() {
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
	 * Gets all {@link CanvasConnectionMap}s by {@link EIDType} containing the
	 * finally transformed and to-canvas-projected selection vertices.
	 * 
	 * @return 2D canvas selection vertices
	 */
	public HashMap<IDType, CanvasConnectionMap> getCanvasConnectionsByType() {
		return canvasConnectionsByType;
	}

	/**
	 * Gets all {@link ConnectionMap}s by {@link EIDType} containing transformed
	 * selection vertices. The remoteViewID field of the contained
	 * {@link ElementConnectionInformation}s references to the view, the
	 * coordinates are related to.
	 * 
	 * @return
	 */
	public HashMap<IDType, ConnectionMap> getTransformedConnectionsByType() {
		return transformedConnectionsByType;
	}

	/**
	 * <code>true</code> if there are new vertices for 2D connection line
	 * drawing, <code>false</code> otherwise
	 * 
	 * @return flag if new connection line vertices exists
	 */
	public boolean isNewCanvasVertices() {
		return newCanvasVertices;
	}

	/**
	 * Sets the status of flag to indicate if new 2D connection line vertices
	 * exists.
	 * 
	 * @newCanvasVertices new value for the flag
	 */
	public void setNewCanvasVertices(boolean newCanvasVertices) {
		this.newCanvasVertices = newCanvasVertices;
	}

}
