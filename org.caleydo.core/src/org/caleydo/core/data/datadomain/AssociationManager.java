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
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.caleydo.core.util.collection.MultiHashMap;

/**
 * This manager maps views to dataDomains, thereby signaling which view can use
 * which dataDomain. The {@link DataDomainManager} holds one instance where all
 * possible associations have to be registered. It is legal however, to hold
 * one's own AssociationManager that for example only has a subset of views.
 * 
 * @author Alexander Lex
 */
public class AssociationManager {

	/** maps a dataDomain to multiple views */
	private MultiHashMap<String, String> dataDomainViewAssociations;
	/** maps a view to multiple datadomains */
	private MultiHashMap<String, String> viewDataDomainAssociations;

	public AssociationManager() {
		dataDomainViewAssociations = new MultiHashMap<String, String>();
		viewDataDomainAssociations = new MultiHashMap<String, String>();
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
	public void registerDatadomainTypeViewTypeAssociation(String dataDomainType,
			String viewType) {
		dataDomainViewAssociations.put(dataDomainType, viewType);
		viewDataDomainAssociations.put(viewType, dataDomainType);
	}

	/**
	 * Wrapper for
	 * {@link #registerDatadomainTypeViewTypeAssociation(String, String)} that
	 * uses a collection for view types
	 * 
	 * @param dataDomainType
	 * @param viewTypes
	 */
	public void registerDatadomainTypeViewTypeAssociation(String dataDomainType,
			Collection<String> viewTypes) {
		for (String viewType : viewTypes)
			registerDatadomainTypeViewTypeAssociation(dataDomainType, viewType);

	}

	/**
	 * Wrapper for
	 * {@link #registerDatadomainTypeViewTypeAssociation(String, String)} that
	 * uses a collection for dataDomain types
	 * 
	 * @param dataDomainTypes
	 * @param viewTypes
	 */
	public void registerDatadomainTypeViewTypeAssociation(
			Collection<String> dataDomainTypes, String viewType) {
		for (String dataDomainType : dataDomainTypes)
			registerDatadomainTypeViewTypeAssociation(dataDomainType, viewType);

	}

	/**
	 * Get all datadomain types for a given view type
	 * 
	 * @param viewType
	 *            the view type for which the datadomains are sought.
	 * @return a set of datadomain types
	 */
	public Set<String> getDataDomainTypesForViewTypes(String viewType) {
		return viewDataDomainAssociations.getAll(viewType);
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
	public ArrayList<IDataDomain> getDataDomainsForView(String viewType) {

		ArrayList<IDataDomain> availabelDataDomains = new ArrayList<IDataDomain>();
		Set<String> dataDomainTypes = getDataDomainTypesForViewTypes(viewType);
		if (dataDomainTypes == null)
			return availabelDataDomains;

		for (String dataDomainType : dataDomainTypes) {
			// IDataDomain dataDomain =
			// DataDomainManager.get().getDataDomainByType(dataDomainType);
			ArrayList<IDataDomain> dataDomains = DataDomainManager.get()
					.getDataDomainsByType(dataDomainType);
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
	public ArrayList<ATableBasedDataDomain> getTableBasedDataDomainsForView(
			String viewType) {

		ArrayList<IDataDomain> allDataDomains = getDataDomainsForView(viewType);

		ArrayList<ATableBasedDataDomain> availabelDataDomains = new ArrayList<ATableBasedDataDomain>(
				allDataDomains.size());

		for (IDataDomain dataDomain : allDataDomains) {
			if (dataDomain instanceof ATableBasedDataDomain) {
				availabelDataDomains.add((ATableBasedDataDomain) dataDomain);
			}
		}
		return availabelDataDomains;
	}

}
