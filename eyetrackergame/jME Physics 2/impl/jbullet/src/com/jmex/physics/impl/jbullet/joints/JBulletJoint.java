package com.jmex.physics.impl.jbullet.joints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.SliderConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraintType;
import com.bulletphysics.linearmath.Transform;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.JointAxis;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.TranslationalJointAxis;
import com.jmex.physics.impl.jbullet.JBulletDynamicPhysicsNode;
import com.jmex.physics.impl.jbullet.JBulletPhysicsSpace;
import com.jmex.physics.impl.jbullet.util.VecmathConverter;

public class JBulletJoint extends Joint {

    private Vector3f anchor = new Vector3f();
    private boolean isDirty = true;
    private JBulletPhysicsSpace space;
    private JBulletDynamicPhysicsNode[] nodes = new JBulletDynamicPhysicsNode[2];
    
    private List<JBulletDynamicPhysicsNode> nodeList = new ArrayList<JBulletDynamicPhysicsNode>();
    private RigidBody staticBody;

    private TypedConstraint constraint = null;
    private TypedConstraint secondaryConstraint = null;
    
    private boolean collisionEnabled = false;

    public JBulletJoint( JBulletPhysicsSpace space ) {
        this.space = space;
    }

    public void buildConstraint()
    {
        if ( constraint != null )
        {
            ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.removeConstraint( constraint );
            constraint = null;
        }
        if ( secondaryConstraint != null )
        {
            ( (JBulletPhysicsSpace) getSpace() ).dynamicsWorld.removeConstraint( secondaryConstraint );
            secondaryConstraint = null;
        }
        isDirty = false;

        //This is a physics picker joint.  We don't handle this yet.
        if ( nodes[0] == null ) {
            return;
        }

        int numTranslational = 0;
        int numRotational = 0;
        int index = 0;
        JointAxis[] axes = new JointAxis[3];
        Vector3f[] directions = new Vector3f[3];
        for ( JointAxis axis : getAxes() ) {
            if ( index > 2 ) {
                unsupported( "this physics implementation support a maximum of three axes " +
                        "for Joints." );
            }
            if ( axis.isTranslationalAxis() )
                numTranslational++;
            if ( axis.isRotationalAxis() )
                numRotational++;
            axes[index] = axis;
            directions[index] = axis.getDirection(directions[index]);
            directions[index].normalizeLocal();
            index++;
        }

        Transform transformRelativeToA = new Transform();
        Transform transformRelativeToB = new Transform();
        transformRelativeToA.setIdentity();
        transformRelativeToB.setIdentity();
        float totalMass;
        
        Vector3f anchor = new Vector3f();
        //This logic will set up joint pivot points.
        if ( nodes[1] == null ) {
        	anchor = getAnchor( anchor );
            totalMass=nodes[0].getMass();
        } else {
        	anchor = getAnchor( anchor );
        	//localToWorldIgnoreScale(nodes[0], anchor, anchor);
        	nodes[0].localToWorld(anchor, anchor);
            totalMass=nodes[0].getMass()+nodes[1].getMass();
        }

        if ( numTranslational > 0 ) { //This is a SLIDER joint
            if ( numRotational > 0 ) {
                unsupported( "this physics implementation supports either rotational" +
                        " axes or a translational axis - not both." );
            }
            if ( numTranslational > 1 ) {
                unsupported( "this physics implementation supports a maximum of 1 " +
                        "translational axis." );
            }

            //Set up the Constraint space
            Quaternion q = new Quaternion();
            prepareRotation(directions[0],Vector3f.UNIT_X.negate(),q);

            transformRelativeToA.setRotation( VecmathConverter.convert( q ) );
            Vector3f tempCM=new Vector3f();
            if(nodes[1]==null)
        	{
            	anchor.set(Vector3f.ZERO);
            	anchor.subtractLocal(nodes[0].getCenterOfMass(tempCM));
                VecmathConverter.convert( anchor , transformRelativeToA.origin );
            	anchor.addLocal(tempCM);
                localToWorldIgnoreScale(nodes[0],anchor,anchor);
        	}
            else
            {
            	worldToLocalIgnoreScale(nodes[0],anchor,anchor);
            	anchor.subtractLocal(nodes[0].getCenterOfMass(tempCM));
                VecmathConverter.convert( anchor , transformRelativeToA.origin );
            	anchor.addLocal(tempCM);
            	localToWorldIgnoreScale(nodes[0],anchor, anchor);
            	worldToLocalIgnoreScale(nodes[1],anchor, anchor);
            	anchor.subtractLocal(nodes[1].getCenterOfMass(tempCM));
            }
	       	transformRelativeToB.setRotation( VecmathConverter.convert( q ) );
	        VecmathConverter.convert( anchor , transformRelativeToB.origin );
            
            JBulletSliderJoint sc;
            if(nodes[1]==null)
            	sc = new JBulletSliderJoint( nodes[0].getBody(), staticBody , transformRelativeToA, transformRelativeToB , false );
            else 
            	sc = new JBulletSliderJoint( nodes[0].getBody(), nodes[1].getBody(), transformRelativeToA, transformRelativeToB, !axes[0].isRelativeToSecondObject() );
            
            JBulletTranslationalJointAxis tAxis = (JBulletTranslationalJointAxis)axes[0];
            tAxis.myJoint = sc;
            sc.totalMass = totalMass;
            sc.setMaxLinMotorForce(tAxis.getAvailableAcceleration() * totalMass);
            sc.setTargetLinMotorVelocity(tAxis.getDesiredVelocity());
            sc.setPoweredLinMotor(sc.getMaxLinMotorForce()>0);
            sc.setUpperLinLimit(tAxis.getPositionMaximum());
            sc.setLowerLinLimit(tAxis.getPositionMinimum());
            sc.setDampingDirLin(getDampingCoefficient());
            sc.setRestitutionDirLin(getSpringConstant());
            constraint = sc;
           
        } else if ( numRotational > 0 ) {
            if ( numRotational == 1 ) {
                // hinge joint
                if ( axes[0].isRelativeToSecondObject() ) {
                    unsupported( "this implemantation supports a rotational axis only if it is " +
                            "relative to the first object" );
                }
                
                //Set the constraint space.
                //For some reason, the hinge assumes a z-axis, rather than x.
                Quaternion q = new Quaternion();
                prepareRotation(directions[0],Vector3f.UNIT_Z,q);

                transformRelativeToA.setRotation( VecmathConverter.convert( q ) );
                Vector3f tempCM=new Vector3f();
                worldToLocalIgnoreScale(nodes[0],anchor, anchor);
            	anchor.subtractLocal(nodes[0].getCenterOfMass(tempCM));
                VecmathConverter.convert( anchor , transformRelativeToA.origin );
            	anchor.addLocal(tempCM);
            	localToWorldIgnoreScale(nodes[0],anchor, anchor);
                if(nodes[1]!=null)
                {
                	worldToLocalIgnoreScale(nodes[1],anchor, anchor);
                	anchor.subtractLocal(nodes[1].getCenterOfMass(tempCM));
                }
    	       	transformRelativeToB.setRotation( VecmathConverter.convert( q ) );
    	        VecmathConverter.convert( anchor , transformRelativeToB.origin );
                
                JBulletHingeJoint hc;
                if(nodes[1]==null)
                	hc = new JBulletHingeJoint( nodes[0].getBody(), staticBody, transformRelativeToA, transformRelativeToB);
                else 
                	hc = new JBulletHingeJoint( nodes[0].getBody(), nodes[1].getBody(), transformRelativeToA, transformRelativeToB);

                JBulletRotationalJointAxis rAxis = (JBulletRotationalJointAxis)axes[0];
                rAxis.myJoint=hc;
                hc.setLimit(rAxis.getPositionMinimum(),rAxis.getPositionMaximum());
                
                constraint = hc;
            } else if ( numRotational == 2 ) {
            	unsupported("2x rotational axis joints are unsupported in this implementation.");
            } else if ( numRotational == 3 ) {
            	
            	//Set up the constraint space for 2-axis or 3.
                Quaternion q = new Quaternion();
                Vector3f[] target = null;

            	target = new Vector3f[]{directions[0],directions[1],directions[2]};

                prepareRotation(target,q);

                transformRelativeToA.setRotation( VecmathConverter.convert( q ) );
                Vector3f tempCM=new Vector3f();
                worldToLocalIgnoreScale(nodes[0],anchor, anchor);
            	anchor.subtractLocal(nodes[0].getCenterOfMass(tempCM));
                VecmathConverter.convert( anchor , transformRelativeToA.origin );
            	anchor.addLocal(tempCM);
            	localToWorldIgnoreScale(nodes[0],anchor, anchor);
                if(nodes[1]!=null)
                {
                	worldToLocalIgnoreScale(nodes[1],anchor, anchor);
                	anchor.subtractLocal(nodes[1].getCenterOfMass(tempCM));
                }
    	       	transformRelativeToB.setRotation( VecmathConverter.convert( q ) );
    	        VecmathConverter.convert( anchor , transformRelativeToB.origin );
                
                Generic6DofConstraint con;
                if(nodes[1]==null)
                	con = new Generic6DofConstraint( nodes[0].getBody(), staticBody, transformRelativeToA, transformRelativeToB, true);
                else 
                	con = new Generic6DofConstraint( nodes[0].getBody(), nodes[1].getBody(), transformRelativeToA, transformRelativeToB, true);
                
                JBulletRotationalJointAxis rAxis1 = (JBulletRotationalJointAxis)axes[0];
                JBulletRotationalJointAxis rAxis2 = (JBulletRotationalJointAxis)axes[1];
                JBulletRotationalJointAxis rAxis3 = (JBulletRotationalJointAxis)axes[2];

                con.setAngularLowerLimit(VecmathConverter.convert(
                		new Vector3f(rAxis1.getPositionMinimum(),
                					 rAxis2.getPositionMinimum(),
                					 rAxis3.getPositionMinimum())));
                
                con.setAngularUpperLimit(VecmathConverter.convert(
                		new Vector3f(rAxis1.getPositionMaximum(),
                					 rAxis2.getPositionMaximum(),
                					 rAxis3.getPositionMaximum())));
                
                constraint = con;
            }
        } else {
        	//Set up the constraint space for 2-axis or 3.
            Quaternion q = new Quaternion();

            transformRelativeToA.setRotation( VecmathConverter.convert( q ) );
            Vector3f tempCM=new Vector3f();
            worldToLocalIgnoreScale(nodes[0],anchor, anchor);
        	anchor.subtractLocal(nodes[0].getCenterOfMass(tempCM));
            VecmathConverter.convert( anchor , transformRelativeToA.origin );
        	anchor.addLocal(tempCM);
        	localToWorldIgnoreScale(nodes[0],anchor, anchor);
            if(nodes[1]!=null)
            {
            	worldToLocalIgnoreScale(nodes[1],anchor, anchor);
            	anchor.subtractLocal(nodes[1].getCenterOfMass(tempCM));
            }
	       	transformRelativeToB.setRotation( VecmathConverter.convert( q ) );
	        VecmathConverter.convert( anchor , transformRelativeToB.origin );
            
            Generic6DofConstraint con;
            if(nodes[1]==null)
            	con = new Generic6DofConstraint( nodes[0].getBody(), staticBody, transformRelativeToA, transformRelativeToB, true);
            else 
            	con = new Generic6DofConstraint( nodes[0].getBody(), nodes[1].getBody(), transformRelativeToA, transformRelativeToB, true);
            
            con.setAngularLowerLimit(VecmathConverter.convert(new Vector3f(0f,0f,0f)));
            
            con.setAngularUpperLimit(VecmathConverter.convert(new Vector3f(0f,0f,0f)));
            
            constraint = con;
        }

        if(constraint!=null)
        	((JBulletPhysicsSpace) getSpace()).dynamicsWorld.addConstraint( constraint, !isCollisionEnabled() );
        if(secondaryConstraint!=null)
        	((JBulletPhysicsSpace) getSpace()).dynamicsWorld.addConstraint( secondaryConstraint, !isCollisionEnabled() );
    }    	
    
