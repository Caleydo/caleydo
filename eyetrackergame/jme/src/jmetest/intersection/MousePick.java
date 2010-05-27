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

package jmetest.intersection;

import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Text;

/**
 * <code>MousePick</code>
 * @author Mark Powell
 * @version
 */
public class MousePick extends MouseInputAction {

    private Camera camera;
    private Node scene;
    private float shotTime = 0;
    private int hits = 0;
    private int shots = 0;
    private Text text;
    private String hitItems;

    public MousePick(Camera camera, Node scene, Text text) {
        this.camera = camera;
        this.scene = scene;
        this.text = text;
    }
    /* (non-Javadoc)
     * @see com.jme.input.action.MouseInputAction#performAction(float)
     */
    public void performAction(InputActionEvent evt) {
        shotTime += evt.getTime();
        if( MouseInput.get().isButtonDown(0) && shotTime > 0.1f) {
            shotTime = 0;
            Ray ray = new Ray(camera.getLocation(), camera.getDirection()); // camera direction is already normalized
            PickResults results = new BoundingPickResults();
            results.setCheckDistance(true);
            scene.findPick(ray,results);


            hits += results.getNumber();
            hitItems = "";
            if(results.getNumber() > 0) {
                for(int i = 0; i < results.getNumber(); i++) {
                    hitItems += results.getPickData(i).getTargetMesh().getName() + " " + results.getPickData(i).getDistance();
                    if(i != results.getNumber() -1) {
                        hitItems += ", ";
                    }
                }
            }
            shots++;
            results.clear();
            text.print("Hits: " + hits + " Shots: " + shots + " : " + hitItems);
        }
    }
}
