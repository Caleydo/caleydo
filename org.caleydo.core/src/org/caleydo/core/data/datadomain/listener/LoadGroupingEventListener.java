/**
 *
 */
package org.caleydo.core.data.datadomain.listener;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.event.LoadGroupingEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.io.gui.dataimport.ImportGroupingCommand;
import org.eclipse.swt.widgets.Display;

/**
 * Listener for {@link LoadGroupingEvent}.
 *
 * @author Christian Partl
 *
 */
public class LoadGroupingEventListener extends AEventListener<ATableBasedDataDomain> {

	public LoadGroupingEventListener(ATableBasedDataDomain dataDomain) {
		setHandler(dataDomain);
		setExclusiveEventSpace(dataDomain.getDataDomainID());
	}
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadGroupingEvent) {
			LoadGroupingEvent loadGroupingEvent = (LoadGroupingEvent) event;
			Display.getDefault().asyncExec(new ImportGroupingCommand(loadGroupingEvent.getIdCategory(), handler));
		}
	}

}
