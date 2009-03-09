package org.caleydo.core.manager.event;

/**
 * Container for view commands. View commands signal a view that something has changed and that they need to
 * react.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ViewCommandEventContainer
	extends AEventContainer {
	private EViewCommand eViewCommand;

	/**
	 * Constructor.
	 */
	public ViewCommandEventContainer(EViewCommand eViewCommand) {
		super(EEventType.VIEW_COMMAND);

		this.eViewCommand = eViewCommand;
	}

	public EViewCommand getViewCommand() {
		return eViewCommand;
	}
}
