package org.caleydo.core.manager.event;

public class ViewCommandEventContainer
	extends AEventContainer
{
	private EViewCommand eViewCommand;
	
	/**
	 * Constructor.
	 */
	public ViewCommandEventContainer(EViewCommand eViewCommand)
	{
		super(EEventType.VIEW_COMMAND);
		
		this.eViewCommand = eViewCommand;
	}

	public EViewCommand getViewCommand()
	{
		return eViewCommand;
	}
}
