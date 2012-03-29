package org.caleydo.core.event.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * Event for hierarchy level highlighting.
 * 
 * @author Michael Lafer
 */

@XmlRootElement
@XmlType
public class LevelHighlightingEvent
	extends AEvent {

	int hierarchyLevel;

	public int getHierarchyLevel() {
		return hierarchyLevel;
	}

	public void setHierarchyLevel(int hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
