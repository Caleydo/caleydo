package com.jmex.effects.particles;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

public class AnimationEntry implements Savable {
    protected float offset = 0.05f; // 5% of life from previous entry
    protected float rate = 0.2f; // 5 fps
    protected int[] frames = new int[1];
    
    public AnimationEntry() {
    }
    
    public AnimationEntry(float offset) {
        this.offset = offset;
    }
    
    public int[] getFrames() {
        return frames;
    }
    public void setFrames(int[] frames) {
        this.frames = frames;
    }
    public float getOffset() {
        return offset;
    }
    public void setOffset(float offset) {
        this.offset = offset;
    }
    public float getRate() {
        return rate;
    }
    public void setRate(float rate) {
        this.rate = rate;
    }

    public Class getClassTag() {
        return getClass();
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        offset = capsule.readFloat("offsetMS", 0.05f);
        rate = capsule.readFloat("rate", 0.2f);
        frames = capsule.readIntArray("frames", null);
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(offset, "offsetMS", 0.05f);
        capsule.write(rate, "rate", 0.2f);
        capsule.write(frames, "frames", null);
    }

    private static String makeText(int[] frames) {
        if (frames == null || frames.length == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        for (int frame : frames) {
            sb.append(frame);
            sb.append(",");
        }
        return sb.substring(0, sb.length()-1);
    }

    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();

        builder.append("prev+");
        builder.append((int)(offset*100));
        builder.append("% age...");

        builder.append("  rate: "+rate);

        builder.append("  sequence: "+makeText(frames));
        
        return builder.toString();
    }
}
