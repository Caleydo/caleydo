/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

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
public class CmdSystemNewFrame 
extends AbstractCommand
implements CommandInterface {

	
	private JComponent parentComponent = null;
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdSystemNewFrame() {
	}
	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdSystemNewFrame( final JComponent setParentComonent ) {
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
				"open new Frame",
				"INFO",
				JOptionPane.PLAIN_MESSAGE );
		
		JFrame newJFrame = new JFrame("new Frame");
		
		newJFrame.setVisible( true );
		newJFrame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );		
		newJFrame.setPreferredSize( new Dimension( 500, 500) );
		newJFrame.pack();
		
		//newJFrame.setMenuBar( );
		
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
