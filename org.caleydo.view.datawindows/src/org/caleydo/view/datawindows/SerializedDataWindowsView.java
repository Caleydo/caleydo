package org.caleydo.view.datawindows;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.pathway.SerializedPathwayView;


/**
 * Serialized form of a scatterplot-view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedDataWindowsView extends ASerializedView {

	
	private List<ASerializedView> testViews;
	
	/**
	 * Default constructor with default initialization
	 */
	public SerializedDataWindowsView() {
	}

	public SerializedDataWindowsView(EDataDomain dataDomain) {
		super(dataDomain);
		ArrayList<ASerializedView> remoteViews = new ArrayList<ASerializedView>();
//		SerializedHeatMapView heatMap = new SerializedHeatMapView(
//				dataDomain);
//		remoteViews.add(heatMap);
//		SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView(
//				dataDomain);
//		remoteViews.add(parCoords);
		
		SerializedPathwayView pathway = new SerializedPathwayView(dataDomain);
		pathway.setPathwayID(GeneralManager.get().getPathwayManager().searchPathwayByName("TGF-beta signaling pathway", EPathwayDatabaseType.KEGG).getID());
		remoteViews.add(pathway);
		
		setTestViews(remoteViews);
		
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC,
				0, 8, 0, 8, -20, 20);
		return viewFrustum;
	}
	
	

	@Override
	public String getViewType() {
		return GLDataWindows.VIEW_ID;
	}
	
	public void setTestViews(List<ASerializedView> TestViews) {
		this.testViews = TestViews;
	}
	
	public List<ASerializedView> getTestViews() {
		return testViews;
	}
}
