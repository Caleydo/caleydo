/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;


import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a internal frame dispaying a 2D heatmap.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameScatterplot2D 
extends ACommand
implements ICommand {

	private JComponent parentComponent = null;
	
	private String sTextMessage = "";
	
	private String sTextHeader = "Info";
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowNewIFrameScatterplot2D() {
		super( null);
	}
	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameScatterplot2D( final JComponent setParentComonent ) {
		super( null);
		parentComponent = setParentComonent;
	}


	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public void setParent( final JComponent setParentComonent) {
		parentComponent = setParentComonent;
	}
	
	public void setText( final String sTextHeader, final String sTextMessage) {
		this.sTextMessage = sTextMessage;
		this.sTextHeader = sTextHeader;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		JOptionPane.showMessageDialog( parentComponent,
				sTextMessage,
				sTextHeader,
				JOptionPane.PLAIN_MESSAGE );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_POPUP_CREDITS;
	}

}
