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
package org.caleydo.view.filterpipeline;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.filter.DimensionFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.RecordFilter;
import org.caleydo.core.data.filter.event.CombineFilterEvent;
import org.caleydo.core.data.filter.event.CombineRecordFilterEvent;
import org.caleydo.core.data.filter.event.MoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.MoveFilterEvent;
import org.caleydo.core.data.filter.event.MoveRecordtFilterEvent;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.RemoveFilterEvent;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.representation.FilterRepresentation;
import org.caleydo.view.filterpipeline.representation.IRenderable;

/**
 * @brief Represents a filter in the GLFilterPipeline view
 * 
 *        Also renders a context menu if needed
 * 
 * @author Thomas Geymayer
 * 
 */
public class FilterItem<DeltaType extends VirtualArrayDelta<?>> implements IRenderable,
		IDropArea {
	private int id;
	private int pickingId;
	private Filter<DeltaType> filter;
	private FilterRepresentation representation = null;

	VirtualArray<?, DeltaType, ?> input = null;
	VirtualArray<?, DeltaType, ?> output = null;
	VirtualArray<?, DeltaType, ?> outputUncertainty = null;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param filter
	 * @param pickingManager
	 * @param uniqueID
	 */
	public FilterItem(int id, Filter<DeltaType> filter, PickingManager pickingManager,
			int iUniqueID) {
		this.id = id;
		pickingId = pickingManager.getPickingID(iUniqueID, PickingType.FILTERPIPE_FILTER,
				id);
		this.filter = filter;
	}

	public Filter<DeltaType> getFilter() {
		return filter;
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer) {
		representation.render(gl, textRenderer);
	}

	/**
	 * Set the filter representation
	 * 
	 * @param representation
	 */
	public void setRepresentation(FilterRepresentation representation) {
		representation.setFilter(this);
		this.representation = representation;
	}

	public FilterRepresentation getRepresentation() {
		return representation;
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return filter.getLabel();
	}

	/**
	 * Set the items this filter should use as input
	 * 
	 * @param input
	 */
	@SuppressWarnings("unchecked")
	public void setInput(VirtualArray<?, ?, ?> input) {
		this.input = (VirtualArray<?, DeltaType, ?>) input;
		output = this.input.clone();

		if (filter.getVADeltaUncertainty() != null)
			outputUncertainty = this.input.clone();

		output.setDelta(filter.getVADelta());

		if (filter.getVADeltaUncertainty() != null)
			outputUncertainty.setDelta(filter.getVADeltaUncertainty());
	}

	/**
	 * Get the items this filter received as input
	 * 
	 * @return
	 */
	public VirtualArray<?, DeltaType, ?> getInput() {
		return input;
	}

	/**
	 * Get the items which passed this filter
	 * 
	 * @return
	 */
	public VirtualArray<?, DeltaType, ?> getOutput() {
		return output;
	}

	/**
	 * Get the uncertain items which passed this filter
	 * 
	 * @return
	 */
	public VirtualArray<?, DeltaType, ?> getUncertaintyOutput() {
		return outputUncertainty;
	}

	public int getSizeVADelta() {
		return filter.getVADelta().size();
	}

	public void showDetailsDialog() {
		filter.getFilterRep().create();
	}

	public void triggerRemove() {
		RemoveFilterEvent<?> filterEvent = null;

		if (filter instanceof RecordFilter) {
			filterEvent = new RemoveRecordFilterEvent();
			((RemoveRecordFilterEvent) filterEvent).setFilter((RecordFilter) filter);
		} else if (filter instanceof DimensionFilter) {
			filterEvent = new RemoveDimensionFilterEvent();
			((RemoveDimensionFilterEvent) filterEvent)
					.setFilter((DimensionFilter) filter);
		} else {
			System.err.println(getClass() + "::triggerRemove(): Unimplemented...");
		}

		if (filterEvent != null) {
			filterEvent.setDataDomainID(filter.getDataDomain().getDataDomainID());
			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
		}
	}

	public void triggerMove(int offset) {
		MoveFilterEvent<?> filterEvent = null;

		if (filter instanceof RecordFilter) {
			filterEvent = new MoveRecordtFilterEvent();
			((MoveRecordtFilterEvent) filterEvent).setFilter((RecordFilter) filter);
		} else if (filter instanceof DimensionFilter) {
			filterEvent = new MoveDimensionFilterEvent();
			((MoveDimensionFilterEvent) filterEvent).setFilter((DimensionFilter) filter);
		} else {
			System.err.println(getClass() + "::triggerMove(): Unimplemented...");
		}

		if (filterEvent != null) {
			filterEvent.setDataDomainID(filter.getDataDomain().getDataDomainID());
			((MoveFilterEvent<?>) filterEvent).setOffset(offset);
			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
		}
	}

	public int getPickingID() {
		return pickingId;
	}

	private FilterRepresentation getFilterRepresentation(Set<IDraggable> draggables) {
		if (draggables.size() > 1) {
			System.err.println("getFilterRepresentation: More than one draggable?");
			return null;
		}

		IDraggable draggable = draggables.iterator().next();
		if (!(draggable instanceof FilterRepresentation))
			return null;

		return (FilterRepresentation) draggable;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {
		if (getFilterRepresentation(draggables) == representation)
			return;

		representation.handleDragOver(gl, draggables, mouseCoordinateX, mouseCoordinateY);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {
		if (getFilterRepresentation(draggables) == representation)
			return;

		representation.handleDrop(gl, draggables, mouseCoordinateX, mouseCoordinateY,
				dragAndDropController);

		CombineFilterEvent<?> filterEvent = null;

		if (filter instanceof RecordFilter) {
			filterEvent = new CombineRecordFilterEvent();
			((CombineRecordFilterEvent) filterEvent).setFilter((RecordFilter) filter);
			((CombineRecordFilterEvent) filterEvent)
					.addCombineFilter((RecordFilter) getFilterRepresentation(draggables)
							.getFilter().filter);
		}
		// else if( filter instanceof DimensionFilter )
		// {
		//
		// }
		else {
			System.err.println(getClass() + "::triggerMove(): Unimplemented...");
		}

		if (filterEvent != null) {
			filterEvent.setDataDomainID(filter.getDataDomain().getDataDomainID());
			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
		}
	}

	public void handleIconMouseOver(int externalID) {
		representation.handleIconMouseOver(externalID);
	}

	public void handleClearMouseOver() {
		representation.handleClearMouseOver();
	}

	@Override
	public void handleDropAreaReplaced()
	{
		// TODO Auto-generated method stub
		
	}
}
