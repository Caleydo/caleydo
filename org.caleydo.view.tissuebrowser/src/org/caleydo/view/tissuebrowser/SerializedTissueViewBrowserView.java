package org.caleydo.view.tissuebrowser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the data flipper view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedTissueViewBrowserView extends ASerializedView {

	/** list of initially contained view-ids */
	private List<ASerializedView> initialContainedViews;

	/**
	 * No-Arg Constructor to create a serialized view browser view with default
	 * parameters.
	 */
	public SerializedTissueViewBrowserView() {
		init();
	}

	public SerializedTissueViewBrowserView(EDataDomain dataDomain) {
		super(dataDomain);
		init();
	}

	public void init() {
		initialContainedViews = new ArrayList<ASerializedView>();

		// SerializedPathwayView pathway = new SerializedPathwayView();
		// pathway
		// .setPathwayID(((PathwayGraph)
		// GeneralManager.get().getPathwayManager().getAllItems().toArray()[0])
		// .getID());
		// pathway.setDataDomain(EDataDomain.PATHWAY_DATA);
		// initialContainedViews.add(pathway);
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
	public String getViewType() {
		return GLTissueViewBrowser.VIEW_ID;
	}
}
