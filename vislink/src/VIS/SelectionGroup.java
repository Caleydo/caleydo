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

public final class SelectionGroup implements java.lang.Cloneable, java.io.Serializable
{
    public int containerID;

    public Selection[] selections;

    public SelectionGroup()
    {
    }

    public SelectionGroup(int containerID, Selection[] selections)
    {
        this.containerID = containerID;
        this.selections = selections;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        SelectionGroup _r = null;
        try
        {
            _r = (SelectionGroup)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(containerID != _r.containerID)
            {
                return false;
            }
            if(!java.util.Arrays.equals(selections, _r.selections))
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
        __h = 5 * __h + containerID;
        if(selections != null)
        {
            for(int __i0 = 0; __i0 < selections.length; __i0++)
            {
                if(selections[__i0] != null)
                {
                    __h = 5 * __h + selections[__i0].hashCode();
                }
            }
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
        __os.writeInt(containerID);
        SelectionListHelper.write(__os, selections);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        containerID = __is.readInt();
        selections = SelectionListHelper.read(__is);
    }
}
