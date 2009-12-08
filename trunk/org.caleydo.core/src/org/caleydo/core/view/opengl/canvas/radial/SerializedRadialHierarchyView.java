package org.caleydo.core.view.opengl.canvas.radial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.usecase.EDataDomain;
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

	public static final String GUI_ID = "org.caleydo.rcp.views.GLRadialHierarchyView";

	private EDrawingStateType drawingStateType;
	private int maxDisplayedHierarchyDepth;
	private int rootElementID;
	private int selectedElementID;
	private float rootElementStartAngle;
	private float selectedElementStartAngle;
	private boolean isNewSelection;
	private EPDDrawingStrategyType defaultDrawingStrategyType;

	/**
	 * No-Arg Constructor to create a serialized radial-view with default parameters.
	 */
	public SerializedRadialHierarchyView() {
		init();
	}

	public SerializedRadialHierarchyView(EDataDomain dataDomain) {
		super(dataDomain);
		init();
	}

	private void init() {
		setMaxDisplayedHierarchyDepth(GLRadialHierarchy.DISP_HIER_DEPTH_DEFAULT);
		setDrawingStateType(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
		setDefaultDrawingStrategyType(EPDDrawingStrategyType.EXPRESSION_COLOR);
		setRootElementID(-1);
		setSelectedElementID(-1);
		setRootElementStartAngle(0);
		setSelectedElementStartAngle(0);
		setNewSelection(true);
	}

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

	public EDrawingStateType getDrawingStateType() {
		return drawingStateType;
	}

	public void setDrawingStateType(EDrawingStateType drawingStateType) {
		this.drawingStateType = drawingStateType;
	}

	public EPDDrawingStrategyType getDefaultDrawingStrategyType() {
		return defaultDrawingStrategyType;
	}

	public void setDefaultDrawingStrategyType(EPDDrawingStrategyType defaultDrawingStrategyType) {
		this.defaultDrawingStrategyType = defaultDrawingStrategyType;
	}

	@Override
	public String getViewGUIID() {
		return GUI_ID;
	}
}
