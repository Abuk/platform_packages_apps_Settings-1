/*
 * Copyright (C) 2015 Android Open Source Illusion Project
 * Copyright (C) 2013 The ChameleonOS Project
 * Copyright (C) 2016 The Dirty Unicorns project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.aosip.gesture;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.Gravity;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.utils.du.ActionHandler;
import com.android.internal.utils.du.Config;
import com.android.internal.utils.du.Config.ActionConfig;
import com.android.internal.utils.du.Config.ButtonConfig;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.preference.ActionPreference;
import com.android.settings.custom.nav.ActionFragment;
import com.android.settings.aosip.seekbar.SeekBarPreference;

public class GestureCategory extends ActionFragment implements OnPreferenceChangeListener {

    private static final String TAG = "Gestures";

    private static final String KEY_ENABLED = "gesture_anywhere_enabled";
    private static final String KEY_POSITION = "gesture_anywhere_position";
    private static final String KEY_GESTURES = "gesture_anywhere_gestures";
    private static final String KEY_TRIGGER_WIDTH = "gesture_anywhere_trigger_width";
    private static final String KEY_TRIGGER_TOP = "gesture_anywhere_trigger_top";
    private static final String KEY_TRIGGER_BOTTOM = "gesture_anywhere_trigger_bottom";

    private ListPreference mPositionPref;
    private SeekBarPreference mTriggerWidthPref;
    private SeekBarPreference mTriggerTopPref;
    private SeekBarPreference mTriggerBottomPref;
    private SwitchPreference mEnabledPref;

    private CharSequence mPreviousTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.aosip_gesture);

        PreferenceScreen prefSet = getPreferenceScreen();

        mEnabledPref = (SwitchPreference) findPreference(KEY_ENABLED);
        mEnabledPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.GESTURE_ANYWHERE_ENABLED, 0) == 1));
        mEnabledPref.setOnPreferenceChangeListener(this);

        mPositionPref = (ListPreference) prefSet.findPreference(KEY_POSITION);
        mPositionPref.setOnPreferenceChangeListener(this);
        int position = Settings.System.getInt(getContentResolver(),
                Settings.System.GESTURE_ANYWHERE_POSITION, Gravity.LEFT);
        mPositionPref.setValue(String.valueOf(position));
        updatePositionSummary(position);

        mTriggerWidthPref = (SeekBarPreference) findPreference(KEY_TRIGGER_WIDTH);
        mTriggerWidthPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.GESTURE_ANYWHERE_TRIGGER_WIDTH, 40));
        mTriggerWidthPref.setOnPreferenceChangeListener(this);

        mTriggerTopPref = (SeekBarPreference) findPreference(KEY_TRIGGER_TOP);
        mTriggerTopPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.GESTURE_ANYWHERE_TRIGGER_TOP, 0));
        mTriggerTopPref.setOnPreferenceChangeListener(this);

        mTriggerBottomPref = (SeekBarPreference) findPreference(KEY_TRIGGER_BOTTOM);
        mTriggerBottomPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.GESTURE_ANYWHERE_TRIGGER_HEIGHT, 100));
        mTriggerBottomPref.setOnPreferenceChangeListener(this);

        Preference pref = findPreference(KEY_GESTURES);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), GestureAnywhereBuilderActivity.class));
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        final ActionBar bar = getActivity().getActionBar();
        mPreviousTitle = bar.getTitle();
        bar.setTitle(R.string.gesture_category);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getActionBar().setTitle(mPreviousTitle);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPositionPref) {
            int position = Integer.valueOf((String) newValue);
            updatePositionSummary(position);
            return true;
        } else if (preference == mEnabledPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.GESTURE_ANYWHERE_ENABLED,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference == mTriggerWidthPref) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.GESTURE_ANYWHERE_TRIGGER_WIDTH, width);
            return true;
        } else if (preference == mTriggerTopPref) {
            int top = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.GESTURE_ANYWHERE_TRIGGER_TOP, top);
            return true;
        } else if (preference == mTriggerBottomPref) {
            int bottom = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.GESTURE_ANYWHERE_TRIGGER_HEIGHT, bottom);
            return true;
        }
        return false;
    }

    private void updatePositionSummary(int value) {
        mPositionPref.setSummary(mPositionPref.getEntries()[mPositionPref.findIndexOfValue("" + value)]);
        Settings.System.putInt(getContentResolver(),
                Settings.System.GESTURE_ANYWHERE_POSITION, value);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.OWLSNEST;
    }
}
