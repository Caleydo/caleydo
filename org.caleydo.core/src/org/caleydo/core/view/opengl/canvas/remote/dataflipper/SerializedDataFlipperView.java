package org.caleydo.core.view.opengl.canvas.remote.dataflipper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.pathway.SerializedPathwayView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHierarchicalHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.caleydo.core.view.opengl.canvas.tissue.SerializedTissueView;

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
	
	/**
	 * No-Arg Constructor to create a serialized data flipper view with default parameters.
	 */
	public SerializedDataFlipperView() {

		initialContainedViews = new ArrayList<ASerializedView>();
		
		IUseCase usecase = GeneralManager.get().getUseCase();
		if (usecase instanceof GeneticUseCase) {
	
			SerializedHierarchicalHeatMapView heatMap = new SerializedHierarchicalHeatMapView();
			initialContainedViews.add(heatMap);	
			SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView();
			initialContainedViews.add(parCoords);
			SerializedPathwayView pathway = new SerializedPathwayView();
			pathway.setPathwayID(((PathwayGraph)GeneralManager.get().getPathwayManager().getAllItems().toArray()[0]).getID());
			initialContainedViews.add(pathway);	
			pathway = new SerializedPathwayView();
			pathway.setPathwayID(((PathwayGraph)GeneralManager.get().getPathwayManager().getAllItems().toArray()[1]).getID());
			initialContainedViews.add(pathway);	
			pathway = new SerializedPathwayView();
			pathway.setPathwayID(((PathwayGraph)GeneralManager.get().getPathwayManager().getAllItems().toArray()[2]).getID());
			initialContainedViews.add(pathway);	
			SerializedTissueView tissue = new SerializedTissueView();
			initialContainedViews.add(tissue);	
		}
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
	public List<ASerializedView> getInitialContainedViews() {
		return initialContainedViews;
	}

	@Override
	public String getViewGUIID() {
		return GUI_ID;
	}
}
