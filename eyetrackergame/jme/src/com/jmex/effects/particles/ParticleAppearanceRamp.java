package com.jmex.effects.particles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

public class ParticleAppearanceRamp implements Savable {

    protected ArrayList<RampEntry> entries = new ArrayList<RampEntry>();

    public void addEntry(RampEntry entry) {
        entries.add(entry);
    }

    public void addEntry(int index, RampEntry entry) {
        entries.add(index, entry);
    }

    public void clearEntries() {
        entries.clear();
    }

    public Iterator<RampEntry> getEntries() {
        return entries.iterator();
    }

    public void removeEntry(RampEntry entry) {
        entries.remove(entry);
    }

    public void removeEntry(int index) {
        entries.remove(index);
    }

    public void getValuesAtAge(float age, float maxAge, ColorRGBA store,
            float[] fStore, ParticleSystem particles) {
        float prevCAge = 0, prevMAge = 0, prevSiAge = 0, prevSpAge = 0;
        float nextCAge = maxAge, nextMAge = maxAge, nextSiAge = maxAge, nextSpAge = maxAge;
        float trAge = 0;
        RampEntry prevCEntry = null, prevMEntry = null, prevSiEntry = null, prevSpEntry = null;
        RampEntry nextCEntry = null, nextMEntry = null, nextSiEntry = null, nextSpEntry = null;
        for (int i = 0; i < entries.size(); i++) {
            RampEntry entry = entries.get(i);
            trAge += entry.getOffset() * maxAge;
            // Color
            if (nextCEntry == null) {
                if (trAge > age) {
                    if (entry.hasColorSet()) {
                        nextCAge = trAge;
                        nextCEntry = entry;
                    }
                } else {
                    if (entry.hasColorSet()) {
                        prevCAge = trAge;
                        prevCEntry = entry;
                    }
                }
            }

            // mass
            if (nextMEntry == null) {
                if (trAge > age) {
                    if (entry.hasMassSet()) {
                        nextMAge = trAge;
                        nextMEntry = entry;
                    }
                } else {
                    if (entry.hasMassSet()) {
                        prevMAge = trAge;
                        prevMEntry = entry;
                    }
                }
            }

            // size
            if (nextSiEntry == null) {
                if (trAge > age) {
                    if (entry.hasSizeSet()) {
                        nextSiAge = trAge;
                        nextSiEntry = entry;
                    }
                } else {
                    if (entry.hasSizeSet()) {
                        prevSiAge = trAge;
                        prevSiEntry = entry;
                    }
                }
            }

            // spin
            if (nextSpEntry == null) {
                if (trAge > age) {
                    if (entry.hasSpinSet()) {
                        nextSpAge = trAge;
                        nextSpEntry = entry;
                    }
                } else {
                    if (entry.hasSpinSet()) {
                        prevSpAge = trAge;
                        prevSpEntry = entry;
                    }
                }
            }

        }

        // color
        {
            float lifeCRatio = (age - prevCAge) / (nextCAge - prevCAge);
            ColorRGBA start = prevCEntry != null ? prevCEntry.getColor()
                    : particles.getStartColor();
            ColorRGBA end = nextCEntry != null ? nextCEntry.getColor()
                    : particles.getEndColor();
            store.interpolate(start, end, lifeCRatio);
        }

        // mass
        {
            float lifeMRatio = (age - prevMAge) / (nextMAge - prevMAge);
            float start = prevMEntry != null ? prevMEntry.getMass() : particles
                    .getStartMass();
            float end = nextMEntry != null ? nextMEntry.getMass() : particles
                    .getEndMass();
            fStore[Particle.VAL_CURRENT_MASS] = (1 - lifeMRatio) * start
                    + lifeMRatio * end;
        }

        // Size
        {
            float lifeSiRatio = (age - prevSiAge) / (nextSiAge - prevSiAge);
            float start = prevSiEntry != null ? prevSiEntry.getSize()
                    : particles.getStartSize();
            float end = nextSiEntry != null ? nextSiEntry.getSize() : particles
                    .getEndSize();
            fStore[Particle.VAL_CURRENT_SIZE] = (1 - lifeSiRatio) * start
                    + lifeSiRatio * end;
        }

        // Spin
        {
            float lifeSpRatio = (age - prevSpAge) / (nextSpAge - prevSpAge);
            float start = prevSpEntry != null ? prevSpEntry.getSpin()
                    : particles.getStartSpin();
            float end = nextSpEntry != null ? nextSpEntry.getSpin() : particles
                    .getEndSpin();
            fStore[Particle.VAL_CURRENT_SPIN] = (1 - lifeSpRatio) * start
                    + lifeSpRatio * end;
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
            entries = new ArrayList<RampEntry>();
        }
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.writeSavableArrayList(entries, "entries", null);
    }

}
