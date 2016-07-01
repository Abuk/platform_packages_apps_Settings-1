/*
 * Copyright (C) 2015 DarkKat
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

package com.android.settings.aosip.statusbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException; 
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class IconsCategory extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_COLOR =
            "notification_icons_color";
    private static final String PREF_COLOR_DARK =
            "notification_icons_color_dark_mode";
    private static final String PREF_SIGNAL =
            "network_status_icons_signal_color";
    private static final String PREF_SIGNAL_DARK =
            "network_status_icons_signal_color_dark_mode";
    private static final String PREF_NO_SIM =
            "network_status_icons_no_sim_color";
    private static final String PREF_NO_SIM_DARK =
            "network_status_icons_no_sim_color_dark_mode";
    private static final String PREF_AIRPLANE_MODE =
            "network_status_icons_airplane_mode_color";
    private static final String PREF_AIRPLANE_MODE_DARK =
            "network_status_icons_airplane_mode_color_dark_mode";
    private static final String PREF_STATUS =
            "network_status_icons_status_color";
    private static final String PREF_STATUS_DARK =
            "network_status_icons_status_color_dark_mode";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";

    private static final int WHITE                  = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT        = 0xff33b5e5;
    private static final int RED_500                = 0xfff44336;
    private static final int BLACK_TRANSLUCENT      = 0x99000000;
    private static final int RED_900_TRANSLUCENT    = 0x99b71c1c;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mSignal;
    private ColorPickerPreference mSignalDark;
    private ColorPickerPreference mNoSim;
    private ColorPickerPreference mNoSimDark;
    private ColorPickerPreference mAirplaneMode;
    private ColorPickerPreference mAirplaneModeDark;
    private ColorPickerPreference mStatus;
    private ColorPickerPreference mStatusDark;
    private ColorPickerPreference mColor;
    private ColorPickerPreference mColorDark;
    private SwitchPreference mStatusBarBrightnessControl;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.aosip_icons);

        final ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarBrightnessControl = (SwitchPreference) findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setOnPreferenceChangeListener(this);
        int statusBarBrightnessControl = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_BRIGHTNESS_CONTROL, 0);
        mStatusBarBrightnessControl.setChecked(statusBarBrightnessControl != 0);
        try {
            if (Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_brightness_control_info);
            }
        } catch (SettingNotFoundException e) {
        }
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mSignal =
                (ColorPickerPreference) findPreference(PREF_SIGNAL);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR,
                WHITE); 
        mSignal.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mSignal.setSummary(hexColor);
        mSignal.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mSignal.setOnPreferenceChangeListener(this);

        mSignalDark =
                (ColorPickerPreference) findPreference(PREF_SIGNAL_DARK);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR_DARK_MODE,
                BLACK_TRANSLUCENT); 
        mSignalDark.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mSignalDark.setSummary(hexColor);
        mSignalDark.setDefaultColors(BLACK_TRANSLUCENT, BLACK_TRANSLUCENT);
        mSignalDark.setOnPreferenceChangeListener(this);

        mNoSim =
                (ColorPickerPreference) findPreference(PREF_NO_SIM);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR,
                WHITE); 
        mNoSim.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNoSim.setSummary(hexColor);
        mNoSim.setDefaultColors(WHITE, RED_500);
        mNoSim.setOnPreferenceChangeListener(this);

        mNoSimDark =
                (ColorPickerPreference) findPreference(PREF_NO_SIM_DARK);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR_DARK_MODE,
                BLACK_TRANSLUCENT); 
        mNoSimDark.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNoSimDark.setSummary(hexColor);
        mNoSimDark.setDefaultColors(BLACK_TRANSLUCENT, RED_900_TRANSLUCENT);
        mNoSimDark.setOnPreferenceChangeListener(this);

        mAirplaneMode =
                (ColorPickerPreference) findPreference(PREF_AIRPLANE_MODE);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
                WHITE); 
        mAirplaneMode.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAirplaneMode.setSummary(hexColor);
        mAirplaneMode.setDefaultColors(WHITE, RED_500);
        mAirplaneMode.setOnPreferenceChangeListener(this);

        mAirplaneModeDark =
                (ColorPickerPreference) findPreference(PREF_AIRPLANE_MODE_DARK);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR_DARK_MODE,
                BLACK_TRANSLUCENT); 
        mAirplaneModeDark.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAirplaneModeDark.setSummary(hexColor);
        mAirplaneModeDark.setDefaultColors(BLACK_TRANSLUCENT, RED_900_TRANSLUCENT);
        mAirplaneModeDark.setOnPreferenceChangeListener(this);

        mStatus =
                (ColorPickerPreference) findPreference(PREF_STATUS);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_STATUS_ICONS_COLOR,
                WHITE); 
        mStatus.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatus.setSummary(hexColor);
        mStatus.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mStatus.setOnPreferenceChangeListener(this);

        mStatusDark =
                (ColorPickerPreference) findPreference(PREF_STATUS_DARK);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_STATUS_ICONS_COLOR_DARK_MODE,
                BLACK_TRANSLUCENT); 
        mStatusDark.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusDark.setSummary(hexColor);
        mStatusDark.setDefaultColors(BLACK_TRANSLUCENT, BLACK_TRANSLUCENT);
        mStatusDark.setOnPreferenceChangeListener(this);

        mColor =
                (ColorPickerPreference) findPreference(PREF_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR,
                WHITE); 
        mColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mColor.setSummary(hexColor);
        mColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
        mColor.setOnPreferenceChangeListener(this);

        mColorDark =
                (ColorPickerPreference) findPreference(PREF_COLOR_DARK);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR_DARK_MODE,
                BLACK_TRANSLUCENT); 
        mColorDark.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mColorDark.setSummary(hexColor);
        mColorDark.setDefaultColors(BLACK_TRANSLUCENT, BLACK_TRANSLUCENT);
        mColorDark.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mColorDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSignal) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSignalDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNoSim) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNoSimDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mAirplaneMode) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mAirplaneModeDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStatus) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_STATUS_ICONS_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStatusDark) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_STATUS_ICONS_COLOR_DARK_MODE,
                    intHex);
            preference.setSummary(hex);
          } else if (preference == mStatusBarBrightnessControl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), STATUS_BAR_BRIGHTNESS_CONTROL,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        IconsCategory getOwner() {
            return (IconsCategory) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_colors_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_STATUS_ICONS_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_STATUS_ICONS_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_aosip,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_SIGNAL_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR,
                                    RED_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_NO_SIM_COLOR_DARK_MODE,
                                    RED_900_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR,
                                    RED_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NETWORK_ICONS_AIRPLANE_MODE_COLOR_DARK_MODE,
                                    RED_900_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_STATUS_ICONS_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_STATUS_ICONS_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR,
                                    HOLO_BLUE_LIGHT);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_NOTIFICATION_ICONS_COLOR_DARK_MODE,
                                    BLACK_TRANSLUCENT);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.OWLSNEST;
    }
}


