// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCLError.java

package bcedit.BCL;


public class BCLError
{

    public BCLError()
    {
    }

    public static final String getErrorString(int n)
    {
        if(n == 255)
            return new String("finished");
        if(n == 254)
            return new String("wrongfile");
        if(n >= 0 && n <= 22)
            return errString[n];
        else
            return new String("undefined");
    }

    public static final int noerr = 0;
    public static final int unknowntoken = 1;
    public static final int datawithouttoken = 2;
    public static final int argumentmissing = 3;
    public static final int wrongdevice = 4;
    public static final int wrongrevision = 5;
    public static final int missingrevision = 6;
    public static final int internal = 7;
    public static final int modemissing = 8;
    public static final int baditemindex = 9;
    public static final int notanumber = 10;
    public static final int valoutofrange = 11;
    public static final int invalidargument = 12;
    public static final int invalidcommand = 13;
    public static final int wrongnumberofargs = 14;
    public static final int toomuchdata = 15;
    public static final int alreadydefined = 16;
    public static final int presetmissing = 17;
    public static final int presettoocomplex = 18;
    public static final int wrongpreset = 19;
    public static final int presettoonew = 20;
    public static final int presetcheck = 21;
    public static final int sequence = 22;
    public static final int wrongcontext = 23;
    public static final int wrongfile = 254;
    public static final int finished = 255;
    public static final String errString[] = {
        "OK", "unknown token", "data without token", "argument missing", "wrong device", "wrong revision", "missing revision", "internal", "mode missing", "bad item index", 
        "not a number", "value out of range", "invalid argument", "invalid command", "wrong number of arguments", "too much data", "already defined", "preset missing", "preset too complex", "wrong preset", 
        "preset to onew", "preset check", "sequence error", "wrong context"
    };

}
