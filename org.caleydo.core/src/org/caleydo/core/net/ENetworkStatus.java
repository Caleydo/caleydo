package org.caleydo.core.net;

/**
 * enumeration of possible network-status 
 * 
 * @author Werner Puff
 */
public enum ENetworkStatus {

	/** status that indicates that no network-services have been created */
	STATUS_STOPPED,
	
	/** status that indicates that network services are started, but no client or server is running */
	STATUS_STARTED,
	
	/** status that indicates that network services are started and a server is running */
	STATUS_SERVER,
	
	/** status that indicates that network-services are started and this application is connected to a server */
	STATUS_CLIENT;
		
}