    private Vector3f localToWorldIgnoreScale(Node node, final Vector3f in, Vector3f store) {
        if (store == null)
            store = new Vector3f();
        return node.getWorldRotation().mult(
                store.set(in), store).addLocal(
                node.getWorldTranslation());
    }
    
    private Vector3f worldToLocalIgnoreScale(Node node, final Vector3f in, final Vector3f store) {
        in.subtract(node.getWorldTranslation(), store);
        node.getWorldRotation().inverse().mult(store, store);
        return store;
    }

    private Quaternion prepareRotation(Vector3f[] axes, Quaternion result)
    {
    	if(result==null)
    		result = new Quaternion();
    	
    	int totalAxes=0;
    	for(Vector3f vec : axes)
    		if(vec!=null) totalAxes++;
    	
    	if(totalAxes==1)
    	{
    		if(axes[0]!=null)
    			return prepareRotation(axes[0],Vector3f.UNIT_X,result);
    		if(axes[1]!=null)
    			return prepareRotation(axes[0],Vector3f.UNIT_Y,result);
    		if(axes[2]!=null)
    			return prepareRotation(axes[0],Vector3f.UNIT_Z,result);
    	}
    	else if(totalAxes==2)
    	{
    		if(axes[0]==null)
    			result.fromAxes(axes[1].cross(axes[2]), axes[1], axes[2]);
    		if(axes[1]==null)
    			result.fromAxes(axes[0], axes[2].cross(axes[0]), axes[2]);
    		if(axes[2]==null)
    			result.fromAxes(axes[0], axes[1], axes[0].cross(axes[1]));
    	}
    	else if(totalAxes==3)
    	{
            result.fromAxes( axes[0], axes[1], axes[2] );
        }
    	
    	return result;
    }
    
