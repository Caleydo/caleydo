/*
 * Copyright (c) 2005-2006 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
package com.jmex.physics.impl.ode.joints;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jmex.physics.*;
import com.jmex.physics.impl.ode.DynamicPhysicsNodeImpl;
import com.jmex.physics.impl.ode.OdePhysicsSpace;
import org.odejava.*;
import org.odejava.Joint;
import org.odejava.ode.OdeConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Irrisor
 */
public class OdeJoint extends com.jmex.physics.Joint {
    private final OdePhysicsSpace space;
    private boolean feedbackEnabled = false;
    private PhysicsUpdateCallback breakCallback;
    private boolean collisionsEnabled;

    public OdeJoint( OdePhysicsSpace space ) {
        this.space = space;
    }

    @Override
    protected TranslationalJointAxis createTranslationalAxisImplementation() {
        return new TranslationalOdeJointAxis();
    }

    @Override
    protected RotationalJointAxis createRotationalAxisImplementation() {
        return new RotationalOdeJointAxis();
    }

    @Override
    public OdePhysicsSpace getSpace() {
        return space;
    }

    @Override
    protected void added( JointAxis axis ) {
        super.added( axis );
        typeChanged = true;
    }

    @Override
    public void removed( JointAxis axis ) {
        super.removed( axis );
        typeChanged = true;
    }

    private boolean typeChanged = true;
    private Joint odeJoint;

    @Override
    public void reset() {
        typeChanged = true;
    }

    private final Vector3f anchor = new Vector3f();

    @Override
    public void setAnchor( Vector3f anchor ) {
        this.anchor.set( anchor );
        if ( odeJoint != null ) {
            adjustAnchor();
        }
    }

    @Override
    public Vector3f getAnchor( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        return store.set( anchor );
    }

    public void setSpring( float springConstant, float dampingCoefficient ) {
        if ( odeJoint == null || odeJoint instanceof JointBall || odeJoint instanceof JointFixed || odeJoint instanceof JointSlider ) {
            //        float r = 2.0f * dampingCoefficient *
            //                FastMath.sqrt( sprungMass * springConstant );
            if ( !Float.isNaN( springConstant ) && !Float.isNaN( dampingCoefficient ) ) {
                float r = dampingCoefficient;
                float hk = getSpace().getODEJavaWorld().getStepSize() * springConstant;

                setERP( hk / ( hk + r ) );
                setCFM( 1.0f / ( hk + r ) );
            } else {
                setERP( Float.NaN );
                setCFM( Float.NaN );
            }
        } else {
            throw new UnsupportedOperationException( "spring only supported for 0 axes, 1 translational axis or 3 rotational axes" );
        }
    }
    
    public float getSpringConstant() {
        //todo: store value instead of recomputing it for better accuracy?
    	float springConstant = Float.NaN;
    	if(!Float.isNaN( erp ) && !Float.isNaN( cfm )) {
    		springConstant = (erp / cfm) / getSpace().getODEJavaWorld().getStepSize();
    	}
    	
    	return springConstant;
    }
    
    public float getDampingCoefficient() {
    	//todo: store value instead of recomputing it for better accuracy?
    	float dampingCeofficient = Float.NaN;
    	if(!Float.isNaN( erp ) && !Float.isNaN( cfm )) {
    		dampingCeofficient = (1 / cfm) - (erp / cfm);
    	}
    	
    	return dampingCeofficient;
    }

    private float erp = Float.NaN;
    private float cfm = Float.NaN;

    public void setERP( float value ) {
        erp = value;
        applyERP();
    }

