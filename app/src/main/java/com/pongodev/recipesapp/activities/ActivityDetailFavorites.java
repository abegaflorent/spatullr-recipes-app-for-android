package com.pongodev.recipesapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.adapters.AdapterDetailPager;
import com.pongodev.recipesapp.fragments.FragmentInfo;
import com.pongodev.recipesapp.fragments.FragmentSummary;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.DBHelperRecipes;
import com.pongodev.recipesapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ActivityDetailFavorites extends ActionBarActivity {

    ImageView imgRecipe;
    TextView txtRecipeName, txtCategory;

    LinearLayout lytDetail, lytTitle;
    ProgressBar prgLoading;
    ButtonFloat btnUnfavorite;
    AdView adView;
    PagerSlidingTabStrip tabs;
    ViewPager pager;

    String selectedId;
    public static String time, minutes, serveFor, persons;
    public static  String recipeId, categoryId, categoryName, recipeName, cookTime, servings, summary, ingredients, steps, recipeImage;


    DBHelperRecipes dbhelperRecipes;
    DBHelperFavorites dbhelperFavorites;


    InterstitialAd interstitialAd;

    AdapterDetailPager adapterDetailPager;

    List<Fragment> pagerFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_favorites);

        time = getResources().getString(R.string.cook_time);
        minutes = getResources().getString(R.string.minutes);
        serveFor = getResources().getString(R.string.serve_for);
        persons = getResources().getString(R.string.persons);

        Intent i = getIntent();
        selectedId = i.getStringExtra(Utils.ARG_KEY);

        Log.d("recipe id",selectedId);
        // Show the Up button in the action bar.
        // Set up the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_favorite_white_36dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        lytDetail = (LinearLayout) findViewById(R.id.lytDetail);
        lytTitle = (LinearLayout) findViewById(R.id.lytTitle);
        imgRecipe = (ImageView) findViewById(R.id.imgRecipe);
        txtRecipeName = (TextView) findViewById(R.id.txtRecipeName);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        btnUnfavorite = (ButtonFloat) findViewById(R.id.btnUnfavorite);
        adView = (AdView) findViewById(R.id.adView);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);



        interstitialAd = new InterstitialAd(this);
        boolean isAdmobVisible = Utils.admobVisibility(adView, Utils.ARG_ADMOB_VISIBILITY);
        if(isAdmobVisible) {
            Utils.loadAdmob(adView);

            int interstitialTrigger = Utils.loadPreferences(Utils.ARG_TRIGGER, this);
            Log.d(Utils.ARG_TRIGGER, interstitialTrigger+"");
            if(interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                Utils.loadAdmobInterstitial(interstitialAd, this);
                Utils.savePreferences(Utils.ARG_TRIGGER, 0, this);
            }else{
                Utils.savePreferences(Utils.ARG_TRIGGER, (interstitialTrigger+1), this);
            }
        }




        dbhelperRecipes = new DBHelperRecipes(this);
        dbhelperFavorites = new DBHelperFavorites(this);

        try {
            dbhelperRecipes.createDataBase();
            dbhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        dbhelperRecipes.openDataBase();
        dbhelperFavorites.openDataBase();


        new syncGetData().execute();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.menuShare:
                        createShareIntent();
                        return true;
                    case android.R.id.home:
                        finish();
                        overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        return true;
                    default:
                        return true;
                }

            }
        });



        btnUnfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivityDetailFavorites.this)
                        .title(R.string.confirm)
                        .content(R.string.confirm_message)
                        .positiveText(R.string.remove)
                        .negativeText(R.string.cancel)
                        .positiveColorRes(R.color.color_primary)
                        .negativeColorRes(R.color.color_primary)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                boolean result = dbhelperFavorites.deleteRecipeFromFavorites(selectedId);
                                if (result) {
                                    Toast.makeText(getApplicationContext(), R.string.success_remove, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                }
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();


            }
        });

    }

    public class syncGetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            lytDetail.setVisibility(View.GONE);
            lytTitle.setVisibility(View.GONE);
            btnUnfavorite.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromDatabase(selectedId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int image = getResources().getIdentifier(recipeImage, "drawable", getPackageName());

            Picasso.with(getApplicationContext())
                    .load(image)
                    .into(imgRecipe);


            createPager();

            prgLoading.setVisibility(View.GONE);
            lytDetail.setVisibility(View.VISIBLE);
            lytTitle.setVisibility(View.VISIBLE);
            btnUnfavorite.setVisibility(View.VISIBLE);

        }
    }

    public void createPager(){
        pagerFragments.add(FragmentSummary.newInstance(cookTime, servings, summary));
        pagerFragments.add(FragmentInfo.newInstance(ingredients));
        pagerFragments.add(FragmentInfo.newInstance(steps));

        adapterDetailPager = new AdapterDetailPager(getSupportFragmentManager(), this, pagerFragments);
        pager.setAdapter(adapterDetailPager);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
    }

    public void getDataFromDatabase(String id){
        ArrayList<Object> row = dbhelperRecipes.getRecipeDetail(id);

            recipeId = row.get(0).toString();
            recipeName = row.get(1).toString();
            categoryId = row.get(2).toString();
            categoryName = row.get(3).toString();
            cookTime = row.get(4).toString();
            servings = row.get(5).toString();
            summary = row.get(6).toString();
            ingredients = row.get(7).toString();
            steps = row.get(8).toString();
            recipeImage = row.get(9).toString();

            txtRecipeName.setText(recipeName);
            txtCategory.setText(categoryName);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    Intent createShareIntent() {
        String intro = getResources().getString(R.string.intro_message);
        String extra = getResources().getString(R.string.extra_message);
        String gPlayURL = getResources().getString(R.string.google_play_url);
        String appName = getResources().getString(R.string.app_name);
        String here = getResources().getString(R.string.here);
        String message = intro+" "+recipeName+extra+" "+appName+" "+here+" "+gPlayURL;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, getResources().getString(R.string.share_to)));
        return i;
    }


    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        dbhelperRecipes.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

}
