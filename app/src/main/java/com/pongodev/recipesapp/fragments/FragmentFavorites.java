package com.pongodev.recipesapp.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdView;
import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.adapters.AdapterFavorites;
import com.pongodev.recipesapp.utils.DBHelperFavorites;
import com.pongodev.recipesapp.utils.DBHelperRecipes;
import com.pongodev.recipesapp.utils.OnTapListener;
import com.pongodev.recipesapp.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by taufanerfiyanto on 10/2/14.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentFavorites extends Fragment {

    RecyclerView recyclerView;
    ProgressBar prgLoading;
    Button btnRefresh;
    AdView adView;

    DBHelperRecipes dbhelperRecipes;
    DBHelperFavorites dbhelperFavorites;
    AdapterFavorites adapterFavorites;

    OnRecipeSelectedListener mCallback;

    ArrayList<ArrayList<Object>> data;


    private String key;
    private String activePage;

    private ArrayList<String> recipeIds = new ArrayList<String>();
    private ArrayList<String> recipeNames = new ArrayList<String>();
    private ArrayList<String> cookTimes = new ArrayList<String>();
    private ArrayList<String> servings = new ArrayList<String>();
    private ArrayList<String> images = new ArrayList<String>();

    public interface OnRecipeSelectedListener {
        public void onRecipeSelected(String ID, String Action);
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentCategories newInstance() {
        FragmentCategories fragment = new FragmentCategories();

        return fragment;
    }

    public FragmentFavorites() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        setRetainInstance(true);


        // Connect view objects and view id on xml.
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        prgLoading = (ProgressBar) rootView.findViewById(R.id.prgLoading);
        adView = (AdView) rootView.findViewById(R.id.adView);

        boolean isAdmobVisible = Utils.admobVisibility(adView, Utils.ARG_ADMOB_VISIBILITY);
        if(isAdmobVisible)
            Utils.loadAdmob(adView);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        dbhelperRecipes = new DBHelperRecipes(getActivity());
        dbhelperFavorites = new DBHelperFavorites(getActivity());

        try {
            dbhelperRecipes.createDataBase();
            dbhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        dbhelperRecipes.openDataBase();
        dbhelperFavorites.openDataBase();


        if (getArguments().containsKey(Utils.ARG_PAGE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            activePage = getArguments().getString(Utils.ARG_PAGE);
            new syncGetData().execute();
        }




        adapterFavorites = new AdapterFavorites(getActivity());

        adapterFavorites.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String ID, String Action) {
                mCallback.onRecipeSelected(ID, Action);
            }
        });


        return rootView;
    }

    public class syncGetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromDatabase(key);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            prgLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapterFavorites.updateList(recipeIds, recipeNames, cookTimes, servings, images);

            recyclerView.setAdapter(adapterFavorites);
        }
    }

    public void getDataFromDatabase(String key){

        if(activePage.equals(Utils.ARG_CATEGORY)) {
            data = dbhelperRecipes.getAllRecipesData(key);
        }else if(activePage.equals(Utils.ARG_SEARCH)){
            data = dbhelperRecipes.getRecipesByName(key);
        }else {
            data = dbhelperFavorites.getAllRecipesData();
            if(data == null) {
                Log.d("Data :", "kosong");
            }else{
                Log.d("Data :","ada");
            }
        }


        for(int i = 0;i < data.size();i++){
            ArrayList<Object> row = data.get(i);

            recipeIds.add(row.get(0).toString());
            recipeNames.add(row.get(1).toString());
            cookTimes.add(row.get(2).toString());
            servings.add(row.get(3).toString());
            images.add(row.get(4).toString());
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnRecipeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRecipeSelectedListener");
        }
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
        dbhelperFavorites.close();
        super.onDestroy();
    }
}