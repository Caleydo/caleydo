package org.caleydo.core.manager;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.util.clusterer.ClusterState;

/**
 * Use cases are the unique points of coordinations for views and its data. Genetic data is one example -
 * another is a more generic one where Caleydo can load arbitrary tabular data but without any special
 * features of genetic analysis.
 * 
 * @author Marc Streit
 */
public interface IUseCase {

	/**
	 * Returns whether the application can load and work with non further specified data (general use case) or
	 * if a more specialized use case (e.g. gene expression) is active.
	 */
	public EDataDomain getDataDomain();

	/**
	 * Returns the Primary VA Type (see ({@link VAType#getPrimaryVAType()}) for a ID category for this use
	 * case, or null if the category is not supported at all.
	 * 
	 * @param idCategory
	 * @return
	 */
	public String getVATypeForIDCategory(EIDCategory idCategory);

	/**
	 * Returns a list of views that can visualize the data in the domain
	 * 
	 * @return
	 */
	public ArrayList<String> getPossibleViews();

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
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon
	 * positive completion to replace their VA.
	 * 
	 * @param setID ID of the set to cluster
	 * @param clusterState
	 */
	public void startClustering(int setID, ClusterState clusterState);

	/**
	 * Resets the context VA to it's initial state
	 */
	public void resetContextVA();

	/**
	 * Gets the parameters for loading the data-{@link Set} contained in this use case
	 * 
	 * @return parameters for loading the data-{@link Set} of this use case
	 */
	public LoadDataParameters getLoadDataParameters();

	/**
	 * Replaces the virtual array of that id category with the virtual array specified
	 * 
	 * @param idCategory
	 *            the type of id
	 * @param the
	 *            type of the virtual array
	 * @param virtualArray
	 *            the new virtual array
	 */
	public void replaceStorageVA(EIDCategory idCategory, StorageVAType vaType,
		StorageVirtualArray virtualArray);

	public void replaceContentVA(EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray);

	/**
	 * Sets the parameters for loading the data-{@link Set} contained in this use case
	 * 
	 * @param loadDataParameters
	 *            parameters for loading the data-{@link Set} of this use case
	 */
	public void setLoadDataParameters(LoadDataParameters loadDataParameters);

	/** Sets the name of the boots-trap xml-file this useCase was or should be loaded */
	public String getBootstrapFileName();

	/** Gets the name of the boots-trap xml-file this useCase was or should be loaded */
	public void setBootstrapFileName(String bootstrapFileName);

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
	
	/**
	 * Restore the original data. All applied filters are undone.
	 */
	public void restoreOriginalContentVA();
	
	public void setOrganism(EOrganism eOrganism);
	

	public EOrganism getOrganism();

}
