package com.pongodev.recipesapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.fragments.FragmentRecipes;
import com.pongodev.recipesapp.suggestion.ProviderSuggestion;
import com.pongodev.recipesapp.utils.Utils;

/**
 * Created by taufanerfiyanto on 11/14/14.
 */
public class ActivitySearch extends ActionBarActivity implements FragmentRecipes.OnRecipeSelectedListener {

    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            keyword = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    ProviderSuggestion.AUTHORITY, ProviderSuggestion.MODE);
            suggestions.saveRecentQuery(keyword, null);
        }
        setContentView(R.layout.activity_search);


        // Show the Up button in the action bar.
        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_favorite_white_36dp);
        toolbar.setTitle(keyword);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Utils.ARG_KEY, keyword);
            arguments.putString(Utils.ARG_PAGE, Utils.ARG_SEARCH);
            FragmentRecipes fragment = new FragmentRecipes();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case android.R.id.home:
                        finish();
                        overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        return true;
                    default:
                        return true;
                }
                //return true;
            }
        });
    }

    @Override
    public void onRecipeSelected(String ID, String CategoryName) {
        Intent detailIntent = new Intent();
        detailIntent.putExtra(Utils.ARG_KEY, ID);
        startActivity(detailIntent.setClass(this, ActivityDetail.class));
        overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
