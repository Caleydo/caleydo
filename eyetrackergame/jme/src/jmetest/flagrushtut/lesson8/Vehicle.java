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

package jmetest.flagrushtut.lesson8;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * Vehicle will be a node that handles the movement of a vehicle in the
 * game. It has parameters that define its acceleration and speed as well
 * as braking. The turn speed defines what kind of handling it has, and the
 * weight will define things such as friction for drifting, how fast it falls
 * etc.
 * @author Mark Powell
 *
 */
/**
 * @author Mark Powell
 *
 */
public class Vehicle extends Node {
    private static final long serialVersionUID = 1L;
    private static final float LEAN_BUFFER = 0.05f;
    private Spatial model;
    private float weight;
    private float velocity;
    private float acceleration;
    private float braking;
    private float turnSpeed;
    
    private float maxSpeed = 30;
    private float minSpeed = 10;
    
//  temporary vector for the rotation
    private static final Vector3f tempVa = new Vector3f();
    private int lean;
    private float leanAngle;
    private Vector3f leanAxis = new Vector3f(0,0,1);
    private Quaternion q = new Quaternion();
    
//  information for rotation of the wheels
    Spatial frontwheel, backwheel;
    //rotate about the Y axis ... this is explained in the tutorial.
    private Vector3f wheelAxis = new Vector3f(0, 1, 0);
    private float angle = 0;
    private Quaternion rotQuat = new Quaternion();
    
    /**
     * Basic constructor takes the model that represents the graphical 
     * aspects of this Vehicle.
     * @param id the id of the vehicle
     * @param model the model representing the graphical aspects.
     */
    public Vehicle(String id, Spatial model) {
        super(id);
        setModel(model);
    }
    
    /**
     * Constructor takes all performance attributes of the vehicle during
     * creation.
     * @param id the id of the vehicle
     * @param model the model representing the graphical apsects.
     * @param maxSpeed the maximum speed this vehicle can reach. (Unit/sec)
     * @param minSpeed the maximum speed this vehicle can reach while traveling in reverse. (Unit/sec)
     * @param weight the weight of the vehicle.
     * @param acceleration how fast this vehicle can reach max speed
     * @param braking how fast this vehicle can slow down and if held long enough reverse
     * @param turnSpeed how quickly this vehicle can rotate.
     */
    public Vehicle(String id, Spatial model, float maxSpeed, float minSpeed, 
            float weight, float acceleration, float braking, float turnSpeed) {
        super(id);
        setModel(model);
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.weight = weight;
        this.acceleration = acceleration;
        this.braking = braking;
        this.turnSpeed = turnSpeed;
    }
    
    /**
     * update applies the translation to the vehicle based on the time passed.
     * @param time the time between frames
     */
    public void update(float time) {
        this.localTranslation.addLocal(this.localRotation.getRotationColumn(2, tempVa)
                .multLocal(velocity * time));
        rotateWheels(time);
        processLean(time);
    }
    
    /**
     * rotateWheels will rotate the wheel (front and back) a certain angle based on
     * the velocity of the bike.
     * @param time the time between frames.
     */
    private void rotateWheels(float time) {
        //Rotate the tires if the vehicle is moving.
        if (vehicleIsMoving()) {
            if(velocity > FastMath.FLT_EPSILON) {
                angle = angle - ((time) * velocity * 0.5f);
                if (angle < -360) {
                    angle += 360;
                }
            } else {
                angle = angle + ((time) * velocity * 0.5f);
                if (angle > 360) {
                    angle -= 360;
                }
            }
            rotQuat.fromAngleAxis(angle, wheelAxis);
            frontwheel.getLocalRotation().multLocal(rotQuat);
            backwheel.setLocalRotation(frontwheel.getLocalRotation());
        }
    }

    /**
     * Convience method that determines if the vehicle is moving or not. This is
     * given if the velocity is approximately zero, taking float point rounding
     * errors into account.
     * @return true if the vehicle is moving, false otherwise.
     */
    public boolean vehicleIsMoving() {
        return velocity > FastMath.FLT_EPSILON
                || velocity < -FastMath.FLT_EPSILON;
    }

