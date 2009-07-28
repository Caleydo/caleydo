package org.caleydo.core.manager.event.view.group;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals the merging of two groups. Depending on a boolean gene or experiment groupInfo has to
 * be used.
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class MergeGroupsEvent
	extends AEvent {

	private boolean bGeneGroup = false;

	public void setGeneExperimentFlag(boolean bGeneGroup) {
		this.bGeneGroup = bGeneGroup;
	}

	public boolean isGeneGroup() {
		return bGeneGroup;
	}

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
