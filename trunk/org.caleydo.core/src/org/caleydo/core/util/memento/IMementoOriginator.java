package org.caleydo.core.util.memento;

public interface IMementoOriginator {

	public void setMemento(IMemento memento);

	public IMemento createMemento();
}
