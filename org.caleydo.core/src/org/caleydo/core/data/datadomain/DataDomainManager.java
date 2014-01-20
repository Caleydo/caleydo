/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.configuration.ChooseDataConfigurationDialog;
import org.caleydo.core.data.datadomain.graph.DataDomainGraph;
import org.caleydo.core.event.data.NewDataDomainEvent;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * Singleton that manages dataDomains based on their dataDomainType (the string
 * plug-in id) and the concrete object.
 * </p>
 * <p>
 * The DataDomainManager holds the default {@link AssociationManager}, which
 * stores associations between views and dataDomains. Notice that it is legal to
 * hold a private AssociationManager for special cases.
 * </p>
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class DataDomainManager {
	public final static String DATA_DOMAIN_INSTANCE_DELIMITER = "_";

	private static DataDomainManager dataDomainManager;

	private Map<String, IDataDomain> byID = new HashMap<>(8);
	private Map<String, List<IDataDomain>> byType = new HashMap<>();

	private final AssociationManager associationManager = new AssociationManager();
	private final DataDomainGraph dataDomainGraph = new DataDomainGraph();

	public static DataDomainManager get() {
		if (dataDomainManager == null) {
			synchronized (DataDomainManager.class) {
				if (dataDomainManager == null) {
					dataDomainManager = new DataDomainManager();
				}
			}
		}
		return dataDomainManager;
	}

	/**
	 * This method is intended for initialization of the data domain in general.
	 * It does not create an data domain instance.
	 *
	 * @param dataDomainType the plug-in id of the data domain
	 */
	public void initalizeDataDomain(String dataDomainType) {
		for (IDataDomainInitialization initializer : ExtensionUtils.findImplementation(
				"org.caleydo.datadomain.DataDomainInitialization", "class", IDataDomainInitialization.class)) {
			try {
				initializer.createIDTypesAndMapping();
			} catch (Exception ex) {
				throw new RuntimeException("Could not instantiate data domain " + dataDomainType, ex);
			}
		}
	}

	/**
	 * <p>
	 * Create a new {@link ADataDomain} of the type specified through
	 * <code>dataDomainType</code>. The created dataDomain is also registered
	 * with the manager.
	 * </p>
	 * <p>
	 * This method also specifies a boolean to determine whether the columns in
	 * a file corresponds to the dimensions in the application. This is only
	 * relevant for {@link ATableBasedDataDomain}s. For other DataDomains or the
	 * default (true) use {@link #createDataDomain(String)}.
	 * </p>
	 *
	 * @param dataDomainType the plug-in id of the data domain
	 * @param isColumnDimension set to false if this dataDomain is of type
	 *            {@link ATableBasedDataDomain} and you want to access the
	 *            columns of the loaded files as records
	 * @return the created {@link IDataDomain}
	 */
	public synchronized IDataDomain createDataDomain(String dataDomainType,
			DataSetDescription dataSetDescription) {

		IExtensionRegistry reg = RegistryFactory.getRegistry();

		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.datadomain.DataDomain");
		IExtension ext = ep.getExtension(dataDomainType);
		IConfigurationElement[] ce = ext.getConfigurationElements();

		try {
			ADataDomain dataDomain = (ADataDomain) ce[0].createExecutableExtension("class");
			if (dataSetDescription != null && dataDomain instanceof ATableBasedDataDomain)
				((ATableBasedDataDomain) dataDomain).setDataSetDescription(dataSetDescription);
			dataDomain.init();
			Thread thread = new Thread(dataDomain, dataDomainType);
			thread.start();
			register(dataDomain);

			return dataDomain;
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate data domain " + dataDomainType,
					ex);
		}
	}

	/**
	 * Create a new {@link ADataDomain} of the type specified through
	 * <code>dataDomainType</code>. The created dataDomain is also registered
	 * with the manager.
	 *
	 * @param dataDomainType the plug-in id of the data domain
	 * @return the created {@link IDataDomain}
	 */
	public IDataDomain createDataDomain(String dataDomainType) {
		return createDataDomain(dataDomainType, null);
	}

	/**
	 * Returns all data domains.
	 *
	 * @return
	 */
	public synchronized Collection<IDataDomain> getDataDomains() {
		return new ArrayList<>(byID.values());
	}

	/**
	 * Get a concrete dataDomain object for the dataDomainID. Returns null if no
	 * dataDomain object is mapped to the ID.
	 *
	 * @param dataDomainID
	 * @return
	 */
	public synchronized IDataDomain getDataDomainByID(String dataDomainID) {
		return byID.get(dataDomainID);
	}

	/**
	 * Get a concrete dataDomain object for the dataDomainType. Returns null if
	 * no dataDomain object is mapped to the type. If more than one data domain
	 * object is registered for that ID, the chooser dialog is opened where the
	 * user can determine the data domain.
	 *
	 * @param dataDomainType
	 * @return
	 */
	public synchronized IDataDomain getDataDomainByType(String dataDomainType) {

		IDataDomain dataDomain = null;
		List<IDataDomain> possibleDataDomains = byType.get(dataDomainType);

		if (possibleDataDomains == null)
			return null;
		if (possibleDataDomains.size() == 1)
			dataDomain = possibleDataDomains.get(0);
		else {
			ChooseDataConfigurationDialog chooseDataDomainDialog = new ChooseDataConfigurationDialog(
					new Shell(), "DataDomainManager");
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
	public synchronized void register(IDataDomain dataDomain) {

		if (!byID.containsKey(dataDomain.getDataDomainID()))
			byID.put(dataDomain.getDataDomainID(), dataDomain);

		if (byType.get(dataDomain.getDataDomainType()) == null) {
			List<IDataDomain> dataDomainList = new ArrayList<IDataDomain>();
			dataDomainList.add(dataDomain);
			byType.put(dataDomain.getDataDomainType(), dataDomainList);
		}
		else {
			byType.get(dataDomain.getDataDomainType()).add(dataDomain);
		}

		// Only assign random color if no color has been set externally
		if (dataDomain.getColor() == null) {
			Color color = ColorManager.get().getFirstMarkedColorOfList(
					ColorManager.QUALITATIVE_COLORS, false);
			ColorManager.get().markColor(ColorManager.QUALITATIVE_COLORS, color, true);
			dataDomain.getDataSetDescription().setColor(color);
		}

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
	public synchronized void unregister(IDataDomain dataDomain) {
		if (dataDomain == null)
			return;

		if (byID.containsKey(dataDomain.getDataDomainID()))
			byID.remove(dataDomain.getDataDomainID());

		if (byType.get(dataDomain.getDataDomainType()) != null) {
			byType.get(dataDomain.getDataDomainType()).remove(dataDomain);
		}

		Color color = dataDomain.getColor();
		ColorManager.get().markColor(ColorManager.QUALITATIVE_COLORS, color, false);

		dataDomainGraph.removeDataDomain(dataDomain);

		GeneralManager.get().getEventPublisher().triggerEvent(new RemoveDataDomainEvent(this, dataDomain));
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
	 * Returns a list of all data domains of this type registered or null if no
	 * such data domain is registered.
	 *
	 * @param dataDomainType
	 * @return
	 */
	public List<IDataDomain> getDataDomainsByType(String dataDomainType) {
		return byType.get(dataDomainType);
	}

	/**
	 * Returns a list containing all DataDomains that are of the type classType.
	 * If no type is registered the list is returned empty.
	 *
	 * @param classType
	 * @return
	 */
	public <T extends ADataDomain> List<T> getDataDomainsByType(Class<T> classType) {
		ArrayList<T> result = new ArrayList<T>();
		for (IDataDomain dataDomain : getDataDomains())
			if (classType.isInstance(dataDomain))
				result.add(classType.cast(dataDomain));
		return result;
	}

	/**
	 * Unregisters all data domains. This is for instance needed when loading a
	 * new cal file during runtime.
	 */
	public void unregisterAllDataDomains() {
		for (IDataDomain domain : new ArrayList<>(getDataDomains())) { // work on a local copy
			unregister(domain);
		}
	}
}
