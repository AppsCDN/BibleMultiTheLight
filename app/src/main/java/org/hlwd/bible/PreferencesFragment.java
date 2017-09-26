
package org.hlwd.bible;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment
{
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final Preference prefThemeName = (Preference) findPreference("THEME_NAME");
        if (prefThemeName != null)
        {
            prefThemeName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    final String themeName = o.toString();
                    PCommon.SetThemeName(preference.getContext(), themeName);

                    final Intent returnIntent = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);

                    return true;
                }
            });
        }

        final Preference prefLayoutColumn = (Preference) findPreference("LAYOUT_COLUMN");
        if (prefLayoutColumn != null) {
            prefLayoutColumn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    final Intent returnIntent = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);

                    return true;
                }
            });
        }
    }
}