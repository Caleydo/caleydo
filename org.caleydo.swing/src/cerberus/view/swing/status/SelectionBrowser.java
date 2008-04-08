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
//import java.awt.Color;
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
//
//import prometheus.data.collection.Set;
//import prometheus.data.collection.Selection;
//import prometheus.data.collection.Storage;
//import prometheus.data.collection.set.SetFlatSimple;
//import prometheus.data.collection.storage.FlatThreadStorageSimple;
//import prometheus.data.collection.selection.SelectionSingleBlock;

import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;

//import cerberus.view.swing.loader.FileLoader;

/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java and ARB_fragment_program by Kenneth Russell
 */

public class SelectionBrowser {

	public static void main(String[] args) {

		JFrame baseFrame = new JFrame();

		SelectionBrowser demo = new SelectionBrowser(baseFrame);

	}

	private JPanel jpanel_setting;
	
	private JLabel jl_Length;
	private JLabel jl_Label;
	private JLabel jl_Id;
	private JLabel jl_Offset;

	private JTextField jtf_Offset;
	private JTextField jtf_Length;
	private JTextField jtf_Label;
	private JTextField jtf_Id;
	private JTextField jtf_CacheId;
	
	
	
	private Color jb_default_color;
	
	private JButton jb_update;
	


	private JFrame refParentExternalFrame = null;

	private JInternalFrame refParentInternalFrame = null;

	private VirtualArrayThreadSingleBlock refSelection;

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
					if ( refSelection.getWriteToken() ) {
						
						refSelection.setLabel( jtf_Label.getText() );
						refSelection.setOffset( Integer.valueOf( jtf_Offset.getText() ) );
						refSelection.setLength( Integer.valueOf( jtf_Length.getText() ) );
						refSelection.setId( Integer.valueOf( jtf_Id.getText() ) );			
						refSelection.setCacheId( Integer.valueOf( jtf_CacheId.getText() ) + 1 );	
						
						refSelection.returnWriteToken();
						
						bWasUpdated = false;
						
						jb_update.setBackground( jb_default_color );
					}
					else {
						System.err.println("Can not update, because write-token is not available.");
					}
				}
				else {
					if ( refSelection.getReadTokenWait() ) {
						
						jtf_Offset.setText( Integer.toString( refSelection.getOffset() ) );
						jtf_Length.setText( Integer.toString( refSelection.length() ) );
						jtf_Label.setText( refSelection.getLabel() );
						jtf_Id.setText(  Integer.toString( refSelection.getId() ) );	
						jtf_CacheId.setText( Integer.toString( refSelection.getCacheId() ) );
						
						refSelection.returnReadToken();
					}
					else {
						System.err.println("Can not update, becasue read-token is not available.");
					}
				}
			}
			else {
				bWasUpdated = true;

				System.err.println("PING");		
				
				jb_update.setBackground( Color.ORANGE );
			}
		}
		
		public void reset() {
			bWasUpdated = false;
		}
		
		public boolean isUpdated() {
			return this.bWasUpdated;
		}
		
	}
	
	public SelectionBrowser(JFrame setRefParentExternalFrame) {
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

	public SelectionBrowser(JInternalFrame setRefParentInternalFrame) {

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
		
		jl_Offset = new JLabel("offset");	
		jl_Length = new JLabel("len");
		jl_Label = new JLabel("label");
		jl_Id = new JLabel("id");
		
		
		jtf_CacheId = new JTextField("-5");
		jtf_Id = new JTextField("300");
		jtf_Offset = new JTextField("10");
		jtf_Length = new JTextField("10");
		jtf_Label = new JTextField("none");
		



		
		jb_update = new JButton("update");
		jb_default_color = jb_update.getBackground();

		ActionListener actionListener = new UpdateActionListener(jb_update);				
		
		
		jpanel_setting.add(jl_Id);
		jpanel_setting.add(jtf_Id);
		
		jpanel_setting.add(jl_Offset);
		jpanel_setting.add(jtf_Offset);
		
		jpanel_setting.add(jl_Length);
		jpanel_setting.add(jtf_Length);
		
		jpanel_setting.add(jl_Label);
		jpanel_setting.add(jtf_Label);
		
		jpanel_setting.add(jtf_CacheId);
		
		jpanel_setting.add(jb_update);
		
		jtf_Id.addActionListener( actionListener );
		jtf_Offset.addActionListener( actionListener );
		jtf_Length.addActionListener( actionListener );
		jtf_Label.addActionListener( actionListener );
		jtf_CacheId.addActionListener( actionListener );
		
		jb_update.addActionListener( actionListener );	
		
		refParentInternalFrame.pack();
		refParentInternalFrame.show();

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

	public void updateGUI() {

	}

}
