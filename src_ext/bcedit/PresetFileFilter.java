// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import java.io.File;
import java.io.FilenameFilter;

class PresetFileFilter
    implements FilenameFilter
{

    public PresetFileFilter(String dirname)
    {
        startpath = null;
        startpath = dirname;
    }

    public boolean accept(File dir, String filename)
    {
        boolean retval = false;
        if(filename.endsWith(".bcp"))
            retval = true;
        return retval;
    }

    public String startpath;
}
