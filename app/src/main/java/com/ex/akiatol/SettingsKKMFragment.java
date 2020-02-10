package com.ex.akiatol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.preference.*;
//import com.atol.drivers.fptr.Fptr;
//import com.atol.drivers.fptr.IFptr;
//import com.atol.drivers.fptr.settings.SettingsActivity;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;
import static com.ex.akiatol.Const.FPTR_PREFERENCES;

/**
 * Created by Leo on 2019-07-22.
 */
public class SettingsKKMFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private static final int REQUEST_FPTR_SETTINGS = 1011;

    private PreferenceCategory kkm_category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_screen_kkm);

        final SharedPreferences prefs = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);

        findPreference("pref_key_kkm_settings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openFprtSettings();
                return false;
            }

        });

        findPreference("prefs_kkm_debug").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (((SwitchPreference) preference).isChecked()) {

                    final CustomAlertDialog dialog = new CustomAlertDialog(getActivity());
                    dialog.setMyTitle(getString(R.string.debug_mode_info));
                    dialog.setButtonText("Понятно", "");
                    dialog.setOnClickPositiveButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }

                return false;
            }

        });

        findPreference("pref_key_kkm_send_log").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String message = "Аккаунт - " + prefs.getString(getContext().getString(R.string.prefs_mail), "") + ".\n" +
                        "Наименование устройства - " + prefs.getString(getContext().getString(R.string.prefs_name), "") + ".\n" +
                        "Версия приложения - " + getString(R.string.version_name);

                boolean isAtol10 = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

                String PACKAGE_NAME = getContext().getPackageName();

                String filename;

                String appPath;
                File extDir = getContext().getExternalFilesDir(null);
                if (extDir != null){
                    appPath = extDir.getPath();
                } else {
                    appPath = "android/data/" + PACKAGE_NAME + "/files";
                }

                if (isAtol10)
                    filename = appPath + "/drivers10/logs/fptr10.log";
                else
                    filename = appPath + "/drivers9/fptr_log.txt";

                File filelocation = new File(filename);
                //Uri path = Uri.fromFile(filelocation);
                Uri path = FileProvider.getUriForFile(
                        getContext(),
                        getContext().getApplicationContext().getPackageName() + ".provider",
                        filelocation);

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"support@akitorg.ru"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Логи ККМ из приложения " + getApplicationName(getContext()));
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (filelocation.exists()) {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(getContext(), "Файл не найден", Toast.LENGTH_LONG).show();
                }

                return false;
            }

        });

        String user = prefs.getString(getContext().getString(R.string.prefs_user_name), "");
        findPreference("prefs_user_name").setSummary(user);

        String inn = prefs.getString(getContext().getString(R.string.prefs_user_inn), "");
        findPreference("prefs_user_inn").setSummary(inn);

        kkm_category = ((PreferenceCategory) findPreference("pref_key_kkm_sett"));
        if (!((SwitchPreference) findPreference("prefs_kkm_empty_mail")).isChecked())
            kkm_category.removePreference(findPreference ("prefs_kkm_default_mail"));
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {}

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean(getString(R.string.prefs_kkm_use_10_driver), true);
        editor.apply();
    }

    private void openFprtSettings() {

        boolean isAtol10 = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

        if (getContext() == null)
            return;

        if (isAtol10) {

            String settings = getContext().getSharedPreferences(FPTR_PREFERENCES, Context.MODE_PRIVATE)
                    .getString(ru.atol.drivers10.fptr.settings.SettingsActivity.DEVICE_SETTINGS, null);

            if (settings == null) {

                ru.atol.drivers10.fptr.Fptr fprint = new ru.atol.drivers10.fptr.Fptr(getContext());
                settings = fprint.getSettings();
                fprint.destroy();
            }


            Intent intent = new Intent(getContext(), ru.atol.drivers10.fptr.settings.SettingsActivity.class);
            intent.putExtra(ru.atol.drivers10.fptr.settings.SettingsActivity.DEVICE_SETTINGS, settings);
            startActivityForResult(intent, REQUEST_FPTR_SETTINGS);

        } else {

//            String settings = getContext().getSharedPreferences(FPTR_PREFERENCES, Context.MODE_PRIVATE)
//                    .getString(SettingsActivity.DEVICE_SETTINGS, null);
//
//            if (settings == null) {
//
//                IFptr fprint = new Fptr();
//                fprint.create(getContext());
//
//                settings = fprint.get_DeviceSettings();
//                fprint.destroy();
//            }
//
//            Intent intent = new Intent(getContext(), SettingsActivity.class);
//            intent.putExtra(SettingsActivity.DEVICE_SETTINGS, settings);
//            startActivityForResult(intent, REQUEST_FPTR_SETTINGS);

        }

    } // openFprtSettings

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {

            case "prefs_kkm_empty_mail":

                if (((SwitchPreference) findPreference("prefs_kkm_empty_mail")).isChecked()) {
                    EditTextPreference default_mail = new EditTextPreference(getContext());
                    default_mail.setTitle("Указать e-mail");
                    default_mail.setKey(getResources().getString(R.string.prefs_kkm_default_mail));
                    kkm_category.addPreference(default_mail);
                } else
                    kkm_category.removePreference(findPreference("prefs_kkm_default_mail"));

                break;
            case "prefs_user_name": {

                Preference connectionPref = findPreference(key);
                String name = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("prefs_user_name", "");

                if (name.equals("")) {
                    Toast.makeText(getActivity(), R.string.name_cant_be_null, Toast.LENGTH_SHORT).show();
                    return;
                }

                connectionPref.setSummary(name);
                SharedPreferences.Editor p_editor = getActivity().getSharedPreferences("prefs", MODE_PRIVATE).edit();
                p_editor.putString("prefs_user_name", name);
                p_editor.apply();

                break;
            }
            case "prefs_user_inn": {

                Preference connectionPref = findPreference(key);
                String name = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("prefs_user_inn", "");

                connectionPref.setSummary(name);
                SharedPreferences.Editor p_editor = getActivity().getSharedPreferences("prefs", MODE_PRIVATE).edit();
                p_editor.putString("prefs_user_inn", name);
                p_editor.apply();

                break;
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_FPTR_SETTINGS) {

            if (data != null && data.getExtras() != null && getContext() != null) {

                SharedPreferences.Editor editor = getContext().getSharedPreferences(FPTR_PREFERENCES, Context.MODE_PRIVATE).edit();

                boolean isAtol10 = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getBoolean(getString(R.string.prefs_kkm_use_10_driver), true);

                if (isAtol10)
                    editor.putString( ru.atol.drivers10.fptr.settings.SettingsActivity.DEVICE_SETTINGS,
                            data.getExtras().getString(ru.atol.drivers10.fptr.settings.SettingsActivity.DEVICE_SETTINGS));
//                else
//                    editor.putString( SettingsActivity.DEVICE_SETTINGS,
//                            data.getExtras().getString(SettingsActivity.DEVICE_SETTINGS));

                editor.apply();

            }

        }

    }
}
