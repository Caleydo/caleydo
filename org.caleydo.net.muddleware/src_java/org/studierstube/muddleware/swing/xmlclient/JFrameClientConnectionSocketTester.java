/* ========================================================================
 * Copyright (C) 2004-2005  Graz University of Technology
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
/**
 * 
 */
package org.studierstube.muddleware.swing.xmlclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
//import javax.swing.JViewport;
import java.util.Iterator;
import java.util.Vector;
//import java.io.IOException;
//import java.net.UnknownHostException;


import org.studierstube.muddleware.swing.xmlclient.AJFrameConnectionSocketTester;
import org.studierstube.muddleware.swing.xmlclient.JPanelMuddleWareMessage;
import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.OperationEnum;

import org.studierstube.util.swing.JTextAreaLogger;


/**
 * Tester emulating Muddleware clinet. Revice messages from Muddleware server.
 * 
 * Note: JAVA only
 * 
 * @author Michael Kalkusch
 *
 */
public class JFrameClientConnectionSocketTester 
extends AJFrameConnectionSocketTester 
implements ErrorMessageHandler {
	
	/**
	 * Used for serialization and deserialization.
	 */
	private static final long serialVersionUID = -5607626346147453206L;

	private ClientByteStreamHandler connection;
	
//	private OutputStream outStream = null;
	
//	private InputStream inStream = null;
	
	private int iOperationType = 0;
	
	private String sServerName = "localhost";
	
	private int iServerPort = 20000;
	
	private boolean bRecordSendAction = false;
	
	private boolean bAutoIncrementId = true;
	
	private boolean bListReceivedMessages = true;
	
	private boolean bListSentMessages = true;
	
	private int iAutoIncrementId = 1;
	
//	private int iMessageInfoStyle = Message.MESSAGE_STYLE_BRIEF;
	
	/* ------------------------
	 *    SERVER CONNECT
	 * ------------------------
	 */
	private JPanel jp_serverConnect;
	
	private JButton jb_serverConnect_Connect2Server;
	
	private JButton jb_logArea_clear;
	
	private JButton jb_logArea_record;
	
	private JButton jb_logArea_play;
	
	private JButton jb_logArea_stoprecord;

	private JButton jb_receive_clear;
	private JButton jb_sent_clear;
	
	private JButton jb_rec_clear;
	private JButton jb_rec_play;
//	private JButton jb_rec_step;
//	private JButton jb_rec_rewind;
	
	private JTextField jtf_serverConnect_serverName;
	
	private JTextField jtf_serverConnect_serverPort;
	

	private Vector <JPanelMuddleWareMessage> vec_rec_Messages;
	
	private Vector <JPanelMuddleWareMessage> vec_received_Messages;
	
	private Vector <JPanelMuddleWareMessage> vec_sent_Messages;

	
//	private Vector <Message> vecReceiveBuffer;
	
	/* ------------------------
	 *    MESSAGE
	 * ------------------------
	 */
	private JPanel jp_sendCommand;
	
	private JButton jb_sendCommand_Send;
	
	private JTextField jtf_sendCommand_operation;
	
	private JTextField jtf_sendCommand_id;
	
	private JTextField jtf_sendCommand_xpath;
	
	private JTextField jtf_sendCommand_data;
	
	private JComboBox jcb_sendCommand_operation;
	
	private JTabbedPane jtp_registers;
	
	private JScrollPane jsp_rec_operationList;
	
	private JScrollPane jsp_receive_operationList;
	
	private JScrollPane jsp_sent_operationList;
	
	private JPanel jp_rec_canvas;
	
	private JPanel jp_sent_canvas;		
	
	private JPanel jp_receive_canvas;
	
//	private JCheckBox jcb_rec_mute;
	
	
	
	private JCheckBox jcb_rec_autoIncrementId;
	
	private JCheckBox jcb_rec_showId;
	
	private JPanel[] jp_mainTabs;
	
	/* ------------------------
	 *    LOGGER
	 * ------------------------
	 */
	private JTextArea jta_logArea;
	
	
	/**
	 * 
	 */
	public JFrameClientConnectionSocketTester() {
		super("MuddleWare XML JAVA Client v0.1");
				
		initJFrame();
		
		/**
		 * Note: call setLogInterface() after calling initJFrame()
		 * because jta_logArea is needed for SwingLogger()!
		 */
		setLogInterface(new JTextAreaLogger(jta_logArea));
		
		connection = new ClientByteStreamHandler( this.getLogInterface() );
	}
	
//	private String[] getOperationName() {
//		String[] names = {"add","remove"};
//		
//		return names;
//	}
 
	private void initJFrame() {
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setSize( 700, 500 );
		this.setLocation( 100, 100 );
		
//		vecReceiveBuffer = new Vector <Message> (40);
		vec_rec_Messages = new Vector <JPanelMuddleWareMessage> (10);		
		vec_received_Messages = new Vector <JPanelMuddleWareMessage> (100);		
		vec_sent_Messages =  new Vector <JPanelMuddleWareMessage> (100);
		
		jtp_registers = new JTabbedPane();
		
		jp_mainTabs = new JPanel [4];
		
		jp_mainTabs[0] = new JPanel();
		jp_mainTabs[1] = new JPanel();
		jp_mainTabs[2] = new JPanel();
		jp_mainTabs[3] = new JPanel();
		
		jtp_registers.addTab( "status", null, jp_mainTabs[0], "status" );
		jtp_registers.addTab( "record", null, jp_mainTabs[1], "record messages" );
		jtp_registers.addTab( "received", null, jp_mainTabs[2], "received messages" );
		jtp_registers.addTab( "sent", null, jp_mainTabs[3], "sent messages" );
		
		
		jp_serverConnect = new JPanel();
		jp_sendCommand = new JPanel();
		jta_logArea = new JTextArea( 15, 40 );
		
		/* buttons.. */
		jp_serverConnect.setLayout( new FlowLayout() );
		
		jtf_serverConnect_serverName = new JTextField( sServerName );
		jtf_serverConnect_serverPort = 
			new JTextField( Integer.toString( iServerPort) );
		
		jb_serverConnect_Connect2Server = new JButton("connect");
		
		jp_serverConnect.add( new JLabel("server") );
		jp_serverConnect.add( jtf_serverConnect_serverName );
		jp_serverConnect.add( new JLabel("port:") );
		jp_serverConnect.add( jtf_serverConnect_serverPort );
		jp_serverConnect.add( new JLabel("  ==>  ") );
		jp_serverConnect.add( jb_serverConnect_Connect2Server );
		
		/* XML command section */
		jp_sendCommand.setLayout( new BorderLayout() );		
		
		jtf_sendCommand_operation = new JTextField( 2 );		
		jtf_sendCommand_id = new JTextField( 5 );		
		jtf_sendCommand_xpath = new JTextField( 30 );	
		jtf_sendCommand_xpath.setText("/StbAM/Clients/Client");
		jtf_sendCommand_data = new JTextField( 30 );
		jcb_sendCommand_operation = new JComboBox( 
				OperationEnum.getAllTitlesForComboBox() );
		
		jb_sendCommand_Send = new JButton("SEND");
		jb_logArea_clear = new JButton("clear");
		
		jb_logArea_record = new JButton("rec");
		jb_logArea_stoprecord = new JButton("stop");
		jb_logArea_play = new JButton("play");
		
		JPanel jp_data_per_message = new JPanel();
		JPanel jp_data_per_message_2 = new JPanel();
		JPanel jp_data_per_message_3 = new JPanel();
		
		jp_data_per_message.add( new JLabel("op:") );
		jp_data_per_message.add( jtf_sendCommand_operation );
		jp_data_per_message.add( jcb_sendCommand_operation );
		
		jp_data_per_message.add( new JLabel("id:") );
		jp_data_per_message.add( jtf_sendCommand_id );
		
		jp_data_per_message.add( jb_sendCommand_Send );
		
		jp_data_per_message_2.add( new JLabel("xPath:") );
		jp_data_per_message_2.add( jtf_sendCommand_xpath );
		
		jp_data_per_message_3.add( new JLabel("data:") );
		jp_data_per_message_3.add( jtf_sendCommand_data );
		
		jp_sendCommand.add( jp_data_per_message, BorderLayout.NORTH );	
		jp_sendCommand.add( jp_data_per_message_2, BorderLayout.CENTER );
		jp_sendCommand.add( jp_data_per_message_3, BorderLayout.SOUTH );
		
		jtf_sendCommand_operation.setEditable( false );
		
		/*
		 * LOG AREA...
		 */
		
		JPanel jp_log_area = new JPanel();
						
		jta_logArea.setSize( this.getWidth()-10, (int) (this.getHeight() / 2 )  );
		
		JScrollPane jsp_log_area = new JScrollPane(jta_logArea, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		jsp_log_area.setSize( this.getWidth()-10, (int) (this.getHeight() / 2 ) ); 
		
		jp_log_area.setLayout( new BorderLayout() );		
		jp_log_area.setSize( this.getWidth()-10, (int) (this.getHeight() / 2 ) ); 
		jp_log_area.add( jsp_log_area, BorderLayout.CENTER );
		
		JPanel jp_log_area_cmd = new JPanel();
		jp_log_area_cmd.setLayout( new BoxLayout( jp_log_area_cmd, BoxLayout.Y_AXIS ) ); 
				
		jb_logArea_clear.setAlignmentX(Component.CENTER_ALIGNMENT);
		jb_logArea_stoprecord.setAlignmentX(Component.CENTER_ALIGNMENT);
		jb_logArea_record.setAlignmentX(Component.CENTER_ALIGNMENT);
		jb_logArea_play.setAlignmentX(Component.CENTER_ALIGNMENT);
					
		jb_logArea_clear.setToolTipText("clear log message screen");
		jb_logArea_record.setToolTipText("record send commands");
		jb_logArea_stoprecord.setToolTipText("stop recording of send commands");
		jb_logArea_play.setToolTipText("play list of recorded commands");
		
		jp_log_area_cmd.add( this.jb_logArea_record );
		jp_log_area_cmd.add( new JLabel( " "));
		jp_log_area_cmd.add( this.jb_logArea_stoprecord );
		jp_log_area_cmd.add( new JLabel( " "));
		jp_log_area_cmd.add( this.jb_logArea_play );
		jp_log_area_cmd.add( new JLabel( " "));
		jp_log_area_cmd.add( new JLabel( " "));
		jp_log_area_cmd.add( new JLabel( " "));
		jp_log_area_cmd.add( jb_logArea_clear );
		
		
		jp_log_area.add( jp_log_area_cmd , BorderLayout.EAST );
		
		
		jp_mainTabs[0].setLayout( new BorderLayout() );				
		jp_mainTabs[0].add( jp_sendCommand,BorderLayout.NORTH  );
		jp_mainTabs[0].add( jp_log_area,BorderLayout.CENTER );
		jp_mainTabs[0].add( jp_serverConnect,BorderLayout.SOUTH );
		
//		jp_mainTabs[0].add( jp_serverConnect,BorderLayout.NORTH );
//		jp_mainTabs[0].add( jp_sendCommand,BorderLayout.CENTER );
//		jp_mainTabs[0].add( jp_log_area,BorderLayout.SOUTH );
		
		this.add( jtp_registers );
		
		
		/**
		 * register callbacks..
		 */
		
//		final JTextField jtf_serverConnect_serverPort_final = 
//			jtf_serverConnect_serverPort;		
		
		jb_serverConnect_Connect2Server.addActionListener( 
				new ActionListener() {
					public void actionPerformed( ActionEvent ae ) {
						if ( ! connection.isConnected() ) {
							updateGUI_serverPort();							
										
							connection.setServerNameAndPort( 
									jtf_serverConnect_serverName.getText(),
									jtf_serverConnect_serverPort.getText() );
							
							setGUIbuttonText_IsConnected( 
									connection.connect() );
						} 
						else 
						{
							connection.disconnect();
							setGUIbuttonText_IsConnected( false );
						}
					}
				});
		
		jb_sendCommand_Send.addActionListener( 
				new ActionListener() {
					public void actionPerformed( ActionEvent ae ) {
						if ( connection.isConnected() ) {
							sendReceiveMuddlewareMessage( null );
						} 
					}
				});		
		
		jb_logArea_clear.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ae ) {
						updataGUI_clearLogArea();
					}
				});	
		
		jcb_sendCommand_operation.addItemListener( 
				new ItemListener() {
					public void itemStateChanged( ItemEvent ie ) {
						if ( ie.getStateChange() == ItemEvent.SELECTED ) {
							setOpertaionType( jcb_sendCommand_operation.getSelectedIndex() );
						}
					}
				}
		);
		
		jb_logArea_record.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ae ) {
						updateGUI_setRecordStatus( true );
					}
				});	
		jb_logArea_stoprecord.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				updateGUI_setRecordStatus( false );
			}
		});
		jb_logArea_play.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				updateGUI_play();
			}
		});	
		
		initRecordTab();
		
		initReceiveTab();
		
		initSentTab();
		

		updateGUI_setRecordStatus( false );
		
		/* not connected, lock send button */
		updateGUI_lockUnlockSendArea( false );
		
		this.setVisible( true );						
	}
	
	private void addMessage( IMessage add ) {
		
		if ( bRecordSendAction ) {
			//int iSize = vec_rec_Messages.size();
			
			JPanelMuddleWareMessage insertPanel = 
				new JPanelMuddleWareMessage( add );
			
			insertPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			jp_rec_canvas.add( insertPanel );
			
			vec_rec_Messages.addElement( insertPanel );
		}
	}
	
	private void bufferSentReceivedMessage( IMessage sentMsg, IMessage recivedMsg ) {
		
		if ( bListSentMessages ) {
			JPanelMuddleWareMessage insertPanel = 
				new JPanelMuddleWareMessage( sentMsg );
			
			insertPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			jp_sent_canvas.add( insertPanel );
			
			vec_sent_Messages.addElement( insertPanel );
		}
		if ( bListReceivedMessages ) {
			
			JPanelMuddleWareMessage insertPanel = 
				new JPanelMuddleWareMessage( recivedMsg );
			
			insertPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			jp_receive_canvas.add( insertPanel );
			
			vec_received_Messages.addElement( insertPanel );
		}
	}
	
