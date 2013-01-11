/**
 *
 */
package org.caleydo.core.data.datadomain.listener;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.event.AskRemoveDataDomainEvent;
import org.caleydo.core.data.datadomain.event.LoadGroupingEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Listener for {@link LoadGroupingEvent}.
 *
 * @author Christian Partl
 *
 */
public class AskRemoveDataDomainEventListener extends AEventListener<ADataDomain> {

	public AskRemoveDataDomainEventListener(ADataDomain dataDomain) {
		setHandler(dataDomain);
		setExclusiveDataDomainID(dataDomain.getDataDomainID());
	}
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AskRemoveDataDomainEvent) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (!MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Are you sure?",
							"Do you really want to delete the data set: " + handler.getLabel() + "?"))
						return;
					DataDomainManager.get().unregister(handler);
				}
			});
		}
	}

}
