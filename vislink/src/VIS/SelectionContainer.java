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

public final class SelectionContainer implements java.lang.Cloneable, java.io.Serializable
{
    public int id;

    public int x;

    public int y;

    public int w;

    public int h;

    public Color4f color;

    public SelectionContainer()
    {
    }

    public SelectionContainer(int id, int x, int y, int w, int h, Color4f color)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        SelectionContainer _r = null;
        try
        {
            _r = (SelectionContainer)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(id != _r.id)
            {
                return false;
            }
            if(x != _r.x)
            {
                return false;
            }
            if(y != _r.y)
            {
                return false;
            }
            if(w != _r.w)
            {
                return false;
            }
            if(h != _r.h)
            {
                return false;
            }
            if(color != _r.color && color != null && !color.equals(_r.color))
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
        __h = 5 * __h + id;
        __h = 5 * __h + x;
        __h = 5 * __h + y;
        __h = 5 * __h + w;
        __h = 5 * __h + h;
        if(color != null)
        {
            __h = 5 * __h + color.hashCode();
        }
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
        __os.writeInt(id);
        __os.writeInt(x);
        __os.writeInt(y);
        __os.writeInt(w);
        __os.writeInt(h);
        color.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        id = __is.readInt();
        x = __is.readInt();
        y = __is.readInt();
        w = __is.readInt();
        h = __is.readInt();
        color = new Color4f();
        color.__read(__is);
    }
}
