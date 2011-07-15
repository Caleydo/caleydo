package org.caleydo.view.pathwaybrowser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of the pathway browser view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedPathwayViewBrowserView extends ASerializedView {

	/** list of initially contained view-ids */
	private List<ASerializedView> initialContainedViews;

	/**
	 * No-Arg Constructor to create a serialized view browser view with default
	 * parameters.
	 */
	public SerializedPathwayViewBrowserView() {
		init();
	}

	public SerializedPathwayViewBrowserView(String dataDomainType) {
		super(dataDomainType);
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

	@XmlElementWrapper
	public List<ASerializedView> getInitialContainedViews() {
		return initialContainedViews;
	}

	@Override
	public String getViewType() {
		return GLPathwayViewBrowser.VIEW_TYPE;
	}
}
