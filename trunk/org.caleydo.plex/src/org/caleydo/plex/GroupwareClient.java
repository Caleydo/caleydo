package org.caleydo.plex;

import DKT._GroupwareClientAppIDisp;
import Ice.Current;

public class GroupwareClient extends _GroupwareClientAppIDisp {

	/** generated serialVersionUID */
	private static final long serialVersionUID = 7619300357581983972L;

	@Override
	public void dummy(String s, Current current) {
		System.out.println(s);

	}

}
