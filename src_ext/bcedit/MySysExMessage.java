// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import java.util.NoSuchElementException;
import java.util.Vector;

class MySysExMessage
{

    public MySysExMessage()
    {
        lastUpdate = -1L;
        lastUpdateDelta = -1L;
        msgs = new Vector();
    }

    public void reset()
    {
        if(msgs.size() != 0)
            msgs.clear();
        lastUpdateDelta = -1L;
        lastUpdate = -1L;
    }

    public synchronized void add(Object o)
    {
        msgs.add(o);
        if(lastUpdate == -1L)
        {
            lastUpdate = System.currentTimeMillis();
            lastUpdateDelta = 0L;
        } else
        {
            long l = System.currentTimeMillis();
            lastUpdateDelta = l - lastUpdate;
            lastUpdate = l;
        }
    }

    public synchronized Object peekLast()
    {
        return msgs.lastElement();
//        NoSuchElementException e;
//        e;
//        return null;
    }

    public synchronized Object peekNext()
    {
    	try {
    		return msgs.firstElement();
    	}
    	catch (NoSuchElementException e) {
    		return null;
    	}
//        NoSuchElementException e;
//        e;
//        return null;
    }

    public synchronized Object getNext()
    {
    	try {
    		return msgs.remove(0);
    	} catch ( java.lang.ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
//        ArrayIndexOutOfBoundsException e;
//        e;
//        return null;
    }

    public synchronized int length()
    {
        return msgs.size();
    }

    public synchronized long getLastUpdateTime()
    {
        return lastUpdate;
    }

    private Vector msgs;
    long lastUpdate;
    long lastUpdateDelta;
}
