package com.codeforgvl.trolleytrackerclient;

import android.support.v4.app.FragmentManager;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractPartial;

/**
 * Created by Adam on 12/30/2015.
 */
public class Utils {
    public static DateTime rollForwardWith(ReadableInstant now, AbstractPartial lp) {
        DateTime dt = lp.toDateTime(now);
        while (dt.isBefore(now)) {
            dt = dt.withFieldAdded(lp.getFieldTypes()[0].getRangeDurationType(), 1);
        }
        return dt;
    }

    public static String getActiveFragmentName(FragmentManager m){
        int stackSize = m.getBackStackEntryCount();
        if(stackSize > 0) {
            return m.getBackStackEntryAt(stackSize - 1).getName();
        }
        else{
            return null;
        }
    }
}