    private void applyERP() {
        if ( odeJoint != null ) {
            if ( !Float.isNaN( erp ) ) {
                odeJoint.setParam( OdeConstants.dParamERP, erp );
                odeJoint.setParam( OdeConstants.dParamStopERP, erp );
                odeJoint.setParam( OdeConstants.dParamStopERP2, erp );
                odeJoint.setParam( OdeConstants.dParamStopERP3, erp );
            } else {
                float globalERP = getSpace().getODEJavaWorld().getErrorReductionParameter();
                odeJoint.setParam( OdeConstants.dParamERP, globalERP );
                odeJoint.setParam( OdeConstants.dParamStopERP, globalERP );
                odeJoint.setParam( OdeConstants.dParamStopERP2, globalERP );
                odeJoint.setParam( OdeConstants.dParamStopERP3, globalERP );
            }
        }
    }

    public void setCFM( float value ) {
        cfm = value;
        applyCFM();
    }

    private void applyCFM() {
        if ( odeJoint != null ) {
            if ( !Float.isNaN( cfm ) ) {
                odeJoint.setParam( OdeConstants.dParamCFM, cfm );
                odeJoint.setParam( OdeConstants.dParamStopCFM, cfm );
                odeJoint.setParam( OdeConstants.dParamStopCFM2, cfm );
                odeJoint.setParam( OdeConstants.dParamStopCFM3, cfm );
            } else {
                float globalCFM = getSpace().getODEJavaWorld().getConstraintForceMix();
                odeJoint.setParam( OdeConstants.dParamCFM, globalCFM );
                odeJoint.setParam( OdeConstants.dParamStopCFM, globalCFM );
                odeJoint.setParam( OdeConstants.dParamStopCFM2, globalCFM );
                odeJoint.setParam( OdeConstants.dParamStopCFM3, globalCFM );
            }
        }
    }

    public void updateJointType() {
        if ( typeChanged ) {
            if ( odeJoint != null ) {
                odeJoint.delete();
                odeJoint = null;
            }
            typeChanged = false;
            int numTranslational = 0;
            int numRotational = 0;
            int index = 0;
            JointAxis[] axes = new JointAxis[3];
            for ( JointAxis axis : getAxes() ) {
                if ( index > 2 ) {
                    unsupported( "this physics implementation support a maximum of three axes " +
                            "for Joints." );
                }
                if ( axis.isTranslationalAxis() ) {
                    numTranslational++;
                    axes[index++] = axis;
                }
                if ( axis.isRotationalAxis() ) {
                    numRotational++;
                    axes[index++] = axis;
                }
            }
            World odeJavaWorld = this.getSpace().getODEJavaWorld();
            if ( numTranslational > 0 ) {
                if ( numRotational > 0 ) {
                    unsupported( "this physics implementation supports either rotational" +
                            " axes or a translational axis - not both." );
                }
                if ( numTranslational > 1 ) {
                    unsupported( "this physics implementation supports a maximum of 1 " +
                            "translational axis." );
                }
                // ok 1 translational axis can be implemented by a SliderJoint
                // but we can't do rotation then
                JointSlider slider = new JointSlider( this.getName(), odeJavaWorld );
                odeJoint = slider;
                attach( true );
                changeDelegate( axes[0], new SliderJointAxis( axes[0], slider ) );
            } else if ( numRotational > 0 ) {
                if ( numRotational == 1 ) {
                    // hinge
                    if ( axes[0].isRelativeToSecondObject() ) {
                        unsupported( "this implemantation supports a rotational axis only if it is " +
                                "relative to the first object" );
                    }
                    JointHinge hinge = new JointHinge( this.getName(), odeJavaWorld );
                    odeJoint = hinge;
                    attach( true );
                    changeDelegate( axes[0], new HingeJointAxis( axes[0], hinge, null ) );
                } else if ( numRotational == 2 ) {
                    // universal or hinge2
                    if ( !Float.isInfinite( axes[1].getPositionMaximum() ) ) {
                        unsupported( "the second rotational axis cannot be restricted in this implementation" );
                    }
                    if ( axes[0].isRelativeToSecondObject() || !axes[1].isRelativeToSecondObject() ) {
                        unsupported( "this implemantation supports two rotational axes only if the second one is " +
                                "relative to the second object, the first axis must be relative to the first object" );
                    }

                    Joint joint;
                    if ( !Float.isInfinite( axes[0].getPositionMinimum() ) ) {
                        // hinge2 has stops
                        joint = new JointHinge2( this.getName(), odeJavaWorld );
                    } else {
                        // universal does not have stops
                        joint = new JointUniversal( this.getName(), odeJavaWorld );
                    }
                    odeJoint = joint;
                    attach( true );
                    changeDelegate( axes[0], new HingeJointAxis( axes[0], joint, this ) );
                    changeDelegate( axes[1], new HingeJointAxis2( axes[1], joint ) );
                } else if ( numRotational == 3 ) {
                    // ball, possibly plus angular motor
                    odeJoint = new JointBall( this.getName(), odeJavaWorld );
                    attach( true );
                    //TODO AMotor
                }
            } else {
                JointFixed jointFixed = new JointFixed( this.getName(), odeJavaWorld );
                odeJoint = jointFixed;
                attach( true );
            }

            if ( odeJoint != null ) {
                odeJoint.setName( getName() );
                setForceInfoEnabled( feedbackEnabled );
                setCollisionEnabled( collisionsEnabled );
            }
        }
    }

