package com.pongodev.recipesapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.pongodev.recipesapp.R;

/**
 * Created by taufanerfiyanto on 12/19/14.
 */
public class Utils {

    // Application parameters. do not change this parameters.
    public static final String ARG_PAGE = "page";
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_SEARCH = "search";
    public static final String ARG_FAVORITES = "favorites";
    public static final String ARG_KEY = "key";
    public static final String ARG_POSITION = "position";
    public static final String ARG_TAG_CONTENT = "CONTENT_HERE";
    public static final String ARG_COOK_TIME = "cook_time";
    public static final String ARG_SERVINGS = "servings";
    public static final String ARG_SUMMARY = "summary";
    public static final String ARG_INFO = "info";
    public static final String ARG_PARENT_ACTIVITY = "parent_activity";
    public static final String ARG_ACTIVITY_HOME = "activities.ActivityHome";
    public static final String ARG_ACTIVITY_SEARCH = "activities.ActivitySearch";
    public static final String ARG_ACTIVITY_FAVORITES = "activities.ActivityFavorites";
    public static final String ARG_TRIGGER = "trigger";
    public static final int ARG_GONE = 8;

    // Configurable parameters. you can configure these parameter.
    // For every recipe detail you want to display interstitial ad
    public static final int ARG_TRIGGER_VALUE = 3;
    // Admob visibility parameter. set 0 to show admob and 8 to hide.
    public static final int ARG_ADMOB_VISIBILITY = 0;


    public static void loadAdmob(final AdView ad){
        final AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        ad.loadAd(adRequest);

        ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (ad != null) {
                    ad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static boolean admobVisibility(AdView ad, int parameter){
        ad.setVisibility(parameter);
        if(parameter == ARG_GONE )
            return false;
        else
            return true;
    }

    public static void loadAdmobInterstitial(final InterstitialAd interstitialAd, Context c){

        interstitialAd.setAdUnitId(c.getResources().getString(R.string.interstitial_ad_unit_id));
        // Create an ad request.
        // Remove addTestDevice() method when you ready to publish the app.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad
        interstitialAd.loadAd(adRequest);



        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {

            }

        });

    }

    // Method to load map type setting
    public static int loadPreferences(String param, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        int value = sharedPreferences.getInt(param, 0);


        return value;
    }

    // Method to save map type setting to SharedPreferences
    public static void savePreferences(String param, int value, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(param, value);
        editor.commit();
    }
}
