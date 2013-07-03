/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.collection.MultiHashMap;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;

/**
 * This manager maps views to dataDomains, thereby signaling which view can use
 * which dataDomain. The {@link DataDomainManager} holds one instance where all
 * possible associations have to be registered. It is legal however, to hold
 * one's own AssociationManager that for example only has a subset of views.
 *
 * @author Alexander Lex
 */
public class AssociationManager {
	private final static String EXTENSION_POINT = "org.caleydo.core.data.datadomain.ViewDataDomainAssociation";
	/** maps a dataDomain to multiple views */
	private final MultiHashMap<String, String> dataDomainViewAssociations = new MultiHashMap<String, String>();
	/** maps a view to multiple datadomains */
	private final MultiHashMap<String, String> viewDataDomainAssociations = new MultiHashMap<String, String>();


	public AssociationManager() {
		if (RegistryFactory.getRegistry() != null) {
			for (IConfigurationElement elem : RegistryFactory.getRegistry()
					.getConfigurationElementsFor(EXTENSION_POINT)) {
				String viewID = elem.getAttribute("view");
				for (IConfigurationElement child : elem.getChildren("dataDomain")) {
					registerDatadomainTypeViewTypeAssociation(child.getAttribute("type"), viewID);
				}
			}
		}
	}

	/**
	 * register a association of a dataDomainType to a viewType, thereby
	 * indicating the principal compatibility of those components. This
	 * registration can be done from any given place. Most commonly, either a
	 * view specifies its datadomains or a datadomain specifies its views.
	 *
	 * @param dataDomainType
	 *            the plugin name of the datadomain, typically
	 *            org.caleydo.datadomain.*
	 * @param viewType
	 *            the plugin name of the view, typically org.caleydo.view.*
	 */
	private void registerDatadomainTypeViewTypeAssociation(String dataDomainType,
			String viewType) {
		dataDomainViewAssociations.put(dataDomainType, viewType);
		viewDataDomainAssociations.put(viewType, dataDomainType);
	}

	/**
	 * Wrapper for
	 * {@link #registerDatadomainTypeViewTypeAssociation(String, String)} that
	 * uses a collection for dataDomain types
	 *
	 * @param dataDomainTypes
	 * @param viewTypes
	 */
	public void registerDatadomainTypeViewTypeAssociation(Iterable<String> dataDomainTypes, String viewType) {
		for (String dataDomainType : dataDomainTypes)
			registerDatadomainTypeViewTypeAssociation(dataDomainType, viewType);
	}

	/**
	 * Get all viewTypes that can handle the datadomain of the specified type
	 *
	 * @param dataDomainType
	 * @return
	 */
	public Set<String> getViewTypesForDataDomain(String dataDomainType) {
		return dataDomainViewAssociations.getAll(dataDomainType);
	}

	/**
	 * Get all loaded concrete dataDomains that can be used by the view
	 * specified. If no dataDomains are registered for the view an empty list is
	 * returned
	 *
	 * @param viewType
	 * @return
	 */
	private List<IDataDomain> getDataDomainsForView(String viewType) {

		List<IDataDomain> availabelDataDomains = new ArrayList<IDataDomain>();
		Collection<String> dataDomainTypes = viewDataDomainAssociations.getAll(viewType);
		if (dataDomainTypes == null)
			return availabelDataDomains;

		for (String dataDomainType : dataDomainTypes) {
			// IDataDomain dataDomain =
			// DataDomainManager.get().getDataDomainByType(dataDomainType);
			List<IDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(dataDomainType);
			if (dataDomains != null)
				availabelDataDomains.addAll(dataDomains);
		}
		return availabelDataDomains;
	}

	/**
	 * Returns all {@link ATableBasedDataDomain}s that are registered for the
	 * specified view type. If no such datadomains are registered an empty list
	 * is returned
	 *
	 * @param viewType
	 *            the type of view
	 */
	public List<ATableBasedDataDomain> getTableBasedDataDomainsForView(String viewType) {
		List<IDataDomain> allDataDomains = getDataDomainsForView(viewType);
		List<ATableBasedDataDomain> availabelDataDomains = new ArrayList<>(allDataDomains.size());

		for (IDataDomain dataDomain : allDataDomains) {
			if (dataDomain instanceof ATableBasedDataDomain) {
				availabelDataDomains.add((ATableBasedDataDomain) dataDomain);
			}
		}
		return availabelDataDomains;
	}

}