    private Quaternion prepareRotation(Vector3f target, Vector3f start, Quaternion result)
    {
    	if(result==null)
    		result = new Quaternion();
    	
        float angleBetween = start.angleBetween( target );
        Vector3f rotationalAxis = start.cross( target );
        result.fromAngleAxis(angleBetween, rotationalAxis);

        return result;
    }
    
    private void unsupported( String message ) {
        setActive( false ); // to allow next update to succeed
        throw new UnsupportedOperationException( message + " Problematic Joint: " + this );
    }

    @Override
    public void attach( DynamicPhysicsNode leftNode, DynamicPhysicsNode rightNode ) {
        if ( !( leftNode instanceof JBulletDynamicPhysicsNode ) ||
                !( rightNode instanceof JBulletDynamicPhysicsNode ) ) {
            throw new IllegalArgumentException( "Cannot attach nodes from a non-JBullet physics space." );
        }

        nodes[0] = (JBulletDynamicPhysicsNode) leftNode;
        nodes[1] = (JBulletDynamicPhysicsNode) rightNode;

        isDirty = true;
    }

    @Override
    public void attach( DynamicPhysicsNode node ) {
        if ( !( node instanceof JBulletDynamicPhysicsNode ) ) {
            throw new IllegalArgumentException( "Cannot attach nodes from a non-JBullet physics space." );
        }

        nodes[0] = (JBulletDynamicPhysicsNode) node;
        nodes[1] = null;

        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo( 0f, null, null );
        staticBody = new RigidBody( info );

        isDirty = true;
    }

