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
//
//import prometheus.data.collection.Set;
//import prometheus.data.collection.Selection;
//import prometheus.data.collection.Storage;
//import prometheus.data.collection.set.SetFlatSimple;
//import prometheus.data.collection.storage.FlatThreadStorageSimple;
//import prometheus.data.collection.selection.SelectionSingleBlock;

import cerberus.data.collection.selection.SelectionThreadSingleBlock;

import cerberus.view.swing.loader.FileLoader;

/**
  Wavelength-dependent refraction demo<br>
  It's a chromatic aberration!<br>
  sgreen@nvidia.com 4/2001<br><p>

  Currently 3 passes - could do it in 1 with 4 texture units<p>

  Cubemap courtesy of Paul Debevec<p>

  Ported to Java and ARB_fragment_program by Kenneth Russell
*/

public class CollectionBrowserTemplate  {
	
  public static void main(String[] args) {
     
	  JFrame baseFrame = new JFrame();
	  
    CollectionBrowserTemplate demo = new CollectionBrowserTemplate( baseFrame );

  }

  private JFrame refParentExternalFrame = null;
  
  private JInternalFrame refParentInternalFrame = null;
  
  
  public FileLoader loader;

  
  public CollectionBrowserTemplate(JFrame setRefParentExternalFrame) {
	  refParentExternalFrame = setRefParentExternalFrame;
	  
	  setRefParentExternalFrame.setTitle( "Collection Browser -Set-");
	  
	  setRefParentExternalFrame.setLayout(new BorderLayout());
	    	   
	  setRefParentExternalFrame.add( new JLabel("Collection Browser"), BorderLayout.CENTER);
	  setRefParentExternalFrame.pack();
	  
	  //setRefParentExternalFrame.show();	

	    JMenu menu = new JMenu("load..");
	    
	    JMenuItem item = new JMenuItem();
	    item.addActionListener( new ActionListener() { 
	    		 public void actionPerformed(ActionEvent e) {
	    			 load();
	    		 }
	    		 });
	    		 
	    menu.add( item );
	    
	    JMenuBar menuBar = new JMenuBar();
	    menuBar.add( menu );
	    
	    setRefParentExternalFrame.setJMenuBar( menuBar );
	    
	    init();
  }
  
  public CollectionBrowserTemplate(JInternalFrame setRefParentInternalFrame) {
	  
	  refParentInternalFrame = setRefParentInternalFrame;
		  
	  
	  refParentInternalFrame.setTitle( "Collection Browser -Set- INTERNAL");
	  
	  refParentInternalFrame.setLayout(new BorderLayout());
	    	   
	  refParentInternalFrame.add( new JLabel("Collection Browser"), BorderLayout.CENTER);
	  refParentInternalFrame.pack();
	  refParentInternalFrame.show();	

	    JMenu menu = new JMenu("load..");
	    
	    JMenuItem item = new JMenuItem();
	    item.addActionListener( new ActionListener() { 
	    		 public void actionPerformed(ActionEvent e) {
	    			 load();
	    		 }
	    		 });
	    		 
	    menu.add( item );
	    
	    JMenuBar menuBar = new JMenuBar();
	    menuBar.add( menu );
	    
	    refParentInternalFrame.setJMenuBar( menuBar );
	    
	    init();
  }
 
  private void init() {
	  loader = new FileLoader();		 
  }
  
  public void load() {
	  loader.load();
  }
  
  public void setSelection( SelectionThreadSingleBlock setRefSelection ) {
	  
  }
  
  public void updateGUI() {
	  
  }
  
}
