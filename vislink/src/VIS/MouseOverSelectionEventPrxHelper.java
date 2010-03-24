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

public final class MouseOverSelectionEventPrxHelper extends Ice.ObjectPrxHelperBase implements MouseOverSelectionEventPrx
{
    public static MouseOverSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        MouseOverSelectionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MouseOverSelectionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::MouseOverSelectionEvent"))
                {
                    MouseOverSelectionEventPrxHelper __h = new MouseOverSelectionEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MouseOverSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        MouseOverSelectionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MouseOverSelectionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::MouseOverSelectionEvent", __ctx))
                {
                    MouseOverSelectionEventPrxHelper __h = new MouseOverSelectionEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MouseOverSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MouseOverSelectionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::MouseOverSelectionEvent"))
                {
                    MouseOverSelectionEventPrxHelper __h = new MouseOverSelectionEventPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static MouseOverSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        MouseOverSelectionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::MouseOverSelectionEvent", __ctx))
                {
                    MouseOverSelectionEventPrxHelper __h = new MouseOverSelectionEventPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static MouseOverSelectionEventPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        MouseOverSelectionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MouseOverSelectionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                MouseOverSelectionEventPrxHelper __h = new MouseOverSelectionEventPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static MouseOverSelectionEventPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MouseOverSelectionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            MouseOverSelectionEventPrxHelper __h = new MouseOverSelectionEventPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _MouseOverSelectionEventDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _MouseOverSelectionEventDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, MouseOverSelectionEventPrx v)
    {
        __os.writeProxy(v);
    }

    public static MouseOverSelectionEventPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            MouseOverSelectionEventPrxHelper result = new MouseOverSelectionEventPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
