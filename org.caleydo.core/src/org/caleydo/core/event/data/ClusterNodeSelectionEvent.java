/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.clusterer.EDrawingStateType;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;

/**
 * Event that should be triggered when cluster nodes are selected.
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
public class ClusterNodeSelectionEvent
	extends AEvent {

	private SelectionDelta selectionDelta;

	private EDrawingStateType drawingStateType;
	private EPDDrawingStrategyType defaultDrawingStrategyType;
	private int maxDisplayedHierarchyDepth;
	private int rootElementID;
	private int selectedElementID;
	private float rootElementStartAngle;
	private float selectedElementStartAngle;
	private boolean isNewSelection;
	private boolean isSenderRadialHierarchy;

	@Override
	public boolean checkIntegrity() {
		if (selectionDelta == null)
			throw new IllegalStateException("selectionDelta was not set");
		return true;
	}

	public EDrawingStateType getDrawingStateType() {
		return drawingStateType;
	}

	public void setDrawingStateType(EDrawingStateType drawingStateType) {
		this.drawingStateType = drawingStateType;
	}

	public int getMaxDisplayedHierarchyDepth() {
		return maxDisplayedHierarchyDepth;
	}

	public void setMaxDisplayedHierarchyDepth(int maxDisplayedHierarchyDepth) {
		this.maxDisplayedHierarchyDepth = maxDisplayedHierarchyDepth;
	}

	public int getRootElementID() {
		return rootElementID;
	}

	public void setRootElementID(int rootElementID) {
		this.rootElementID = rootElementID;
	}

	public int getSelectedElementID() {
		return selectedElementID;
	}

	public void setSelectedElementID(int selectedElementID) {
		this.selectedElementID = selectedElementID;
	}

	public float getRootElementStartAngle() {
		return rootElementStartAngle;
	}

	public void setRootElementStartAngle(float rootElementStartAngle) {
		this.rootElementStartAngle = rootElementStartAngle;
	}

	public float getSelectedElementStartAngle() {
		return selectedElementStartAngle;
	}

	public void setSelectedElementStartAngle(float selectedElementStartAngle) {
		this.selectedElementStartAngle = selectedElementStartAngle;
	}

	public boolean isNewSelection() {
		return isNewSelection;
	}

	public void setNewSelection(boolean isNewSelection) {
		this.isNewSelection = isNewSelection;
	}

	public EPDDrawingStrategyType getDefaultDrawingStrategyType() {
		return defaultDrawingStrategyType;
	}

	public void setDefaultDrawingStrategyType(EPDDrawingStrategyType defaultDrawingStrategyType) {
		this.defaultDrawingStrategyType = defaultDrawingStrategyType;
	}

	public boolean isSenderRadialHierarchy() {
		return isSenderRadialHierarchy;
	}

	public void setSenderRadialHierarchy(boolean isSenderRadialHierarchy) {
		this.isSenderRadialHierarchy = isSenderRadialHierarchy;
	}

	public SelectionDelta getSelectionDelta() {
		return selectionDelta;
	}

	public void setSelectionDelta(SelectionDelta selectionDelta) {
		this.selectionDelta = selectionDelta;
	}

}
