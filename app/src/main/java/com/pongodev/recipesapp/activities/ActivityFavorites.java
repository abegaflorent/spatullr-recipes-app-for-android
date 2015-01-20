package com.pongodev.recipesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.fragments.FragmentRecipes;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.Utils;

import java.io.IOException;

public class ActivityFavorites extends ActionBarActivity implements FragmentRecipes.OnRecipeSelectedListener {

    // Create instance of database helper.
    DBHelperFavorites dbhelperFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create object of database helper.
        dbhelperFavorites = new DBHelperFavorites(this);

        // Create favorites database.
        try {
            dbhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open favorites database
        dbhelperFavorites.openDataBase();

        if (savedInstanceState == null) {
            // Create the recipes fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Utils.ARG_PAGE, Utils.ARG_FAVORITES);
            FragmentRecipes fragment = new FragmentRecipes();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        // Handle item menu in toolbar.
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case android.R.id.home:
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onRecipeSelected(String ID, String CategoryName) {
        // Call Detail screen and passing recipe id to that screen.
        Intent detailIntent = new Intent(getApplicationContext(), ActivityDetail.class);
        detailIntent.putExtra(Utils.ARG_KEY, ID);
        detailIntent.putExtra(Utils.ARG_PARENT_ACTIVITY, Utils.ARG_ACTIVITY_FAVORITES);
        startActivity(detailIntent);
        overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbhelperFavorites.close();
    }

    // Add transition when back button pressed.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-create recipes fragment when back to Favorites screen.
        Bundle arguments = new Bundle();
        arguments.putString(Utils.ARG_PAGE, Utils.ARG_FAVORITES);
        FragmentRecipes fragment = new FragmentRecipes();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
