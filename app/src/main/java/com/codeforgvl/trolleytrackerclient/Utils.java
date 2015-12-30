package com.codeforgvl.trolleytrackerclient;

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
}
