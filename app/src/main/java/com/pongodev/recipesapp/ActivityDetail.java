package com.pongodev.recipesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.DBHelperRecipes;
import com.pongodev.recipesapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class ActivityDetail extends ActionBarActivity {

    ImageView imgRecipe;
    TextView txtRecipeName, txtCategory, txtInfo, txtSummary;
    WebView webIngredients, webSteps;
    ScrollView sclDetail;
    ProgressBar prgLoading;
    ImageButton btnFavorite;
    AdView adView;

    String selectedId;
    private String time, minutes, serveFor, persons;
    private String recipeId, categoryId, categoryName, recipeName, cookTime, servings, summary, ingredients, steps, recipeImage;


    DBHelperRecipes dbhelperRecipes;
    DBHelperFavorites dbhelperFavorites;

    private final String TAG_CONTENT = "CONTENT_HERE";

    String htmlFormat = "<font color=\"#3E3E3E\">"+TAG_CONTENT+ "</font>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        imgRecipe = (ImageView) findViewById(R.id.imgRecipe);
        txtRecipeName = (TextView) findViewById(R.id.txtRecipeName);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtSummary = (TextView) findViewById(R.id.txtSummary);
        webIngredients = (WebView) findViewById(R.id.webIngredients);
        webSteps = (WebView) findViewById(R.id.webSteps);
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
        adView = (AdView) findViewById(R.id.adView);

        boolean isAdmobVisible = Utils.admobVisibility(adView, Utils.ARG_ADMOB_VISIBILITY);
        if(isAdmobVisible)
            Utils.loadAdmob(adView);




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


                        //finish();
                        overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        return true;
                    default:
                        return true;
                }

            }
        });



        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!dbhelperFavorites.isDataAvailable(recipeId)) {
                    boolean result = dbhelperFavorites.addRecipeToFavorites(recipeId, categoryId, recipeName,
                            cookTime, servings, summary,
                            ingredients, steps, recipeImage);

                    if(result){
                        Toast.makeText(getApplicationContext(), getString(R.string.success_add_data), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.data_exist), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public class syncGetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            sclDetail.setVisibility(View.GONE);
            btnFavorite.setVisibility(View.GONE);
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

            prgLoading.setVisibility(View.GONE);
            sclDetail.setVisibility(View.VISIBLE);
            btnFavorite.setVisibility(View.VISIBLE);

        }
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
            txtInfo.setText(time+" "+cookTime+" "+minutes+", "+
                    serveFor+" "+servings+" "+persons);
            txtSummary.setText(summary);

            //webIngredients.loadData(htmlFormat.replace(TAG_CONTENT, ingredients), "text/html", "UTF-8");

            /*
            webIngredients.post(new Runnable() {
                @Override
                public void run() {
                    webIngredients.loadData(htmlFormat.replace(TAG_CONTENT, ingredients), "text/html", "UTF-8");
                }
            });

            webSteps.post(new Runnable(){
                @Override
                public void run() {
                    webSteps.loadData(htmlFormat.replace(TAG_CONTENT,steps), "text/html", "UTF-8");
                }
            });
            */


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

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent= getIntent();
        String className = parentIntent.getStringExtra(Utils.ARG_PARENT_ACTIVITY); //getting the parent class name

        Intent newIntent=null;
        try {
            //you need to define the class with package name
            newIntent = new Intent(this,Class.forName(getApplicationContext().getPackageName()+"."+className));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }
}
