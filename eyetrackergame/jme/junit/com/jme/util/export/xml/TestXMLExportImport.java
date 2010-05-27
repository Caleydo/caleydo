package com.jme.util.export.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.util.export.binary.Bar;
import com.jme.util.export.binary.Foo;

public class TestXMLExportImport extends junit.framework.TestCase {
    private static final Logger logger = Logger
            .getLogger(TestXMLExportImport.class.getName());

    public void testSimpleSavable() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        Foo f = new Foo();
        try {
            f.x = 8;
            Bar y = new Bar();
            y.f = 7.5f;
            y.g = 9.32423f;

            Bar z = y;

            f.y = y;
            f.z = z;
            XMLExporter.getInstance().save(f, bos);
        } catch (IOException e) {
            logger.logp(Level.SEVERE, TestXMLExportImport.class.toString(),
                    "main(args)", "Exception", e);
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        try {
            Foo j = (Foo) XMLImporter.getInstance().load(bis);
            
            assertEquals("x", 8, j.x);

            assertTrue("y", j.y instanceof Bar);
            assertEquals("y", 7.5f, j.y.f, FastMath.FLT_EPSILON);
            assertEquals("y", 9.32423f, j.y.g, FastMath.FLT_EPSILON);

            assertTrue("z", j.y instanceof Bar);
            assertEquals("z", 7.5f, j.z.f, FastMath.FLT_EPSILON);
            assertEquals("z", 9.32423f, j.z.g, FastMath.FLT_EPSILON);
            
            assertEquals("y == z", j.y, j.z);
        } catch (IOException e) {
            logger.logp(Level.SEVERE, TestXMLExportImport.class.toString(),
                    "main(args)", "Exception", e);
        }
    }
}
