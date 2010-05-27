package com.jmex.effects.particles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

public class TexAnimation implements Savable {

    protected ArrayList<AnimationEntry> entries = new ArrayList<AnimationEntry>();

    public void addEntry(AnimationEntry entry) {
        entries.add(entry);
    }

    public void addEntry(int index, AnimationEntry entry) {
        entries.add(index, entry);
    }

    public void clearEntries() {
        entries.clear();
    }

    public Iterator<AnimationEntry> getEntries() {
        return entries.iterator();
    }

    public void removeEntry(AnimationEntry entry) {
        entries.remove(entry);
    }

    public void removeEntry(int index) {
        entries.remove(index);
    }

    public int getTexIndexAtAge(float age, float maxAge, ParticleSystem particles) {
        // find what AnimationEntry we last passed...
        float trAge = 0, lastAge = 0;
        AnimationEntry latest = null;
        maxAge /= 1000f;
        age /= 1000f;
        for (int i = 0; i < entries.size(); i++) {
            AnimationEntry entry = entries.get(i);
            trAge += (entry.getOffset() * maxAge);
            if (trAge <= age) {
                latest = entry;
                lastAge = trAge;
            } else {
                break;
            }
        }
        
        if (latest == null) {
            return particles.getStartTexIndex();
        } else {
            int index = (int)((age - lastAge) / latest.rate);
            index %= latest.frames.length;
            return latest.frames[index];
        }
    }

    public Class getClassTag() {
        return getClass();
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        entries = capsule.readSavableArrayList("entries", null);
        if (entries == null) {
            entries = new ArrayList<AnimationEntry>();
        }
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.writeSavableArrayList(entries, "entries", null);
    }

}
