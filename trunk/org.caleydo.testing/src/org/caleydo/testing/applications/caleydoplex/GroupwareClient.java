package org.caleydo.testing.applications.caleydoplex;

import DKT._GroupwareClientAppIDisp;
import Ice.Current;

public class GroupwareClient extends _GroupwareClientAppIDisp{
	
	@Override
	public void dummy(String s, Current current) {
		System.out.println(s);
		
	}
	
}


