package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is no different from {@link SelectionCommandEvent} except for it's type thereby allowing a
 * different listener to react on it.
 * 
 * @author Werner Puff
 * @author Alexander lex
 */
@XmlRootElement
@XmlType
public class TriggerPropagationCommandEvent
	extends SelectionCommandEvent {

}
