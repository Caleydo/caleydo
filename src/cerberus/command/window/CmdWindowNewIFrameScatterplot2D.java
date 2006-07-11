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
import cerberus.command.base.CommandAbstractBase;
import cerberus.util.exception.PrometheusCommandException;

/**
 * Creates a internal frame dispaying a 2D heatmap.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameScatterplot2D 
extends CommandAbstractBase
implements CommandInterface {

	private JComponent parentComponent = null;
	
	private String sTextMessage = "";
	
	private String sTextHeader = "Info";
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowNewIFrameScatterplot2D() {
	}
	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameScatterplot2D( final JComponent setParentComonent ) {
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
	
	public void setText( final String sTextHeader, final String sTextMessage) {
		this.sTextMessage = sTextMessage;
		this.sTextHeader = sTextHeader;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws PrometheusCommandException {
		JOptionPane.showMessageDialog( parentComponent,
				sTextMessage,
				sTextHeader,
				JOptionPane.PLAIN_MESSAGE );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws PrometheusCommandException {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws PrometheusCommandException {
		return CommandType.WINDOW_POPUP_CREDITS;
	}

}
