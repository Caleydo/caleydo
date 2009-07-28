package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AFlagSetterEvent;

/**
 * Event that signals that the parallel coordinates should change their occlusion prevention state.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class PreventOcclusionEvent
	extends AFlagSetterEvent {

	/**
	 * Default no-arg constuctor
	 */
	public PreventOcclusionEvent() {
		// nothing to initialize
	}
	
	/**
	 * True for flag means that the parallel coordinates should enable occlusion prevention, flase disable
	 * 
	 * @param bFlag
	 */
	public PreventOcclusionEvent(boolean bFlag) {
		super(bFlag);
	}

}
