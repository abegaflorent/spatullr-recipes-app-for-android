package com.pongodev.recipesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.fragments.FragmentRecipes;
import com.pongodev.recipesapp.utils.Utils;

/**
 * Created by taufanerfiyanto on 11/14/14.
 */
public class ActivityRecipes extends ActionBarActivity implements FragmentRecipes.OnRecipeSelectedListener {

    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);


        // Show the Up button in the action bar.
        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_favorite_white_36dp);
        toolbar.setTitle(getIntent().getStringExtra(Utils.ARG_CATEGORY));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Intent i = getIntent();
            String key = i.getStringExtra(Utils.ARG_KEY);
            String activePage = i.getStringExtra(Utils.ARG_PAGE);
            Bundle arguments = new Bundle();
            arguments.putString(Utils.ARG_KEY, key);
            arguments.putString(Utils.ARG_PAGE, activePage);
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
