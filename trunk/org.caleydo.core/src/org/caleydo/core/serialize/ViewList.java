package org.caleydo.core.serialize;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Collection-class for a list of all views to store
 * 
 * @author Werner Puff
 */
@XmlType
@XmlRootElement
public class ViewList {

	/** list of all views to (re-)store */
	private List<ASerializedView> views;

	@XmlElementWrapper
	public List<ASerializedView> getViews() {
		return views;
	}

	public void setViews(List<ASerializedView> views) {
		this.views = views;
	}

}
