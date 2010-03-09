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

public final class SelectionReport implements java.lang.Cloneable, java.io.Serializable
{
    public String pointerId;

    public SelectionGroup[] selectionGroups;

    public SelectionReport()
    {
    }

    public SelectionReport(String pointerId, SelectionGroup[] selectionGroups)
    {
        this.pointerId = pointerId;
        this.selectionGroups = selectionGroups;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        SelectionReport _r = null;
        try
        {
            _r = (SelectionReport)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(pointerId != _r.pointerId && pointerId != null && !pointerId.equals(_r.pointerId))
            {
                return false;
            }
            if(!java.util.Arrays.equals(selectionGroups, _r.selectionGroups))
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
        if(pointerId != null)
        {
            __h = 5 * __h + pointerId.hashCode();
        }
        if(selectionGroups != null)
        {
            for(int __i0 = 0; __i0 < selectionGroups.length; __i0++)
            {
                if(selectionGroups[__i0] != null)
                {
                    __h = 5 * __h + selectionGroups[__i0].hashCode();
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
        __os.writeString(pointerId);
        SelectionGroupListHelper.write(__os, selectionGroups);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        pointerId = __is.readString();
        selectionGroups = SelectionGroupListHelper.read(__is);
    }
}
