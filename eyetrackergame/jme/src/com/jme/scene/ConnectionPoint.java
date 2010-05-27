package com.jme.scene;

import java.io.IOException;

import com.jme.animation.Bone;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;

public class ConnectionPoint extends Node {
    private static final long serialVersionUID = -3767376526385942925L;
    private Bone target;
    
    public ConnectionPoint() {
    }
    
    public ConnectionPoint(String name, Bone target) {
        super(name);
        this.target = target;
    }
    
    public void updateWorldData(float time) {
        updateWorldVectors();
        
        if (children != null)
        for (int i = 0; i < children.size(); i++) {
            Spatial child = children.get(i);
            if (child != null) {
                child.updateGeometricState(time, false);
            }
        }
    }
    
    public void updateWorldVectors() {
        if(target == null) {
            return;
        }
        if (((lockedMode & Spatial.LOCKED_TRANSFORMS) == 0)) {
            worldScale.set(parent.getWorldScale()).multLocal(target.getWorldScale());
            parent.getWorldRotation().mult(target.getWorldRotation(), worldRotation);
            worldTranslation = parent.localToWorld( target.getWorldTranslation(), worldTranslation );
        }
    }
    
    @Override
    public void read(JMEImporter im) throws IOException {
        super.read(im);
        target = (Bone)im.getCapsule(this).readSavable("target", null);
    }
    
    @Override
    public void write(JMEExporter ex) throws IOException {
        super.write(ex);
        ex.getCapsule(this).write(target, "target", null);
    }
}