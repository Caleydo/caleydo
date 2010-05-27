/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.awt.swingui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.jme.app.SimpleGame;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.awt.swingui.JMEAction;
import com.jmex.awt.swingui.JMEDesktop;

/**
 * Very short example for JMEDesktop - see {@link TestJMEDesktop} for more features.
 */
public class HelloJMEDesktop extends SimpleGame {
    private static final Logger logger = Logger.getLogger(HelloJMEDesktop.class
            .getName());

    private Node guiNode;

    protected void simpleInitGame() {
        // create a node for ortho gui stuff
        guiNode = new Node( "gui" );
        guiNode.setRenderQueueMode( Renderer.QUEUE_ORTHO );

        // create the desktop Quad
        final JMEDesktop desktop = new JMEDesktop( "desktop", 500, 400, input );
        // and attach it to the gui node
        guiNode.attachChild( desktop );
        // center it on screen
        desktop.getLocalTranslation().set( display.getWidth() / 2 - 30, display.getHeight() / 2 + 50, 0 );

        // perform all the swing stuff in the swing thread
        // (Only access the Swing UI from the Swing event dispatch thread!
        // See SwingUtilities.invokeLater()
        // and http://java.sun.com/docs/books/tutorial/uiswing/concurrency/index.html for details.)
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // make it transparent blue
                desktop.getJDesktop().setBackground( new Color( 0, 0, 1, 0.2f ) );

                // create a swing button
                final JButton button = new JButton( "click me" );
                // and put it directly on the desktop
                desktop.getJDesktop().add( button );
                // desktop has no layout - we layout ourselfes (could assign a layout to desktop here instead)
                button.setLocation( 200, 200 );
                button.setSize( button.getPreferredSize() );
                // add some actions
                // standard swing action:
                button.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        // this gets executed in swing thread
                        // alter swing components ony in swing thread!
                        button.setLocation( FastMath.rand.nextInt( 400 ), FastMath.rand.nextInt( 300 ) );
                        logger.info( "clicked!" );
                    }
                } );
                // action that gets executed in the update thread:
                button.addActionListener( new JMEAction( "my action", input ) {
                    public void performAction( InputActionEvent evt ) {
                        // this gets executed in jme thread
                        // do 3d system calls in jme thread only!
                        guiNode.updateRenderState(); // this call has no effect but should be done in jme thread :)
                    }
                });
            }
        } );

        // don't cull the gui away
        guiNode.setCullHint( Spatial.CullHint.Never );
        // gui needs no lighting
        guiNode.setLightCombineMode( Spatial.LightCombineMode.Off );
        // update the render states (especially the texture state of the deskop!)
        guiNode.updateRenderState();
        // update the world vectors (needed as we have altered local translation of the desktop and it's
        //  not called in the update loop)
        guiNode.updateGeometricState( 0, true );

        // finally show the system mouse cursor to allow the user to click our button
        MouseInput.get().setCursorVisible( true );
    }

    protected void simpleRender() {
        // draw the gui stuff after the scene
        display.getRenderer().draw( guiNode );
    }

    public static void main( String[] args ) {
        new HelloJMEDesktop().start();
    }
}
