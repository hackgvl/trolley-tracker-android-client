package com.codeforgvl.trolleytrackerclient.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.codeforgvl.trolleytrackerclient.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;

import java.util.HashMap;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class IconFactory {
    private static HashMap<Integer, BitmapDescriptor> stopIcons = new HashMap<>();
    public static BitmapDescriptor getStopIcon(Context context, int color){
        if(stopIcons.containsKey(color)){
            return stopIcons.get(color);
        }

        Drawable d = ContextCompat.getDrawable(context, R.drawable.gmap_stop);
        Drawable m = ContextCompat.getDrawable(context, R.drawable.trolley_gmap_stop_mask);

        m.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        int width = d.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = d.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        m.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        m.draw(canvas);

        BitmapDescriptor stopIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        stopIcons.put(color, stopIcon);
        return stopIcon;
    }

    private static HashMap<Integer, BitmapDescriptor> trolleyIcons = new HashMap<>();
    public static BitmapDescriptor getTrolleyIcon(Context context, int color){
        if(trolleyIcons.containsKey(color)){
            return trolleyIcons.get(color);
        }

        Drawable d = ContextCompat.getDrawable(context, R.drawable.marker_border);
        Drawable m = ContextCompat.getDrawable(context, R.drawable.marker_mask);

        m.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        int width = d.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = d.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        m.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        m.draw(canvas);

        BitmapDescriptor trolleyIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        trolleyIcons.put(color, trolleyIcon);
        return trolleyIcon;
    }
}
