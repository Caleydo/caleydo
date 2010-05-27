/*
 * Copyright (c) 2006 World of Mystery Project Team
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

package jmetest.awt.swingui.dnd;

import java.awt.BorderLayout;
import java.awt.dnd.DnDConstants;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import com.jmex.awt.swingui.dnd.JMEDndException;
import com.jmex.awt.swingui.dnd.JMEDragAndDrop;
import com.jmex.awt.swingui.dnd.JMEDragGestureEvent;
import com.jmex.awt.swingui.dnd.JMEDragGestureListener;
import com.jmex.awt.swingui.dnd.JMEDragSourceEvent;
import com.jmex.awt.swingui.dnd.JMEDragSourceListener;
import com.jmex.awt.swingui.dnd.JMEDropTargetEvent;
import com.jmex.awt.swingui.dnd.JMEDropTargetListener;
import com.jmex.awt.swingui.dnd.JMEMouseDragGestureRecognizer;


public class DndImage extends JInternalFrame implements JMEDragSourceListener, JMEDropTargetListener, JMEDragGestureListener {
    private static final Logger logger = Logger.getLogger(DndImage.class
            .getName());

    private static final long serialVersionUID = 6297095858466971972L;
    private JLabel label;
    private JLabel info;
    private ImageIcon bgIcon;
    private JMEDragAndDrop dnd;

    public DndImage( JMEDragAndDrop dragAndDropSupport ) {
        setLayout( new BorderLayout() );
        bgIcon = new ImageIcon( DndImage.class.getResource( "test.png" ) );
        label = new JLabel( bgIcon );
        label.setName( "DndImage.Label" );
        add( label );
        info = new JLabel( " " );
        add( info, BorderLayout.SOUTH );
        dnd = dragAndDropSupport;
        new JMEMouseDragGestureRecognizer( dragAndDropSupport, this, DnDConstants.ACTION_COPY_OR_MOVE, this );
    }

    public void dragEnter( JMEDragSourceEvent e ) {
        info.setText( "dragEnter" );
    }

    public void dragExit( JMEDragSourceEvent e ) {
        info.setText( "dragExit" );
    }

    public void dragDropEnd( JMEDragSourceEvent e ) {
        if ( !e.getDropSuccess() ) {
            label.setIcon( bgIcon );
        }
        info.setText( "dragDropEnd: " + e.getDropSuccess() );
    }

    public void dragEnter( JMEDropTargetEvent e ) {
        info.setText( "dragEnter" );
    }

    public void dragExit( JMEDropTargetEvent e ) {
        info.setText( "dragExit" );
    }

    public void dragOver( JMEDropTargetEvent e ) {
        info.setText( "dragOver" );
    }

    public void drop( JMEDropTargetEvent e ) {
        try {
            e.acceptDrop( e.getAction() );
            label.setIcon( (ImageIcon) e.getTransferable().getTransferData( TransferableImage.IMAGE_FLAVOR ) );
            e.dropComplete( true );
        } catch ( Exception ex ) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "drop(JMEDropTargetEvent e)", "Exception", ex);
        }
    }

    public void dragGestureRecognized( JMEDragGestureEvent dge ) {
        info.setText( "dragGestureRecognized" );
        ImageIcon icon = (ImageIcon) label.getIcon();
        if ( icon == null ) {
            return;
        }
        TransferableImage transferable = new TransferableImage( icon );
        try {
            dnd.startDrag( dge, icon, transferable, this );
            label.setIcon( null );
        } catch ( JMEDndException e ) {
            logger.log(Level.WARNING, "invalid dnd action", e);
        }
        catch ( Exception e ) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "dragGestureRecognized(JMEDragGestureEvent dge)", "Exception", e);
        }
    }
}
