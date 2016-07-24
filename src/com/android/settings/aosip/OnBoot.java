package com.android.settings.aosip;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.utils.CMDProcessor;
import com.android.settings.R;

import java.io.IOException;
import java.util.List;

public class OnBoot extends BroadcastReceiver {

    Context settingsContext = null;
    private static final String TAG = "SettingsOnBoot";
    Boolean mSetupRunning = false;

    protected int getMetricsCategory() {
        return MetricsLogger.OWLSNEST;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean mSelinuxCmdline = false;
        Boolean mSelinuxSwitch = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            if(procInfos.get(i).processName.equals("com.google.android.setupwizard")) {
                mSetupRunning = true;
            }
        }
        if(!mSetupRunning) {
            try {
                settingsContext = context.createPackageContext("com.android.settings", 0);
            } catch (Exception e) {
                Log.e(TAG, "Package not found", e);
            }

            if (CMDProcessor.runShellCommand("getenforce").getStdout().contains("Enforcing")) {
                mSelinuxCmdline = true;
                Log.d(TAG, "cmdline: selinux:Enforcing");
            } else {
                mSelinuxCmdline = false;
                Log.d(TAG, "cmdline: selinux:Permissive");
            }

            SharedPreferences sharedpreferences = settingsContext.getSharedPreferences("com.android.settings_preferences",
                    Context.MODE_PRIVATE);

            if (sharedpreferences.contains("selinux")) {
                mSelinuxSwitch = sharedpreferences.getBoolean("selinux", false);
                if(mSelinuxSwitch == true) {
                    if (mSelinuxCmdline == false) {
                        Log.d(TAG, "Setting selinux to Enforcing");
                        CMDProcessor.runShellCommand("echo 1 > /sys/fs/selinux/enforce");
                    }
                } else if (mSelinuxSwitch == false) {
                    if (mSelinuxCmdline == true) {
                        Log.d(TAG, "Setting selinux to Permissive");
                        CMDProcessor.runShellCommand("echo 0 > /sys/fs/selinux/enforce");
                        showToast(context.getString(R.string.selinux_permissive_toast_title), context);
                    }
                }
            } else {
                Log.d(TAG, "SharedPreference do not exists");
            }
        }
    }

    private void showToast(String toastString, Context context) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT)
                .show();
    }
}
