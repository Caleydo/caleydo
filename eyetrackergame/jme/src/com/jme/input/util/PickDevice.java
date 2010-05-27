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
package com.jme.input.util;

import java.util.HashMap;
import java.util.Map;

import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

public class PickDevice {
    private final Spatial root;
    private final InputHandler inputHandler;
    private final Vector2f mousePosition = new Vector2f();
    private final Vector3f pickedScreenPos = new Vector3f();
    private PickAction pickAction;
    private MoveAction moveAction;
    private Geometry picked;
    private SyntheticButton pickedButton;

    private Map<Spatial, SyntheticButton> buttons = new HashMap<Spatial, SyntheticButton>();

    public PickDevice( Spatial root, InputHandler inputHandler ) {
        this.root = root;
        this.inputHandler = new InputHandler();
        inputHandler.addToAttachedHandlers( this.inputHandler );

        pickAction = new PickAction();
        this.inputHandler.addAction( pickAction, InputHandler.DEVICE_MOUSE, 0, InputHandler.AXIS_NONE, false );

        moveAction = new MoveAction();
        this.inputHandler.addAction( moveAction, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE,
                InputHandler.AXIS_ALL, false );
    }

    public SyntheticButton createButton( Spatial toPick ) {
        SyntheticButton button = buttons.get( toPick );
        if ( button == null ) {
            button = new SyntheticButton( toPick.getName() );
            buttons.put( toPick, button );
        }
        return button;
    }

    public SyntheticButton getPickedButton() {
        return pickedButton;
    }

    private class PickAction extends InputAction {
        private final Ray pickRay = new Ray();
        private final TrianglePickResults pickResults = new TrianglePickResults();

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, 0, pickRay.origin );
                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, 0.3f, pickRay.direction );
                pickRay.direction.subtractLocal( pickRay.origin ).normalizeLocal();

                pickResults.clear();
                pickResults.setCheckDistance( true );
                root.findPick( pickRay, pickResults );
                loopResults:
                for ( int i = 0; i < pickResults.getNumber(); i++ ) {
                    PickData data = pickResults.getPickData( i );
                    if ( data.getTargetTris() != null && data.getTargetTris().size() > 0 ) {
                        Geometry geom = data.getTargetMesh();
                        Spatial target = geom;
                        while ( target != null ) {
                            SyntheticButton button = buttons.get( target );
                            if ( button != null ) {
                                button.trigger( 0, (char) 0, 0, evt.getTriggerPressed(), geom );
                                pickedButton = button;
                                picked = geom;

                                //todo: for dragging:
//                                DisplaySystem.getDisplaySystem().getScreenCoordinates( geom.getWorldTranslation(), pickedScreenPos );
//                                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, pickedScreenPos.z, pickedWorldOffset );
//                                pickedWorldOffset.subtractLocal( geom.getWorldTranslation() );

                                break loopResults;
                            }
                            target = target.getParent();
                        }
                    }
                }
            } else {
                if ( pickedButton != null ) {
                    pickedButton.trigger( 0, (char) 0, 0, evt.getTriggerPressed(), picked );
                    pickedButton = null;
                }
            }
        }
    }

    private class MoveAction extends InputAction {
        public void performAction( InputActionEvent evt ) {

            switch ( evt.getTriggerIndex() ) {
                case 0:
                    mousePosition.x = evt.getTriggerPosition() * DisplaySystem.getDisplaySystem().getWidth();
                    break;
                case 1:
                    mousePosition.y = evt.getTriggerPosition() * DisplaySystem.getDisplaySystem().getHeight();
                    break;
                case 2:
                    // move into z direction with the wheel
                    if ( evt.getTriggerDelta() > 0 ) {
                        pickedScreenPos.z += ( 1 - pickedScreenPos.z ) / 10;
                    } else {
                        pickedScreenPos.z = ( 10 * pickedScreenPos.z - 1 ) / 9;
                    }
                    break;
            }

            //todo: implement dragging:
//            if ( pickedButton != null ) {
//                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, pickedScreenPos.z, anchor );
//                geom.getLocalTranslation().set( anchor.subtractLocal( pickedWorldOffset ) );
//            }
        }
    }
}

/*
 * $Log: PickDevice.java,v $
 * Revision 1.1  2007/09/22 16:46:35  irrisor
 * Minor: fixed problems with calculating distance after pick (updateWorldVectors not called twice anymore), fixed bouding problems with floating point precision (scene was culled if values were large)
 *
 */

