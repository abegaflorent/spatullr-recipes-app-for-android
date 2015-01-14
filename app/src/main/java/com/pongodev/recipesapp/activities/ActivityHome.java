package com.pongodev.recipesapp.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.fragments.FragmentCategories;
import com.pongodev.recipesapp.fragments.FragmentRecipes;
import com.pongodev.recipesapp.utils.Utils;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ActivityDetail} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link FragmentCategories} and the item details
 * (if present) is a {@link FragmentCategories}.
 * <p>
 * This activity also implements the required
 * {@link FragmentCategories.OnCategorySelectedListener} interface
 * to listen for item selections.
 */
public class ActivityHome extends ActionBarActivity
        implements FragmentCategories.OnCategorySelectedListener, FragmentRecipes.OnRecipeSelectedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up the action bar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_logo);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuSearch:
                        return true;
                    case R.id.menuFavorites:
                        Intent favoritesIntent = new Intent(getApplicationContext(), ActivityFavorites.class);
                        startActivity(favoritesIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    case R.id.menuAbout:
                        // Open About screen.
                        Intent aboutIntent = new Intent(getApplicationContext(), ActivityAbout.class);
                        startActivity(aboutIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    default:
                        return true;
                }
                //return true;
            }
        });



        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((FragmentCategories) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link FragmentCategories.OnCategorySelectedListener}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onCategorySelected(String ID, String CategoryName) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putString(Utils.ARG_KEY, ID);
            arguments.putString(Utils.ARG_PAGE, Utils.ARG_CATEGORY);
            FragmentRecipes fragment = new FragmentRecipes();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent recipesIntent = new Intent(this, ActivityRecipes.class);
            recipesIntent.putExtra(Utils.ARG_KEY, ID);
            recipesIntent.putExtra(Utils.ARG_PAGE, Utils.ARG_CATEGORY);
            recipesIntent.putExtra(Utils.ARG_CATEGORY, CategoryName);
            startActivity(recipesIntent);
            overridePendingTransition(R.anim.open_next, R.anim.close_main);
        }
    }

    @Override
    public void onRecipeSelected(String ID, String CategoryName) {
        Intent detailIntent = new Intent(this, ActivityDetail.class);
        detailIntent.putExtra(Utils.ARG_KEY, ID);
        //detailIntent.putExtra(Utils.ARG_PARENT_ACTIVITY, Utils.ARG_ACTIVITY_HOME);
        startActivity(detailIntent.setClass(this, ActivityDetail.class));
        overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        // get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        // assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // do not iconify the widget; expand it by default
        searchView.setIconifiedByDefault(false);

        return true;
    }

}
