/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the odejava nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import java.util.Iterator;
import java.util.LinkedList;

import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dJointGroupID;

/**
 * A joint group is a special container that holds joints in a world. Joints can
 * be added to a group, and then when those joints are no longer needed the
 * entire group of joints can be very quickly destroyed with one function call.
 * However, individual joints in a group can not be destroyed before the entire
 * group is emptied. This is most useful with contact joints, which are added
 * and remove from the world in groups every time step. Created 20.12.2003
 * (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class JointGroup {
    private SWIGTYPE_p_dJointGroupID jointGroupId;
    protected LinkedList<Joint> jointList;

    /**
     * Create joint group in the given jointGroupId. If you wish to create joint
     * group normally, set max_size to 0.
     *
     * @param max_size
     */
    public JointGroup( String name, int max_size ) {
        jointGroupId = Ode.dJointGroupCreate( max_size );
        jointList = new LinkedList<Joint>();
        // jointMap = new HashMap();
    }

    /**
     * Create jointGroup normally (id=0).
     */
    public JointGroup( String name ) {
        this( name, 0 );
    }

    public JointGroup() {
        this( null, 0 );
    }

    public void addJoint( Joint joint ) {
        jointList.add( joint );
        // if (joint.getName() != null)
        // jointMap.put(joint.getName(), joint);
    }

    /**
     * @return Returns the jointGroupId.
     */
    public SWIGTYPE_p_dJointGroupID getId() {
        return jointGroupId;
    }

    public void empty() {
        Iterator<Joint> i = jointList.iterator();
        while ( i.hasNext() ) {
            ( i.next() ).delete();
        }
        jointList = new LinkedList<Joint>();
        // jointMap = new HashMap();
        Ode.dJointGroupEmpty( jointGroupId );
    }

    /**
     * @return Returns the jointList.
     */
    public LinkedList<? extends Joint> getJointList() {
        return jointList;
    }

    // /**
    // * @return Returns the jointMap.
    // */
    // public HashMap getJointMap() {
    // return jointMap;
    // }

    // /**
    // * Get joint by name.
    // *
    // * @param name
    // * @return joint
    // */
    // public Joint getJoint(String name) {
    // return (Joint) jointMap.get(name);
    // }

    public void delete() {
        empty();
        Ode.dJointGroupDestroy( jointGroupId );
    }

}
