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

package jmetest.renderer;

import java.util.logging.Logger;

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;

/*
 * spatialTest.java
 *
 * Created on 05 April 2005, 19:00 BST. 
 * Spatial diagnostics tests.
 * Activate the tests by setting the 'test' global, and executing, see code for details.
 * Note the timeDivision setting must be >=2 for the scale and transform tests (need at least three time points).
 *
 * TESTS :
 * 0            Static, default scale,positon and rotation of translation object **
 * 1            Rotation (0 - 180 - 0)
 * 2            Scale (0 - 5 - 0)
 * 3            Rotation + scale.
 * 4            Translation.
 * 5            Translation + Rotation.
 * 6            Translation + Scale.
 * 7            All three.
 *
 * ** For test 0 the controller is NOT attached, since no key frames are created for this test, see line 105.
 */

/**
 *
 * @author Philip Wainwright
 * 
 * Updated on 07 April 2005, 20:00 BST : 
 *      Changed variable keyFrames to timeDivisions
 *      Modified rotate test [ setRotation() ] to rotate the Transform box through 360 degrees.
 */

public class TestSpatialTransform extends SimpleGame{
    private static final Logger logger = Logger
            .getLogger(TestSpatialTransform.class.getName());
   
    //Test setting, 0 to 7.
    private final int test=7;
    
    //time in seconds to complete a loop of the test.
    private final float time = 5; 
    
    //number of time divisions (>=2 for transform and scale tests)
    private final float timeDivision = 10;
    
    private SpatialTransformer spt;
    
    /** Creates a new instance of spatialTest */
    public TestSpatialTransform() {
        //only testing one object
        spt = new SpatialTransformer(1);
        spt.setRepeatType(Controller.RT_WRAP);
    }
       
    public static void main(String[] args)
    {
        TestSpatialTransform testApp = new TestSpatialTransform();
        
        logger.info("Spatial transform diagnostic tests, 5/4/2005");
        
        //show the properties all the time (fsp, non of triangles, etc..)
        testApp.setConfigShowMode(ConfigShowMode.AlwaysShow);        
        //start the application.
        testApp.start();        
    }    
    
    
    public void simpleInitGame()
    {
        //two boxes, one is the reference.
        Box ref = new Box("Reference",new Vector3f(0,0,0),new Vector3f(1f,1f,1f));
        Box trans = new Box("Transform",new Vector3f(0,0,0),new Vector3f(1f,1f,1f));
        
        //Set the transform box to 2x
        trans.setLocalScale(2f);        
        
        //locate the transform box at 1,1,1 
        //(in front of, above and to the right of the reference box, for the default camera position).
        trans.setLocalTranslation(new Vector3f(1,1,1));
        
        //45 degrees all axis.
        Quaternion quat45 = new Quaternion();
        quat45.fromAngleAxis(0.7854f, new Vector3f(1,1,1));
        
        //Rotate the transform box 45 degrees, all axis.
        trans.setLocalRotation(quat45);
        
        //Camera (set to allow full vewing of the scene)
        this.cam.setLocation(new Vector3f(1f,2.5f,12));        
        
        //setup the test
        setTest(trans);
        
        //DIAGNOSTIC Display the scale, rotation and translation of the translation object         
        //The previous bugs would have affected these values, at this stage (post interpolateMissing())
        logger.info("DIAGNOSTIC ::");
        logger.info("SCALE :"+trans.getLocalScale());
        logger.info("ROTATION :"+trans.getLocalRotation());
        logger.info("TRANSLATION :"+trans.getLocalTranslation());
        
        //attach our controller to the transform box for all valid tests.
        if(test>0)
            trans.addController(this.spt);
        
        //attach both boxes to the root node
        this.rootNode.attachChild(ref);
        this.rootNode.attachChild(trans);        
    }
    
   /**
     *
     * Sets up the spatial tests.
     * @param spatial
     *          Spatial to affect.
     */
    private void setTest(Spatial spatial){        
        
        //Set at index 0, no parent.
        spt.setObject(spatial,0,-1);
        
        //This uses a bit mask, values 0 through to 7 will test all possible combinations.        
        if( (this.test&1)==1)
            setRotation();
        
        if( (this.test&2)==2)
            setScale();
        
        if( (this.test&4)==4)
            setTranslation();
        
        spt.interpolateMissing();        
    }
    
    
    /**
     * Sets the test rotation of 0 to 360 degrees, all axis.     
     */
    private void setRotation()
    {
        Quaternion rotRef = new Quaternion();
        
        float rotation=0;
        
        //iterate over the range, go over by one step to accommodate mathematical error
        for(float timeElp=0;timeElp<(this.time+(this.time/this.timeDivision));timeElp+=(this.time/this.timeDivision))
        {
            rotation = (timeElp/this.time)*360;
         
            if(rotation>360)
                rotation=360;//lock to 360
            
            rotRef.fromAngleAxis((float)(Math.PI/180)*rotation,new Vector3f(1,1,1));
            
            this.spt.setRotation(0,timeElp,rotRef);
        }
    }
    
     /**
     * Sets the test translation, an orbit in X,Y,Z around the reference Box object.     
     */
    private void setTranslation()
    {
        Vector3f transRef = new Vector3f();
        
        float translation;
        
        //iterate over the range, go over by one step to accommodate mathematical error
        for(float timeElp=0;timeElp<(this.time+(this.time/this.timeDivision));timeElp+=(this.time/this.timeDivision))
        {
            translation = (float)((timeElp/this.time)*(Math.PI*2));
            
            //correct for mathematical errors
            if(translation>(Math.PI*2))
                translation=(float)Math.PI*2;
                
                          
            transRef.x = (float)Math.sin(translation)*5;
            transRef.y = (float)Math.cos(translation)*2.5f;
            transRef.z = (float)Math.cos(translation)*5;
            
            this.spt.setPosition(0,timeElp,transRef);
        }
    }
    
     /**
     * Sets the test scale, a scaling from 0 to 5 and back again.
     */
    private void setScale()
    {
        Vector3f scaleRef = new Vector3f();
        
        float scaling;
        
        //iterate over the range, go over by one step to accommodate mathematical error
        for(float timeElp=0;timeElp<(this.time+(this.time/this.timeDivision));timeElp+=(this.time/this.timeDivision))
        {
            
            if(timeElp<(this.time/2))
                scaling = (timeElp/this.time)*10;
            else
                scaling = ((1-timeElp/this.time)*10);
           
            scaleRef.x = scaling;
            scaleRef.y = scaling;
            scaleRef.z = scaling;
            
            this.spt.setScale(0,timeElp,scaleRef);
        }
    }
}
