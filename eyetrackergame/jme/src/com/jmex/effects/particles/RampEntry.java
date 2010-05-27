package com.jmex.effects.particles;

import java.io.IOException;

import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>RampEntry</code> defines an entry for a ParticleAppearanceRamp.
 * 
 * @author Joshua Slack
 * @see ParticleAppearanceRamp
 */
public class RampEntry implements Savable {

    public static final float DEFAULT_OFFSET = 0.05f; // (5% of lifetime)
    public static final float DEFAULT_SIZE = -1; // special case -> negative = no size change at this entry
    public static final float DEFAULT_SPIN = Float.MAX_VALUE; // special case -> no spin change
    public static final float DEFAULT_MASS = Float.MAX_VALUE; // special case -> no mass change
    public static final ColorRGBA DEFAULT_COLOR = null; // special case -> no color change

    protected float offset = DEFAULT_OFFSET;
    protected ColorRGBA color = DEFAULT_COLOR; // no color change at this entry
    protected float size = DEFAULT_SIZE;
    protected float spin = DEFAULT_SPIN;
    protected float mass = DEFAULT_MASS;

    public RampEntry() {
    }

    /**
     * Construct new addition to color ramp
     * @param offset amount of time (as a percent of total lifetime) between the last appearance and this one.
     */
    public RampEntry(float offset) {
        setOffset(offset);
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }
    
    public boolean hasColorSet() {
        return color != DEFAULT_COLOR;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
    
    public boolean hasSizeSet() {
        return size != DEFAULT_SIZE;
    }

    public float getSpin() {
        return spin;
    }

    public void setSpin(float spin) {
        this.spin = spin;
    }
    
    public boolean hasSpinSet() {
        return spin != DEFAULT_SPIN;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }
    
    public boolean hasMassSet() {
        return mass != DEFAULT_MASS;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public Class getClassTag() {
        return getClass();
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        offset = capsule.readFloat("offsetMS", DEFAULT_OFFSET);
        size = capsule.readFloat("size", DEFAULT_SIZE);
        spin = capsule.readFloat("spin", DEFAULT_SPIN);
        mass = capsule.readFloat("mass", DEFAULT_MASS);
        color = (ColorRGBA) capsule.readSavable("color", DEFAULT_COLOR);
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(offset, "offsetMS", DEFAULT_OFFSET);
        capsule.write(size, "size", DEFAULT_SIZE);
        capsule.write(spin, "spin", DEFAULT_SPIN);
        capsule.write(mass, "mass", DEFAULT_MASS);
        capsule.write(color, "color", DEFAULT_COLOR);
    }
    
    private static String convColorToHex(ColorRGBA color) {
        if (color == null)
            return null;
        String sRed = Integer.toHexString((int)(color.r*255+.5f));
        if (sRed.length() == 1)
            sRed = "0" + sRed;
        String sGreen = Integer.toHexString((int)(color.g*255+.5f));
        if (sGreen.length() == 1)
            sGreen = "0" + sGreen;
        String sBlue = Integer.toHexString((int)(color.b*255+.5f));
        if (sBlue.length() == 1)
            sBlue = "0" + sBlue;
        return "#" + sRed + sGreen + sBlue;
    }

    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        if (offset > 0) {
            builder.append("prev+");
            builder.append((int)(offset*100));
            builder.append("% age...");
        }
        if (color != DEFAULT_COLOR) {
            builder.append("  color:");
            builder.append(convColorToHex(color).toUpperCase());
            builder.append(" a: ");
            builder.append((int)(color.a*100));
            builder.append("%");
        }

        if (size != DEFAULT_SIZE) {
            builder.append("  size: "+size);
        }

        if (mass != DEFAULT_MASS) {
            builder.append("  mass: "+spin);
        }

        if (spin != DEFAULT_SPIN) {
            builder.append("  spin: "+spin);
        }
        
        return builder.toString();
    }
}
