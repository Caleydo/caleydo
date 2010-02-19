// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package VIS;

public final class Color4f implements java.lang.Cloneable, java.io.Serializable
{
    public float r;

    public float g;

    public float b;

    public float a;

    public Color4f()
    {
    }

    public Color4f(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Color4f _r = null;
        try
        {
            _r = (Color4f)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(r != _r.r)
            {
                return false;
            }
            if(g != _r.g)
            {
                return false;
            }
            if(b != _r.b)
            {
                return false;
            }
            if(a != _r.a)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        __h = 5 * __h + java.lang.Float.floatToIntBits(r);
        __h = 5 * __h + java.lang.Float.floatToIntBits(g);
        __h = 5 * __h + java.lang.Float.floatToIntBits(b);
        __h = 5 * __h + java.lang.Float.floatToIntBits(a);
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeFloat(r);
        __os.writeFloat(g);
        __os.writeFloat(b);
        __os.writeFloat(a);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        r = __is.readFloat();
        g = __is.readFloat();
        b = __is.readFloat();
        a = __is.readFloat();
    }
}
