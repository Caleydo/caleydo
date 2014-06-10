/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.dvi.tableperspective;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.util.base.ILabeled;

/**
 * Class for creating {@link TablePerspective}s using initialization data. Also creates record- or dimension-
 * {@link Perspective}s when necessary.
 *
 * @author Christian Partl
 *
 */
public class TablePerspectiveCreator implements ILabeled {

	public static class Builder {
		private final ATableBasedDataDomain dataDomain;

		private Perspective recordPerspective;
		private Perspective dimensionPerspective;
		private VirtualArray recordVA;
		private Group recordGroup;
		private VirtualArray dimensionVA;
		private Group dimensionGroup;

		public Builder(ATableBasedDataDomain dataDomain) {
			this.dataDomain = dataDomain;
		}

		public Builder recordPerspective(Perspective recordPerspective) {
			this.recordPerspective = recordPerspective;
			return this;
		}

		public Builder dimensionPerspective(Perspective dimensionPerspective) {
			this.dimensionPerspective = dimensionPerspective;
			return this;
		}

		public Builder recordVA(VirtualArray recordVA) {
			this.recordVA = recordVA;
			return this;
		}

		public Builder dimensionVA(VirtualArray dimensionVA) {
			this.dimensionVA = dimensionVA;
			return this;
		}

		public Builder recordGroup(Group recordGroup) {
			this.recordGroup = recordGroup;
			return this;
		}

		public Builder dimensionGroup(Group dimensionGroup) {
			this.dimensionGroup = dimensionGroup;
			return this;
		}

		public TablePerspectiveCreator build() {
			if ((recordPerspective == null && (recordVA == null || recordGroup == null))
					|| (dimensionPerspective == null && (dimensionVA == null || dimensionGroup == null))) {
				throw new IllegalStateException("TablePerspectiveCreator is not initialized properly!");
			}
			return new TablePerspectiveCreator(this);
		}
	}

	private final ATableBasedDataDomain dataDomain;

	private Perspective recordPerspective;
	private Perspective dimensionPerspective;
	final private VirtualArray recordVA;
	final private Group recordGroup;
	final private VirtualArray dimensionVA;
	final private Group dimensionGroup;

	private TablePerspectiveCreator(Builder builder) {
		this.dataDomain = builder.dataDomain;
		this.dimensionPerspective = builder.dimensionPerspective;
		this.recordPerspective = builder.recordPerspective;
		this.recordVA = builder.recordVA;
		this.recordGroup = builder.recordGroup;
		this.dimensionVA = builder.dimensionVA;
		this.dimensionGroup = builder.dimensionGroup;
	}

	public TablePerspective create() {

		if (recordPerspective == null)
			createRecordPerspective();
		if (dimensionPerspective == null)
			createDimensionPerspective();

		TablePerspective tablePerspective = dataDomain.getTablePerspective(recordPerspective.getPerspectiveID(),
				dimensionPerspective.getPerspectiveID());
		tablePerspective.setLabel(getLabel(), false);

		if (tablePerspective.isPrivate()) {
			tablePerspective.setPrivate(false);

			DataDomainUpdateEvent event = new DataDomainUpdateEvent(dataDomain);
			event.setSender(this);
			
			EventPublisher.trigger(event);
		}
		return tablePerspective;
	}

	private void createRecordPerspective() {
		recordPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
		List<Integer> indices = recordVA.getIDsOfGroup(recordGroup.getGroupIndex());
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(indices);
		recordPerspective.init(data);
		recordPerspective.setLabel(recordGroup.getLabel(), true);
		// TODO: Shall we really set it private?
		recordPerspective.setPrivate(true);
		recordGroup.setPerspectiveID(recordPerspective.getPerspectiveID());
		dataDomain.getTable().registerRecordPerspective(recordPerspective);
	}

	private void createDimensionPerspective() {
		dimensionPerspective = new Perspective(dataDomain, dataDomain.getDimensionIDType());
		List<Integer> indices = dimensionVA.getIDsOfGroup(dimensionGroup.getGroupIndex());
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(indices);
		dimensionPerspective.init(data);
		dimensionPerspective.setLabel(dimensionGroup.getLabel(), true);
		// TODO: Shall we really set it private?
		dimensionPerspective.setPrivate(true);
		dimensionGroup.setPerspectiveID(dimensionPerspective.getPerspectiveID());
		dataDomain.getTable().registerDimensionPerspective(dimensionPerspective);
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public String getLabel() {
		return dataDomain.getLabel() + " - "
				+ ((recordPerspective != null) ? recordPerspective.getLabel() : recordGroup.getLabel()) + "/"
				+ ((dimensionPerspective != null) ? dimensionPerspective.getLabel() : dimensionGroup.getLabel());
	}

}
