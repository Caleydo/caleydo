package com.jme.util.export.binary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

public class Foo implements Savable {
    private static final Logger logger = Logger.getLogger(Foo.class.getName());
    
    public int x = 0;
    public Bar y = null;
    public Bar z = null;
    
    public Foo() {
        
    }
    
    public void write(JMEExporter e) {
        try {
            e.getCapsule(this).write(x, "x", 0);
            e.getCapsule(this).write(y, "y", null);
            e.getCapsule(this).write(z, "z", null);
        } catch (IOException ex) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "write(JMEExporter e)", "Exception", ex);
        }
    }

    public void read(JMEImporter e) {
        try {
            x = e.getCapsule(this).readInt("x", 0);
            y = (Bar)e.getCapsule(this).readSavable("y", null);
            z = (Bar)e.getCapsule(this).readSavable("z", null);
            
        } catch (IOException ex) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "read(JMEImporter e)", "Exception", ex);
        }
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}