    @Override
    protected void added( JointAxis axis ) {
        super.added( axis );
        isDirty=true;
    }

    @Override
    public void removed( JointAxis axis ) {
        super.removed( axis );
        isDirty=true;
    }

    @Override
    protected RotationalJointAxis createRotationalAxisImplementation() {
        return new JBulletRotationalJointAxis();
    }

    @Override
    protected TranslationalJointAxis createTranslationalAxisImplementation() {
        return new JBulletTranslationalJointAxis();
    }

    @Override
    public void detach() {
        nodes[0] = null;
        nodes[1] = null;

        isDirty = true;
    }

    @Override
    public Vector3f getAnchor( Vector3f store ) {
        if(store==null)
        {
        	store = new Vector3f();
        }
        store.set( anchor );
        return store;
    }

    @Override
    public float getBreakingLinearForce() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getBreakingTorque() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getDampingCoefficient() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<? extends DynamicPhysicsNode> getNodes() {
        if ( nodeList == null ) {
            nodeList = new ArrayList<JBulletDynamicPhysicsNode>();
        }
        nodeList.clear();
        if ( nodes[0] != null ) {
            nodeList.add( nodes[0] );
        }
        if ( nodes[1] != null ) {
            nodeList.add( nodes[1] );
        }
        return Collections.unmodifiableList( nodeList );
    }

    public float getPosition(JointAxis axis)
    {
    	int index = getAxes().indexOf(axis);
    	if(index==-1)
    		throw new IllegalArgumentException("The specified axis is a part of this joint.");
    	if(constraint==null)
    		return 0;
    	if(index==0 && axis.isTranslationalAxis() && constraint.getConstraintType().equals(TypedConstraintType.SLIDER_CONSTRAINT_TYPE))
    		return ((SliderConstraint)constraint).getLinearPos();

    	if(index==0 && axis.isRotationalAxis() && constraint.getConstraintType().equals(TypedConstraintType.HINGE_CONSTRAINT_TYPE))
    		return ((HingeConstraint)constraint).getHingeAngle();
    	
    	if(index==1 && axis.isRotationalAxis() && constraint.getConstraintType().equals(TypedConstraintType.HINGE_CONSTRAINT_TYPE) && secondaryConstraint!=null)
    		return ((HingeConstraint)secondaryConstraint).getHingeAngle();
    	
    	if(axis.isRotationalAxis() && constraint.getConstraintType().equals(TypedConstraintType.D6_CONSTRAINT_TYPE))
    		return ((Generic6DofConstraint)constraint).getAngle(index);
    	
    	throw new IllegalArgumentException("The specified Axis is not valid for the currently built joint.");
    }
    
    @Override
    public PhysicsSpace getSpace() {
        return this.space;
    }

    @Override
    public float getSpringConstant() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isCollisionEnabled() {
    	return collisionEnabled;
    }

    @Override
    public void reset() {

        isDirty = true;
    }

    @Override
    public void setAnchor( Vector3f anchor ) {
        this.anchor.set( anchor );
        isDirty = true;
    }

    @Override
    public void setBreakingLinearForce( float breakingLinearForce ) {

        isDirty = true;
    }

    @Override
    public void setBreakingTorque( float breakingTorque ) {

        isDirty = true;
    }

    public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
    public void setCollisionEnabled( boolean enabled ) {
		if(enabled==collisionEnabled) return;
		collisionEnabled = enabled;		
        isDirty = true;
    }

    @Override
    public void setSpring( float springConstant, float dampingCoefficient ) {

        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

}
