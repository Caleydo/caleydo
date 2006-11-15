// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCSettings.java

package bcedit;


class MySettings
{

    public MySettings()
    {
        init(null, ((String) (null)));
    }

    public MySettings(String k, String v)
    {
        init(k, v);
    }

    public MySettings(String k, boolean v)
    {
        init(k, v);
    }

    public MySettings(String k, double v)
    {
        init(k, v);
    }

    public MySettings(String k, int v)
    {
        init(k, v);
    }

    public MySettings(String k, long v)
    {
        init(k, v);
    }

    public synchronized void init(String k, double v)
    {
        key = new String(k);
        value = Double.toString(v);
    }

    public synchronized void init(String k, boolean v)
    {
        key = new String(k);
        value = Boolean.toString(v);
    }

    public synchronized void init(String k, long v)
    {
        key = new String(k);
        value = Long.toString(v);
    }

    public synchronized void init(String k, int v)
    {
        key = new String(k);
        value = Integer.toString(v);
    }

    public synchronized void init(String k, String v)
    {
        key = new String(k);
        value = new String(v);
    }

    String key;
    String value;
}
