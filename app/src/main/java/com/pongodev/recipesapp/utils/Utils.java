package com.pongodev.recipesapp.utils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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



    // Admob visibility parameter. set 0 to show admob and 8 to hide.
    public static final int ARG_ADMOB_VISIBILITY = 0;

    public static final int ARG_GONE = 8;
    public static final int ARG_VISIBLE = 0;

    public static void loadAdmob(AdView ad)
    {
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        ad.loadAd(adRequest);
    }

    public static boolean admobVisibility(AdView ad, int parameter){
        ad.setVisibility(parameter);
        if(parameter == ARG_GONE )
            return false;
        else
            return true;
    }
}
