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

package jmetest.audio;

import java.net.URL;
import java.util.ArrayList;

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.RangedAudioTracker;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.MusicTrackQueue.RepeatType;

/**
 * Demonstrates the use and some of the functionality of the com.jmex.audio
 * package.
 * 
 * @author Joshua Slack
 * @version $Id: TestJmexAudio.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestJmexAudio extends SimpleGame {

    private AudioSystem audio;
    private ArrayList<RangedAudioTracker> trackers = new ArrayList<RangedAudioTracker>();

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestJmexAudio app = new TestJmexAudio();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    @Override
    protected void simpleInitGame() {

        // setup a very simple scene
        Sphere emit1 = new Sphere("emit1", new Vector3f(0,0,0), 24, 16, 3);
        rootNode.attachChild(emit1);
        Box emit2 = new Box("emit2", new Vector3f(0,0,0), 1, 1, 1);
        emit2.setLocalTranslation(new Vector3f(10, 0, 0));
        rootNode.attachChild(emit2);
        SpatialTransformer st = new SpatialTransformer(1);
        st.setObject(emit2, 0, -1);
        st.setPosition(0, 0.0f, new Vector3f(8,-8,0));
        st.setPosition(0, 0.5f, new Vector3f(6,-4,-6));
        st.setPosition(0, 1.0f, new Vector3f(0,0,-8));
        st.setPosition(0, 1.5f, new Vector3f(-6,4,-6));
        st.setPosition(0, 2.0f, new Vector3f(-8,8,0));
        st.setPosition(0, 2.5f, new Vector3f(-6,4,6));
        st.setPosition(0, 3.0f, new Vector3f(0,0,8));
        st.setPosition(0, 3.5f, new Vector3f(6,-4,6));
        st.setPosition(0, 4.0f, new Vector3f(8,-8,0));
        st.interpolateMissing();
        st.setRepeatType(Controller.RT_WRAP);
        emit2.addController(st);
        
        // SOUND STUFF BELOW
        
        // grab a handle to our audio system.
        audio = AudioSystem.getSystem();
        
        // setup our ear tracker to track the camera's position and orientation.
        audio.getEar().trackOrientation(cam);
        audio.getEar().trackPosition(cam);
        
        // setup a music score for our demo
        AudioTrack music1 = getMusic(TestJmexAudio.class.getResource("/jmetest/data/sound/test.ogg"));
        audio.getMusicQueue().setRepeatType(RepeatType.ALL);
        audio.getMusicQueue().setCrossfadeinTime(2.5f);
        audio.getMusicQueue().setCrossfadeoutTime(2.5f);
        audio.getMusicQueue().addTrack(music1);
        audio.getMusicQueue().play();

        // setup positional sounds in our scene
        AudioTrack sfx1 = getSFX(TestJmexAudio.class.getResource("/jmetest/data/sound/CHAR_CRE_1.ogg"));
        RangedAudioTracker track1 = new RangedAudioTracker(sfx1, 25, 30);
        track1.setToTrack(emit1);
        track1.setTrackIn3D(true);
        track1.setMaxVolume(0.35f);  // set volume on the tracker as it will control fade in, etc.
        trackers.add(track1);
        
        AudioTrack sfx2 = getSFX(TestJmexAudio.class.getResource("/jmetest/data/sound/Footsteps.wav"));
        RangedAudioTracker track2 = new RangedAudioTracker(sfx2, 25, 30);
        track2.setToTrack(emit2);
        track2.setTrackIn3D(true);
        track2.setMaxVolume(1.0f);
        trackers.add(track2);
    }

    private AudioTrack getMusic(URL resource) {
        // Create a non-streaming, non-looping, relative sound clip.
        AudioTrack sound = AudioSystem.getSystem().createAudioTrack(resource, true);
        sound.setType(TrackType.MUSIC);
        sound.setRelative(true);
        sound.setTargetVolume(0.7f);
        sound.setLooping(false);
        return sound;
    }

    private AudioTrack getSFX(URL resource) {
        // Create a non-streaming, looping, positional sound clip.
        AudioTrack sound = AudioSystem.getSystem().createAudioTrack(resource, false);
        sound.setType(TrackType.POSITIONAL);
        sound.setRelative(false);
        sound.setLooping(true);
        return sound;
    }
    
    @Override
    protected void simpleUpdate() {
        // update our audio system here:
        audio.update();
        
        for (int x = trackers.size(); --x >= 0; ) {
            RangedAudioTracker t = trackers .get(x);
            t.checkTrackAudible(cam.getLocation());
        }
    }

    @Override
    protected void cleanup() {
        audio.cleanup();
    }
}
