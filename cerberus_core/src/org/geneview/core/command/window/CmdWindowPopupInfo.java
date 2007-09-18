/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.geneview.core.command.base.ACommand;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Creates a popup window dispaying info.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowPopupInfo 
extends ACommand {

	private JComponent parentComponent = null;
	
	private String sTextMessage = "";
	
	private String sTextHeader = "Info";
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowPopupInfo( final IGeneralManager refGeneralManager, final String details ) {
		super( -1, refGeneralManager,null,null);
	}
	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowPopupInfo( final JComponent setParentComonent ) {
		super( -1,null,null,null);
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
	public void doCommand() throws GeneViewRuntimeException {
		JOptionPane.showMessageDialog( parentComponent,
				sTextMessage,
				sTextHeader,
				JOptionPane.PLAIN_MESSAGE );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
