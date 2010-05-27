package com.jme.util.export;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import com.jme.util.export.StringIntMap;
import com.jme.util.export.Savable;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.xml.XMLExporter;
import com.jme.util.export.xml.XMLImporter;

/**
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class SavableALATest {
    public SavableALATest() {}

    @org.junit.Test
    public void trivial() throws IOException {
        SAL sal = new SAL();
        ArrayList<StringIntMap> all;
        StringIntMap sim;
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        sim = new StringIntMap(); sim.put("two", 2);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        File f = File.createTempFile(
                getClass().getName().replaceFirst(".*\\.", ""), "-jme.xml");
        try {
            XMLExporter.getInstance().save(sal, f);
            assertEquals("Failed to restore simple 0/1/2/3 list.  See '"
                    + f.getAbsolutePath() + "'", sal,
                    XMLImporter.getInstance().load(f));
            f.delete(); // Only remove if this test succeeded.
                        // Very useful to retain if the test failed
        } catch (IOException ioe) {
            fail("Failed to restore simple 0/1/2/3 list.  See '"
                    + f.getAbsolutePath() + "':  " + ioe);
        }
    }

    @org.junit.Test
    public void dups() throws IOException {
        SAL sal1 = new SAL();
        ArrayList<StringIntMap> all;
        StringIntMap sim;
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("two", 2);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        SAL sal2 = new SAL();
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("two", 2);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        File f = File.createTempFile(
                getClass().getName().replaceFirst(".*\\.", ""), "-jme.xml");
        try {
            XMLExporter.getInstance().save(sal1, f);
            assertEquals("Failed to restore exact dups.  See '"
                    + f.getAbsolutePath() + "'", sal2,
                    XMLImporter.getInstance().load(f));
            f.delete(); // Only remove if this test succeeded.
                        // Very useful to retain if the test failed
        } catch (IOException ioe) {
            fail("Failed to restore exact dups.  See '"
                    + f.getAbsolutePath() + "':  " + ioe);
        }
    }

    @org.junit.Test
    public void reordered() throws IOException {
        SAL sal1 = new SAL();
        ArrayList<StringIntMap> all;
        StringIntMap sim;
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("two", 2);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        SAL sal2 = new SAL();
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("two", 2);  // CHANGE OF SEQ. HERE!
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        File f = File.createTempFile(
                getClass().getName().replaceFirst(".*\\.", ""), "-jme.xml");
        try {
            XMLExporter.getInstance().save(sal1, f);
            assertFalse("Failed to restore exact dups.  See '"
                    + f.getAbsolutePath() + "'", sal2.equals(
                    XMLImporter.getInstance().load(f)));
            f.delete(); // Only remove if this test succeeded.
                        // Very useful to retain if the test failed
        } catch (IOException ioe) {
            fail("Failed to restore exact dups.  See '"
                    + f.getAbsolutePath() + "':  " + ioe);
        }
    }

    @org.junit.Test
    public void differ() throws IOException {
        SAL sal1 = new SAL();
        ArrayList<StringIntMap> all;
        StringIntMap sim;
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("two", 2);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal1.add(all);
        SAL sal2 = new SAL();
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("oNe", 1);  // DIFF HERE!
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("two", 2);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal2.add(all);
        File f = File.createTempFile(
                getClass().getName().replaceFirst(".*\\.", ""), "-jme.xml");
        try {
            XMLExporter.getInstance().save(sal1, f);
            assertFalse("Failed to restore exact dups.  See '"
                    + f.getAbsolutePath() + "'", sal2.equals(
                    XMLImporter.getInstance().load(f)));
            f.delete(); // Only remove if this test succeeded.
                        // Very useful to retain if the test failed
        } catch (IOException ioe) {
            fail("Failed to restore exact dups.  See '"
                    + f.getAbsolutePath() + "':  " + ioe);
        }
    }

    @org.junit.Test
    public void withNull() throws IOException {
        SAL sal = new SAL();
        ArrayList<StringIntMap> all;
        StringIntMap sim;
        sim = new StringIntMap(); sim.put("zero", 0);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        sim = new StringIntMap(); sim.put("one", 1);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        sal.add(null);  // NULL HERE!
        sim = new StringIntMap(); sim.put("three", 3);
        all = new ArrayList<StringIntMap>(); all.add(sim);
        sal.add(all);
        File f = File.createTempFile(
                getClass().getName().replaceFirst(".*\\.", ""), "-jme.xml");
        try {
            XMLExporter.getInstance().save(sal, f);
            assertEquals("Failed to restore list with null element.  See '"
                    + f.getAbsolutePath() + "'", sal,
                    XMLImporter.getInstance().load(f));
            f.delete(); // Only remove if this test succeeded.
                        // Very useful to retain if the test failed
        } catch (IOException ioe) {
            fail("Failed to restore list with null element.  See '"
                    + f.getAbsolutePath() + "':  " + ioe);
        }
    }

    @SuppressWarnings("serial")
    static public class SAL extends ArrayList<ArrayList<StringIntMap>>
            implements Savable {
        public void read(JMEImporter e) throws IOException {
            InputCapsule cap = e.getCapsule(this);
            //System.err.println("I m reading " + cap.readString("lst", null));
            @SuppressWarnings("unchecked")
            ArrayList<StringIntMap>[] inList = (ArrayList<StringIntMap>[])
                    cap.readSavableArrayListArray("lst", null);
            System.err.println("I m reading " + inList);
            addAll(Arrays.asList(inList));
        }

        public void write(JMEExporter e) throws IOException {
            OutputCapsule cap = e.getCapsule(this);
            cap.writeSavableArrayListArray(toArray(new ArrayList[0]), "lst", null);
            //cap.write("helo", "h", null);
        }

        public Class<?> getClassTag() {
            return this.getClass();
        }
    }
}
