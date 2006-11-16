/*
 * Portions Copyright (C) 2003 Sun Microsystems, Inc.
 * All rights reserved.
 */

/*
 *
 * COPYRIGHT NVIDIA CORPORATION 2003. ALL RIGHTS RESERVED.
 * BY ACCESSING OR USING THIS SOFTWARE, YOU AGREE TO:
 *
 *  1) ACKNOWLEDGE NVIDIA'S EXCLUSIVE OWNERSHIP OF ALL RIGHTS
 *     IN AND TO THE SOFTWARE;
 *
 *  2) NOT MAKE OR DISTRIBUTE COPIES OF THE SOFTWARE WITHOUT
 *     INCLUDING THIS NOTICE AND AGREEMENT;
 *
 *  3) ACKNOWLEDGE THAT TO THE MAXIMUM EXTENT PERMITTED BY
 *     APPLICABLE LAW, THIS SOFTWARE IS PROVIDED *AS IS* AND
 *     THAT NVIDIA AND ITS SUPPLIERS DISCLAIM ALL WARRANTIES,
 *     EITHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED
 *     TO, IMPLIED WARRANTIES OF MERCHANTABILITY  AND FITNESS
 *     FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL NVIDIA OR ITS SUPPLIERS BE LIABLE FOR ANY
 * SPECIAL, INCIDENTAL, INDIRECT, OR CONSEQUENTIAL DAMAGES
 * WHATSOEVER (INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS
 * OF BUSINESS PROFITS, BUSINESS INTERRUPTION, LOSS OF BUSINESS
 * INFORMATION, OR ANY OTHER PECUNIARY LOSS), INCLUDING ATTORNEYS'
 * FEES, RELATING TO THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF NVIDIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 */

package cerberus.view.swing.status;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//import java.awt.image.*;
//import java.io.*;
//import java.nio.*;
//import java.util.*;
//import javax.imageio.*;
//import javax.imageio.stream.*;
//import javax.swing.*;
//import javax.swing.SwingUtilities;
//
//import javax.media.opengl.*;
//import javax.media.opengl.glu.*;
//import com.sun.opengl.util.*;
//import com.sun.opengl.util.texture.*;
//import demos.common.*;
//import demos.util.*;
//import gleem.*;
//import gleem.linalg.*;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetFlatSimple;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;
import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;


/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java and ARB_fragment_program by Kenneth Russell
 */

public class SetBrowser {

	public static void main(String[] args) {

		JFrame baseFrame = new JFrame();

		SetBrowser demo = new SetBrowser(baseFrame);

	}

	private JPanel jpanel_setting;
	
	private JLabel refOffset;

	private JTextField jtf_NumberSelections;
	private JTextField jtf_ReferenceSelect;
	private JTextField jtf_ReferenceStore;
	private JTextField jtf_Label;
	private JTextField jtf_Id;
	private JTextField jtf_CacheId;
	
	private JButton jb_update;
	
	private JLabel refLength;

	private JLabel refLabel;

	private JFrame refParentExternalFrame = null;

	private JInternalFrame refParentInternalFrame = null;

	private VirtualArrayThreadSingleBlock refSelection;

	private SetFlatThreadSimple refSet = null;
	
	//public FileLoader loader;

	protected class UpdateActionListener implements ActionListener {
		
		private boolean bWasUpdated = false;
		
		private final JButton jb_target;
		
		public UpdateActionListener( final JButton target ) {
			this.jb_target = target;
		}
		
		public void actionPerformed(ActionEvent e) {
			if ( e.getSource() == jb_target) {
				
				if ( bWasUpdated ) {
					if ( refSet.getWriteToken() ) {
						
						refSet.setLabel( jtf_Label.getText() );
						refSet.setId( Integer.valueOf( jtf_Id.getText() ) );			
						
						refSet.setCacheId( refSet.getCacheId() + 1 );
						
						refSet.returnWriteToken();
						
						bWasUpdated = false;
					}
					else {
						System.err.println("Can not update, because write-token is not available.");
					}
				}
				else {
					if ( refSet.getReadTokenWait() ) {
						
						int iCountDimensions = refSet.getDimensions();
						
						jtf_NumberSelections.setText( Integer.toString(iCountDimensions ) );
						
						String selectText = "";
						String storeText ="";
						IVirtualArray [] selBuffer;
						IStorage [] storeBuffer;
						
						for ( int i=0; i< iCountDimensions; i++ ){
							if( i != 0) {
								selectText += " ";
							}
							selectText += Integer.toString( refSet.getDimensionSize(i) );
							
							selectText += " select:[";
							selBuffer = refSet.getSelectionByDim(i);							
							for ( int j=0; j< selBuffer.length;j++ ) {
								if ( j != 0 ) {
									selectText += ",";
								}
								selectText += Integer.toString( selBuffer[j].getId() );
								selectText += ":" + selBuffer[j].getLabel();
							}
							selectText += "]";
							
							storeText += "[";
							
							storeBuffer = refSet.getStorageByDim(i);
							for ( int j=0; j< storeBuffer.length;j++ ) {
								if ( j != 0 ) {
									storeText += ",";
								}
								storeText += Integer.toString( storeBuffer[j].getId() );
								storeText += ":" + storeBuffer[j].getLabel();
							}
							storeText += "]";
						}
						jtf_ReferenceSelect.setText( selectText );
						jtf_ReferenceStore.setText( storeText );
						
						jtf_Label.setText( refSet.getLabel() );
						jtf_Id.setText(  Integer.toString( refSet.getId() ) );	
						jtf_CacheId.setText( Integer.toString( refSet.getCacheId() ));
						
						refSet.returnReadToken();
					}
					else {
						System.err.println("Can not update, becasue read-token is not available.");
					}
				}
			}
			else {
				bWasUpdated = true;

				System.err.println("PING");				
			}
		}
		
