package codeforgvl.com.trolley_tracker_client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class IconFactory {
    public static BitmapDescriptor getCustomMarker(Context context, Icon icon){
        return getCustomMarker(context, icon, 1, Color.BLACK);
    }
    public static BitmapDescriptor getCustomMarker(Context context, Icon icon, double scale){
        return getCustomMarker(context, icon, scale, Color.BLACK);
    }
    public static BitmapDescriptor getCustomMarker(Context context, Icon icon, double scale, int color){
        IconDrawable id = new IconDrawable(context, icon).actionBarSize();
        id.color(color);
        Drawable d = id.getCurrent();
        Bitmap bm = Bitmap.createBitmap(id.getIntrinsicWidth(), id.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        if(scale != 1)
            bm = Bitmap.createScaledBitmap(bm, id.getIntrinsicWidth(), id.getIntrinsicHeight(), false);
        Canvas c = new Canvas(bm);

        d.draw(c);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }
}
