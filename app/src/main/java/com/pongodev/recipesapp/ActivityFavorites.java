package com.pongodev.recipesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.pongodev.recipesapp.fragments.FragmentFavorites;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.Utils;

import java.io.IOException;

/**
 * Created by taufanerfiyanto on 11/14/14.
 */
public class ActivityFavorites extends ActionBarActivity implements FragmentFavorites.OnRecipeSelectedListener {

    private String keyword;
    DBHelperFavorites dbhelperFavorites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // Show the Up button in the action bar.
        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_favorite_white_36dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbhelperFavorites = new DBHelperFavorites(this);

        try {
            dbhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        dbhelperFavorites.openDataBase();

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Utils.ARG_PAGE, Utils.ARG_FAVORITES);
            FragmentFavorites fragment = new FragmentFavorites();
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
    public void onRecipeSelected(String ID, String Action) {

            // Display confirm dialog
            final String id = ID;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.actions);
            builder.setItems(R.array.actions,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which){
                        case 0:
                            Intent detailIntent = new Intent();
                            detailIntent.putExtra(Utils.ARG_KEY, id);
                            detailIntent.putExtra(Utils.ARG_PARENT_ACTIVITY, Utils.ARG_ACTIVITY_FAVORITES);
                            startActivity(detailIntent.setClass(getApplicationContext(), ActivityDetail.class));
                            overridePendingTransition(R.anim.open_next, R.anim.close_main);
                            break;
                        case 1:
                            boolean result = dbhelperFavorites.deleteRecipeFromFavorites(id);
                            if(result) {
                                Toast.makeText(getApplicationContext(), R.string.success_remove, Toast.LENGTH_SHORT).show();

                                Bundle arguments = new Bundle();
                                arguments.putString(Utils.ARG_PAGE, Utils.ARG_FAVORITES);
                                FragmentFavorites fragment = new FragmentFavorites();
                                fragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.item_container, fragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .commit();
                                dialog.dismiss();

                            }
                            break;
                    }
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbhelperFavorites.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
