package com.pongodev.recipesapp.suggestion;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by taufanerfiyanto on 11/14/14.
 */
public class ProviderSuggestion extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.pongodev.recipesapp.suggestion.ProviderSuggestion";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ProviderSuggestion(){
        setupSuggestions(AUTHORITY, MODE);
    }

}
