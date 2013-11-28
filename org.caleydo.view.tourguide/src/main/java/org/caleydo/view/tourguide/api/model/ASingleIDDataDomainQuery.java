/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ASingleIDDataDomainQuery extends ADataDomainQuery {
	private final IDType idType;
	private final VirtualArray ids;
	protected final EDimension dim;

	public ASingleIDDataDomainQuery(ATableBasedDataDomain dataDomain, EDimension dim) {
		super(dataDomain);
		this.dim = dim;
		if (dim.isRecord()) {
			this.idType = dataDomain.getRecordIDType();
			this.ids = dataDomain.getTable().getDefaultRecordPerspective(false).getVirtualArray();
		} else {
			this.idType = dataDomain.getDimensionIDType();
			this.ids = dataDomain.getTable().getDefaultDimensionPerspective(false).getVirtualArray();
		}
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	/**
	 * @return the categoryIDType, see {@link #idType}
	 */
	public IDType getSingleIDType() {
		return idType;
	}

	@Override
	public boolean apply(AScoreRow row) {
		assert row.getDataDomain() == dataDomain;
		return true;
	}

	@Override
	public boolean apply(Group group) {
		if (!super.apply(group))
			return false;
		return true;
	}

	@Override
	protected List<AScoreRow> getAll() {
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);

		IIDTypeMapper<Integer, String> toLabel = idMappingManager.getIDTypeMapper(idType, idType
				.getIDCategory().getHumanReadableIDType());

		List<AScoreRow> r = new ArrayList<>(); // just stratifications
		for (int category : ids) {
			String label = toLabel.apply(category).iterator().next();
			r.add(new SingleIDPerspectiveRow(label, category, this));
		}

		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		// up to now not possible to create new categories
		return null;
	}

	/**
	 * builds a virtual array given a category and a label for it
	 *
	 * @param label
	 * @param category
	 * @return
	 */
	public VirtualArray createVirtualArray(String label, Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();

		// reuse existing
		TablePerspective tablePerspective = findExistingTablePerspective(label);
		if (tablePerspective != null) {
			if (dataDomain2.getRecordIDType() != getSingleIDType()) {
				return tablePerspective.getRecordPerspective().getVirtualArray();
			} else {
				return tablePerspective.getDimensionPerspective().getVirtualArray();
			}
		}

		return createVirtualArrayImpl(label, category);
	}

	/**
	 * @param label
	 * @param category
	 * @return
	 */
	protected abstract VirtualArray createVirtualArrayImpl(String label, Integer id);

	/**
	 * creates a {@link TablePerspective} out of the given data
	 *
	 * @param label
	 * @param id
	 *            the category
	 * @param va
	 *            the record dimension to use
	 * @return
	 */
	public TablePerspective createTablePerspective(String label, Integer id, VirtualArray va) {
		ATableBasedDataDomain d = getDataDomain();
		final Table table = d.getTable();

		TablePerspective tablePerspective = findExistingTablePerspective(label);
		if (tablePerspective != null)
			return tablePerspective;

		Perspective cat = new Perspective(dataDomain, idType);
		cat.setPrivate(false);
		cat.setLabel(label, false);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(Lists.newArrayList(id));
		cat.init(data);

		Perspective items = new Perspective(dataDomain, d.getOppositeIDType(idType));
		data = new PerspectiveInitializationData();
		data.setData(va);
		items.init(data);
		items.setLabel(label, false);
		items.setPrivate(false);

		if (dim.isRecord()) {
			table.registerRecordPerspective(cat, false);
			table.registerDimensionPerspective(items, false);
			tablePerspective = d.getTablePerspective(cat.getPerspectiveID(), items.getPerspectiveID());
		} else {
			table.registerDimensionPerspective(cat, false);
			table.registerRecordPerspective(items, false);
			tablePerspective = d.getTablePerspective(items.getPerspectiveID(), cat.getPerspectiveID());
		}
		tablePerspective.setLabel(label, false);
		tablePerspective.setPrivate(false);

		return tablePerspective;
	}

	private TablePerspective findExistingTablePerspective(String label) {
		// use existing by name
		for (TablePerspective p : getDataDomain().getAllTablePerspectives()) {
			if (p.getLabel().equals(label))
				return p;
		}
		return null;
	}

	/**
	 * @return the dim, see {@link #dim}
	 */
	public EDimension getDim() {
		return dim;
	}

	/**
	 * @param id
	 * @return
	 */
	public abstract Collection<GroupInfo> getGroupInfos(Integer id);

	/**
	 * @param id
	 * @return
	 */
	public abstract int getGroupSize(Integer id);
}
