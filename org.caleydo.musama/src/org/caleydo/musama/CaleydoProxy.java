package org.caleydo.musama;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import de.mmis.core.base.event.Observable;
import de.mmis.core.base.event.Observer;
import de.mmis.core.sexpression.Atom;
import de.mmis.core.tuplespace.TuplespaceException;
import de.mmis.core.tuplespace.TuplespaceProxy;

public class CaleydoProxy implements Observer<InformationProviderEvent> {

	InformationProvider informationProvider;
	GeneralManager caleydo;

	public CaleydoProxy(GeneralManager caleydo, String host, int port) throws UnknownHostException, IOException,
			TuplespaceException, TimeoutException {
		informationProvider = TuplespaceProxy.createProxy(InformationProvider.class, new Atom("InformationProvider"), host, port);
		this.caleydo = caleydo;
		informationProvider.addObserver(this);
	}

	@Override
	public void notify(Observable<? extends InformationProviderEvent> sender,
			InformationProviderEvent event) {
		System.out.println("Event: "+event);
		System.out.println("Caleydo: "+caleydo);	
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.caleydo.view.datawindows");
				}
				catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
