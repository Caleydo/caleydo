package com.jme.util.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestByteUtils extends junit.framework.TestCase {
    private static final Logger logger = Logger.getLogger(TestByteUtils.class
            .getName());

    public void testaConvert() {

    }

    public void testBytes() {
        byte[] byteArray = ByteUtils.convertToBytes(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

        try {
            boolean result = ByteUtils.readBoolean(inputStream);
            assertEquals(true, result);
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "testRead()", "Exception", e);
            fail(e.toString());
        }
    }

    public void testWrite() {

    }
}
