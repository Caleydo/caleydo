package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.configuration.ChooseDataConfigurationDialog;
import org.caleydo.core.event.data.NewDataDomainEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;
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

	public final static String DATA_DOMAIN_INSTANCE_DELIMITER = "_";

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
	 * <p>
	 * Create a new {@link ADataDomain} of the type specified through <code>dataDomainType</code>. The created
	 * dataDomain is also registered with the manager.
	 * </p>
	 * <p>
	 * This method also specifies a boolean to determine whether the columns in a file corresponds to the
	 * dimensions in the application. This is only relevant for {@link ATableBasedDataDomain}s. For other
	 * DataDomains or the default (true) use {@link #createDataDomain(String)}.
	 * </p>
	 * 
	 * @param dataDomainType
	 *            the plug-in id of the data domain
	 * @param isColumnDimension
	 *            set to false if this dataDomain is of type {@link ATableBasedDataDomain} and you want to
	 *            access the columns of the loaded files as records
	 * @return the created {@link IDataDomain}
	 */
	public IDataDomain createDataDomain(String dataDomainType, DataDomainConfiguration dataDomainConfiguration) {

		IExtensionRegistry reg = Platform.getExtensionRegistry();

		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.datadomain.DataDomain");
		IExtension ext = ep.getExtension(dataDomainType);
		IConfigurationElement[] ce = ext.getConfigurationElements();

		try {
			ADataDomain dataDomain = (ADataDomain) ce[0].createExecutableExtension("class");
			if (dataDomainConfiguration != null && dataDomain instanceof ATableBasedDataDomain)
				((ATableBasedDataDomain) dataDomain).setDataDomaiConfiguration(dataDomainConfiguration);
			dataDomain.init();
			Thread thread = new Thread(dataDomain, dataDomainType);
			thread.start();
			register(dataDomain);

			return dataDomain;
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate data domain " + dataDomainType, ex);
		}
	}

	/**
	 * Create a new {@link ADataDomain} of the type specified through <code>dataDomainType</code>. The created
	 * dataDomain is also registered with the manager.
	 * 
	 * @param dataDomainType
	 *            the plug-in id of the data domain
	 * @return the created {@link IDataDomain}
	 */
	public IDataDomain createDataDomain(String dataDomainType) {
		return createDataDomain(dataDomainType, null);
	}

	/**
	 * Returns all data domains. The collection is backed by the manager, so do NOT! modify it, otherwise you
	 * will modify the manager contents.
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
			ChooseDataConfigurationDialog chooseDataDomainDialog =
				new ChooseDataConfigurationDialog(new Shell(), "DataDomainManager");
			chooseDataDomainDialog.setBlockOnOpen(true);
			chooseDataDomainDialog.open();
			dataDomain = chooseDataDomainDialog.getDataConfiguration().getDataDomain();
			// chooseDataDomainDialog.setPossibleDataDomains(possibleDataDomains);
			// dataDomain = chooseDataDomainDialog.open();
		}

		return dataDomain;
	}

	/**
	 * Register a concrete data domain
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

		Color color = ColorManager.get().getFirstMarkedColorOfList(ColorManager.DATA_DOMAIN_COLORS, false);
		ColorManager.get().markColor(ColorManager.DATA_DOMAIN_COLORS, color, true);
		dataDomain.setColor(color);

		dataDomainGraph.addDataDomain(dataDomain);

		NewDataDomainEvent event = new NewDataDomainEvent(dataDomain);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	/**
	 * Unregister a concrete data domain
	 * 
	 * @param dataDomain
	 */
	public void unregister(IDataDomain dataDomain) {

		if (registeredDataDomainsByID.containsKey(dataDomain.getDataDomainID()))
			registeredDataDomainsByID.remove(dataDomain.getDataDomainID());

		if (registeredDataDomainsByType.get(dataDomain.getDataDomainType()) != null) {
			registeredDataDomainsByType.get(dataDomain.getDataDomainType()).remove(dataDomain);
		}

		Color color = dataDomain.getColor();
		ColorManager.get().markColor(ColorManager.DATA_DOMAIN_COLORS, color, false);

		dataDomainGraph.removeDataDomain(dataDomain);
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

	/**
	 * Returns a list of all data domains of this type registered or null if no such data domain is
	 * regeisterd.
	 * 
	 * @param dataDomainType
	 * @return
	 */
	public ArrayList<IDataDomain> getDataDomainsByType(String dataDomainType) {
		return registeredDataDomainsByType.get(dataDomainType);
	}

	/**
	 * Returns a list containing all DataDomains that are of the type classType. If no type is registered the
	 * list is returned empty.
	 * 
	 * @param classType
	 * @return
	 */
	public <T extends ADataDomain> ArrayList<T> getDataDomainsByType(Class<T> classType) {
		ArrayList<T> result = new ArrayList<T>();
		Collection<IDataDomain> allDataDomains = getDataDomains();

		Iterator<IDataDomain> iterator = allDataDomains.iterator();
		while (iterator.hasNext()) {
			IDataDomain dataDomain = iterator.next();
			try {
				T typedDataDomain = classType.cast(dataDomain);
				// if we get here cast was successful
				result.add(typedDataDomain);
			}
			catch (ClassCastException e) {
				// this is expected for every failed cast, i.e. the checked dataDomain is not an instance of
				// the specified class.
			}
		}
		return result;
	}
}
