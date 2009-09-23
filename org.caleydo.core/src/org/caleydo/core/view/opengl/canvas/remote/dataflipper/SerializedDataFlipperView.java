package org.caleydo.core.view.opengl.canvas.remote.dataflipper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.caleydo.core.view.opengl.canvas.pathway.SerializedPathwayView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHierarchicalHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.caleydo.core.view.opengl.canvas.tissue.SerializedTissueView;

/**
 * Serialized form of the data flipper view.
 * 
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

	}

	public SerializedDataFlipperView(EDataDomain dataDomain) {
		super(dataDomain);
		init();
	}

	public void init() {
		initialContainedViews = new ArrayList<ASerializedView>();

		SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView();
		parCoords.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(parCoords);

		SerializedTissueView tissue = new SerializedTissueView();
		tissue.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(tissue);

		SerializedHierarchicalHeatMapView heatMap = new SerializedHierarchicalHeatMapView();
		heatMap.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(heatMap);

		SerializedGlyphView glyph = new SerializedGlyphView();
		glyph.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(glyph);

		SerializedPathwayView pathway = new SerializedPathwayView();
		pathway = new SerializedPathwayView();
		pathway
			.setPathwayID(((PathwayGraph) GeneralManager.get().getPathwayManager().getAllItems().toArray()[0])
				.getID());
		pathway.setDataDomain(EDataDomain.GENETIC_DATA);
		initialContainedViews.add(pathway);

		parCoords = new SerializedParallelCoordinatesView();
		parCoords.setDataDomain(EDataDomain.CLINICAL_DATA);
		initialContainedViews.add(parCoords);

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
