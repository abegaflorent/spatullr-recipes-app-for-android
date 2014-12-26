package com.pongodev.recipesapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.pongodev.recipesapp.R;


public class FragmentAbout extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    Preference prefTellFriend, prefRate, prefReview;


    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupSimplePreferencesScreen();

        prefTellFriend = (Preference) findPreference(getString(R.string.pref_key_tell_friend));
        prefRate = (Preference) findPreference(getString(R.string.pref_key_rate_app));
        prefReview = (Preference) findPreference(getString(R.string.pref_key_review));

        prefTellFriend.setOnPreferenceClickListener(this);
        prefRate.setOnPreferenceClickListener(this);
        prefReview.setOnPreferenceClickListener(this);



    }


    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_about);

    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals(getString(R.string.pref_key_tell_friend))){
            Intent iShare = new Intent(Intent.ACTION_SEND);
            iShare.setType("text/plain");
            iShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
            iShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.message)+" "+getString(R.string.google_play_url));
            startActivity(Intent.createChooser(iShare, getString(R.string.share)));
        }else if(key.equals(getString(R.string.pref_key_rate_app)) || key.equals(getString(R.string.pref_key_review))){
            Intent iRate = new Intent(Intent.ACTION_VIEW);
            iRate.setData(Uri.parse(getString(R.string.google_play_url)));
            startActivity(iRate);
        }
        return true;
    }

}
