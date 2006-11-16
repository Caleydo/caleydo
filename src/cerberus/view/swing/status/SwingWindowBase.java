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

import java.util.HashMap;


import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetFlatSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;
import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;

import cerberus.manager.IGeneralManager;
import cerberus.manager.singelton.IGeneralManagerSingelton;
import cerberus.view.swing.loader.FileLoader;

/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java and ARB_fragment_program by Kenneth Russell
 */

public class SwingWindowBase implements AWTEventListener {

	private HashMap framemap_internalFrame;
	
	private HashMap framemap_externalFrame;

	private final IGeneralManagerSingelton refGeneralManagerSingelton;

	protected final String sTitle;
	
	protected final String sWindowName;
	
	protected JMenu j_menu;
	
	protected JMenuBar j_menuBar;

	private final boolean bIsExternalFrame;
	
	private JFrame refParentExternalFrame = null;

	private JInternalFrame refParentInternalFrame = null;
	
	
	public SwingWindowBase( final IGeneralManagerSingelton refGeneralManager,
			JFrame setRefParentExternalFrame, 
			final String sSetTitle,
			final String sSetWindowName ) {
		refParentExternalFrame = setRefParentExternalFrame;

		this.sTitle = sSetTitle;
		this.sWindowName = sSetWindowName;
		this.refGeneralManagerSingelton = refGeneralManager;
		this.bIsExternalFrame = true;
		
		initMenu();
		
		setRefParentExternalFrame.setTitle(sTitle + " -long-");
		setRefParentExternalFrame.setLayout(new BorderLayout());
		setRefParentExternalFrame.setName( sWindowName );		
		setRefParentExternalFrame.add(new JLabel(sTitle),BorderLayout.CENTER);		
		setRefParentExternalFrame.setJMenuBar(j_menuBar);
		
		setRefParentExternalFrame.pack();
		setRefParentExternalFrame.setVisible(true);	

	}
	
	public SwingWindowBase( final IGeneralManagerSingelton refGeneralManager,
			JInternalFrame setRefParentInternalFrame,
			final String sSetTitle,
			final String sSetWindowName  ) {

		this.sTitle = sSetTitle;
		this.sWindowName = sSetWindowName;
		this.refGeneralManagerSingelton = refGeneralManager;
		this.bIsExternalFrame = false;
		
		initMenu();
		
		refParentInternalFrame = setRefParentInternalFrame;
		refParentInternalFrame.setTitle(sSetTitle + " INTERNAL");
		refParentInternalFrame.setLayout(new BorderLayout());
		refParentInternalFrame.add(new JLabel(sSetTitle),BorderLayout.SOUTH);
		refParentInternalFrame.setJMenuBar(j_menuBar);
		
		refParentInternalFrame.pack();
		refParentInternalFrame.show();

	}

	
	 public void eventDispatched(AWTEvent evt) {
	        try {
	            if(evt.getID() == WindowEvent.WINDOW_OPENED) {
	                ComponentEvent cev = (ComponentEvent)evt;
	                if(cev.getComponent() instanceof JFrame) {
	                	refGeneralManagerSingelton.setErrorMessage( "event: " + evt);
	                    JFrame frame = (JFrame)cev.getComponent();
	                    loadSettings(frame);
	                }
	            }
	        }catch(Exception ex) {
	            refGeneralManagerSingelton.setErrorMessage( ex.toString() );
	        }
	    }

	 protected static int getInt(Properties props, String name, int value) {
	        String v = props.getProperty(name);
	        if(v == null) {
	            return value;
	        }
	        return Integer.parseInt(v);
	    }
	 
	 public void loadSettings(JFrame frame) throws IOException {
	        Properties settings = new Properties();
	        // if this file does not already exist, create an empty one
	        try {
	            settings.load(new FileInputStream("configuration.props"));
	        } catch (FileNotFoundException fnfe) {
	            settings.store (new FileOutputStream ("configuration.props"),
	                            "Window settings");
	        }
	        String name = frame.getName();
	        int x = getInt(settings,name+".x",100);
	        int y = getInt(settings,name+".y",100);
	        int w = getInt(settings,name+".w",500);
	        int h = getInt(settings,name+".h",500);
	        
	        if ( bIsExternalFrame ) {
	        	refParentExternalFrame.setLocation(x,y);
	        	refParentExternalFrame.setSize(new Dimension(w,h));
	        	framemap_externalFrame.put(name,frame);
	        	refParentExternalFrame.validate();
	        }
	        else 
	        {
	        	refParentInternalFrame.setLocation(x,y);
	        	refParentInternalFrame.setSize(new Dimension(w,h));
	        	framemap_internalFrame.put(name,frame);
	        	refParentInternalFrame.validate();
	        }
	    }
	 
	 public void saveSettings() throws IOException {
	        Properties settings = new Properties();
	        try {
	            settings.load(new FileInputStream("configuration.props"));
	        } catch (FileNotFoundException fnfe) {
	            // quietly ignore and overwrite anyways
	        }
	        Iterator it_external = framemap_externalFrame.keySet().iterator();
	        while(it_external.hasNext()) {
	            String name = (String)it_external.next();
	            JFrame frame = (JFrame)framemap_externalFrame.get(name);
	            settings.setProperty(name+".x",""+frame.getX());
	            settings.setProperty(name+".y",""+frame.getY());
	            settings.setProperty(name+".w",""+frame.getWidth());
	            settings.setProperty(name+".h",""+frame.getHeight());
	        }
	        
	        Iterator it_internal = framemap_internalFrame.keySet().iterator();
	        while(it_internal.hasNext()) {
	            String name = (String)it_internal.next();
	            JFrame frame = (JFrame)framemap_internalFrame.get(name);
	            settings.setProperty(name+".x",""+frame.getX());
	            settings.setProperty(name+".y",""+frame.getY());
	            settings.setProperty(name+".w",""+frame.getWidth());
	            settings.setProperty(name+".h",""+frame.getHeight());
	        }
	        
	        settings.store(new FileOutputStream("configuration.props"),null);
	    }
	 
	private void initMenu() {
		j_menu = new JMenu();		
		j_menuBar = new JMenuBar();
		
		j_menuBar.add(j_menu);
	}
	
	


}
