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

package jmetest.flagrushtut.lesson7;

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
        if(velocity < 0) {
            velocity += ((weight/5) * time);
        } else {
            velocity -= ((weight/5) * time);
        }
    }

    

    
}
