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

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a About - cridts box.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemNewFrame 
extends ACommand
implements ICommand {

	
	private JComponent parentComponent = null;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdSystemNewFrame(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(-1,
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}
	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdSystemNewFrame(final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final JComponent setParentComonent ) {
		
		super(-1,
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

}
