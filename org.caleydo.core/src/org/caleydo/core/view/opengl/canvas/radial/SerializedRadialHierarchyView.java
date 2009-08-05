package org.caleydo.core.view.opengl.canvas.radial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the radial hierarchy view.
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
public class SerializedRadialHierarchyView
	extends ASerializedView {

	EDrawingStateType drawingStateType;
	private int maxDisplayedHierarchyDepth;
	private int rootElementID;
	private int selectedElementID;
	private int mouseOverElementID;
	private float rootElementStartAngle;
	private float selectedElementStartAngle;
	private boolean isNewSelection;
	

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_RADIAL_HIERARCHY;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	public int getMaxDisplayedHierarchyDepth() {
		return maxDisplayedHierarchyDepth;
	}

	public void setMaxDisplayedHierarchyDepth(int maxDisplayedHierarchyDepth) {
		this.maxDisplayedHierarchyDepth = maxDisplayedHierarchyDepth;
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

	public boolean isNewSelection() {
		return isNewSelection;
	}

	public void setNewSelection(boolean isNewSelection) {
		this.isNewSelection = isNewSelection;
	}

	public int getMouseOverElementID() {
		return mouseOverElementID;
	}

	public void setMouseOverElementID(int mouseOverElementID) {
		this.mouseOverElementID = mouseOverElementID;
	}

	public EDrawingStateType getDrawingStateType() {
		return drawingStateType;
	}

	public void setDrawingStateType(EDrawingStateType drawingStateType) {
		this.drawingStateType = drawingStateType;
	}
}
