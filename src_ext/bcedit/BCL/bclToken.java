// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   bclToken.java

package bcedit.BCL;


// Referenced classes of package bcedit.BCL:
//            NamedDefine

public class bclToken extends NamedDefine
{

    public bclToken(int id, String string)
    {
        this.id = id;
        this.string = string;
    }

    public int getid()
    {
        return id;
    }

    public String getstring()
    {
        return string;
    }

    public void setid(int id)
    {
        this.id = id;
    }

    public void setstring(String string)
    {
        this.string = string;
    }
}
