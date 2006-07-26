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


import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.AbstractCommand;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a About - cridts box.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowPopupCredits 
extends AbstractCommand
implements CommandInterface {

	private JComponent parentComponent = null;
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowPopupCredits() {
	}
	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowPopupCredits( final JComponent setParentComonent ) {
		parentComponent = setParentComonent;
	}


	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public void setParent( final JComponent setParentComonent) {
		parentComponent = setParentComonent;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		JOptionPane.showMessageDialog( parentComponent,
				"project: GenView\n" +
				"http://www.icg.tu-graz.ac.at/genview/\n\n" +				
				"software design:         Michael Kalkusch\n" +
				"software implementation: Michael Kalkusch\n" +
				"visualization concepts:  Michael Kalkusch\n" +
				"medical knowhow:         Martin Asslaber\n\n",
				"About..",
				JOptionPane.PLAIN_MESSAGE );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_POPUP_CREDITS;
	}

}
