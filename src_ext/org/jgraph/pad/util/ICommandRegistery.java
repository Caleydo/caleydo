package org.jgraph.pad.util;

import javax.swing.Action;

public interface ICommandRegistery {
	public Action getCommand(String key);
    public void initCommand(Action action);
}
