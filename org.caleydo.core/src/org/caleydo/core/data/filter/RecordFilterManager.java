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

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.event.CombineRecordFilterEvent;
import org.caleydo.core.data.filter.event.CombineRecordFilterListener;
import org.caleydo.core.data.filter.event.MoveRecordFilterListener;
import org.caleydo.core.data.filter.event.MoveRecordtFilterEvent;
import org.caleydo.core.data.filter.event.NewRecordFilterEvent;
import org.caleydo.core.data.filter.event.NewRecordFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListListener;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.data.filter.event.RemoveRecordFilterListener;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;

/**
 * Concrete implementation of {@link FilterManager} for {@link RecordVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class RecordFilterManager
	extends FilterManager<RecordPerspective, RecordVADelta, RecordFilter, RecordVirtualArray> {

	// private RecordVADeltaListener recordVAUpdateListener;
	private RemoveRecordFilterListener removeContentFilterListener;
	private MoveRecordFilterListener moveContentFilterListener;
	private CombineRecordFilterListener combineContentFilterListener;
	private NewRecordFilterListener newContentFilterListener;
	private ReEvaluateRecordFilterListListener reEvaluateContentFilterListListener;

	public RecordFilterManager(ATableBasedDataDomain dataDomain, RecordPerspective perspective) {
		super(dataDomain, perspective);
	}

	@Override
	public void registerEventListeners() {
		removeContentFilterListener = new RemoveRecordFilterListener();
		removeContentFilterListener.setHandler(this);
		removeContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveRecordFilterEvent.class, removeContentFilterListener);

		moveContentFilterListener = new MoveRecordFilterListener();
		moveContentFilterListener.setHandler(this);
		moveContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveRecordtFilterEvent.class, moveContentFilterListener);

		combineContentFilterListener = new CombineRecordFilterListener();
		combineContentFilterListener.setHandler(this);
		combineContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(CombineRecordFilterEvent.class, combineContentFilterListener);

		newContentFilterListener = new NewRecordFilterListener();
		newContentFilterListener.setHandler(this);
		newContentFilterListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewRecordFilterEvent.class, newContentFilterListener);

		reEvaluateContentFilterListListener = new ReEvaluateRecordFilterListListener();
		reEvaluateContentFilterListListener.setHandler(this);
		reEvaluateContentFilterListListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher
			.addListener(ReEvaluateRecordFilterListEvent.class, reEvaluateContentFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (removeContentFilterListener != null) {
			eventPublisher.removeListener(removeContentFilterListener);
			removeContentFilterListener = null;
		}

		if (moveContentFilterListener != null) {
			eventPublisher.removeListener(moveContentFilterListener);
			moveContentFilterListener = null;
		}

		if (combineContentFilterListener != null) {
			eventPublisher.removeListener(combineContentFilterListener);
			combineContentFilterListener = null;
		}

		if (newContentFilterListener != null) {
			eventPublisher.removeListener(newContentFilterListener);
			newContentFilterListener = null;
		}

		if (reEvaluateContentFilterListListener != null) {
			eventPublisher.removeListener(reEvaluateContentFilterListListener);
			reEvaluateContentFilterListListener = null;
		}
	}

	// @Override
	// protected void triggerReplaceVAEvent() {
	// RecordReplaceVAEvent event = new RecordReplaceVAEvent();
	// event.setVAType(perspective.getPerspectiveID());
	// event.setVirtualArray(perspective.getVirtualArray());
	// event.setSender(this);
	// event.setDataDomainID(dataDomain.getDataDomainID());
	// eventPublisher.triggerEvent(event);
	// }

	@Override
	protected void triggerVADeltaEvent(RecordVADelta delta) {
		RecordVADeltaEvent event = new RecordVADeltaEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void resetVA() {
		perspective.reset();
	}

}