    @Override
    public void setName( final String value ) {
        super.setName( value );
        if ( odeJoint != null ) {
            odeJoint.setName( value );
        }
    }

    private final Vector3f anchorTmp = new Vector3f();

    private void attach( boolean reanchor ) {
        updateJointType();
        if ( odeJoint != null ) {
            enableBodies();
            applyCFM();
            applyERP();
            if ( reanchor ) {
                adjustAnchor();
                if ( odeJoint instanceof JointFixed ) {
                    JointFixed jointFixed = (JointFixed) odeJoint;
                    jointFixed.setFixed();
                }
            }
        }
    }

    private void enableBodies() {
        Body body1 = nodes.size() > 0 ? nodes.get( 0 ).getBody() : null;
        Body body2 = nodes.size() > 1 ? nodes.get( 1 ).getBody() : null;
        Body oldBody1 = odeJoint.getBody1();
        Body oldBody2 = odeJoint.getBody2();
        odeJoint.attach( body1, body2 );
        if ( body1 != null ) {
            body1.setEnabled( true );
        }
        if ( body2 != null ) {
            body2.setEnabled( true );
        }
        if ( oldBody1 != null ) {
            oldBody1.setEnabled( true );
        }
        if ( oldBody2 != null ) {
            oldBody2.setEnabled( true );
        }
    }

    private void adjustAnchor() {
        if ( nodes.size() < 2 ) {
            odeJoint.setAnchor( anchor.x, anchor.y, anchor.z );
        } else {
            anchorTmp.set( anchor );
            DynamicPhysicsNodeImpl node1 = nodes.get( 0 );
            node1.getWorldRotation().multLocal( anchorTmp );
            anchorTmp.addLocal( node1.getWorldTranslation() );
            odeJoint.setAnchor( anchorTmp.x, anchorTmp.y, anchorTmp.z );
        }
    }

    private void changeDelegate( JointAxis toBeReplaced, JointAxis newAxis ) {
        ( (OdeJointAxis) toBeReplaced ).setDelegate( newAxis );
    }

    @Override
    public boolean setActive( boolean value ) {
        boolean changed = super.setActive( value );
        if ( changed ) {
            if ( !value ) {
                detach();
            } else {
                attach( false );
            }
        }
        return changed;
    }

    @Override
    public void attach( DynamicPhysicsNode leftNode, DynamicPhysicsNode rightNode ) {
        leftNode.updateWorldVectors();
        rightNode.updateWorldVectors();
        nodes.clear();
        nodes.add( (DynamicPhysicsNodeImpl) leftNode );
        nodes.add( (DynamicPhysicsNodeImpl) rightNode );
        attach( true );
    }

    @Override
    public void attach( DynamicPhysicsNode node ) {
        node.updateWorldVectors();
        nodes.clear();
        nodes.add( (DynamicPhysicsNodeImpl) node );
        attach( true );
    }

