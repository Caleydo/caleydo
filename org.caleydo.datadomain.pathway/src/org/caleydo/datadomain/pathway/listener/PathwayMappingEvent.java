/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.listener;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.ADirectedEvent;

/**
 * Events that signals that gene mapping within pathway views should be enabled.
 *
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class PathwayMappingEvent extends ADirectedEvent {

	private TablePerspective tablePerspective = null;

	/**
	 * Default Constructor.
	 */
	public PathwayMappingEvent() {
	}

	public PathwayMappingEvent(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	/**
	 * @param tablePerspective
	 *            setter, see {@link tablePerspective}
	 */
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
