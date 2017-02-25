package com.codeforgvl.trolleytrackerclient;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractPartial;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codeforgvl.trolleytrackerclient.activities.MainActivity;


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
            return MainActivity.MAP_FRAGMENT_TAG;
        }
    }

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission, boolean finishActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Display a dialog with rationale.
            Utils.RationaleDialog.newInstance(requestId, finishActivity)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);

        }
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     * <p>
     * The activity should implement
     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
     * to handle permit or denial of this permission request.
     */
    public static class RationaleDialog extends DialogFragment {

        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        private boolean mFinishActivity = false;

        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the location
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param requestCode    Id of the request that is used to request the permission. It is
         *                       returned to the
         *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
         * @param finishActivity Whether the calling Activity should be finished if the dialog is
         *                       cancelled.
         */
        public static RationaleDialog newInstance(int requestCode, boolean finishActivity) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
            mFinishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);

            return new MaterialDialog.Builder(getContext())
                    .content(R.string.permission_rationale_location)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            // After click on Ok, request the permission.
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    requestCode);
                            // Do not finish the Activity while requesting permission.
                            mFinishActivity = false;
                        }
                    }).build();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            Activity mActivity = getActivity();
            if (mActivity != null && mFinishActivity) {
                Toast.makeText(mActivity,
                        R.string.permission_required_toast,
                        Toast.LENGTH_SHORT)
                        .show();
                mActivity.finish();
            }
        }
    }

    /**
     * When inside a nested fragment and Activity gets recreated due to reasons like orientation
     * change, {@link android.support.v4.app.Fragment#getActivity()} returns old Activity but the top
     * level parent fragment's {@link android.support.v4.app.Fragment#getActivity()} returns current,
     * recreated Activity. Hence use this method in nested fragments instead of
     * android.support.v4.app.Fragment#getActivity()
     *
     * @param fragment
     *  The current nested Fragment
     *
     * @return current Activity that fragment is hosted in
     */
    public static Activity getActivity(Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment.getActivity();
    }
}