    @Override
    public void detach() {
        nodes.clear();
        if ( odeJoint != null ) {
            enableBodies();
            odeJoint.attach( null, null );
        }
    }

    private final List<DynamicPhysicsNodeImpl> nodes = new ArrayList<DynamicPhysicsNodeImpl>( 2 );
    private List<? extends DynamicPhysicsNode> immutableNodes;

    @Override
    public List<? extends DynamicPhysicsNode> getNodes() {
        if ( immutableNodes == null ) {
            immutableNodes = Collections.unmodifiableList( nodes );
        }
        return immutableNodes;
    }

    private void unsupported( String message ) {
        setActive( false ); // to allow next update to succeed
        throw new UnsupportedOperationException( message + " Problematic Joint: " + this );
    }

    void checkType() {
        typeChanged = true;
    }

    public void setForceInfoEnabled( boolean state ) {
        feedbackEnabled = state;
        if ( odeJoint != null ) {
            odeJoint.enableFeedbackTracking( state );
        }
    }

    private float breakingLinearForce = Float.POSITIVE_INFINITY;
    private float breakingTorque = Float.POSITIVE_INFINITY;

    public float getBreakingLinearForce() {
        return breakingLinearForce;
    }

    public void setBreakingLinearForce( float breakingLinearForce ) {
        if ( Float.isNaN( breakingLinearForce ) ) {
            throw new IllegalArgumentException( "Parameter cannot be NaN!" );
        }
        if ( breakingLinearForce < 0 ) {
            throw new IllegalArgumentException( "Parameter cannot be smaller than 0!" );
        }
        this.breakingLinearForce = breakingLinearForce;
        processBreaking();
    }

    private void processBreaking() {
        boolean enabled = !Float.isInfinite( getBreakingLinearForce() ) || !Float.isInfinite( getBreakingTorque() );
        setForceInfoEnabled( enabled );
        if ( enabled ) {
            if ( breakCallback == null ) {
                breakCallback = new PhysicsUpdateCallback() {
                    public void beforeStep( PhysicsSpace space, float time ) {

                    }

                    public void afterStep( PhysicsSpace space, float time ) {
                        JointFeedback feedback = odeJoint.getFeedback();
                        float maxForce = getBreakingLinearForce();
                        if ( !Float.isInfinite( maxForce ) ) {
                            float force = FastMath.sqrt( feedback.getForce1().lengthSquared()
                                    + feedback.getForce2().lengthSquared() );
                            if ( force > maxForce ) {
                                breakJoint();
                            }
                        }
                        float maxTorque = getBreakingTorque();
                        if ( !Float.isInfinite( maxTorque ) ) {
                            float torque = FastMath.sqrt(feedback.getTorque1().lengthSquared()
                                    + feedback.getTorque2().lengthSquared());
                            if ( torque > maxTorque ) {
                                breakJoint();
                            }
                        }
                    }
                };
            }
            getSpace().addToUpdateCallbacks( breakCallback );
        } else {
            if ( breakCallback != null ) {
                getSpace().removeFromUpdateCallbacks( breakCallback );
                breakCallback = null;
            }
        }
    }

    private void breakJoint() {
        //TODO: fire event!
        detach();
    }

    public float getBreakingTorque() {
        return breakingTorque;
    }

    public void setBreakingTorque( float breakingTorque ) {
        if ( Float.isNaN( breakingTorque ) ) {
            throw new IllegalArgumentException( "Parameter cannot be NaN!" );
        }
        if ( breakingTorque < 0 ) {
            throw new IllegalArgumentException( "Parameter cannot be smaller than 0!" );
        }
        this.breakingTorque = breakingTorque;
        processBreaking();
    }

    public void setCollisionEnabled(boolean enabled) {
        collisionsEnabled = enabled;
        if ( odeJoint != null )
        {
            odeJoint.setBodiesCollide( enabled );
        }
    }

    public boolean isCollisionEnabled() {
        return collisionsEnabled;
    }
}

/*
 * $log$
 */