//	private void sentMessage( Message msg ) {
//		
//		if ( bListSentMessages ) {
//			int iSize = vec_rec_Messages.size();
//			
//			JPanelMuddleWareMessage insertPanel = 
//				new JPanelMuddleWareMessage( msg );
//			
//			insertPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
//			
//			jp_receive_canvas.add( insertPanel );
//			
//			vec_received_Messages.addElement( insertPanel );
//		}
//	}
//
//	private void removeMessage( Message remove ) {
//		
//	}
	
	private void updateGUI_showHideId( boolean status ) {

		Iterator <JPanelMuddleWareMessage> iter = vec_rec_Messages.iterator();		
		
		while (iter.hasNext() ) {
			
			JPanelMuddleWareMessage panel = iter.next();
			
			panel.updateGUIshowHideId( status );
			panel.updateGUIshowHideOperation( status );
		}
		
		jp_rec_canvas.updateUI();
		jp_rec_canvas.repaint();		
	}
	
	private void initRecordTab() {
		
		JPanel jp_rec_header = new JPanel( new FlowLayout() );		
		JPanel jp_rec_footer = new JPanel( new FlowLayout() );
		
		jp_rec_canvas = new  JPanel(  );
		jp_rec_canvas.setLayout( new BoxLayout(jp_rec_canvas, BoxLayout.PAGE_AXIS ) );

//		jcb_rec_mute = new JCheckBox("show mute", true);
		jcb_rec_showId = new JCheckBox("show Id", true);
		jcb_rec_autoIncrementId = new JCheckBox("autoinc Id", true);
		
						
		jsp_rec_operationList = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		jsp_rec_operationList.setSize( this.getWidth()- 50, this.getHeight() - 100);	
		jsp_rec_operationList.setViewportView( jp_rec_canvas );
		
		
		jb_rec_play = new JButton("play");
//		jb_rec_step = new JButton("step");
//		jb_rec_rewind = new JButton("rewind");
		
		jb_rec_clear = new JButton("clear");		
		jb_rec_clear.setToolTipText("clear and wipe play list of messages");

		
		jp_rec_header.add( jb_rec_clear );
		jp_rec_header.add( jb_rec_play );
//		jp_rec_header.add( jb_rec_step );
//		jp_rec_header.add( jb_rec_rewind );
		
		jp_rec_footer.add( new JLabel("") );
		
//		jp_rec_footer.add( jcb_rec_mute );
		jp_rec_footer.add( jcb_rec_showId ); 
		jp_rec_footer.add( jcb_rec_autoIncrementId );
        
		
		
		addMessage( null );

		

		
		jp_mainTabs[1].setLayout( new BorderLayout() );		
		jp_mainTabs[1].add( jp_rec_header, BorderLayout.NORTH );
		jp_mainTabs[1].add( jsp_rec_operationList, BorderLayout.CENTER );
		jp_mainTabs[1].add( jp_rec_footer, BorderLayout.SOUTH );
		
		jcb_rec_showId.addItemListener( 
				new ItemListener() {
					public void itemStateChanged( ItemEvent ie ) {
						updateGUI_showHideId( jcb_rec_showId.isSelected() ); 						
					}
				}
		);
		
		jb_rec_clear.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ae ) {
						updateGUI_clearRecordList();
					}
				});
		
		jb_rec_play.addActionListener( new ActionListener() {
					public void actionPerformed( ActionEvent ae ) {
						updateGUI_play();
					}
				});
		
		jcb_rec_autoIncrementId.addItemListener( 
				new ItemListener() {
					public void itemStateChanged( ItemEvent ie ) {
						updateGUI_toggleAutoIncremetnId(); 						
					}
				});
	
		
	}
	
	
	private void initReceiveTab() {
		
		
		jp_receive_canvas = new  JPanel(  );
		jp_receive_canvas.setLayout( new BoxLayout(jp_receive_canvas, BoxLayout.PAGE_AXIS ) );
				
		jsp_receive_operationList = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		jsp_receive_operationList.setSize( this.getWidth()- 50, this.getHeight() - 100);	
		jsp_receive_operationList.setViewportView( jp_receive_canvas );
		
		jb_receive_clear = new JButton("clear");
		jb_receive_clear.setToolTipText("Clear received message list");
		
		
		JPanel received_Header = new JPanel( new FlowLayout() );
		
		received_Header.add( new JLabel("list of received messages") );
		received_Header.add( jb_receive_clear );
		
		jp_mainTabs[2].setLayout( new BorderLayout() );		
		jp_mainTabs[2].add( received_Header , BorderLayout.NORTH );
		jp_mainTabs[2].add( jsp_receive_operationList,  BorderLayout.CENTER );
		
		jb_receive_clear.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				updateGUI_clear_receiveList();
			}
		});
		
	}
	
	private void initSentTab() {
				
		jp_sent_canvas = new  JPanel( );
		jp_sent_canvas.setLayout( new BoxLayout(jp_sent_canvas, BoxLayout.PAGE_AXIS ) );
				
		jsp_sent_operationList = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		jsp_sent_operationList.setSize( this.getWidth()- 50, this.getHeight() - 100);	
		jsp_sent_operationList.setViewportView( jp_sent_canvas );
		
		jb_sent_clear = new JButton("clear");
		jb_sent_clear.setToolTipText("Clear sent message list");
		
		
		JPanel sent_Header = new JPanel( new FlowLayout() );
		
		sent_Header.add( new JLabel("list of sent messages") );
		sent_Header.add( jb_sent_clear );
		
		jp_mainTabs[3].setLayout( new BorderLayout() );		
		jp_mainTabs[3].add( sent_Header , BorderLayout.NORTH );
		jp_mainTabs[3].add( jsp_sent_operationList,  BorderLayout.CENTER );
		
		jb_sent_clear.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				updateGUI_clear_sentList();
			}
		});
		
	}
	
	private void setOpertaionType( int type ) {
		this.iOperationType = type;
		
		this.jtf_sendCommand_operation.setText( Integer.toString(type) );
	}
	
	private void updateGUI_lockUnlockSendArea( boolean state) {
		jb_sendCommand_Send.setEnabled( state );
		jcb_sendCommand_operation.setEnabled( state );
		
		if ( vec_rec_Messages.isEmpty() ) {
			jb_rec_clear.setEnabled( false );
			jb_rec_play.setEnabled( false );
//			jb_rec_step.setEnabled( state );
//			jb_rec_rewind.setEnabled( state );
		} else {
			jb_rec_clear.setEnabled( true );
			jb_rec_play.setEnabled( true );
		}
		
		jb_receive_clear.setEnabled( state );
		
		jb_sent_clear.setEnabled( state );
		
		jb_logArea_play.setEnabled( state );		
		
		if ( state ) {
			jb_logArea_record.setEnabled( !bRecordSendAction );
			jb_logArea_stoprecord.setEnabled( bRecordSendAction );
		} else {
			jb_logArea_record.setEnabled( false );
			jb_logArea_stoprecord.setEnabled( false );
		}
		
		if ( state ) {
			jb_logArea_play.setEnabled( ! this.vec_rec_Messages.isEmpty() );
		} else {
			jb_logArea_play.setEnabled( false );
		}
	}
	
	private void updateGUI_toggleAutoIncremetnId() {
		if ( bAutoIncrementId ) {
			bAutoIncrementId = false;
			return;
		}
		bAutoIncrementId = true;
	}
	
	private void updataGUI_clearLogArea() {
		this.jta_logArea.setText("");
	}
	
	private void updateGUI_clearRecordList() {
		
		vec_rec_Messages.clear();		
		jp_rec_canvas.removeAll();
		jp_rec_canvas.repaint();
		jb_logArea_play.setEnabled( false );	
		jb_rec_play.setEnabled( false );
		jb_rec_clear.setEnabled( false );
	}
	
	private void updateGUI_play() {
		
		updateGUI_setRecordStatus(false);
		
		Iterator <JPanelMuddleWareMessage> iter = vec_rec_Messages.iterator();		
		
		jta_logArea.append("  --- PLAY ---\n");
		while (iter.hasNext() ) {
			JPanelMuddleWareMessage panel = iter.next();
			
			panel.updateGUItoData();
			
			sendReceiveMuddlewareMessage( panel.getMessage() ); 
			
			panel.updateGUIfromData();
		}
		
		jta_logArea.append("  --- PLAY (DONE) ---\n");
	}
	
	private void updateGUI_serverPort() {
		sServerName = jtf_serverConnect_serverName.getText();
		if ( ! setServerPort( jtf_serverConnect_serverPort.getText() ) ) {								
			jtf_serverConnect_serverPort.setText( 
					Integer.toString( iServerPort ) );
		}
	}
	
	private void updateGUI_clear_receiveList() {
		vec_received_Messages.clear();
		jp_receive_canvas.removeAll();
		jp_receive_canvas.updateUI();
		jp_receive_canvas.repaint();	
		
		logMsg(this,"clear receive");
	}
	
	private void updateGUI_clear_sentList() {
		vec_sent_Messages.clear();
		jp_sent_canvas.removeAll();
		jp_sent_canvas.updateUI();
		jp_sent_canvas.repaint();	
		
		logMsg(this,"clear receive");
	}
	
	private void updateGUI_setRecordStatus( boolean status) {
		if ( status ) {
			jb_logArea_record.setEnabled( false );
			jb_logArea_stoprecord.setEnabled( true );			
		} 
		else 
		{
			jb_logArea_record.setEnabled( true );
			jb_logArea_stoprecord.setEnabled( false );			
		}
		
		boolean bIsRecordListIsSet = ! vec_rec_Messages.isEmpty();
		
		jb_logArea_play.setEnabled( bIsRecordListIsSet );
		jb_rec_play.setEnabled( bIsRecordListIsSet );
		jb_rec_clear.setEnabled( bIsRecordListIsSet );
				
		bRecordSendAction = status;	
	}
	
	public boolean setServerPort( String nSetServerPort ) {
		
		int iSetServerPort = Integer.getInteger( nSetServerPort, this.iServerPort ).intValue();
		
		if (( iSetServerPort > 1024 )&&(iSetServerPort <= 65535)) {
			this.iServerPort = iSetServerPort;
			
			return true;
		} else {
			logMsg(this,"server port [" + nSetServerPort + 
					"] invalid, because range is exceeded [1025..65535]");
			
			return false;
		}
	}
	
	public void sendReceiveMuddlewareMessage( IMessage send ) {
		
		
		IMessage sendMsg;
		
		if ( send == null  ) {
		
			int id = 3;						
		
			/* Read id from GUI */
			try {
				id = Integer.parseInt( jtf_sendCommand_id.getText() );
			} catch ( NumberFormatException nfe) {
				
			}			
			
			IOperation op = new Operation( iOperationType );
			op.setXPath( "/StbAM/Users/User/");
			
			String xPathText = jtf_sendCommand_xpath.getText();
			op.setXPath( xPathText );
			String nodeData = this.jtf_sendCommand_data.getText();
			if ( nodeData.length() > 0 ) {
				op.addNodeString( nodeData );
			}
			op.setClientData( 13 );
						
			sendMsg = new Message();
			sendMsg.setId( id ); 
			sendMsg.addOperation( op );
			
		} else {
			sendMsg = send;
			
			/* Write id of message to GUI */
			jtf_sendCommand_id.setText( Integer.toString( sendMsg.getId() ) );
		}
		
		
		if ( bAutoIncrementId ) {
			jtf_sendCommand_id.setText( Integer.toString( iAutoIncrementId++ ) );
			
			sendMsg.setId( iAutoIncrementId ); 
		} 
		
		if ( this.bRecordSendAction ) {
			
			addMessage(sendMsg);
		}
		
		IMessage receiveMsg = 
			connection.sendReceiveMessage( sendMsg );

		if ( receiveMsg != null ) {
			
			bufferSentReceivedMessage( sendMsg, receiveMsg );
		}
		
	}
	
