package org.caleydo.core.manager.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.gui.dialog.ChooseDataDomainDialog;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * Singleton that manages dataDomains based on their dataDomainType (the string plug-in id) and the concrete
 * object.
 * </p>
 * <p>
 * The DataDomainManager holds the default {@link AssociationManager}, which stores associations between views
 * and dataDomains. Notice that it is legal to hold a private AssociationManager for special cases.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class DataDomainManager {

	private static DataDomainManager dataDomainManager;
	private HashMap<String, IDataDomain> registeredDataDomainsByID = new HashMap<String, IDataDomain>(8);
	private HashMap<String, ArrayList<IDataDomain>> registeredDataDomainsByType =
		new HashMap<String, ArrayList<IDataDomain>>();

	private AssociationManager associationManager = new AssociationManager();
	private DataDomainGraph dataDomainGraph = new DataDomainGraph();;

	public static DataDomainManager get() {
		if (dataDomainManager == null)
			dataDomainManager = new DataDomainManager();

		return dataDomainManager;
	}

	/**
	 * Create a new dataDomain. The created dataDomain is also registered with the manager.
	 * 
	 * @param dataDomainType
	 *            the plug-in id of the data domain
	 * @return
	 */
	public IDataDomain createDataDomain(String dataDomainType) {

		IExtensionRegistry reg = Platform.getExtensionRegistry();

		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.datadomain.DataDomain");
		IExtension ext = ep.getExtension(dataDomainType);
		IConfigurationElement[] ce = ext.getConfigurationElements();

		try {
			ADataDomain dataDomain = (ADataDomain) ce[0].createExecutableExtension("class");
			Thread thread = new Thread(dataDomain, dataDomainType);
			thread.start();
			return dataDomain;
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate data domain " + dataDomainType, ex);
		}
	}

	/**
	 * Returns all data domains
	 * 
	 * @return
	 */
	public Collection<IDataDomain> getDataDomains() {
		return registeredDataDomainsByID.values();
	}

	/**
	 * Get a concrete dataDomain object for the dataDomainID. Returns null if no dataDomain object is mapped
	 * to the ID.
	 * 
	 * @param dataDomainID
	 * @return
	 */
	public IDataDomain getDataDomainByID(String dataDomainID) {

		return registeredDataDomainsByID.get(dataDomainID);
	}

	/**
	 * Get a concrete dataDomain object for the dataDomainType. Returns null if no dataDomain object is mapped
	 * to the type. If more than one data domain object is registered for that ID, the chooser dialog is
	 * opened where the user can determine the data domain.
	 * 
	 * @param dataDomainType
	 * @return
	 */
	public IDataDomain getDataDomainByType(String dataDomainType) {

		IDataDomain dataDomain = null;
		ArrayList<IDataDomain> possibleDataDomains = registeredDataDomainsByType.get(dataDomainType);

		if (possibleDataDomains == null)
			return null;
		if (possibleDataDomains.size() == 1)
			dataDomain = possibleDataDomains.get(0);
		else {
			ChooseDataDomainDialog chooseDataDomainDialog = new ChooseDataDomainDialog(new Shell());
			chooseDataDomainDialog.setPossibleDataDomains(possibleDataDomains);
			dataDomain = chooseDataDomainDialog.open();
		}

		return dataDomain;
	}

	/**
	 * register a concrete data domain
	 * 
	 * @param dataDomain
	 */
	public void register(IDataDomain dataDomain) {

		if (!registeredDataDomainsByID.containsKey(dataDomain.getDataDomainID()))
			registeredDataDomainsByID.put(dataDomain.getDataDomainID(), dataDomain);

		if (registeredDataDomainsByType.get(dataDomain.getDataDomainType()) == null) {
			ArrayList<IDataDomain> dataDomainList = new ArrayList<IDataDomain>();
			dataDomainList.add(dataDomain);
			registeredDataDomainsByType.put(dataDomain.getDataDomainType(), dataDomainList);
		}
		else {
			registeredDataDomainsByType.get(dataDomain.getDataDomainType()).add(dataDomain);
		}

		dataDomainGraph.addDataDomain(dataDomain);
	}

	/**
	 * Returns the default association manager which is valid system-wide.
	 * 
	 * @return
	 */
	public AssociationManager getAssociationManager() {
		return associationManager;
	}

	public DataDomainGraph getDataDomainGraph() {
		return dataDomainGraph;
	}
}