    /**
     * processlean will adjust the angle of the bike model based on 
     * a lean factor. We angle the bike rather than the Vehicle, as the
     * Vehicle is worried about position about the terrain.
     * @param time the time between frames
     */
    private void processLean(float time) {
        //check if we are leaning at all
        if(lean != 0) {
            if(lean == -1 && leanAngle < 0) {
                leanAngle += -lean * 4 * time;
            } else if(lean == 1 && leanAngle > 0) {
                leanAngle += -lean * 4 * time;
            } else {
                leanAngle += -lean * 2 * time;
            }
            //max lean is 1 and -1
            if(leanAngle > 1) {
                leanAngle = 1;
            } else if(leanAngle < -1) {
                leanAngle = -1;
            }
        } else { //we are not leaning, so right ourself back up.
            if(leanAngle < LEAN_BUFFER && leanAngle > -LEAN_BUFFER) {
                leanAngle = 0;
            }
            else if(leanAngle < -FastMath.FLT_EPSILON) {
                leanAngle += time * 4;
            } else if(leanAngle > FastMath.FLT_EPSILON) {
                leanAngle -= time * 4;
            } else {
                leanAngle = 0;
            }
        }
       
        q.fromAngleAxis(leanAngle, leanAxis);
        model.setLocalRotation(q);
        
        lean = 0;
    }
    
    /**
     * set the weight of this vehicle
     * @param weight the weight of this vehicle
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    /**
     * retrieves the weight of this vehicle.
     * @return the weight of this vehicle.
     */
    public float getWeight() {
        return weight;
    }

    /**
     * retrieves the acceleration of this vehicle.
     * @return the acceleration of this vehicle.
     */
    public float getAcceleration() {
        return acceleration;
    }

    /**
     * set the acceleration rate of this vehicle
     * @param acceleration the acceleration rate of this vehicle
     */
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * retrieves the braking speed of this vehicle.
     * @return the braking speed of this vehicle.
     */
    public float getBraking() {
        return braking;
    }

    /**
     * set the braking speed of this vehicle
     * @param braking the braking speed of this vehicle
     */
    public void setBraking(float braking) {
        this.braking = braking;
    }

    /**
     * retrieves the model Spatial of this vehicle.
     * @return the model Spatial of this vehicle.
     */
    public Spatial getModel() {
        return model;
    }

    /**
     * sets the model spatial of this vehicle. It first
     * detaches any previously attached models.
     * @param model the model to attach to this vehicle.
     */
    public void setModel(Spatial model) {
        this.detachChild(this.model);
        this.model = model;
        this.attachChild(this.model);
        //obtain references to the front and back wheel
        
        backwheel = ((Node)model).getChild("backwheel");
        frontwheel = ((Node)model).getChild("frontwheel");
    }

    /**
     * retrieves the velocity of this vehicle.
     * @return the velocity of this vehicle.
     */
    public float getVelocity() {
        return velocity;
    }

    /**
     * set the velocity of this vehicle
     * @param velocity the velocity of this vehicle
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
    
    /**
     * retrieves the turn speed of this vehicle.
     * @return the turn speed of this vehicle.
     */
    public float getTurnSpeed() {
        return turnSpeed;
    }

    /**
     * set the turn speed of this vehicle
     * @param turnSpeed the turn speed of this vehicle
     */
    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }
    
    /**
     * retrieves the maximum speed of this vehicle.
     * @return the maximum speed of this vehicle.
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * sets the maximum speed of this vehicle.
     * @param maxSpeed the maximum speed of this vehicle.
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * retrieves the minimum speed of this vehicle.
     * @return the minimum speed of this vehicle.
     */
    public float getMinSpeed() {
        return minSpeed;
    }

    /**
     * sets the minimum speed of this vehicle.
     * @param minSpeed the minimum speed of this vehicle.
     */
    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }
    
    /**
     * brake adjusts the velocity of the vehicle based on the braking speed. If the
     * velocity reaches 0, braking will put the vehicle in reverse up to the minimum 
     * speed.
     * @param time the time between frames.
     */
    public void brake(float time) {
        velocity -= time * braking;
        if(velocity < -minSpeed) {
            velocity = -minSpeed;
        }
    }
    
    /**
     * accelerate adjusts the velocity of the vehicle based on the acceleration. The velocity
     * will continue to raise until maxSpeed is reached, at which point it will stop.
     * @param time the time between frames.
     */
    public void accelerate(float time) {
        velocity += time * acceleration;
        if(velocity > maxSpeed) {
            velocity = maxSpeed;
        }
    }
    
    /**
     * drift calculates what happens when the vehicle is neither braking or accelerating. 
     * The vehicle will slow down based on its weight.
     * @param time the time between frames.
     */
    public void drift(float time) {
        if(velocity < -FastMath.FLT_EPSILON) {
            velocity += ((weight/5) * time);
            //we are drifting to a stop, so we shouldn't go
            //above 0
            if(velocity > 0) {
                velocity = 0;
            }
        } else if(velocity > FastMath.FLT_EPSILON){
            velocity -= ((weight/5) * time);
            //we are drifting to a stop, so we shouldn't go
            //below 0
            if(velocity < 0) {
                velocity = 0;
            }
        }
    }

    public void setRotateOn(int modifier) {
        lean = modifier;
    }

    

    
}
