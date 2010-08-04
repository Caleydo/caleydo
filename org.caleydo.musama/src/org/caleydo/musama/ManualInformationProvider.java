package org.caleydo.musama;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.musama.InformationProviderEvent.Type;

import de.mmis.core.base.event.AbstractObservable;
import de.mmis.core.pojop.NetworkPublisher;
import de.mmis.core.pojop.NetworkPublisher.ServerType;
import de.mmis.core.pojop.Server;
import de.mmis.core.pojop.logging.LoggingMode;
import de.mmis.core.sexpression.Atom;
import de.mmis.core.tuplespace.TuplespaceException;
import de.mmis.core.tuplespace.TuplespacePublisher;

public class ManualInformationProvider extends
		AbstractObservable<InformationProviderEvent> implements
		InformationProvider {
	Map<String, String> id2role;
	
	public ManualInformationProvider() {
		id2role = new HashMap<String, String>();
	}
	
	public void setRole(String id, String role) {
		id2role.put(id, role);
		fireEvent(new InformationProviderEvent(Type.ROLE_CHANGED, id));
	}

	public String getRole(String id) {
		return id2role.get(id);
	}
	
	public static void main(String[] args) throws TuplespaceException,
			InterruptedException, IOException {
		
		InformationProvider informationProvider = new ManualInformationProvider();

		TuplespacePublisher tsp = new TuplespacePublisher(informationProvider,
				new Atom("InformationProvider"), "localhost", 21801,
				LoggingMode.NONE, null);
		NetworkPublisher networkPublisher = new NetworkPublisher(
				informationProvider, new Atom("InformationProvider"));
		Server server = networkPublisher.publish(ServerType.Socket, 21900);

		System.out.println("Press enter to stop information provider");
		System.in.read();
		
		tsp.close();
		server.stopListening();
	}

}
