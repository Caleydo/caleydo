/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import javax.swing.JProgressBar;

import cerberus.command.base.ACommand;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Creates a popup window dispaying info.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowPopupProgressBarSwing 
extends ACommand {

	private JProgressBar progressBar;
	
	//private JComponent parentComponent = null;
	
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowPopupProgressBarSwing( final String details ) {
		super( -1,null,null,null);
		progressBar = new JProgressBar(0,100);
		progressBar.setToolTipText( details );
		
		throw new RuntimeException("Not tested yet!");
	}
	
//	/**
//	 * ISet the reference to the parent JComponent.
//	 * 
//	 * @param setParentComonent parent JComponenet
//	 */
//	public CmdWindowPopupProgressBarSwing( final JComponent setParentComonent ) {
//		parentComponent = setParentComonent;
//		
//		progressBar = new JProgressBar();
//	}


//	/**
//	 * ISet the reference to the parent JComponent.
//	 * 
//	 * @param setParentComonent parent JComponenet
//	 */
//	public void setParent( final JComponent setParentComonent) {
//		parentComponent = setParentComonent;
//	}
	
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
	
	/**
	 * Value must be in the range of 0..100
	 * 
	 * @param iPercent range [0..100] inclusive
	 */
	public void setPercent( final int iPercent ) {
		if ( iPercent < 0 ) {
			progressBar.setValue( 0 );
			return;
		}
		else if ( iPercent > 100 ) 
		{
			progressBar.setValue( 100 );
			return;
		}
		
		progressBar.setValue( iPercent );		
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		
		progressBar.setStringPainted( true );
		//progressBar.setVisible( true );
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		progressBar.setVisible( false );
		
		progressBar = null;
	}

}
