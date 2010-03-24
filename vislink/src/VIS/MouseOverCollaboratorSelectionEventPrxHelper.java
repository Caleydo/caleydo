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

public final class MouseOverCollaboratorSelectionEventPrxHelper extends Ice.ObjectPrxHelperBase implements MouseOverCollaboratorSelectionEventPrx
{
    public static MouseOverCollaboratorSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        MouseOverCollaboratorSelectionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MouseOverCollaboratorSelectionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::MouseOverCollaboratorSelectionEvent"))
                {
                    MouseOverCollaboratorSelectionEventPrxHelper __h = new MouseOverCollaboratorSelectionEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MouseOverCollaboratorSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        MouseOverCollaboratorSelectionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MouseOverCollaboratorSelectionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::MouseOverCollaboratorSelectionEvent", __ctx))
                {
                    MouseOverCollaboratorSelectionEventPrxHelper __h = new MouseOverCollaboratorSelectionEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MouseOverCollaboratorSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MouseOverCollaboratorSelectionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::MouseOverCollaboratorSelectionEvent"))
                {
                    MouseOverCollaboratorSelectionEventPrxHelper __h = new MouseOverCollaboratorSelectionEventPrxHelper();
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

    public static MouseOverCollaboratorSelectionEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        MouseOverCollaboratorSelectionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::MouseOverCollaboratorSelectionEvent", __ctx))
                {
                    MouseOverCollaboratorSelectionEventPrxHelper __h = new MouseOverCollaboratorSelectionEventPrxHelper();
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

    public static MouseOverCollaboratorSelectionEventPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        MouseOverCollaboratorSelectionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MouseOverCollaboratorSelectionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                MouseOverCollaboratorSelectionEventPrxHelper __h = new MouseOverCollaboratorSelectionEventPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static MouseOverCollaboratorSelectionEventPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MouseOverCollaboratorSelectionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            MouseOverCollaboratorSelectionEventPrxHelper __h = new MouseOverCollaboratorSelectionEventPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _MouseOverCollaboratorSelectionEventDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _MouseOverCollaboratorSelectionEventDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, MouseOverCollaboratorSelectionEventPrx v)
    {
        __os.writeProxy(v);
    }

    public static MouseOverCollaboratorSelectionEventPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            MouseOverCollaboratorSelectionEventPrxHelper result = new MouseOverCollaboratorSelectionEventPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