//	public Message sendMessageGetReceivedMesage( Message sendMsg ) {
//		
//		
//		Message receiveMsg = new Message();
//		
//		if ( bAutoIncrementId ) {
//			sendMsg.setId( iAutoIncrementId++ );
//		}
//		
//		logMsg( "SEND: " + sendMsg.toString(iMessageInfoStyle), false );
//		
//		byte [] sendByteArray = sendMsg.createMessageByteArray();
//		
//		try {
//			outStream.write( sendByteArray );
//			
//			addMessage( sendMsg );
//			
//			receiveMsg.parseByteArrayFromInStream( inStream, this );
//			
//			logMsg( "RECEIVE:" + receiveMsg.toString(iMessageInfoStyle), true );
//			
//			return receiveMsg;
//			
//		} catch (IOException ioe) {
//			logMsg( "ERROR while send/receive message: " + ioe.toString(), true );
//			
//			return null;
//		}
//		
//	}
	
	protected boolean setServerPort( int nSetServerPort ) {
		if (( nSetServerPort > 1024 )&&(nSetServerPort <= 65535)) {
			this.iServerPort = nSetServerPort;
			
			return true;
		} else {
			logMsg(this,"server port [" + nSetServerPort + 
					"] invalid, because range is exceeded [1025..65535]");
			
			return false;
		}
	}
	
	public void setGUIbuttonText_IsConnected( boolean toggle ) {
		if ( toggle == true ) {
			jb_serverConnect_Connect2Server.setText( "disconnect" );
			jtf_serverConnect_serverName.setEditable( false );
			jtf_serverConnect_serverPort.setEditable( false );
			
			updateGUI_lockUnlockSendArea( true );
		} else {
			jb_serverConnect_Connect2Server.setText( "connect" );
			jtf_serverConnect_serverName.setEditable( true );
			jtf_serverConnect_serverPort.setEditable( true );
			
			updateGUI_lockUnlockSendArea( false );
		}
	}
	
	public void setServerByName( String nServerName ) {
		this.sServerName = nServerName;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		JFrameClientConnectionSocketTester tester = new JFrameClientConnectionSocketTester();
		
		int iargs = args.length;
		
		if ( iargs > 0 ) {
			tester.setServerByName(args[0]);
			
			if (iargs > 1) {
				try {
					tester.setServerPort(Integer.parseInt(args[1]));
				} catch ( NumberFormatException nfe) {					
					System.out.println("usage: " + 
							tester.getClass().getSimpleName().toString() +
							" [servername] [server port]");
				}
			}
			
			tester.getConnectionData();
		}		
	}
	
	
	public void getConnectionData() {
		
		if ( ! connection.isConnected() ) {
			
			System.out.println("Shut down!");
			return;
		}
		
		IMessage sendMsg = new Message( OperationEnum.OP_REQUEST_CLIENTID );
		
		sendMsg.setId( 14 );
		IMessage receivedMsg = connection.sendReceiveMessage( sendMsg );
		
		if ( receivedMsg != null ) {
			logMsg(this, "P: RECEIVE: " + receivedMsg.toString());
		} else {
			logMsg(this, "\n  FAILED! ");
		}
		
		connection.disconnect();
	}
}
