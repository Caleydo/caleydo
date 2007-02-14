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
 * Creates a About - cridts box.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowPopupCredits 
extends ACommand
implements ICommand {

	private JComponent parentComponent = null;
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowPopupCredits() {
		super( -1,null,null);
	}
	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowPopupCredits( final JComponent setParentComonent ) {
		super( -1,null,null);
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
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

}
