package org.caleydo.core.manager;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.canvas.listener.ForeignSelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;

public interface ISetBasedDataDomain
	extends IContentVAUpdateHandler, IStorageVAUpdateHandler, ISelectionUpdateHandler,
	ISelectionCommandHandler, IDataDomain, IListenerOwner {
	/**
	 * Returns the Primary VA Type (see ({@link VAType#getPrimaryVAType()}) for a ID category for this use
	 * case, or null if the category is not supported at all.
	 * 
	 * @param idCategory
	 * @return
	 */
	public String getVATypeForIDCategory(EIDCategory idCategory);

	/**
	 * Returns the set which is currently loaded and used inside the views for this use case.
	 * 
	 * @return a data set
	 */
	public ISet getSet();

	/**
	 * Sets the set which is currently loaded and used inside the views for this use case.
	 * 
	 * @param set
	 *            The new set which replaced the currenlty loaded one.
	 */
	public void setSet(ISet set);

	/**
	 * Update the data set in the view of this use case.
	 */
	public void updateSetInViews();

	/**
	 * Returns the content label. E.g. gene for genome use case, entity for generic use case
	 * 
	 * @param bUpperCase
	 *            TRUE makes the label upper case
	 * @param bPlural
	 *            TRUE label = plural, FALSE label = singular
	 * @return label valid for the specific use case
	 */
	public String getContentLabel(boolean bUpperCase, boolean bPlural);

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return
	 */
	public ContentVirtualArray getContentVA(ContentVAType vaType);

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return
	 */
	public StorageVirtualArray getStorageVA(StorageVAType vaType);

	/**
	 * Replaces the storage virtual array with the virtual array specified, if the dataDomain matches. If the
	 * dataDomain doesn't match, the method
	 * {@link #handleForeignContentVAUpdate(int, String, ContentVAType, ContentVirtualArray)} is
	 * called.
	 * 
	 * @param idCategory
	 *            the type of id
	 * @param the
	 *            type of the virtual array
	 * @param virtualArray
	 *            the new virtual array
	 */
	public void replaceStorageVA(String dataDomainType, StorageVAType vaType, StorageVirtualArray virtualArray);

	/**
	 * This method is called if a content VA Update was requested, but the dataDomainType specified was not
	 * this dataDomains type. Concrete handling can only be done in concrete dataDomains.
	 * 
	 * @param setID
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public void handleForeignContentVAUpdate(int setID, String dataDomainType,
		ContentVAType vaType, ContentVirtualArray virtualArray);

	/**
	 * This method is called by the {@link ForeignSelectionUpdateListener}, signaling that a selection form
	 * another dataDomain is available. If possible, it is converted to be compatible with the local
	 * dataDomain and then sent out via a {@link SelectionUpdateEvent}.
	 * 
	 * @param dataDomainType
	 *            the type of the dataDomain for which this selectionUpdate is intended
	 * @param delta
	 * @param scrollToSelection
	 * @param info
	 */
	public void handleForeignSelectionUpdate(String dataDomainType, ISelectionDelta delta,
		boolean scrollToSelection, String info);

	/**
	 * Replaces the content virtua
	 * 
	 * @param dataDomainType
	 * @param vaType
	 * @param virtualArray
	 */
	public void replaceContentVA(String dataDomainType, ContentVAType vaType, ContentVirtualArray virtualArray);

	/**
	 * Restore the original data. All applied filters are undone.
	 */
	public void restoreOriginalContentVA();

	/**
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon
	 * positive completion to replace their VA.
	 * 
	 * @param setID
	 *            ID of the set to cluster
	 * @param clusterState
	 */
	public void startClustering(int setID, ClusterState clusterState);

	/**
	 * Resets the context VA to it's initial state
	 */
	public void resetContextVA();

	public void setContentVirtualArray(ContentVAType vaType, ContentVirtualArray virtualArray);

	public void setStorageVirtualArray(StorageVAType vaType, StorageVirtualArray virtualArray);

	/**
	 * Returns a clone of the content selection manager. You have to set your virtual array manually. This is
	 * the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the content selection manager
	 */
	public ContentSelectionManager getContentSelectionManager();

	/**
	 * Returns a clone of the storage selection manager. You have to set your virtual array manually. This is
	 * the preferred way to initialize SelectionManagers.
	 * 
	 * @return a clone of the storage selection manager
	 */
	public StorageSelectionManager getStorageSelectionManager();
}
