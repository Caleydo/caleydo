package org.caleydo.core.serialize;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Collection-class for a list of all views to store.
 * NOTE that view data of the serialized views are stored via the RCP workbench mechanism.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class ViewList {

	/** list of all views to (re-)store */
	private List<? extends ASerializedView> views;

	@XmlElementWrapper
	public List<? extends ASerializedView> getViews() {
		return views;
	}

	public void setViews(List<? extends ASerializedView> views) {
		this.views = views;
	}
}
