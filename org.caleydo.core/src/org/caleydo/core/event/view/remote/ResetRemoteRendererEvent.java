package org.caleydo.core.event.view.remote;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * Reset the remote renderer
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ResetRemoteRendererEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
