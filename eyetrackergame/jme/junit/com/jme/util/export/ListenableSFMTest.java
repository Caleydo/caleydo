package com.jme.util.export;

import static org.junit.Assert.*;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.jme.util.export.xml.XMLExporter;
import com.jme.util.export.xml.XMLImporter;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;

/**
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class ListenableSFMTest {
    public ListenableSFMTest() {}

    static private class CountingFloatListener
            implements ListenableStringFloatMap.FloatListener {
        public int count;
        public void floatChanged(StringFloatMap sfm) {
            count++;
        }
        public void reset() {
            count = 0;
        }
    }

    @org.junit.Test
    public void puts() {
        ListenableStringFloatMap lsfm = new ListenableStringFloatMap();
        CountingFloatListener listener1 = new CountingFloatListener();
        CountingFloatListener listener2 = new CountingFloatListener();

        lsfm.put("alpha", 11f);
        lsfm.put("beta", 12f);
        lsfm.put("alpha", 13f);

        lsfm.addListener(listener1, Arrays.asList("beta", "gamma"));
        lsfm.addListener(listener1, Arrays.asList("gamma", "delta"));

        //System.err.println(lsfm.listenerReport());

        assertEquals("Sanity pre-valiation failed", 0, listener1.count);
        assertEquals("Sanity pre-valiation failed", 0, listener2.count);

        lsfm.put("beta", 11f);
        lsfm.put("beta", 12f);
        lsfm.put("gamma", 13f);
        lsfm.put("delta", 13f);
        lsfm.put("epsilon", 13f);
        lsfm.put("epsilon", 13f);

        assertEquals(4, listener1.count);
        assertEquals(0, listener2.count);

        lsfm.removeListener(listener1);
        listener1.reset(); listener2.reset();
        lsfm.addListener(listener1, Arrays.asList("beta", "gamma"));
        lsfm.addListener(listener2, Arrays.asList("gamma", "delta"));

        assertEquals("Reset failed", 0, listener1.count);
        assertEquals("Reset feailed", 0, listener1.count);

        lsfm.put("beta", 11f);
        lsfm.put("beta", 12f);
        lsfm.put("gamma", 13f);
        lsfm.put("delta", 13f);
        lsfm.put("epsilon", 13f);
        lsfm.put("epsilon", 13f);

        assertEquals(3, listener1.count);
        assertEquals(2, listener2.count);
    }

    /**
     * For now, this test only tests persistence of the Map data, not of
     * any listeners registered.
     */
    @org.junit.Test
    public void xmlPersistence() throws IOException {
        ListenableStringFloatMap lsfmOut = new ListenableStringFloatMap();
        lsfmOut.put("delta", 13f);
        lsfmOut.put("epsilon", 13f);
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream =
                new PipedInputStream(pipedOutputStream);
        XMLExporter.getInstance().save(lsfmOut, pipedOutputStream);
        pipedOutputStream.flush();
        pipedOutputStream.close();
        Object objectIn = XMLImporter.getInstance().load(pipedInputStream);
        assertEquals("Wrong class read back in from XML",
                ListenableStringFloatMap.class, objectIn.getClass());
        ListenableStringFloatMap lsfmIn =  (ListenableStringFloatMap) objectIn;
        assertEquals("Stored map != map restored from XML", lsfmOut, lsfmIn);
    }

    /**
     * For now, this test only tests persistence of the Map data, not of
     * any listeners registered.
     */
    @org.junit.Test
    public void binaryPersistence() throws IOException {
        ListenableStringFloatMap lsfmOut = new ListenableStringFloatMap();
        lsfmOut.put("delta", 13f);
        lsfmOut.put("epsilon", 13f);
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream =
                new PipedInputStream(pipedOutputStream);
        BinaryExporter.getInstance().save(lsfmOut, pipedOutputStream);
        pipedOutputStream.flush();
        pipedOutputStream.close();
        Object objectIn = BinaryImporter.getInstance().load(pipedInputStream);
        assertEquals("Wrong class read back in from Binary",
                ListenableStringFloatMap.class, objectIn.getClass());
        ListenableStringFloatMap lsfmIn =  (ListenableStringFloatMap) objectIn;
        assertEquals("Stored map != map restored from Binary", lsfmOut, lsfmIn);
    }
}
