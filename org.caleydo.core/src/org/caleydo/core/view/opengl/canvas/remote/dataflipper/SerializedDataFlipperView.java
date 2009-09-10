package org.caleydo.core.view.opengl.canvas.remote.dataflipper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHierarchicalHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;

/**
 * Serialized form of the data flipper view. 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedDataFlipperView 
	extends ASerializedView {

	public static final String GUI_ID = "org.caleydo.rcp.views.opengl.GLDataFlipperView";

	/** list of initially contained view-ids */
	private List<ASerializedView> initialContainedViews;
	
	private ASerializedView focusView;
	
	/**
	 * No-Arg Constructor to create a serialized data flipper view with default parameters.
	 */
	public SerializedDataFlipperView() {
		
		ArrayList<ASerializedView> remoteViews = new ArrayList<ASerializedView>();

		IUseCase usecase = GeneralManager.get().getUseCase();
		if (usecase instanceof GeneticUseCase) {

			SerializedHierarchicalHeatMapView heatMap = new SerializedHierarchicalHeatMapView();
			remoteViews.add(heatMap);
			SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView();
			remoteViews.add(parCoords);			
		}
		
		focusView = remoteViews.remove(0);
		
//		ArrayList<ASerializedView> initialContainedViews = new ArrayList<ASerializedView>();
//		if (remoteViews.size() > 0) {
//			focusLevel.add(remoteViews.remove(0));
//		}
//		setFocusViews(focusLevel);
	}
	
	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_DATA_FLIPPER;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@XmlElementWrapper
	public ASerializedView getFocusView() {
		return focusView;
	}
//
//	public void setFocusViews(List<ASerializedView> focusViews) {
//		this.focusViews = focusViews;
//	}
//
//	@XmlElementWrapper
//	public List<ASerializedView> getStackViews() {
//		return stackViews;
//	}
//
//	public void setStackViews(List<ASerializedView> stackViews) {
//		this.stackViews = stackViews;
//	}

	@Override
	public String getViewGUIID() {
		return GUI_ID;
	}
}
