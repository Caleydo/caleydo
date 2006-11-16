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
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;
import demos.common.*;
import demos.util.*;
import gleem.*;
import gleem.linalg.*;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.set.SetFlatSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.selection.VirtualArraySingleBlock;
import cerberus.data.collection.selection.VirtualArrayThreadSingleBlock;
import cerberus.data.collection.storage.FlatThreadStorageSimple;

import cerberus.view.swing.loader.FileLoader;

/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java and ARB_fragment_program by Kenneth Russell
 */

public class StorageBrowser {

	public static void main(String[] args) {

		JFrame baseFrame = new JFrame();

		StorageBrowser demo = new StorageBrowser(baseFrame);

	}

	private JPanel jpanel_setting;
	
	private JLabel refOffset;

	private JTextField jtf_NumberArrays;
	private JTextField jtf_SizeArrays;
	private JTextField jtf_Label;
	private JTextField jtf_Id;
	private JTextField jtf_CacheId;
	
	private JButton jb_update;
	
	private JLabel refLength;

	private JLabel jl_Label;
	private JLabel jl_Id;

	private JFrame refParentExternalFrame = null;

	private JInternalFrame refParentInternalFrame = null;

	private FlatThreadStorageSimple refStorage;

	public FileLoader loader;

	protected class UpdateActionListener implements ActionListener {
		
		private boolean bWasUpdated = false;
		
		private final JButton jb_target;
		
		public UpdateActionListener( final JButton target ) {
			this.jb_target = target;
		}
		
		public void actionPerformed(ActionEvent e) {
			if ( e.getSource() == jb_target) {
				
				if ( bWasUpdated ) {
					if ( refStorage.getWriteToken() ) {
						
						refStorage.setLabel( jtf_Label.getText() );					
						refStorage.setId( Integer.valueOf( jtf_Id.getText() ) );			
						
						refStorage.setCacheId( refStorage.getCacheId() + 5 );
						
						refStorage.returnWriteToken();
						
						bWasUpdated = false;
					}
					else {
						System.err.println("Can not update, because write-token is not available.");
					}
				}
				else {
					if ( refStorage.getReadTokenWait() ) {
						
						Hashtable <StorageType,Integer> hastLookupTable =
							refStorage.getAllSize();
						
						String sBuffer = "";
						
						Enumeration <StorageType> items = hastLookupTable.keys();
						
						while ( items.hasMoreElements() ) 
						{
							int iBuffer = hastLookupTable.get( items.nextElement() );
							
							sBuffer += " " + iBuffer;
							
						}
						
						jtf_NumberArrays.setText( Integer.toString( refStorage.getNumberArrays() ) );
						jtf_SizeArrays.setText( sBuffer );
						jtf_Label.setText( refStorage.getLabel() );
						jtf_Id.setText( Integer.toString( refStorage.getId() ));
						jtf_CacheId.setText( 
								Integer.toString( refStorage.getCacheId() ) );
						
						refStorage.returnReadToken();
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
	
	public StorageBrowser(JFrame setRefParentExternalFrame) {
		refParentExternalFrame = setRefParentExternalFrame;

		setRefParentExternalFrame.setTitle("Collection Browser -IStorage-");

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

	public StorageBrowser(JInternalFrame setRefParentInternalFrame) {

		refParentInternalFrame = setRefParentInternalFrame;

		refParentInternalFrame.setTitle("Collection Browser -IStorage- INTERNAL");

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
		jl_Label = new JLabel("label");
		jl_Id = new JLabel("Id");
		
		
		jtf_Id = new JTextField("300");
		jtf_NumberArrays = new JTextField("10");
		jtf_SizeArrays = new JTextField("10");
		jtf_Label = new JTextField("none");
		jtf_CacheId = new JTextField("-5");
		

		
		jb_update = new JButton("update");

		ActionListener actionListener = new UpdateActionListener(jb_update);				
		
		jpanel_setting.add(refOffset);
		jpanel_setting.add(refLength);
		
		jpanel_setting.add(jl_Id);
		jpanel_setting.add(jtf_Id);
		
		jpanel_setting.add(jtf_NumberArrays);
		jpanel_setting.add(jtf_SizeArrays);
		
		jpanel_setting.add(jl_Label);
		jpanel_setting.add(jtf_Label);
		jpanel_setting.add(jtf_CacheId);
		jpanel_setting.add(jb_update);
		
		jtf_Id.addActionListener( actionListener );
		jtf_NumberArrays.addActionListener( actionListener );
		jtf_SizeArrays.addActionListener( actionListener );
		jtf_Label.addActionListener( actionListener );				
		jtf_CacheId.addActionListener( actionListener );	
		
		refParentInternalFrame.pack();
		refParentInternalFrame.show();

		init();
	}

	private void init() {
		//loader = new FileLoader();

	}

	public void load() {
		loader.load();
	}

	public void setStorage(FlatThreadStorageSimple setRefStorage) {
		refStorage = setRefStorage;
	}

	public void updateGUI() {

	}

}
