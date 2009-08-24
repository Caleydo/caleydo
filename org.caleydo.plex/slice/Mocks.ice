module DKT {

	interface MasterApplicationI;	

    sequence<string> StringList;

    struct GroupwareInformation
	{
	    // unique display ID in Deskotheque 
	    // (displays are spatially separated screen regions) 
		int displayID;

		// indicates whether the display is private
		bool isPrivate;

		// identification string of the groupware
		string groupwareID;

		// Deskotheque unique XID consisting of 
		//  - Server hostname 
		//  - window X ID 
		string deskoXID;
	}; 

    interface ResourceManagerI
    {
        StringList getAvailableGroupwareClients(string clientID);
        string getHomeGroupwareClient(string clientID);
        string getPublicGroupwareClient(string clientID);
        void unregisterGroupwareClient(string clientID); 
    };

    interface GroupwareClientAppI
	{
	     void dummy(string filename); 
	}; 

	interface ApplicationI
	{
	};
	
    interface ServerApplicationI extends ApplicationI
    {
    	MasterApplicationI* getMasterProxy();
    };

    interface MasterApplicationI extends ApplicationI
    {
    	ResourceManagerI* getResourceManagerProxy();
        GroupwareInformation registerGroupwareClient(GroupwareClientAppI* client, string id, ServerApplicationI* serverApp, int x, int y, int w, int h);
    };

};
