/**
 * 
 */
package cerberus.view.manager.swing;

import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * @author Michael Kalkusch
 *
 */
public class CerberusJStatusBar extends JPanel {

	protected JPanel jp_centerPanel;
	protected JPanel jp_rightPanel;
	protected JPanel jp_leftPanel;
	
	protected JLabel jl_statusTextA;
	protected JLabel jl_statusTextB;
	
	protected JButton jb_hideStatusBar;
	

	/**
	 * 
	 */
	public CerberusJStatusBar() {
		super();
		init();
	}
	
	private void init() {
		jp_leftPanel = new JPanel();
		add(jp_leftPanel, BorderLayout.WEST);
		
		jp_centerPanel = new JPanel();
		jp_centerPanel.setLayout( new BorderLayout());
		
		add(jp_centerPanel, BorderLayout.CENTER);
		
		jp_rightPanel = new JPanel();
		jp_rightPanel.setLayout( new FlowLayout() );
		add(jp_rightPanel, BorderLayout.EAST);
		
		jl_statusTextA = new JLabel("...");
		jl_statusTextB = new JLabel("-ok-");
		JLabel jl_resizeIconLabel = new JLabel( "RIGHT" );
		
		jb_hideStatusBar = new JButton("hide");
		/*
		 * Add content to panel...
		 */
		jp_leftPanel.add( new JLabel("LEFT"));
		
		jp_centerPanel.add(jl_statusTextA,BorderLayout.WEST);
		jp_centerPanel.add(jl_statusTextB,BorderLayout.EAST);		
				
		jp_rightPanel.add(jl_resizeIconLabel);
		jp_rightPanel.add(jb_hideStatusBar);
		
		/*
		 * event...
		 */
		jb_hideStatusBar.addActionListener(
				new ActionListener() { 
		    		 public void actionPerformed(ActionEvent e) {
		    			 showStatusBar(false); 
		    		 }
				}
		);
		
	}
				
	public void showStatusBar( final boolean bShowStatusBar) {
		this.setVisible( bShowStatusBar );
	}
	
	public void setStatusText( String sStatusText ){
		this.jl_statusTextA.setText( sStatusText );
	}
	
	public void setErrorText( String sErrorText ){
		this.jl_statusTextB.setText( sErrorText );
	}

}
