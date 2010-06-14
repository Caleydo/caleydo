package org.caleydo.musama;

import de.mmis.core.base.event.Observable;

public interface InformationProvider extends
		Observable<InformationProviderEvent> {
	public String getRole(String id);

}
