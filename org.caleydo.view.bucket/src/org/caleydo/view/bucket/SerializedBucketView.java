package org.caleydo.view.bucket;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.view.heatmap.heatmap.SerializedHeatMapView;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedBucketView extends ASerializedView {

	/** @see org.caleydo.core.org.caleydo.core.view.opengl.canvas.AGLViewBrowser.pathwayTexturesEnabled */
	private boolean pathwayTexturesEnabled;

	/** @see org.caleydo.core.org.caleydo.core.view.opengl.canvas.AGLViewBrowser.geneMappingEnabled */
	private boolean geneMappingEnabled;

	/** @see org.caleydo.core.org.caleydo.core.view.opengl.canvas.AGLViewBrowser.neighborhoodEnabled */
	private boolean neighborhoodEnabled;

	/** @see org.caleydo.core.org.caleydo.core.view.opengl.canvas.AGLViewBrowser.connectionLinesEnabled */
	private boolean connectionLinesEnabled;

	/** list of view-ids contained in the focus-level */
	private List<ASerializedView> focusViews;

	/** list of view-ids contained in the stack-level */
	private List<ASerializedView> stackViews;

	/**
	 * No-Arg Constructor to create a serialized bucket-view with default
	 * parameters.
	 */
	public SerializedBucketView() {
		this(EDataDomain.GENETIC_DATA);
	}

	public SerializedBucketView(EDataDomain dataDomain) {
		super(dataDomain);
		setPathwayTexturesEnabled(true);
		setNeighborhoodEnabled(true);
		setGeneMappingEnabled(true);
		setConnectionLinesEnabled(true);

		ArrayList<ASerializedView> remoteViews = new ArrayList<ASerializedView>();

		if (GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA) != null) {
			SerializedHeatMapView heatMap = new SerializedHeatMapView(
					dataDomain);
			remoteViews.add(heatMap);
			SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView(
					dataDomain);
			remoteViews.add(parCoords);
		}

//		if (GeneralManager.get().getUseCase(EDataDomain.CLINICAL_DATA) != null) {
//			SerializedGlyphView glyph1 = new SerializedGlyphView(
//					EDataDomain.CLINICAL_DATA);
//			remoteViews.add(glyph1);
//			SerializedGlyphView glyph2 = new SerializedGlyphView(
//					EDataDomain.CLINICAL_DATA);
//			remoteViews.add(glyph2);
//		}

		ArrayList<ASerializedView> focusLevel = new ArrayList<ASerializedView>();
		if (remoteViews.size() > 0) {
			focusLevel.add(remoteViews.remove(0));
		}
		setFocusViews(focusLevel);
		setStackViews(remoteViews);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	public boolean isPathwayTexturesEnabled() {
		return pathwayTexturesEnabled;
	}

	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		this.pathwayTexturesEnabled = pathwayTexturesEnabled;
	}

	public boolean isGeneMappingEnabled() {
		return geneMappingEnabled;
	}

	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		this.geneMappingEnabled = geneMappingEnabled;
	}

	public boolean isNeighborhoodEnabled() {
		return neighborhoodEnabled;
	}

	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		this.neighborhoodEnabled = neighborhoodEnabled;
	}

	public boolean isConnectionLinesEnabled() {
		return connectionLinesEnabled;
	}

	public void setConnectionLinesEnabled(boolean connectionLinesEnabled) {
		this.connectionLinesEnabled = connectionLinesEnabled;
	}

	@XmlElementWrapper
	public List<ASerializedView> getFocusViews() {
		return focusViews;
	}

	public void setFocusViews(List<ASerializedView> focusViews) {
		this.focusViews = focusViews;
	}

	@XmlElementWrapper
	public List<ASerializedView> getStackViews() {
		return stackViews;
	}

	public void setStackViews(List<ASerializedView> stackViews) {
		this.stackViews = stackViews;
	}

	@Override
	public String getViewType() {
		return GLBucket.VIEW_ID;
	}

	@Override
	public EDataDomain getDataDomain() {
		return dataDomain;
	}
}