		public void reset() {
			bWasUpdated = false;
		}
		
		public boolean isUpdated() {
			return this.bWasUpdated;
		}
		
	}
	
	public SetBrowser(JFrame setRefParentExternalFrame) {
		refParentExternalFrame = setRefParentExternalFrame;

		setRefParentExternalFrame.setTitle("Collection Browser -ISet-");

		setRefParentExternalFrame.setLayout(new BorderLayout());

		setRefParentExternalFrame.add(new JLabel("Collection Browser"),
				BorderLayout.CENTER);
		setRefParentExternalFrame.pack();

		//setRefParentExternalFrame.show();	

		JMenu menu = new JMenu("load..");

		JMenuItem item = new JMenuItem();
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});

		menu.add(item);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);

		setRefParentExternalFrame.setJMenuBar(menuBar);

		init();
	}

	public SetBrowser(JInternalFrame setRefParentInternalFrame) {

		refParentInternalFrame = setRefParentInternalFrame;

		refParentInternalFrame.setTitle("Collection Browser -ISet- INTERNAL");

		refParentInternalFrame.setLayout(new BorderLayout());

		refParentInternalFrame.add(new JLabel("Collection Browser"),
				BorderLayout.SOUTH);

		JMenu menu = new JMenu("load..");

		JMenuItem item = new JMenuItem();
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});

		menu.add(item);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);

		refParentInternalFrame.setJMenuBar(menuBar);

		JPanel jpanel_setting = new JPanel();
		
		refParentInternalFrame.add( jpanel_setting, BorderLayout.CENTER );
		
		refOffset = new JLabel("10");	
		refLength = new JLabel("10");
		refLabel = new JLabel("none");
		
		
		jtf_Id = new JTextField("300");
		jtf_NumberSelections = new JTextField("10");
		jtf_ReferenceSelect = new JTextField("--",30);
		jtf_ReferenceStore = new JTextField("--",30);
		jtf_Label = new JTextField("none");
		jtf_CacheId = new JTextField("-5");



		
		jb_update = new JButton("update");

		ActionListener actionListener = new UpdateActionListener(jb_update);				
		
		jpanel_setting.add(refOffset);
		jpanel_setting.add(refLength);
		jpanel_setting.add(refLabel);
		
		jpanel_setting.add(jtf_Id);
		jpanel_setting.add(jtf_NumberSelections);
		jpanel_setting.add(jtf_ReferenceSelect);
		jpanel_setting.add(jtf_ReferenceStore);
		jpanel_setting.add(jtf_Label);
		jpanel_setting.add(jb_update);
		jpanel_setting.add(jtf_CacheId);
		
		jtf_Id.addActionListener( actionListener );
		jtf_NumberSelections.addActionListener( actionListener );
		jtf_ReferenceSelect.addActionListener( actionListener );
		jtf_ReferenceStore.addActionListener( actionListener );
		jtf_Label.addActionListener( actionListener );
		jtf_CacheId.addActionListener( actionListener );
		
		jb_update.addActionListener( actionListener );	
		
		jtf_CacheId.setEditable( false );
		
		refParentInternalFrame.pack();
		refParentInternalFrame.setVisible( true );

		init();
	}

	private void init() {
		//loader = new FileLoader();

	}

	public void load() {
		assert false : "not implemented";
		//loader.load();
	}

	public void setSelection(VirtualArrayThreadSingleBlock setRefSelection) {
		refSelection = setRefSelection;
	}
	
	public void setStorage(VirtualArrayThreadSingleBlock setRefSelection) {
		refSelection = setRefSelection;
	}
	
	public void setSet(SetFlatThreadSimple setRefSet) {
		refSet = setRefSet;
	}

	public void updateGUI() {
		
		int iNewCacheId = this.refSet.getCacheId();
		
		/*
		 * Force hasCacheChanged to return true... 
		 */
		if ( ! refSet.hasCacheChanged(iNewCacheId)) {
			refSet.setCacheId( iNewCacheId + 1);
		}
	}

}
