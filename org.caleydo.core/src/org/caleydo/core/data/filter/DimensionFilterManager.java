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
package org.caleydo.core.data.filter;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.filter.event.MoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.MoveDimensionFilterListener;
import org.caleydo.core.data.filter.event.NewDimensionFilterEvent;
import org.caleydo.core.data.filter.event.NewDimensionFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListListener;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterListener;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaEvent;

/**
 * Concrete implementation of {@link FilterManager} for {@link DimensionVirtualArray}s.
 *
 * @author Alexander Lex
 */
public class DimensionFilterManager
	extends FilterManager<DimensionPerspective, DimensionVADelta, DimensionFilter, DimensionVirtualArray> {

	private RemoveDimensionFilterListener removeDimensionFilterListener;
	private MoveDimensionFilterListener moveDimensionFilterListener;
	private NewDimensionFilterListener newDimensionFilterListener;
	private ReEvaluateDimensionFilterListListener reEvaluateDimensionFilterListListener;

	public DimensionFilterManager(IDataDomain dataDomain, DimensionPerspective perspective) {
		super(dataDomain, perspective);

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		removeDimensionFilterListener = new RemoveDimensionFilterListener();
		removeDimensionFilterListener.setHandler(this);
		removeDimensionFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveDimensionFilterEvent.class, removeDimensionFilterListener);

		moveDimensionFilterListener = new MoveDimensionFilterListener();
		moveDimensionFilterListener.setHandler(this);
		moveDimensionFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveDimensionFilterEvent.class, moveDimensionFilterListener);

		newDimensionFilterListener = new NewDimensionFilterListener();
		newDimensionFilterListener.setHandler(this);
		newDimensionFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewDimensionFilterEvent.class, newDimensionFilterListener);

		reEvaluateDimensionFilterListListener = new ReEvaluateDimensionFilterListListener();
		reEvaluateDimensionFilterListListener.setHandler(this);
		reEvaluateDimensionFilterListListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReEvaluateDimensionFilterListEvent.class,
			reEvaluateDimensionFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (removeDimensionFilterListener != null) {
			eventPublisher.removeListener(removeDimensionFilterListener);
			removeDimensionFilterListener = null;
		}

		if (moveDimensionFilterListener != null) {
			eventPublisher.removeListener(moveDimensionFilterListener);
			moveDimensionFilterListener = null;
		}

		if (newDimensionFilterListener != null) {
			eventPublisher.removeListener(newDimensionFilterListener);
			newDimensionFilterListener = null;
		}

		if (reEvaluateDimensionFilterListListener != null) {
			eventPublisher.removeListener(reEvaluateDimensionFilterListListener);
			reEvaluateDimensionFilterListListener = null;
		}
	}

	@Override
	protected void triggerVADeltaEvent(DimensionVADelta delta) {
		DimensionVADeltaEvent event = new DimensionVADeltaEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	// @Override
	// protected void triggerReplaceVAEvent() {
	// DimensionReplaceVAEvent event = new DimensionReplaceVAEvent(dataDomain.getDataDomainID(),
	// perspective.getPerspectiveID(), perspective.getVirtualArray());
	// event.s(perspective.getPerspectiveID());
	// event.setVirtualArray(perspective.getVirtualArray());
	// event.setSender(this);
	// event.setDataDomainID(dataDomain.getDataDomainID());
	// eventPublisher.triggerEvent(event);
	// }

	@Override
	protected void resetVA() {
		perspective.reset();
	}

}
