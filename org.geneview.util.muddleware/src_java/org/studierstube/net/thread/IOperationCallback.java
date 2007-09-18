package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IOperation;

public interface IOperationCallback {

	public void callbackOperationOnReceive(IOperation sent, IOperation received);
	
}
