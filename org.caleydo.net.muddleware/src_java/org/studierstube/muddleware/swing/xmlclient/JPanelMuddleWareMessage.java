/* ========================================================================
 * Copyright (C) 2006-2007  Graz University of Technology
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For further information please contact Dieter Schmalstieg under
 * <schmalstieg@icg.tu-graz.ac.at> or write to Dieter Schmalstieg,
 * Graz University of Technology, Institut für Maschinelles Sehen und Darstellen,
 * Inffeldgasse 16a, 8010 Graz, Austria.
 * ========================================================================
 * PROJECT: Muddleware
 * ======================================================================== */

package org.studierstube.muddleware.swing.xmlclient;

//import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.OperationEnum;

/**
 * JPanel shows parameters of an operation.
 * 
 * @see org.studierstube.net.protocol.muddleware.Operation
 * 
 * @author Michael Kalkusch
 *
 */
public class JPanelMuddleWareMessage extends JPanel {

	/**
	 * Used for serialization and deserialization.
	 */
	private static final long serialVersionUID = 6454469783202360202L;

	private boolean bJPanel_isActive = true;
	
	protected JCheckBox jcb_enableOperation;
	
	//protected JCheckBox jcb_selected;
	
	protected JTextField jtf_id;
	
	protected JTextField jtf_operationId;
	
	protected JTextField jtf_xPath;
	
	protected JTextField jtf_name;
	
	protected JTextField jtf_operation;
	
	protected JComboBox jcb_operation;
	
	protected IMessage message;
	
	
//	/**
//	 * @param arg0
//	 * @param arg1
//	 */
//	private JPanelMuddleWareMessage(LayoutManager arg0, boolean arg1) {
//		super(arg0, arg1);
//	}
//
//	/**
//	 * @param arg0
//	 */
//	private JPanelMuddleWareMessage(LayoutManager arg0) {
//		super(arg0);
//	}
//
//	/**
//	 * @param arg0
//	 */
//	private JPanelMuddleWareMessage(boolean arg0) {
//		super(arg0);
//	}

	/**
	 * 
	 */
	protected JPanelMuddleWareMessage() {
		super( new FlowLayout() );
		
		initDatastructues();
		
		this.setVisible( true );
	}
	
	public JPanelMuddleWareMessage( IMessage setMessage ) {
		super( new FlowLayout() );
		
		initDatastructues();
		
		this.setVisible( true );
		
		/* set message and update GUI from data...*/
		setMessage( setMessage );		
		updateGUIfromData();
	}
	
	private void initDatastructues() {
		jcb_enableOperation = new JCheckBox("",true);
		jcb_enableOperation.setToolTipText("select to enable operation");
		
//		jcb_selected = new JCheckBox("s", false );
//		jcb_selected.setToolTipText("select opertion for editing");
		
		jtf_id = new JTextField(4);
		jtf_id.setToolTipText("id");
		jtf_id.setText("0");
		
		jtf_xPath = new JTextField(15);
		jtf_xPath.setToolTipText("XPath");
		
		jtf_name = new JTextField(15);
		jtf_name.setToolTipText("name");
		
		jtf_operation = new JTextField(2);
		jtf_operation.setToolTipText("operation id");
		jtf_operation.setEditable( false );
		jtf_operation.setText("0");
		
		jcb_operation = new JComboBox( OperationEnum.getAllTitlesForComboBox() );
		
		
		
		jtf_operationId = new JTextField(2);
		jtf_operationId.setToolTipText("operation order");
		jtf_operationId.setText("0");
		
//		this.add( jcb_selected );
		this.add( jcb_enableOperation );	
		this.add( jtf_operation );
		this.add( jcb_operation );
		this.add( jtf_operationId );
		this.add( jtf_xPath );
		this.add( jtf_name );
		this.add( jtf_id );
		
		
		jcb_operation.addItemListener( 
				new ItemListener() {
					public void itemStateChanged( ItemEvent ie ) {
						if ( ie.getStateChange() == ItemEvent.SELECTED ) {
							setOperationType( jcb_operation.getSelectedIndex() );
						}
					}
				}
		);
		
	}
	
	private void setOperationType( int iType ) {
		jcb_operation.setSelectedIndex( iType );
		jtf_operation.setText( Integer.toString( iType) );
	}
	
	public void setMessage( IMessage nMessage ) {
		this.message = nMessage;
		if ( nMessage == null ) {
			
			return;
		}
		
		
	}
	
	public IMessage getMessage() {
		return this.message;
	}
	
	private void setGUIstatus( boolean bEnable ) {
		jtf_operation.setEditable( bEnable );
		jcb_operation.setEditable( bEnable ); 
		jtf_xPath.setEditable( bEnable );
		jtf_name.setEditable( bEnable );
		jtf_id.setEditable( bEnable );
		jtf_operationId.setEditable( bEnable );		
		jcb_enableOperation.setSelected( false );
	}
	
	public void updateGUIfromData() {
		if ( message == null ) {
			
			if ( bJPanel_isActive ) {
				/* disable GUI once */
				bJPanel_isActive = false;
				setGUIstatus( false );
			}
			
			
			return;
		}
		
		if ( ! bJPanel_isActive ) {
			/* enable GUI, if it was disabeled */
			bJPanel_isActive = true;
			setGUIstatus( true );
		}
		
		IOperation op;
		int iOperationId = setOrDefaultValue( jtf_operationId.getText(), 0 );
		if (( iOperationId >=0 )&&( iOperationId < message.getNumOperations() )) {
			op = message.getOperation(iOperationId);
		} else {
			jtf_operationId.setText( Integer.toString( message.getNumOperations()) );
			return;
		}

		jtf_id.setText( Integer.toString( message.getId() ) );
		jtf_xPath.setText( op.getXPath() );
		jtf_name.setText( op.getNodeString() );
		jtf_operation.setText( op.getOperation().name() );
		jcb_operation.setSelectedIndex( op.getOperation().getIndex() );
	}
	
	private int setOrDefaultValue( String nSet, int nDefaultValue ) {
		try {
			return Integer.parseInt( nSet );
		} catch ( NumberFormatException nfe) {
			return nDefaultValue;
		}
	}
	
	public void updateGUItoData() {
		if (( ! bJPanel_isActive )&&( message != null )) {
			message.setId( 
					setOrDefaultValue( jtf_id.getText(), 
							message.getId() ) );	
//			message.getOperation()
//					setOrDefaultValue( jtf_id.getText(), 
//							message.getId() ) );	
		}
	}
	
	public void updateGUIshowHideId( boolean status) {
		jtf_id.setVisible( status );
	}
	
	public void updateGUIshowHideOperation( boolean status) {
		jtf_operation.setVisible( status );
	}

}
